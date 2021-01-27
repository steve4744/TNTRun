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
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import tntrun.arena.Arena;
import tntrun.arena.handlers.SoundHandler;
import tntrun.arena.handlers.VaultHandler;
import tntrun.utils.Bars;
import tntrun.utils.JoinMenu;
import tntrun.utils.Shop;
import tntrun.utils.Stats;
import tntrun.utils.TitleMsg;
import tntrun.commands.ConsoleCommands;
import tntrun.commands.GameCommands;
import tntrun.commands.setup.SetupCommandsHandler;
import tntrun.datahandler.ArenasManager;
import tntrun.datahandler.PlayerDataStore;
import tntrun.eventhandler.MenuHandler;
import tntrun.eventhandler.PlayerLeaveArenaChecker;
import tntrun.eventhandler.PlayerStatusHandler;
import tntrun.eventhandler.RestrictionHandler;
import tntrun.lobby.GlobalLobby;
import tntrun.messages.Messages;
import tntrun.signs.SignHandler;
import tntrun.signs.editor.SignEditor;

public class TNTRun extends JavaPlugin {

	private Logger log;

	public PlayerDataStore pdata;
	public ArenasManager amanager;
	public GlobalLobby globallobby;
	public SignEditor signEditor;
	private boolean file = false;
	private boolean usestats = false;
	private boolean placeholderapi = false;
	public boolean needUpdate = false;
	public String[] version = {"Nothing", "Nothing"};
	public Stats stats;
	private JoinMenu joinMenu;
	private SoundHandler soundHandler;
	private VaultHandler vaultHandler;

	public static TNTRun instance;

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();
		signEditor = new SignEditor(this);
		globallobby = new GlobalLobby(this);
		Messages.loadMessages(this);
		Bars.loadBars(this);
		TitleMsg.loadTitles(this);
		pdata = new PlayerDataStore();
		amanager = new ArenasManager();
		joinMenu = new JoinMenu(this);
		soundHandler = new SoundHandler(this);
		getCommand("tntrunsetup").setExecutor(new SetupCommandsHandler(this));
		getCommand("tntrun").setExecutor(new GameCommands(this));
		getCommand("tntrunconsole").setExecutor(new ConsoleCommands(this));
		getServer().getPluginManager().registerEvents(new PlayerStatusHandler(this), this);
		getServer().getPluginManager().registerEvents(new RestrictionHandler(this), this);
		getServer().getPluginManager().registerEvents(new PlayerLeaveArenaChecker(this), this);
		getServer().getPluginManager().registerEvents(new SignHandler(this), this);
		getServer().getPluginManager().registerEvents(new MenuHandler(this), this);
		getServer().getPluginManager().registerEvents(new Shop(this), this);

		Plugin PlaceholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI");
		if (PlaceholderAPI != null && PlaceholderAPI.isEnabled()) {
			placeholderapi = true;
			log.info("Successfully linked with PlaceholderAPI, version " + PlaceholderAPI.getDescription().getVersion());
			new TNTRunPlaceholders(this).register();
		}

		vaultHandler = new VaultHandler(this);

		// config
	    saveDefaultConfig();
	    getConfig().options().copyDefaults(true);
	    saveConfig();
		// load arenas
		final File arenasfolder = new File(getDataFolder() + File.separator + "arenas");
		arenasfolder.mkdirs();
		getServer().getScheduler().scheduleSyncDelayedTask(
			this,
			new Runnable() {
				@Override
				public void run() {
					// load global lobby
					globallobby.loadFromConfig();
					// load arenas
					for (String file : arenasfolder.list()) {
						Arena arena = new Arena(file.substring(0, file.length() - 4), instance);
						arena.getStructureManager().loadFromConfig();
						arena.getStatusManager().enableArena();
						amanager.registerArena(arena);
					}
					// load signs
					signEditor.loadConfiguration();
				}
			},
			20
		);

		checkUpdate();

		log.info("Attempting to start metrics (bStats)...");
		new Metrics(this, 2192);

		setStorage();
		if (usestats) {
			stats = new Stats(this);
		}
	}

	public static TNTRun getInstance(){
		return instance;
	}

	@Override
	public void onDisable() {
		//Close mysql connection
		if(!file){
			mysql.close();
		}
		// save arenas
		for (Arena arena : amanager.getArenas()) {
			arena.getStructureManager().getGameZone().regenNow();
			arena.getStatusManager().disableArena();
			arena.getStructureManager().saveToConfig();
		}
		// save lobby
		globallobby.saveToConfig();
		globallobby = null;
		// save signs
		signEditor.saveConfiguration();
		signEditor = null;
		// unload other things
		pdata = null;
		amanager = null;
		log = null;
	}

	public void logSevere(String message) {
		log.severe(message);
	}

	public boolean isPlaceholderAPI() {
		return placeholderapi;
	}

	public VaultHandler getVaultHandler() {
		return vaultHandler;
	}

	public boolean useStats() {
		return usestats;
	}

	public void setUseStats(boolean usestats) {
		this.usestats = usestats;
	}

	public boolean isFile() {
		return file;
	}

	public JoinMenu getJoinMenu() {
		return joinMenu;
	}

	public SoundHandler getSoundHandler() {
		return soundHandler;
	}

	private void checkUpdate(){
		if(!getConfig().getBoolean("special.CheckForNewVersion", true)){
			return;
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), new Runnable(){
			public void run(){
				log.info(" ");
				log.info("Checking plugin version...");
				new VersionChecker();
				version = VersionChecker.get().getVersion().split(";");
				if(version[0].equalsIgnoreCase("error")){
					throw new NullPointerException("An error was occured while checking version! Please report this here: https://www.spigotmc.org/threads/tntrun_reloaded.303586/");
				}else{
					if(version[0].equalsIgnoreCase(getDescription().getVersion())){
						log.info("You are running the most recent version");
						needUpdate = false;
					}else{
						log.info("Your version: " + getDescription().getVersion());
						log.info("New version : " + version[0] + " for Minecraft 1.13+");
						log.info("New version available! Download now: https://www.spigotmc.org/resources/tntrun_reloaded.53359/");
						needUpdate = true;
						for(Player p : Bukkit.getOnlinePlayers()){
							if(p.hasPermission("tntrun.version.check")){
								p.sendMessage(" ");
								p.sendMessage("§7[§6TNTRun§7] §6New update available!");
								p.sendMessage("§7[§6TNTRun§7] §7Your version: §6" + getDescription().getVersion());
								p.sendMessage("§7[§6TNTRun§7] §7New version : §6" + version[0] + " for Minecraft 1.13+");
								p.sendMessage("§7[§6TNTRun§7] §7New version available! Download now: §6https://www.spigotmc.org/resources/tntrun_reloaded.53359/");
							}
						}
					}
				}
				log.info(" ");
			}
		}, 30L);
	}

	public MySQL mysql;

	private void connectToMySQL(){
		log.info("Connecting to MySQL database...");
		String host = this.getConfig().getString("MySQL.host");
        Integer port = this.getConfig().getInt("MySQL.port");
        String name = this.getConfig().getString("MySQL.name");
        String user = this.getConfig().getString("MySQL.user");
        String pass = this.getConfig().getString("MySQL.pass");
        mysql = new MySQL(host, port, name, user, pass, this);

        mysql.query("CREATE TABLE IF NOT EXISTS `stats` ( `username` varchar(50) NOT NULL, "
                + "`looses` int(16) NOT NULL, `wins` int(16) NOT NULL, "
                + "`played` int(16) NOT NULL, "
                + "UNIQUE KEY `username` (`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        
        log.info("Connected to MySQL database!");
	}

	private void setStorage() {
		if (this.getConfig().getString("database").equals("file")) {
			usestats = true;
			file = true;
		} else if (this.getConfig().getString("database").equals("sql")) {
			this.connectToMySQL();
			usestats = true;
			file = false;
		} else {
			log.info("This database is not supported, supported database types: sql, file");
			usestats = false;
			file = false;
			log.info("Disabling stats...");
		}
	}
}
