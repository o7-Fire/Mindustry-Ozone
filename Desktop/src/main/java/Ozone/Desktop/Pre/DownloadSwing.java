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

package Ozone.Desktop.Pre;

import Atom.Net.Download;
import Ozone.Desktop.Swing.DownloadBar;

import javax.swing.*;
import java.io.File;
import java.net.URL;

//probably will be relocated to Atom library
public class DownloadSwing extends Download {
	
	private JProgressBar progressBar = null;
	private JLabel label = null;
	private JFrame frame = null;
	private DownloadBar DownloadBar = null;
	private long lastRecordTime = 0, lastRecord = 0;
	private boolean displayed = false;
	
	// Constructor for Download.
	public DownloadSwing(URL url, File file) {
		super(url, file);
	}
	
	public void display(JFrame frame) {
		this.frame = frame;
	}
	
	public void display(JLabel label) {
		this.label = label;
		this.label.setText("Connecting...." + url.toString());
		if (frame != null) frame.pack();
	}
	
	public void display(JProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(100);
		this.progressBar.setValue(0);
		this.progressBar.setVisible(true);
		if (frame != null) frame.pack();
		
	}
	
	
	public void display() {
		if (displayed) return;
		displayed = true;
		DownloadBar = new DownloadBar();
		DownloadBar.setTitle(url.toString());
		DownloadBar.setDesk(file.getAbsolutePath());
	}
	
	protected void setMax(long max) {
		if (progressBar != null) progressBar.setMaximum(1000);
		if (DownloadBar != null) {
			DownloadBar.setMax(1000);
			if (getSize() < 10000000) DownloadBar.setDesk(file.getName() + " " + (getSize() / 1000) + " KB");
			else DownloadBar.setDesk(file.getName() + " " + (getSize() / 1000000) + " MB");
		}
		if (label != null) {
			if (getSize() < 10000000) label.setText(file.getName() + " " + (getSize() / 1000) + " KB");
			else label.setText(file.getName() + " " + (getSize() / 1000000) + " MB");
		}
		if (frame != null) frame.pack();
		
	}
	
	
	private int getProgress() {
		return (int) (((double) downloaded.get() / size) * 1000);
	}
	
	protected void updateProgress() {
		if (progressBar != null) progressBar.setValue(getProgress());
		
		
		if ((System.currentTimeMillis() - lastRecordTime) > 800) {
			if (lastRecord != 0) {
				float down = downloaded.get() - lastRecord;
				down = down / 100000;
				if (DownloadBar != null) DownloadBar.setDesk(getUserReading(size) + "      " + down + " Mb/Second");
				if (label != null) label.setText(getUserReading(size) + "    " + down + " Mb/Second");
			}
			lastRecord = downloaded.get();
			lastRecordTime = System.currentTimeMillis();
		}
		if (DownloadBar != null) DownloadBar.setValue(getProgress());
		if (frame != null) frame.pack();
	}
	
	
	@Override
	protected void close() {
		super.close();
		if (DownloadBar != null) DownloadBar.setVisible(false);
		
	}
}