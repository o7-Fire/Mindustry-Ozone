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

package Ozone.Desktop.Swing;

import javax.swing.*;
import java.awt.*;

public class DownloadBar {
	JLabel label1 = new JLabel("Title");
	JLabel label2 = new JLabel("Desk");
	JProgressBar pbar = new JProgressBar();
	JFrame frame = new JFrame();
	
	public DownloadBar() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new GridLayout(3, 1));
		frame.add(label1);
		frame.add(label2);
		frame.add(pbar);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setVisible(true);
		pack();
	}
	
	public void setMax(int max) {
		pbar.setMinimum(0);
		pbar.setMaximum(max);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setValue(int value) {
		pbar.setValue(value);
	}
	
	public void setTitle(String t) {
		label1.setText(t);
		pack();
	}
	
	public void setDesk(String t) {
		label2.setText(t);
		pack();
	}
	
	public void pack() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setSize(width / 2, height / 16);
	}
}
