package mindustry.core;

import Atom.Utility.Random;
import Ozone.Desktop.Propertied;
import arc.Core;
import arc.Files.FileType;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.OS;
import arc.util.Strings;
import arc.util.io.PropertiesUtils;
import io.sentry.Sentry;

import java.util.HashMap;
import java.util.Map;

public class Version {
	/**
	 * Build type. 'official' for official releases; 'custom' or 'bleeding edge' are also used.
	 */
	public static String type = "unknown";
	/**
	 * Build modifier, e.g. 'alpha' or 'release'
	 */
	public static String modifier = "unknown";
	/**
	 * Number specifying the major version, e.g. '4'
	 */
	public static int number;
	/**
	 * Build number, e.g. '43'. set to '-1' for custom builds.
	 */
	public static int build = 0;
	/**
	 * Revision number. Used for hotfixes. Does not affect server compatibility.
	 */
	public static int revision = 0;
	/**
	 * Whether version loading is enabled.
	 */
	public static boolean enabled = true;
	public static HashMap<String, String> h = new HashMap<>();
	
	public static void init() {
		if (!enabled) return;
		h = Propertied.read("version.properties");
		Sentry.configureScope(scope -> {
			for (Map.Entry<String, String> e : h.entrySet())
				scope.setTag("Mindustry-" + e.getKey(), e.getValue());
		});
		h.put("Ozone-Version", Ozone.Watcher.Version.semantic + ":" + Settings.Version.semantic);
		Fi file = OS.isAndroid || OS.isIos ? Core.files.internal("version.properties") : new Fi("version.properties", FileType.internal);
		
		ObjectMap<String, String> map = new ObjectMap<>();
		PropertiesUtils.load(map, file.reader());
		
		type = map.get("type");
		number = Integer.parseInt(map.get("number", "4"));
		modifier = map.get("modifier");
		if (map.get("build").contains(".")) {
			String[] split = map.get("build").split("\\.");
			try {
				build = Integer.parseInt(split[0]);
				revision = Integer.parseInt(split[1]);
			}catch (Throwable e) {
				e.printStackTrace();
				build = -1;
			}
		}else {
			build = Strings.canParseInt(map.get("build")) ? Integer.parseInt(map.get("build")) : -1;
		}
	}
	
	public static String buildString() {
		return build < 0 ? "custom" : build + (revision == 0 ? "" : "." + revision);
	}
	
	private static String versionColorized(String s) {
		StringBuilder sb = new StringBuilder();
		for (String eh : s.split("\\.")) {
			Random r = new Random();
			int he = Integer.parseInt(eh);
			if (he == 0) he = 10215;
			r.setSeed(he * 5000000L);
			int i = r.nextInt(12777215 - 7000000 + 1) + 12777215;
			String e = String.format("[#%06x]", i);
			sb.append(e).append(eh).append(".");
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	
	/**
	 * get menu version without colors
	 */
	public static String combined() {
		if (build == -1) {
			return "custom build";
		}
		return (type.equals("official") ? modifier : type) + " build " + build + (revision == 0 ? "" : "." + revision) + " [royal]Ozone[white] " + versionColorized(Ozone.Watcher.Version.semantic) + " [white]:  " + versionColorized(Settings.Version.semantic) + "[white]";
	}
}
