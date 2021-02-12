package mindustry.core;

import Atom.Utility.Random;
import Ozone.Desktop.Bootstrap.DesktopBootstrap;
import Ozone.Propertied;
import Ozone.Settings.BaseSettings;
import arc.Core;
import arc.Files.FileType;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.Log;
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
	
	static {
		if (DesktopBootstrap.debug) Log.level = Log.LogLevel.debug;
	}
	
	public static void init() {
		if (!enabled) return;
		h = Propertied.read("version.properties");
		HashMap<String, String> hm = new HashMap<>();
		for (Map.Entry<String, String> s : h.entrySet())
			hm.put("Mindustry-" + s.getKey(), s.getValue());
		h = hm;
		Sentry.configureScope(scope -> {
			for (Map.Entry<String, String> e : h.entrySet())
				scope.setTag(e.getKey(), e.getValue());
		});
		h.put("Ozone-Version", Ozone.Version.core + ":" + Ozone.Version.desktop);
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
	
	/**
	 * @return whether the version is greater than the specified version string, e.g. "120.1"
	 */
	public static boolean isAtLeast(String str) {
		if (build <= 0 || str == null || str.isEmpty()) return true;
		
		int dot = str.indexOf('.');
		if (dot != -1) {
			int major = Strings.parseInt(str.substring(0, dot), 0), minor = Strings.parseInt(str.substring(dot + 1), 0);
			return build > major || (build == major && revision >= minor);
		}else {
			return build >= Strings.parseInt(str, 0);
		}
	}
	
	public static String buildString() {
		return build < 0 ? "custom" : build + (revision == 0 ? "" : "." + revision);
	}
	
	/**
	 * get menu version without colors
	 */
	public static String combined() {
		if (build == -1) {
			return "custom build";
		}
		return (type.equals("official") ? modifier : type) + " build " + build + (revision == 0 ? "" : "." + revision) + (BaseSettings.colorPatch ? "[royal]" : "") + " Ozone[white] " + versionColorized(Ozone.Version.core) + " [white]:  " + versionColorized(Ozone.Version.desktop) + "[white]";
	}
	
	private static String versionColorized(String s) {
		if (!BaseSettings.colorPatch) return s;
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
		return sb.substring(0, sb.length() - 1);
	}
	
}
