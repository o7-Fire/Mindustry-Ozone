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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Premain;

import Atom.File.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class InstallerEntryPoint {
	public static void main(String[] args) throws IOException {
		File target = new File(FileUtility.getAppdata(), "/Mindustry/mods/Ozone.jar");
		System.out.println("Copying to:");
		System.out.println(target.getAbsolutePath());
		if (!target.exists()) target.createNewFile();//100
		Files.copy(new File(InstallerEntryPoint.class.getProtectionDomain().getCodeSource().getLocation().getFile()).toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		System.out.println("Finished");
		
	}
}
