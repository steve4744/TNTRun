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

package tntrun.messages;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tntrun.FormattingCodesParser;
import tntrun.TNTRun;

public class Messages {

	public static String menutitle = "&1TNTRun_reloaded Arenas";
	public static String menuarenaname = "&a{ARENA}";
	public static String menutext = "&5Players:&6";
	public static String nopermission = "&7[&6TNTRun&7] &cYou don't have permission to do this";

	public static String teleporttolobby = "&7[&6TNTRun&7] Teleported to lobby";

	public static String availablearenas = "&7[&6TNTRun&7] Available arenas:&r ";
	public static String noarenas = "&7[&6TNTRun&7] &cThere are no arenas available to join";
	public static String arenawolrdna = "&7[&6TNTRun&7] Arena world is not loaded";
	public static String arenadisabled = "&7[&6TNTRun&7] Arena is disabled";
	public static String arenarunning = "&7[&6TNTRun&7] Arena already running";
	public static String arenaregenerating = "&7[&6TNTRun&7] Arena is regenerating";
	public static String arenavehicle = "&7[&6TNTRun&7] You can't join the game while sitting inside a vehicle";
	public static String arenajoined = "&c You have already joined an arena";
	public static String arenadisabling = "&7[&6TNTRun&7] &6Arena is disabling";
	public static String arenastarting = "&7[&6TNTRun&7] &6Arena already starting";
	public static String arenawaiting = "&6 Arena is waiting";
	public static String signcreate = "&7[&6TNTRun&7] Sign successfully created";
	public static String signremove = "&7[&6TNTRun&7] Sign successfully removed";
	
	public static String playerscountinarena = "&7[&6TNTRun&7] &6Current number of players in arena: {COUNT} players";
	public static String limitreached = "&7[&6TNTRun&7] Arena is full.";
	public static String playerjoinedtoplayer = "&7[&6TNTRun&7] You joined the arena";
	public static String playerjoinedtoothers = "&7[&6TNTRun&7] Player &6{PLAYER} &7joined the arena";
	public static String playerlefttoplayer = "&7[&6TNTRun&7] You left the arena";
	public static String playerlefttoothers = "&7[&6TNTRun&7] Player &6{PLAYER} &7left the game";
	public static String playervotedforstart = "&7[&6TNTRun&7] You voted for game start";
	public static String playeralreadyvotedforstart = "&7[&6TNTRun&7] You already voted";
	public static String arenastarted = "&7[&6TNTRun&7] Arena started. Time limit is {TIMELIMIT} seconds";
	public static String arenacountdown = "&7[&6TNTRun&7] Arena starts in {COUNTDOWN} seconds";
	public static String arenatimeout = "&7[&6TNTRun&7] Time is up. Ending game";
	public static String playerwontoplayer = "&7[&6TNTRun&7] You won the game!";
	public static String playerlosttoplayer = "&7[&6TNTRun&7] You lost the game";
	public static String playerlosttoothers = "&7[&6TNTRun&7] Player &6{PLAYER} &7lost the game";
	public static String playerwonbroadcast = "&7[&6TNTRun&7] &6{PLAYER}&7 won the game on arena &6{ARENA}";
	public static String playerrewardmessage = "&7[&6TNTRun&7] You have been rewarded: &6{REWARD}";
	public static String playerboughtitem = "&7[&6TNTRun&7] You have bought item &6{ITEM} &7for &6{MONEY} &7coins";
	public static String playerboughtwait = "&7[&6TNTRun&7] You will get your items when the game starts";
	public static String notenoughtmoney = "&7[&6TNTRun&7] &cYou need {MONEY} coins to buy this item";
	public static String alreadyboughtitem = "&7[&6TNTRun&7] &cYou already bought item";
	public static String getdoublejumpsaction = "&7&lYour double jumps: &6&l{DB}";
	public static String playernotinarena = "&7[&6TNTRun&7] &cYou are not in an arena";
	
	public static String gamesplayed = "&7Games played: &6";
	public static String gameswon = "&7Wins: &6";
	public static String gameslost = "&7Losses: &6";
	public static String statsdisabled = "&7[&6TNTRun&7] &cStats are currently disabled";
	public static String leaderboard = "{POSITION}) &b{PLAYER} &f: Wins &3{WINS}";
	public static String leaderhead = "&7======[&6TNTRun Leaderboard&7]======";
	
	public static String helplobby = "Teleport to lobby";
	public static String helplist = "List all arenas &for &cList arena details";
	public static String helpjoin = "Join the arena";
	//public static String helpjoin = "Open join menu OR Join the arena";
	public static String helpautojoin = "Automatically join the first available arena";
	public static String helpleave = "Leave the current arena";
	public static String helpvote = "Vote to force-start current arena";
	public static String helpcmds = "View all TNTRun commands";
	public static String helpinfo = "View plugin info";
	public static String helpstats = "View player stats";
	public static String helplb = "Display the TNTRun leaderboard";
	
	public static String setuplobby = "Set lobby at your current location";
	public static String setupcreate = "Create a new arena";
	public static String setupbounds = "Set bounds for arena";
	public static String setuploselevel = "Set loselevel bounds for arena";
	public static String setupspawn = "Set arena spawn at your current location";
	public static String setupspectate = "Set spectator spawn at your current location";
	public static String setupfinish = "Finish arena and save";
	public static String setupdelspectate = "Delete spectator spawn for arena";
	public static String setupdelay = "Set a delay for removing blocks when player steps on it";
	public static String setupmax = "Set maximum players for arena (Default: 15)";
	public static String setupmin = "Set minimum players for arena (Default: 2)";
	public static String setupvote = "Set a vote percentage to force-start arena (Default: 0.75)";
	public static String setuptimelimit = "Set a time limit for arena (Default: 300 seconds)";
	public static String setupcountdown = "Set a countdown for arena (Default: 15 seconds)";
	public static String setupmoney = "Set a money reward for winning player";
	public static String setupteleport = "Set teleport when you lose or win in arena";
	public static String setupdamage = "Set a pvp for arena";
	public static String setupreloadbars = "Reload Bar messages";
	public static String setupreloadtitles = "Reload Title messages";
	public static String setupreloadmsg = "Reload arena messages";
	public static String setupreloadconfig = "Reload config file";
	public static String setupenable = "Enable arena";
	public static String setupdisable = "Disable arena";
	public static String setupdelete = "Delete arena";
	public static String setupreward = "Set the rewards for the arena";
	
	
	public static void sendMessage(CommandSender sender, String message) {
		if (!message.equals("")) {
			sender.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}

	public static void broadcastMessage(String message) {
		if (!message.equals("")) {
			Bukkit.broadcastMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}

	public static void loadMessages(TNTRun plugin) {
		File messageconfig = new File(plugin.getDataFolder(), "messages.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(messageconfig);
		menutitle = config.getString("menutitle", menutitle);
		menuarenaname = config.getString("menuarenaname", menuarenaname);
		menutext = config.getString("menutext", menutext);
		nopermission = config.getString("nopermission", nopermission);
		teleporttolobby = config.getString("teleporttolobby", teleporttolobby);
		availablearenas = config.getString("availablearenas", availablearenas);
		noarenas = config.getString("noarenas", noarenas);
		arenawolrdna = config.getString("arenawolrdna", arenawolrdna);
		arenadisabled = config.getString("arenadisabled", arenadisabled);
		arenarunning = config.getString("arenarunning", arenarunning);
		arenaregenerating = config.getString("arenaregenerating", arenaregenerating);
		arenavehicle = config.getString("arenavehicle", arenavehicle);
		arenajoined = config.getString("arenajoined", arenajoined);
		arenadisabling = config.getString("arenadisabling", arenadisabling);
		arenastarting = config.getString("arenastarting", arenastarting);
		arenawaiting = config.getString("arenawaiting", arenawaiting);
		signcreate = config.getString("signcreate", signcreate);
		signremove = config.getString("signremove", signremove);
		playerscountinarena = config.getString("playerscountinarena", playerscountinarena);
		limitreached = config.getString("limitreached", limitreached);
		playerjoinedtoplayer = config.getString("playerjoinedtoplayer", playerjoinedtoplayer);
		playerjoinedtoothers = config.getString("playerjoinedtoothers", playerjoinedtoothers);
		playerlefttoplayer = config.getString("playerlefttoplayer", playerlefttoplayer);
		playerlefttoothers = config.getString("playerlefttoothers", playerlefttoothers);
		playervotedforstart = config.getString("playervotedforstart", playervotedforstart);
		playeralreadyvotedforstart = config.getString("playeralreadyvotedforstart", playeralreadyvotedforstart);
		arenastarted = config.getString("arenastarted", arenastarted);
		arenacountdown = config.getString("arenacountdown", arenacountdown);
		arenatimeout = config.getString("arenatimeout", arenatimeout);
		playerwontoplayer = config.getString("playerwontoplayer", playerwontoplayer);
		playerlosttoplayer = config.getString("playerlosttoplayer", playerlosttoplayer);
		playerlosttoothers = config.getString("playerlosttoothers", playerlosttoothers);
		playerwonbroadcast = config.getString("playerwonbroadcast", playerwonbroadcast);
		playerrewardmessage = config.getString("playerrewardmessage", playerrewardmessage);
		playerboughtitem = config.getString("playerboughtitem", playerboughtitem);
		playerboughtwait = config.getString("playerboughtwait", playerboughtwait);
		notenoughtmoney = config.getString("notenoughtmoney", notenoughtmoney);
		alreadyboughtitem = config.getString("alreadyboughtitem", alreadyboughtitem);
		getdoublejumpsaction = config.getString("getdoublejumpsaction", getdoublejumpsaction);
		playernotinarena = config.getString("playernotinarena", playernotinarena);
		gamesplayed = config.getString("gamesplayed", gamesplayed);
		gameswon = config.getString("gameswon", gameswon);
		gameslost = config.getString("gameslost", gameslost);
		statsdisabled = config.getString("statsdisabled", statsdisabled);
		leaderboard = config.getString("leaderboard", leaderboard);
		leaderhead = config.getString("leaderhead", leaderhead);
		helplobby = config.getString("helplobby", helplobby);
		helplist = config.getString("helplist", helplist);
		helpjoin = config.getString("helpjoin", helpjoin);
		helpleave = config.getString("helpleave", helpleave);
		helpvote = config.getString("helpvote", helpvote);
		helpcmds = config.getString("helpcmds", helpcmds);
		helpinfo = config.getString("helpinfo", helpinfo);
		helpstats = config.getString("helpstats", helpstats);
		helplb = config.getString("helplb", helplb);
		setuplobby = config.getString("setuplobby", setuplobby);
		setupcreate = config.getString("setupcreate", setupcreate);
		setupbounds = config.getString("setupbounds", setupbounds);
		setuploselevel = config.getString("setuploselevel", setuploselevel);
		setupspawn = config.getString("setupspawn", setupspawn);
		setupspectate = config.getString("setupspectate", setupspectate);
		helpautojoin = config.getString("helpautojoin", helpautojoin);
		setupfinish = config.getString("setupfinish", setupfinish);
		setupdelspectate = config.getString("setupdelspectate", setupdelspectate);
		setupdelay = config.getString("setupdelay", setupdelay);
		setupmax = config.getString("setupmax", setupmax);
		setupmin = config.getString("setupmin", setupmin);
		setupvote = config.getString("setupvote", setupvote);
		setuptimelimit = config.getString("setuptimelimit", setuptimelimit);
		setupcountdown = config.getString("setupcountdown", setupcountdown);
		setupmoney = config.getString("setupmoney", setupmoney);
		setupteleport = config.getString("setupteleport", setupteleport);
		setupdamage = config.getString("setupdamage", setupdamage);
		setupreloadbars = config.getString("setupreloadbars", setupreloadbars);
		setupreloadtitles = config.getString("setupreloadtitles", setupreloadtitles);
		setupreloadmsg = config.getString("setupreloadmsg", setupreloadmsg);
		setupreloadconfig = config.getString("setupreloadconfig", setupreloadconfig);
		setupenable = config.getString("setupenable", setupenable);
		setupdisable = config.getString("setupdisable", setupdisable);
		setupdelete = config.getString("setupdelete", setupdelete);
		setupreward = config.getString("setupreward", setupreward);
		saveMessages(messageconfig);
	}

	private static void saveMessages(File messageconfig) {
		FileConfiguration config = new YamlConfiguration();
		
		config.set("menutitle",  menutitle);
		config.set("menuarenaname", menuarenaname);
		config.set("menutext",  menutext);
		config.set("nopermission", nopermission);
		config.set("teleporttolobby", teleporttolobby);
		config.set("availablearenas", availablearenas);
		config.set("noarenas", noarenas);
		config.set("arenawolrdna", arenawolrdna);
		config.set("arenadisabled", arenadisabled);
		config.set("arenarunning", arenarunning);
		config.set("arenaregenerating", arenaregenerating);
		config.set("arenavehicle", arenavehicle);
		config.set("arenajoined", arenajoined);
		config.set("arenadisabling", arenadisabling);
		config.set("arenastarting", arenastarting);
		config.set("arenawaiting", arenawaiting);
		config.set("signcreate", signcreate);
		config.set("signremove", signremove);
		config.set("playerscountinarena", playerscountinarena);
		config.set("limitreached", limitreached);
		config.set("playerjoinedtoplayer", playerjoinedtoplayer);
		config.set("playerjoinedtoothers", playerjoinedtoothers);
		config.set("playerlefttoplayer", playerlefttoplayer);
		config.set("playerlefttoothers", playerlefttoothers);
		config.set("playervotedforstart", playervotedforstart);
		config.set("playeralreadyvotedforstart", playeralreadyvotedforstart);
		config.set("arenastarted", arenastarted);
		config.set("arenacountdown", arenacountdown);
		config.set("arenatimeout", arenatimeout);
		config.set("playerwontoplayer", playerwontoplayer);
		config.set("playerlosttoplayer", playerlosttoplayer);
		config.set("playerlosttoothers", playerlosttoothers);
		config.set("playerwonbroadcast", playerwonbroadcast);
		config.set("playerrewardmessage", playerrewardmessage);
		config.set("playerboughtitem", playerboughtitem);
		config.set("playerboughtwait", playerboughtwait);
		config.set("notenoughtmoney", notenoughtmoney);
		config.set("alreadyboughtitem", alreadyboughtitem);
		config.set("getdoublejumpsaction", getdoublejumpsaction);
		config.set("playernotinarena", playernotinarena);
		config.set("gamesplayed", gamesplayed);
		config.set("gameswon", gameswon);
		config.set("gameslost", gameslost);
		config.set("statsdisabled", statsdisabled);
		config.set("leaderhead", leaderhead);
		config.set("leaderboard", leaderboard);
		config.set("helplobby", helplobby);
		config.set("helplist", helplist);
		config.set("helpjoin", helpjoin);
		config.set("helpleave", helpleave);
		config.set("helpvote", helpvote);
		config.set("helpcmds", helpcmds);
		config.set("helpinfo", helpinfo);
		config.set("helpstats", helpstats);
		config.set("helplb", helplb);
		config.set("setuplobby", setuplobby);
		config.set("setupcreate", setupcreate);
		config.set("setupbounds", setupbounds);
		config.set("setuploselevel", setuploselevel);
		config.set("setupspawn", setupspawn);
		config.set("setupspectate", setupspectate);
		config.set("helpautojoin", helpautojoin);
		config.set("setupfinish", setupfinish);
		config.set("setupdelspectate", setupdelspectate);
		config.set("setupdelay", setupdelay);
		config.set("setupmax", setupmax);
		config.set("setupmin", setupmin);
		config.set("setupvote", setupvote);
		config.set("setuptimelimit", setuptimelimit);
		config.set("setupcountdown", setupcountdown);
		config.set("setupmoney", setupmoney);
		config.set("setupteleport", setupteleport);
		config.set("setupdamage", setupdamage);
		config.set("setupreloadbars", setupreloadbars);
		config.set("setupreloadtitles", setupreloadtitles);
		config.set("setupreloadmsg", setupreloadmsg);
		config.set("setupreloadconfig", setupreloadconfig);
		config.set("setupenable", setupenable);
		config.set("setupdisable", setupdisable);
		config.set("setupdelete", setupdelete);
		config.set("setupreward", setupreward);
		try {
			config.save(messageconfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
