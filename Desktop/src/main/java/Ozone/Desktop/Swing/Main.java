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

/*
 * Created by JFormDesigner on Sat Sep 26 01:19:52 CEST 2020
 */

package Ozone.Desktop.Swing;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


/**
 * @author Itzbenz
 */
public class Main extends JPanel {
	private static boolean set;
	
	static {
		setTheme();
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	public JDialog dialog1;
	public JFileChooser fileChooser1;
	public JFrame frame1;
	public JLabel label1;
	public JLabel label4;
	public JButton button3;
	public JLabel labelStatus;
	public JProgressBar progressBar1;
	public JButton buttonInstall;
	public JButton buttonExit;
	
	public Main() {
		initComponents();
	}
	
	public static void setTheme() {
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialog1 = new JDialog();
		fileChooser1 = new JFileChooser();
		frame1 = new JFrame();
		label1 = new JLabel();
		label4 = new JLabel();
		button3 = new JButton();
		labelStatus = new JLabel();
		progressBar1 = new JProgressBar();
		buttonInstall = new JButton();
		buttonExit = new JButton();
		
		//======== dialog1 ========
		{
			dialog1.setResizable(false);
			dialog1.setTitle("Select Mindustry Directory");
			Container dialog1ContentPane = dialog1.getContentPane();
			dialog1ContentPane.setLayout(new MigLayout("hidemode 3",
					// columns
					"[fill]" + "[fill]" + "[fill]" + "[fill]",
					// rows
					"[]" + "[]" + "[]" + "[]"));
			dialog1ContentPane.add(fileChooser1, "cell 0 0 4 3");
			dialog1.pack();
			dialog1.setLocationRelativeTo(dialog1.getOwner());
		}
		
		//======== frame1 ========
		{
			frame1.setResizable(false);
			frame1.setTitle("Ozone Installer v0.5");
			Container frame1ContentPane = frame1.getContentPane();
			frame1ContentPane.setLayout(new MigLayout("fill,hidemode 3",
					// columns
					"[fill]" + "[fill]",
					// rows
					"[]" + "[]" + "[]" + "[]" + "[]"));
			
			//---- label1 ----
			label1.setText("Mindustry");
			frame1ContentPane.add(label1, "cell 0 0 2 1");
			
			//---- label4 ----
			label4.setText("Mindustry/mods");
			frame1ContentPane.add(label4, "cell 0 1");
			
			//---- button3 ----
			button3.setText("Open");
			frame1ContentPane.add(button3, "cell 1 1");
			
			//---- labelStatus ----
			labelStatus.setText("Downloading Library");
			frame1ContentPane.add(labelStatus, "cell 0 2 2 1");
			frame1ContentPane.add(progressBar1, "cell 0 3 2 1");
			
			//---- buttonInstall ----
			buttonInstall.setText("Install");
			frame1ContentPane.add(buttonInstall, "cell 0 4");
			
			//---- buttonExit ----
			buttonExit.setText("Run");
			frame1ContentPane.add(buttonExit, "cell 1 4");
			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
