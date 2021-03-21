/*
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
 */

public class Meth {
	public static void main(String[] args) {
		String s = "ˈ⃢ˈ⃢ˈ⃢ˈ⃢ˈ⃢";
		for (int i : s.toCharArray())
			System.out.println(i);
		int total = 2;
		for (int i = 0; i < 15; i++) {
			System.out.println(i + ". " + total);
			total = total + 3;
			
		}
		System.out.println(total);
		//if(true) return;
		
		recommendedThreadCount();
		int sum = 0, sum2;
		int n = 6;
		int k = 2;
		int i;
		
		// A summation can be written as a for loop. Here the loop represents a summation from i=1 to n.
		for (i = k; i <= n; i++) {
			sum += calc(i);
			System.out.println(sum + " : " + calc(i));
		}
		
		// You can also use the formula to give an answer for sum.
		sum2 = n * (n + 1) / 2;
		sum2 = calc(sum2);
		System.out.println(sum == sum2 ? "Calculation Valid" : "Invalid: " + sum2);
	}
	
	public static int recommendedThreadCount() {
		int mRtnValue = 0;
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long mTotalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		int mAvailableProcessors = runtime.availableProcessors();
		long increase = freeMemory * 10;
		freeMemory += increase;
		mTotalMemory += increase;
		maxMemory += increase;
		mAvailableProcessors += 20;
		long mTotalFreeMemory = freeMemory + (maxMemory - mTotalMemory);
		mRtnValue = (int) (mTotalFreeMemory / 1024);
		
		int mNoOfThreads = mAvailableProcessors - 1;
		if (mNoOfThreads < mRtnValue) mRtnValue = mNoOfThreads;
		return mRtnValue;
	}
	
	public static int calc(int i) {
		return i * 5 - 6;
	}
}
