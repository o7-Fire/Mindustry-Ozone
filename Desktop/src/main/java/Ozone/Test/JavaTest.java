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

import Atom.Utility.Meth;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Propertied;
import arc.util.Strings;
import mindustry.core.Version;

import java.math.BigInteger;

public class JavaTest extends Test {
	public JavaTest() {
		add("Java Logic", () -> {
			assert 1 == 1;
			assert "b".equals("B".toLowerCase());
			long a = Random.getInt();
			long b = Random.getInt();
			//if by chance its same, its a ~~miracle~~ bug
			assert a != b : "2 Random Integer is same how ??: " + a;
			
			//Encryption Number
			StringBuilder s = new StringBuilder();
			String message = Utility.shuffle(" a secret message that is" + Random.getString());
			
			
			BigInteger privateKey = new BigInteger(Random.getLong() + "");//Anything
			BigInteger publicKey = new BigInteger(Random.getLong() + "");//Public key must same on 2 side, can be anything
			BigInteger sharedKey = publicKey.multiply(privateKey);//gonna be sended to other side, so they can generate common key
			BigInteger receivedSharedKey = new BigInteger(Random.getLong() + "");//sharedKey from other side
			sharedKey = sharedKey.multiply(receivedSharedKey);//A common key generated, can be used to encrypt message
			int commonSeparator = Meth.positive(Random.getInt());// can be received from other side after key exchange
			Log.info("SharedKey:" + sharedKey);
			for (int c : message.toCharArray()) s.append(c).append(commonSeparator);
			Log.info("Encoded Message:\n" + s.toString());
			BigInteger encryptedMessage = new BigInteger(s.toString()).multiply(sharedKey);
			Log.info("Encrypted Encoded Message: " + encryptedMessage);
			
			String decryptedS = encryptedMessage.divide(sharedKey).toString();
			
			StringBuilder decryptedMessage = new StringBuilder();
			for (String se : decryptedS.split(commonSeparator + ""))
				decryptedMessage.append((char) Integer.parseInt(se));
			Log.info("Decrypted Encoded Message: " + decryptedS);
			Log.info("Decrypted Message: " + decryptedMessage);
			assert decryptedS.equals(s.toString()) : "Encoded message not same";
			assert decryptedMessage.toString().equals(message) : "Message received not same";
		});
		add("Garbage Collector", () -> {
			System.gc();
			Runtime runtime = Runtime.getRuntime();
			for (int i = 5; i < 100000; i += 10000) {
				StringBuilder s = new StringBuilder("1");
				for (int j = 1; j < i; j++) {
					s.append(Random.getInt(0, 9));
				}
				System.gc();
				long memory = runtime.totalMemory() - runtime.freeMemory();
				BigInteger b = new BigInteger(s.toString());
				long usedMemory = runtime.totalMemory() - runtime.freeMemory();
				Log.info("BigInteger Length: " + b.toString().length());
				Log.info("Used memory: " + (usedMemory - memory));
			}
		});
		add("Strip Colors, Version Class Patch, Version Loading", () -> {
			Version.init();
			Log.info(Strings.stripColors(Version.combined()));
		});
		
		add("Random Generation, Benchmark", () -> {
			long s = System.currentTimeMillis();
			int i = 0;
			while ((System.currentTimeMillis() - s) < 200) {
				java.util.Random random = new java.util.Random();
				random.nextInt(1000000000);
				i++;
			}
			arc.util.Log.info("Generated " + i + " random number in " + (System.currentTimeMillis() - s) + "ms");
		});
		
		add("Manifest Validation, Version Validation", () -> {
			Log.info(Propertied.Manifest.size() + " Manifest");
			assert Propertied.Manifest.size() == 11 : "Invalid Manifest";
			//Major Build Validation
			int a = Version.build;
			String s = Propertied.Manifest.getOrDefault("MindustryVersion", "-2").substring(1);
			if (s.contains(".")) s = s.substring(0, s.indexOf('.'));
			int b = Integer.parseInt(s);
			assert b == a : "Manifest Build Number Mismatch a:b " + a + ":" + b;
		});
	}
}
