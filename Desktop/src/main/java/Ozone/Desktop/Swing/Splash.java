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
import java.net.URL;

public class Splash {
	JWindow window = new JWindow();
	JLabel label;
	
	public Splash(URL gif) {
		label = new JLabel("", new ImageIcon(gif), SwingConstants.CENTER);
		window.setAlwaysOnTop(true);
		window.getContentPane().add(label);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	public void dispose() {
		window.dispose();
	}
	
	public void setLabel(String t) {
		label.setText(t);
		window.setLocationRelativeTo(null);
		window.pack();
	}
	
	public void setVisible(boolean b) {
		window.setVisible(b);
	}
}
