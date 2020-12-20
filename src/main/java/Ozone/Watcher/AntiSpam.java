/*
 * Copyright 2020 Itzbenz
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

package Ozone.Watcher;

public class AntiSpam {
	public static int MaxDataPerSecond = 5;
	public final Object id;
	public final String alias;
	public Reason reason = Reason.Nothing;
	private Object lastData = null;
	private int lastDuplicate = 0;
	private long lastDataCaptured = 0L;
	private String reasonString = "";

	public AntiSpam(Object id, String alias) {
		this.id = id;
		this.alias = alias;
	}

	public static long rateLimit() {
		return MaxDataPerSecond / 1000;
	}

	public boolean accepted() {
		return accepted(lastData);
	}

	public boolean accepted(Object last) {
		reason = Reason.NotEnoughData;
		if (last == null) return true;
		long captureMillis = (System.currentTimeMillis() - lastDataCaptured);
		if (captureMillis < rateLimit()) {
			reasonMaxDataPerSecond(captureMillis);
			return false;
		}
		if (last.equals(lastData)) {
			reasonDuplicate();
			return false;
		}
		reason = Reason.Nothing;
		return true;
	}

	public void setData(Object data) {
		if (data.equals(lastData)) lastDuplicate++;
		else lastDuplicate = 0;
		lastData = data;
		lastDataCaptured = System.currentTimeMillis();
	}

	public String getReason() {
		return (reasonString + (reasonString = ""));//order of operation
	}

	private void reasonDuplicate() {
		reason = Reason.Duplicate;
		reasonString += "[" + lastDuplicate + "x]" + "Duplicate last message for " + alias;
	}

	private void reasonMaxDataPerSecond(long capture) {
		reason = Reason.MaxDataPerSecond;
		reasonString += "MaxDataPerSecond reached for \"" + alias + "\" in " + capture + " ms\n";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AntiSpam) {
			AntiSpam a = (AntiSpam) obj;
			return a.id.equals(id);
		}
		return super.equals(obj);
	}

	public enum Reason {
		Duplicate("Duplicate Message/Spam"), MaxDataPerSecond("Ratelimit Reached"), NotEnoughData("Unknown/Null"), Nothing("Nothing");
		String description;

		Reason(String description) {
			this.description = description;
		}
	}

}
