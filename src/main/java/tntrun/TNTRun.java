/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import tntrun.arena.Arena;
import tntrun.arena.handlers.BungeeHandler;
import tntrun.arena.handlers.SoundHandler;
import tntrun.arena.handlers.VaultHandler;
import tntrun.utils.Bars;
import tntrun.utils.Shop;
import tntrun.utils.Sounds;
import tntrun.utils.Stats;
import tntrun.utils.TitleMsg;
import tntrun.utils.Utils;
import tntrun.commands.AutoTabCompleter;
import tntrun.commands.ConsoleCommands;
import tntrun.commands.GameCommands;
import tntrun.commands.setup.SetupCommandsHandler;
import tntrun.commands.setup.SetupTabCompleter;
import tntrun.datahandler.ArenasManager;
import tntrun.datahandler.PlayerDataStore;
import tntrun.datahandler.ScoreboardManager;
import tntrun.eventhandler.HeadsPlusHandler;
import tntrun.eventhandler.MenuHandler;
import tntrun.eventhandler.PlayerLeaveArenaChecker;
import tntrun.eventhandler.PlayerStatusHandler;
import tntrun.eventhandler.RestrictionHandler;
import tntrun.eventhandler.ShopHandler;
import tntrun.kits.Kits;
import tntrun.lobby.GlobalLobby;
import tntrun.menu.Menus;
import tntrun.messages.Language;
import tntrun.messages.Messages;
import tntrun.parties.Parties;
import tntrun.signs.SignHandler;
import tntrun.signs.editor.SignEditor;

public class TNTRun extends JavaPlugin {

	private Logger log;
	private boolean mcMMO = false;
	private boolean headsplus = false;
	private boolean usestats = false;
	private boolean needupdate = false;
	private boolean placeholderapi = false;
	private boolean adpParties = false;
	private boolean file = false;
	private VaultHandler vaultHandler;
	private BungeeHandler bungeeHandler;
	private GlobalLobby globallobby;
	private Menus menus;
	private PlayerDataStore pdata;
	private SignEditor signEditor;
	private Kits kitmanager;
	private Sounds sound;
	private Language language;
	private Parties parties;
	private Stats stats;
	private MySQL mysql;
	private Shop shop;

	public ArenasManager amanager;
	private ScoreboardManager scoreboardManager;

	private static TNTRun instance;
	private static final int BSTATS_PLUGIN_ID = 2192;

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();

		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();

		signEditor = new SignEditor(this);
		globallobby = new GlobalLobby(this);
		kitmanager = new Kits();
		language = new Language(this);
		Messages.loadMessages(this);
		Bars.loadBars(this);
		TitleMsg.loadTitles(this);
		pdata = new PlayerDataStore(this);
		amanager = new ArenasManager();
		menus = new Menus(this);
		parties = new Parties(this);

		setupPlugin();
		setupScoreboards();

		loadArenas();
		checkUpdate();
		sound = new SoundHandler(this);

		if (isBungeecord()) {
			log.info("Bungeecord is enabled");
			bungeeHandler = new BungeeHandler(this);
		}

		if (getConfig().getBoolean("special.Metrics", true)) {
			log.info("Attempting to start metrics (bStats)...");
			new Metrics(this, BSTATS_PLUGIN_ID);
		}

		setStorage();
		if (usestats) {
			stats = new Stats(this);
		}
	}

	public static TNTRun getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
		if (!file) {
			mysql.close();
		}
		saveArenas();
		globallobby.saveToConfig();
		globallobby = null;

		kitmanager.saveToConfig();
		kitmanager = null;

		signEditor.saveConfiguration();
		signEditor = null;

		amanager = null;
		pdata = null;
		stats = null;
		log = null;
	}

	private void saveArenas() {
		for (Arena arena : amanager.getArenas()) {
			arena.getStructureManager().getGameZone().regenNow();
			arena.getStatusManager().disableArena();
			arena.getStructureManager().saveToConfig();
			Bars.removeAll(arena.getArenaName());
		}
	}

	public void logSevere(String message) {
		log.severe(message);
	}

	public boolean isHeadsPlus() {
		return headsplus;
	}

	public boolean isMCMMO() {
		return mcMMO;
	}

	public boolean isPlaceholderAPI() {
		return placeholderapi;
	}

	public boolean isParties() {
		return getConfig().getBoolean("parties.enabled");
	}

	public boolean isAdpParties() {
		return adpParties && getConfig().getBoolean("parties.usePartiesPlugin") && !isParties();
	}

	public boolean useStats() {
		return usestats;
	}

	public void setUseStats(boolean usestats) {
		this.usestats = usestats;
	}

	public boolean needUpdate() {
		return needupdate;
	}

	public void setNeedUpdate(boolean needupdate) {
		this.needupdate = needupdate;
	}

	public boolean isFile() {
		return file;
	}

	public boolean useUuid() {
		return Bukkit.getOnlineMode() || (isBungeecord() && getConfig().getBoolean("bungeecord.useUUID"));
	}

	public boolean isBungeecord() {
		return getConfig().getBoolean("bungeecord.enabled");
	}

	private void checkUpdate() {
		if (getConfig().getBoolean("special.CheckForNewVersion", true)) {
			Utils.checkUpdate(getDescription().getVersion());
		}
	}

	private void connectToMySQL() {
		log.info("Connecting to MySQL database...");
		mysql = new MySQL(getConfig().getString("MySQL.host"),
				getConfig().getInt("MySQL.port"),
				getConfig().getString("MySQL.name"),
				getConfig().getString("MySQL.user"),
				getConfig().getString("MySQL.pass"),
				getConfig().getString("MySQL.useSSL"),
				getConfig().getString("MySQL.flags"),
				getConfig().getBoolean("MySQL.legacyDriver"),
				this);

		new BukkitRunnable() {
			@Override
			public void run() {

				mysql.query("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("MySQL.table") + "` ( `username` varchar(50) NOT NULL, "
						+ "`looses` int(16) NOT NULL, `wins` int(16) NOT NULL, "
						+ "`played` int(16) NOT NULL, "
						+ "UNIQUE KEY `username` (`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");

				log.info("Connected to MySQL database!");
			}
		}.runTaskAsynchronously(this);
	}

	private void setupPlugin() {
		getCommand("tntrun").setExecutor(new GameCommands(this));
		getCommand("tntrunsetup").setExecutor(new SetupCommandsHandler(this));
		getCommand("tntrunconsole").setExecutor(new ConsoleCommands(this));
		getCommand("tntrun").setTabCompleter(new AutoTabCompleter());
		getCommand("tntrunsetup").setTabCompleter(new SetupTabCompleter());

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerStatusHandler(this), this);
		pm.registerEvents(new RestrictionHandler(this), this);
		pm.registerEvents(new PlayerLeaveArenaChecker(this), this);
		pm.registerEvents(new SignHandler(this), this);
		pm.registerEvents(new MenuHandler(this), this);
		pm.registerEvents(new ShopHandler(this), this);

		setupShop();

		Plugin HeadsPlus = pm.getPlugin("HeadsPlus");
		if (HeadsPlus != null && HeadsPlus.isEnabled()) {
			pm.registerEvents(new HeadsPlusHandler(this), this);
			headsplus = true;
			log.info("Successfully linked with HeadsPlus, version " + HeadsPlus.getDescription().getVersion());
		}
		Plugin MCMMO = pm.getPlugin("mcMMO");
		if (MCMMO != null && MCMMO.isEnabled()) {
			mcMMO = true;
			log.info("Successfully linked with mcMMO, version " + MCMMO.getDescription().getVersion());
		}
		Plugin PlaceholderAPI = pm.getPlugin("PlaceholderAPI");
		if (PlaceholderAPI != null && PlaceholderAPI.isEnabled()) {
			placeholderapi = true;
			log.info("Successfully linked with PlaceholderAPI, version " + PlaceholderAPI.getDescription().getVersion());
			new TNTRunPlaceholders(this).register();
		}
		Plugin Parties = getServer().getPluginManager().getPlugin("Parties");
		if (Parties != null && Parties.isEnabled()) {
			adpParties = true;
			log.info("Successfully linked with Parties, version " + Parties.getDescription().getVersion());
		}

		vaultHandler = new VaultHandler(this);
	}

	public void setupShop() {
		if (isGlobalShop()) {
			shop = new Shop(this);
		}
	}

	public boolean isGlobalShop() {
		return getConfig().getBoolean("shop.enabled");
	}

	public VaultHandler getVaultHandler() {
		return vaultHandler;
	}

	public BungeeHandler getBungeeHandler() {
		return bungeeHandler;
	}

	public Parties getParties() {
		return parties;
	}

	private void loadArenas() {
		final File arenasfolder = new File(getDataFolder() + File.separator + "arenas");
		arenasfolder.mkdirs();
		new BukkitRunnable() {

			@Override
			public void run() {
				globallobby.loadFromConfig();
				kitmanager.loadFromConfig();

				List<String> arenaList = Arrays.asList(arenasfolder.list());
				for (String file : arenaList) {
					Arena arena = new Arena(file.substring(0, file.length() - 4), instance);
					arena.getStructureManager().loadFromConfig();
					amanager.registerArena(arena);
					Bars.createBar(arena.getArenaName());
					if (arena.getStructureManager().isEnableOnRestart()) {
						arena.getStatusManager().enableArena();
					}
				}
				if (isBungeecord()) {
					amanager.setBungeeArena();
				}

				signEditor.loadConfiguration();
			}
		}.runTaskLater(this, 20L);
	}

	private void setStorage() {
		String storage = this.getConfig().getString("database");
		switch (storage) {
			case "file" -> {
				usestats = true;
				file = true;
			}
			case "sql", "mysql" -> {
				this.connectToMySQL();
				usestats = true;
				file = false;
			}
			default -> {
				log.info("The database " + storage + " is not supported, supported database types: sql, mysql, file");
				usestats = false;
				file = false;
				log.info("Disabling stats...");
			}
		}
	}

	public Menus getMenus() {
		return menus;
	}

	public PlayerDataStore getPData() {
		return pdata;
	}

	public Stats getStats() {
		return stats;
	}

	public Kits getKitManager() {
		return kitmanager;
	}

	public GlobalLobby getGlobalLobby() {
		return globallobby;
	}

	public Sounds getSound() {
		return sound;
	}

	public Language getLanguage() {
		return language;
	}

	public SignEditor getSignEditor() {
		return signEditor;
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public MySQL getMysql() {
		return mysql;
	}

	public Shop getShop() {
		return shop;
	}

	public void setupScoreboards() {
		if (getConfig().getBoolean("special.UseScoreboard")) {
			scoreboardManager = new ScoreboardManager(this);
		}
	}
}
