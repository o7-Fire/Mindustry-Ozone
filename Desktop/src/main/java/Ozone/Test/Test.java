/*******************************************************************************
 * Copyright 2021 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package Ozone.Test;

import Atom.Utility.Log;
import Atom.Utility.Pool;
import Shared.WarningHandler;
import Shared.WarningReport;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Future;

public class Test {
	protected static Result successDefault = new Result("Success", true), failedDefault = new Result("Failed", false);
	protected static Log Log;
	private static int staticTest = 0;
	protected ArrayList<Result> testResult = new ArrayList<>();
	protected ArrayList<SubTest> subTests = new ArrayList<>();
	protected int testConducted = 0;
	
	public Test() {
	
	}
	
	public static String getResult(ArrayList<Test.Result> results) {
		StringBuilder sb = new StringBuilder();
		if (results.isEmpty()) sb.append("No Result Found");
		for (Test.Result r : results)
			sb.append(r.reason).append(":\n").append(r.success ? "Success" : "Failed").append(" in ").append(r.duration).append("ms\n");
		return sb.toString();
	}
	
	public static Result test(Testable r, String name) {
		@NotNull ITransaction transaction = Sentry.startTransaction("Test", name);
		long start = System.currentTimeMillis();
		Throwable t = null;
		try {
			r.run();
		}catch (Throwable e) {
			t = e;
			e.printStackTrace();
			WarningHandler.handleProgrammerFault(e);
			if (transaction != null) {
				transaction.setThrowable(e);
				transaction.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			
		}
		Result result = new Result(t == null, start);
		result.reason = name + " " + result.reason;
		if (t != null) {
			result.reason += ": " + t.toString();
			result.t = t;
			new WarningReport(result.reason).setWhyItsAProblem("Test failed").setLevel(WarningReport.Level.warn).report();
		}else {
			new WarningReport(result.reason).setWhyItsAProblem("Test success").report();
		}
		if (transaction != null) transaction.finish();
		return result;
	}
	
	public static ArrayList<Result> runTest(ArrayList<SubTest> ar) {
		ArrayList<Result> results = new ArrayList<>();
		for (SubTest s : ar) {
			Log.info("Starting: " + s.name + " Test");
			results.add(test(s.testable, s.name));
			Log.info("Finished: " + s.name + " Test");
		}
		return results;
	}
	
	public static void setLog(Log log) {
		Log = log;
	}
	
	public static Set<Class<? extends Test>> getRawTestKit() {
		return new Reflections(ConfigurationBuilder.build(Test.class.getPackageName(), SubTypesScanner.class).addClassLoader(Test.class.getClassLoader())).getSubTypesOf(Test.class);
	}
	
	public static ArrayList<Test> getTestKit() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		ArrayList<Test> testKit = new ArrayList<>();
		for (Class<? extends Test> test : getRawTestKit())
			testKit.add(test.getDeclaredConstructor().newInstance());
		return testKit;
	}
	
	public static Result test(SubTest subTest) {
		return test(subTest.testable, subTest.name);
	}
	
	public static Result test(Testable r) {
		return test(r, "Test #" + staticTest++);
	}
	
	public static ArrayList<Result> runConcurrentTest(ArrayList<SubTest> ar) {
		ArrayList<Future<Result>> task = new ArrayList<>();
		ArrayList<Result> results = new ArrayList<>();
		for (SubTest s : ar) {
			Log.info("Running: " + s.name + " Test");
			task.add(Pool.submit(() -> {
				Result r = test(s.testable, s.name);
				Log.info(s.name + " Test Finished");
				return r;
			}));
		}
		task.forEach(f -> {
			try { results.add(f.get()); }catch (Throwable ignored) { }
		});
		return results;
	}
	
	public void add(String name, Testable testable) {
		subTests.add(new SubTest(name, testable));
	}
	
	public void add(SubTest t) {
		subTests.add(t);
	}
	
	public ArrayList<Result> runSync() {
		testResult.clear();
		testResult.addAll(runTest(subTests));
		return testResult;
	}
	
	public ArrayList<Result> run() {
		testResult.clear();
		testResult.addAll(runConcurrentTest(subTests));
		return testResult;
	}
	
	protected void tryTest(SubTest t) {
		tryTest(t.testable, t.name);
	}
	
	protected void tryTest(Testable r) {
		testResult.add(test(r));
	}
	
	protected void tryTest(Testable r, String name) {
		testResult.add(test(r, name));
	}
	
	public ArrayList<SubTest> getSubTest() {
		return subTests;
	}
	
	public static class SubTest {
		public final String name;
		public final Testable testable;
		
		public SubTest(String name, Testable testable) {
			this.name = name;
			this.testable = testable;
		}
	}
	
	public static class Result implements Serializable {
		public String reason;
		public boolean success;
		public long duration;
		public Throwable t;
		
		public Result() {
		
		}
		
		public Result(Throwable e, long duration) {
			this(e.toString(), false, duration);
			t = e;
		}
		
		public Result(Throwable e) {
			this(e, System.currentTimeMillis());
		}
		
		public Result(boolean success) {
			this(success, System.currentTimeMillis());
		}
		
		public Result(String reason, boolean success) {
			this(reason, success, System.currentTimeMillis());
		}
		
		public Result(boolean success, long duration) {
			this(success ? "Success" : "Failed", success, duration);
		}
		
		public Result(String reason, boolean success, long duration) {
			this.reason = reason;
			this.success = success;
			setDuration(duration);
		}
		
		public void setDuration(long duration) {
			this.duration = System.currentTimeMillis() - duration;
		}
		
	}
}
