package mindustry;

import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Patch.Updater;
import arc.Core;
import arc.Events;
import arc.Settings;
import arc.assets.Loadable;
import arc.files.Fi;
import arc.graphics.Color;
import arc.scene.ui.layout.Scl;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.*;
import arc.util.Log.LogHandler;
import io.sentry.Sentry;
import mindustry.ai.BaseRegistry;
import mindustry.ai.BlockIndexer;
import mindustry.ai.Pathfinder;
import mindustry.ai.WaveSpawner;
import mindustry.async.AsyncCore;
import mindustry.core.*;
import mindustry.entities.EntityCollisions;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.Schematics;
import mindustry.game.Universe;
import mindustry.game.Waves;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.graphics.MenuRenderer;
import mindustry.input.Binding;
import mindustry.io.JsonIO;
import mindustry.logic.GlobalConstants;
import mindustry.maps.Map;
import mindustry.maps.Maps;
import mindustry.mod.Mods;
import mindustry.net.BeControl;
import mindustry.net.Net;
import mindustry.net.ServerGroup;
import mindustry.world.Tile;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import static arc.Core.*;

public class Vars implements Loadable {
	/**
	 * Maximum extra padding around deployment schematics.
	 */
	public static final int maxLoadoutSchematicPad = 5;
	/**
	 * Maximum schematic size.
	 */
	public static final int maxSchematicSize = 32;
	/**
	 * All schematic base64 starts with this string.
	 */
	public static final String schematicBaseStart = "bXNjaA";
	/**
	 * IO buffer size.
	 */
	public static final int bufferSize = 8192;
	/**
	 * global charset, since Android doesn't support the Charsets class
	 */
	public static final Charset charset = Charset.forName("UTF-8");
	/**
	 * URL for itch.io donations.
	 */
	public static final String donationURL = "https://anuke.itch.io/mindustry/purchase";
	/**
	 * URL the links to the wiki's modding guide.
	 */
	public static final String modGuideURL = "https://mindustrygame.github.io/wiki/modding/1-modding/";
	/**
	 * URL for discord invite.
	 */
	public static final String discordURL = "https://discord.gg/mindustry";
	/**
	 * URL for sending crash reports to
	 */
	public static final String crashReportURL = "http://192.99.169.18/report";
	/**
	 * maximum size of any block, do not change unless you know what you're doing
	 */
	public static final int maxBlockSize = 16;
	/**
	 * URL to the JSON file containing all the global, public servers. Not queried in BE.
	 */
	public static final String serverJsonURL = "https://raw.githubusercontent.com/Anuken/Mindustry/master/servers.json";
	/**
	 * URL to the JSON file containing all the BE servers. Only queried in BE.
	 */
	public static final String serverJsonBeURL = "https://raw.githubusercontent.com/Anuken/Mindustry/master/servers_be.json";
	/**
	 * URL to the JSON file containing all the BE servers. Only queried in the V6 alpha (will be removed once it's out).
	 */
	public static final String serverJsonV6URL = "https://raw.githubusercontent.com/Anuken/Mindustry/master/servers_v6.json";
	/**
	 * URL of the github issue report template.
	 */
	public static final String reportIssueURL = "https://github.com/Anuken/Mindustry/issues/new?labels=bug&template=bug_report.md";
	/**
	 * list of built-in servers.
	 */
	public static final Seq<ServerGroup> defaultServers = Seq.with();
	/**
	 * maximum distance between mine and core that supports automatic transferring
	 */
	public static final float mineTransferRange = 220f;
	/**
	 * chance of an invasion per turn, 1 = 100%
	 */
	public static final float baseInvasionChance = 1f / 100f;
	/**
	 * max chat message length
	 */
	public static final int maxTextLength = 150;
	/**
	 * max player name length in bytes
	 */
	public static final int maxNameLength = 40;
	/**
	 * displayed item size when ingame.
	 */
	public static final float itemSize = 5f;
	/**
	 * units outside of this bound will die instantly
	 */
	public static final float finalWorldBounds = 500;
	/**
	 * mining range for manual miners
	 */
	public static final float miningRange = 70f;
	/**
	 * range for building
	 */
	public static final float buildingRange = 220f;
	/**
	 * range for moving items
	 */
	public static final float itemTransferRange = 220f;
	/**
	 * range for moving items for logic units
	 */
	public static final float logicItemTransferRange = 45f;
	/**
	 * duration of time between turns in ticks
	 */
	public static final float turnDuration = 2 * Time.toMinutes;
	/**
	 * how many turns have to pass before invasions start
	 */
	public static final int invasionGracePeriod = 20;
	/**
	 * min armor fraction damage; e.g. 0.05 = at least 5% damage
	 */
	public static final float minArmorDamage = 0.1f;
	/**
	 * launch animation duration
	 */
	public static final float launchDuration = 140f;
	/**
	 * size of tiles in units
	 */
	public static final int tilesize = 8;
	/**
	 * size of one tile payload (^2)
	 */
	public static final float tilePayload = tilesize * tilesize;
	/**
	 * all choosable player colors in join/host dialog
	 */
	public static final Color[] playerColors = {Color.valueOf("82759a"), Color.valueOf("c0c1c5"), Color.valueOf("ffffff"), Color.valueOf("7d2953"), Color.valueOf("ff074e"), Color.valueOf("ff072a"), Color.valueOf("ff76a6"), Color.valueOf("a95238"), Color.valueOf("ffa108"), Color.valueOf("feeb2c"), Color.valueOf("ffcaa8"), Color.valueOf("008551"), Color.valueOf("00e339"), Color.valueOf("423c7b"), Color.valueOf("4b5ef1"), Color.valueOf("2cabfe"),};
	/**
	 * default server port
	 */
	public static final int port = 6567;
	/**
	 * multicast discovery port.
	 */
	public static final int multicastPort = 20151;
	/**
	 * multicast group for discovery.
	 */
	public static final String multicastGroup = "227.2.7.7";
	/**
	 * map file extension
	 */
	public static final String mapExtension = "msav";
	/**
	 * save file extension
	 */
	public static final String saveExtension = "msav";
	/**
	 * schematic file extension
	 */
	public static final String schematicExtension = "msch";
	/**
	 * main application name, capitalized
	 */
	public static String appName = "Mindustry";
	/**
	 * Whether to load locales.
	 */
	public static boolean loadLocales = true;
	/**
	 * Whether the logger is loaded.
	 */
	public static boolean loadedLogger = false, loadedFileLogger = false;
	/**
	 * Whether to enable various experimental features (e.g. cliffs)
	 */
	public static boolean experimental = false;
	/**
	 * tile used in certain situations, instead of null
	 */
	public static Tile emptyTile;
	/**
	 * for map generator dialog
	 */
	public static boolean updateEditorOnChange = false;
	/**
	 * if true, UI is not drawn
	 */
	public static boolean disableUI;
	/**
	 * if true, game is set up in mobile mode, even on desktop. used for debugging
	 */
	public static boolean testMobile;
	/**
	 * whether the game is running on a mobile device
	 */
	public static boolean mobile;
	/**
	 * whether the game is running on an iOS device
	 */
	public static boolean ios;
	/**
	 * whether the game is running on an Android device
	 */
	public static boolean android;
	/**
	 * whether the game is running on a headless server
	 */
	public static boolean headless;
	/**
	 * whether steam is enabled for this game
	 */
	public static boolean steam;
	/**
	 * whether typing into the console is enabled - developers only
	 */
	public static boolean enableConsole = false;
	/**
	 * whether to clear sector saves when landing
	 */
	public static boolean clearSectors = false;
	/**
	 * whether any light rendering is enabled
	 */
	public static boolean enableLight = true;
	/**
	 * Whether to draw shadows of blocks at map edges and static blocks.
	 * Do not change unless you know exactly what you are doing.
	 */
	public static boolean enableDarkness = true;
	/**
	 * application data directory, equivalent to {@link Settings#getDataDirectory()}
	 */
	public static Fi dataDirectory;
	/**
	 * data subdirectory used for screenshots
	 */
	public static Fi screenshotDirectory;
	/**
	 * data subdirectory used for custom maps
	 */
	public static Fi customMapDirectory;
	/**
	 * data subdirectory used for custom map previews
	 */
	public static Fi mapPreviewDirectory;
	/**
	 * tmp subdirectory for map conversion
	 */
	public static Fi tmpDirectory;
	/**
	 * data subdirectory used for saves
	 */
	public static Fi saveDirectory;
	/**
	 * data subdirectory used for mods
	 */
	public static Fi modDirectory;
	/**
	 * data subdirectory used for schematics
	 */
	public static Fi schematicDirectory;
	/**
	 * data subdirectory used for bleeding edge build versions
	 */
	public static Fi bebuildDirectory;
	/**
	 * empty map, indicates no current map
	 */
	public static Map emptyMap;
	/**
	 * list of all locales that can be switched to
	 */
	public static Locale[] locales;
	
	public static FileTree tree = new FileTree();
	public static Net net;
	public static ContentLoader content;
	public static GameState state;
	public static EntityCollisions collisions;
	public static Waves waves;
	public static Platform platform = new Platform() {};
	public static Mods mods;
	public static Schematics schematics;
	public static BeControl becontrol;
	public static AsyncCore asyncCore;
	public static BaseRegistry bases;
	public static GlobalConstants constants;
	
	public static Universe universe;
	public static World world;
	public static Maps maps;
	public static WaveSpawner spawner;
	public static BlockIndexer indexer;
	public static Pathfinder pathfinder;
	
	public static Control control;
	public static Logic logic;
	public static Renderer renderer;
	public static UI ui;
	public static NetServer netServer;
	public static NetClient netClient;
	
	public static Player player;
	public static MenuRenderer menuRenderer;
	
	public Vars() {
		menuRenderer = new MenuRenderer();
	}
	
	public static void init() {
		Groups.init();
		
		if (loadLocales) {
			//load locales
			String[] stra = Core.files.internal("locales").readString().split("\n");
			locales = new Locale[stra.length];
			for (int i = 0; i < locales.length; i++) {
				String code = stra[i];
				if (code.contains("_")) {
					locales[i] = new Locale(code.split("_")[0], code.split("_")[1]);
				}else {
					locales[i] = new Locale(code);
				}
			}
			
			Arrays.sort(locales, Structs.comparing(l -> l.getDisplayName(l), String.CASE_INSENSITIVE_ORDER));
			locales = Seq.with(locales).and(new Locale("router")).toArray(Locale.class);
		}
		
		Version.init();
		
		dataDirectory = settings.getDataDirectory();
		screenshotDirectory = dataDirectory.child("screenshots/");
		customMapDirectory = dataDirectory.child("maps/");
		mapPreviewDirectory = dataDirectory.child("previews/");
		saveDirectory = dataDirectory.child("saves/");
		tmpDirectory = dataDirectory.child("tmp/");
		modDirectory = dataDirectory.child("mods/");
		schematicDirectory = dataDirectory.child("schematics/");
		bebuildDirectory = dataDirectory.child("be_builds/");
		emptyMap = new Map(new StringMap());
		emptyTile = null;
		
		if (tree == null) tree = new FileTree();
		if (mods == null) mods = new Mods();
		
		content = new ContentLoader();
		waves = new Waves();
		collisions = new EntityCollisions();
		world = new World();
		universe = new Universe();
		becontrol = new BeControl();
		asyncCore = new AsyncCore();
		
		maps = new Maps();
		spawner = new WaveSpawner();
		indexer = new BlockIndexer();
		pathfinder = new Pathfinder();
		bases = new BaseRegistry();
		constants = new GlobalConstants();
		
		state = new GameState();
		
		mobile = Core.app.isMobile() || testMobile;
		ios = Core.app.isIOS();
		android = Core.app.isAndroid();
		
		modDirectory.mkdirs();
		
		mods.load();
		maps.load();
	}
	
	public static void loadFileLogger() {
		if (loadedFileLogger) return;
		
		settings.setAppName(appName);
		
		try {
			Writer writer = settings.getDataDirectory().child("last_log.txt").writer(false);
			LogHandler log = Log.logger;
			//ignore it
			Log.logger = (level, text) -> {
				log.log(level, text);
				
				try {
					writer.write("[" + Character.toUpperCase(level.name().charAt(0)) + "] " + Log.removeColors(text) + "\n");
					writer.flush();
				}catch (IOException e) {
					e.printStackTrace();
					//ignore it
				}
			};
		}catch (Exception e) {
			//handle log file not being found
			Log.err(e);
		}
		
		loadedFileLogger = true;
	}
	
	public static void loadSettings() {
		settings.setJson(JsonIO.json());
		settings.setAppName(appName);
		
		if (steam || (Version.modifier != null && Version.modifier.contains("steam"))) {
			settings.setDataDirectory(Core.files.local("saves/"));
		}
		
		settings.defaults("locale", "default", "blocksync", true);
		keybinds.setDefaults(Binding.values());
		settings.setAutosave(false);
		settings.load();
		
		Scl.setProduct(settings.getInt("uiscale", 100) / 100f);
		
		if (!loadLocales) return;
		
		try {
			//try loading external bundle
			Fi handle = Core.files.local("bundle");
			
			Locale locale = Locale.ENGLISH;
			Core.bundle = I18NBundle.createBundle(handle, locale);
			
			Log.info("NOTE: external translation bundle has been loaded.");
			
			if (!headless) {
				Time.run(10f, () -> ui.showInfo("Note: You have successfully loaded an external translation bundle."));
			}
		}catch (Throwable e) {
			//no external bundle found
			
			Fi handle = Core.files.internal("bundles/bundle");
			Locale locale;
			String loc = settings.getString("locale");
			if (loc.equals("default")) {
				locale = Locale.getDefault();
			}else {
				Locale lastLocale;
				if (loc.contains("_")) {
					String[] split = loc.split("_");
					lastLocale = new Locale(split[0], split[1]);
				}else {
					lastLocale = new Locale(loc);
				}
				
				locale = lastLocale;
			}
			
			Locale.setDefault(locale);
			Core.bundle = I18NBundle.createBundle(handle, locale);
			
			//router
			if (locale.getDisplayName().equals("router")) {
				bundle.debug("router");
			}
		}
	}
	
	public static void loadLogger() {
		if (loadedLogger) return;
		
		String[] tags = {"[green][D][]", "[royal][I][]", "[yellow][W][]", "[scarlet][E][]", ""};
		String[] stags = {"&lc&fb[D]", "&lb&fb[I]", "&ly&fb[W]", "&lr&fb[E]", ""};
		
		Seq<String> logBuffer = new Seq<>();
		if (SharedBootstrap.debug) Log.level = Log.LogLevel.debug;
		Log.logger = (level, text) -> {
			String result = text;
			String rawText = Log.format(stags[level.ordinal()] + "&fr " + text);
			System.out.println(rawText);
			
			result = tags[level.ordinal()] + " " + result;
			if (!text.startsWith("Ozone-Event-")) Sentry.addBreadcrumb(text, level.name());
			if (!headless && (ui == null || ui.scriptfrag == null)) {
				logBuffer.add(result);
			}else if (!headless) {
				if (!OS.isWindows) {
					for (String code : ColorCodes.values) {
						result = result.replace(code, "");
					}
				}
				
				ui.scriptfrag.addMessage(Log.removeColors(result));
			}
		};
		
		Events.on(ClientLoadEvent.class, e -> logBuffer.each(ui.scriptfrag::addMessage));
		
		loadedLogger = true;
	}
	
	@Override
	public void loadSync() {
		assets.load(menuRenderer);
	}
	
	@Override
	public void loadAsync() {
		loadSettings();
		init();
		Updater.async();
	}
}
