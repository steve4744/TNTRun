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

import tntrun.TNTRun;
import tntrun.utils.FormattingCodesParser;

public class Messages {

	public static String trprefix = "&7[&6TNTRun_reloaded&7]";
	public static String menutitle = "&1TNTRun_reloaded Arenas";
	public static String menuarenaname = "&a{ARENA}";
	public static String menutext = "&5Players: &6{PS}/{MPS}";
	public static String menufee = "&5Join Fee: &6{FEE}";
	public static String menupvp = "&5PVP: &6Enabled";
	public static String nopermission = "&c You don't have permission to do this";
	public static String nolobby = "&c Global lobby isn't set, run &6/trsetup setlobby";
	public static String lobbyunloaded = "&c Lobby world is unloaded or doesn't exist";
	public static String teleporttolobby = "&7 Teleported to lobby";

	public static String availablearenas = "&7 Available arenas: {COUNT}";
	public static String arenanotexist = "&c Arena &6{ARENA}&c doesn't exist";
	public static String noarenas = "&c There are no arenas available to join";
	public static String arenanotdisabled = "&c Please disable arena: &6/trsetup disable {ARENA}";
	public static String arenanospectatorspawn = "&c No spectator spawn set for arena &6{ARENA}";
	public static String availablekits = "&7 Available kits: {COUNT}";
	public static String arenawolrdna = "&7 Arena world is not loaded";
	public static String arenanobounds = "&7 Arena bounds not set";
	public static String arenadisabled = "&7 Arena is disabled";
	public static String arenarunning = "&7 Arena already running";
	public static String arenaregenerating = "&7 Arena is regenerating";
	public static String arenavehicle = "&c You can't join the game while sitting inside a vehicle";
	public static String arenajoined = "&c You have already joined an arena";
	public static String arenadisabling = "&6 Arena is disabling";
	public static String arenastarting = "&6 Arena already starting";
	public static String signcreate = "&7 Sign successfully created";
	public static String signremove = "&7 Sign successfully removed";
	public static String arenafee = "&7 You have been charged &6{FEE} &7to enter the arena";
	public static String arenanofee = "&c You require a fee of &6{FEE} &cto enter the arena";
	public static String arenawaiting = "&6 Arena is waiting";

	public static String playerscountinarena = "&6 Current number of players in arena: {COUNT} players";
	public static String playersrequiredtostart = "&c There must be at least 2 players in the arena to force-start";
	public static String limitreached = "&7 Arena is full.";
	public static String playerjoinedtoothers = "&c {RANK}&6{PLAYER} &7joined the arena (&6{PS}&7/&6{MPS}&7)";
	public static String playerjoininvite = "&c{RANK}&6{PLAYER} &7has joined TNTRun arena &6{ARENA}&7, click to play";
	public static String playerborderinvite = "&b---------------------------------------";
	public static String playerclickinvite = "Click to join {ARENA}";
	public static String playerjoinedasspectator = "&7 You joined arena &6{ARENA} &7as a spectator";
	public static String playerlefttoplayer = "&7 You left the arena";
	public static String playerlefttoothers = "&c {RANK}&6{PLAYER} &7left the game (&6{PS}&7/&6{MPS}&7)";
	public static String playervotedforstart = "&f You voted to force-start the game";
	public static String playeralreadyvotedforstart = "&c You have already voted";
	public static String playercannotvote = "&c You cannot vote to force-start the arena";
	public static String arenastarted = "&7 Arena started. Time limit is {TIMELIMIT} seconds";
	public static String arenanolimit = "&7 Arena started. No time limit";
	public static String arenacountdown = "&7 Arena starts in {COUNTDOWN} seconds";
	public static String arenatimeout = "&7 Time is up. Ending game";
	public static String playerwontoplayer = "&7 You won the game!";
	public static String playerlosttoplayer = "&7 You lost the game";
	public static String playerlosttoothers = "&c {RANK}&6{PLAYER} &7lost the game";
	public static String playerfirstplace = "&a1st place: &f{RANK}";
	public static String playersecondplace = "&a2nd place: &f{RANK}";
	public static String playerthirdplace = "&a3rd place: &f{RANK}";
	public static String playerrewardmessage = "&7 You have been rewarded: &6{REWARD}";
	public static String playerrewardmaterial = "&6 Material :  &f";
	public static String playerrewardcommand = "&6 Command : &f";
	public static String playerrewardmoney = "&6 Money :    &f";
	public static String playerrewardxp = "&6 XP :      &l  &f";
	public static String playerboughtitem = "&7 You have bought item &6{ITEM} &7for &6{MONEY}";
	public static String playerboughtwait = "&7 You will get your items when the game starts";
	public static String notenoughmoney = "&c You need {MONEY} to buy this item";
	public static String alreadyboughtitem = "&c You have already bought a shop item";
	public static String shopnostock = "&c This item is currently out of stock";
	public static String maxdoublejumpsexceeded = "&c You cannot exceed the maximum doublejumps allowed ({MAXJUMPS})";
	public static String playernotinarena = "&c You are not in a TNTRun arena";
	public static String playerkit = "&7 You have been given kit &6{KIT}";
	public static String kitadd = "&7 The kit &6{KIT} &7has been created";
	public static String kitdel = "&7 The kit &6{KIT} &7has been deleted";
	public static String kitexists = "&c The specified kit already exists : &6{KIT}";
	public static String kitnotexists = "&c The specified kit does not exist : &6{KIT}";

	public static String statshead = "&7=========[&6TNTRun Stats&7]=========";
	public static String rewardshead = "&7======[&6TNTRun Rewards&7 - &a{ARENA}&7]======";
	public static String leaderhead = "&7======[&6TNTRun Leaderboard&7]======";
	public static String resultshead = "&7============[&6TNTRun&7]============";
	public static String gamesplayed = "&bGames played &f: ";
	public static String gameswon = "&bWins &f: ";
	public static String gameslost = "&bLosses &f: ";
	public static String statsdisabled = "&c Stats are currently disabled";
	public static String leaderboard = "{POSITION}) &b{PLAYER} &f: Wins &3{WINS}";
	public static String leadersign = "&1{PLAYER} &4{WINS}";
	public static String shopmoneyheader = "&fTNTRun Shop Account";
	public static String shopmoneybalance = "&5Your balance is &6{BAL}";

	public static String helplobby = "Teleport to lobby";
	public static String helplist = "List all arenas OR List arena details";
	public static String helpjoin = "Open join menu OR Join the arena";
	public static String helpautojoin = "Automatically join the first available arena";
	public static String helpspectate = "Join the arena as a spectator";
	public static String helpleave = "Leave the current arena";
	public static String helpvote = "Vote to force-start current arena";
	public static String helpcmds = "View all TNTRun commands";
	public static String helpinfo = "View plugin info";
	public static String helpstats = "View player stats";
	public static String helplb = "Display the TNTRun leaderboard";
	public static String helpstart = "Force-start the arena";
	public static String helplistkit = "List all kits OR List kit details";
	public static String helplistrewards = "List the rewards for the arena";

	public static String setuplobby = "Set the global lobby at your current location";
	public static String setupdellobby = "Delete the global lobby";
	public static String setupcreate = "Create a new arena";
	public static String setupbounds = "Set bounds for arena";
	public static String setuploselevel = "Set loselevel bounds";
	public static String setupspawn = "Set arena spawn at your feet";
	public static String setupspectate = "Set spectator spawn point";
	public static String setupaddspawn = "Set an additional spawn at your feet";
	public static String setupdelspawns = "Delete all spawn points created with the addspawn command";
	public static String setupfinish = "Finish arena and save";
	public static String setupdelspectate = "Delete spectator spawn for arena";
	public static String setupdelay = "Set delay for removing block when player steps on it";
	public static String setupregendelay = "Set delay before players can re-join arena (Default: 60)";
	public static String setupmax = "Set maximum players for arena (Default: 15)";
	public static String setupmin = "Set minimum players for arena (Default: 2)";
	public static String setupvote = "Set vote percentage to force-start arena (Default: 0.75)";
	public static String setuptimelimit = "Set a time limit for arena (Default: 300 seconds)";
	public static String setupcountdown = "Set a countdown for arena (Default: 15 seconds)";
	public static String setupmoney = "Set a money reward for winning player";
	public static String setupteleport = "Set teleport when you lose or win in arena";
	public static String setupdamage = "Enable/disable PVP for the arena";
	public static String setupbarcolor = "Set the BossBar color";
	public static String setupreloadbars = "Reload Bar messages";
	public static String setupreloadtitles = "Reload Title messages";
	public static String setupreloadmsg = "Reload arena messages";
	public static String setupreloadconfig = "Reload config file";
	public static String setupenable = "Enable arena";
	public static String setupdisable = "Disable arena";
	public static String setupdelete = "Delete arena";
	public static String setupreward = "Set the rewards for the arena";
	public static String setupfee = "Set the cost to join the arena";
	public static String setupcurrency = "Set the currency to join the arena";
	public static String setuphelp = "Commands required to setup a new arena";
	public static String setupenablekits = "Enable kits for the arena";
	public static String setupdisablekits = "Disable kits for the arena";
	public static String setupaddkit = "Create a TNTRun kit";
	public static String setupdelkit = "Delete a TNTRun kit";
	public static String setupp1 = "Set targeted block as first selection point";
	public static String setupp2 = "Set targeted block as second selection point";
	public static String setupclear = "Clear selection points";
	public static String setupwhitelist = "Whitelist a command for use in-game";
	
	
	public static void sendMessage(CommandSender sender, String message) {
		sendMessage(sender, message, true);
	}

	public static void sendMessage(CommandSender sender, String message, boolean addPrefix) {
		if (message.isEmpty()) {
			return;
		}
		if (addPrefix) {
			message = trprefix + message;
		}
		sender.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
	}

	public static void broadcastMessage(String message) {
		if (!message.isEmpty()) {
			Bukkit.broadcastMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}

	public static void loadMessages(TNTRun plugin) {
		File messageconfig = new File(plugin.getDataFolder(), "messages.yml");
		plugin.getLanguage().updateLangFile(messageconfig);
		FileConfiguration config = YamlConfiguration.loadConfiguration(messageconfig);

		trprefix = config.getString("trprefix", trprefix);
		menutitle = config.getString("menutitle", menutitle);
		menuarenaname = config.getString("menuarenaname", menuarenaname);
		menutext = config.getString("menutext", menutext);
		menufee = config.getString("menufee", menufee);
		menupvp = config.getString("menupvp", menupvp);
		nopermission = config.getString("nopermission", nopermission);
		nolobby = config.getString("nolobby", nolobby);
		lobbyunloaded = config.getString("lobbyunloaded", lobbyunloaded);
		teleporttolobby = config.getString("teleporttolobby", teleporttolobby);
		availablearenas = config.getString("availablearenas", availablearenas);
		availablekits = config.getString("availablekits", availablekits);
		arenanotexist = config.getString("arenanotexist", arenanotexist);
		noarenas = config.getString("noarenas", noarenas);
		arenanotdisabled = config.getString("arenanotdisabled", arenanotdisabled);
		arenanospectatorspawn = config.getString("arenanospectatorspawn", arenanospectatorspawn);
		arenawolrdna = config.getString("arenawolrdna", arenawolrdna);
		arenanobounds = config.getString("arenanobounds", arenanobounds);
		arenadisabled = config.getString("arenadisabled", arenadisabled);
		arenarunning = config.getString("arenarunning", arenarunning);
		arenaregenerating = config.getString("arenaregenerating", arenaregenerating);
		arenavehicle = config.getString("arenavehicle", arenavehicle);
		arenajoined = config.getString("arenajoined", arenajoined);
		arenadisabling = config.getString("arenadisabling", arenadisabling);
		arenastarting = config.getString("arenastarting", arenastarting);
		signcreate = config.getString("signcreate", signcreate);
		signremove = config.getString("signremove", signremove);
		arenafee = config.getString("arenafee", arenafee);
		arenanofee = config.getString("arenanofee", arenanofee);
		arenawaiting = config.getString("arenawaiting", arenawaiting);
		playerscountinarena = config.getString("playerscountinarena", playerscountinarena);
		limitreached = config.getString("limitreached", limitreached);
		playerjoinedtoothers = config.getString("playerjoinedtoothers", playerjoinedtoothers);
		playerjoininvite = config.getString("playerjoininvite", playerjoininvite);
		playerborderinvite = config.getString("playerborderinvite", playerborderinvite);
		playerclickinvite = config.getString("playerclickinvite", playerclickinvite);
		playerjoinedasspectator = config.getString("playerjoinedasspectator", playerjoinedasspectator);
		playerlefttoplayer = config.getString("playerlefttoplayer", playerlefttoplayer);
		playerlefttoothers = config.getString("playerlefttoothers", playerlefttoothers);
		playervotedforstart = config.getString("playervotedforstart", playervotedforstart);
		playeralreadyvotedforstart = config.getString("playeralreadyvotedforstart", playeralreadyvotedforstart);
		playercannotvote = config.getString("playercannotvote", playercannotvote);
		playersrequiredtostart = config.getString("playersrequiredtostart", playersrequiredtostart);
		arenastarted = config.getString("arenastarted", arenastarted);
		arenanolimit = config.getString("arenanolimit", arenanolimit);
		arenacountdown = config.getString("arenacountdown", arenacountdown);
		arenatimeout = config.getString("arenatimeout", arenatimeout);
		playerwontoplayer = config.getString("playerwontoplayer", playerwontoplayer);
		playerlosttoplayer = config.getString("playerlosttoplayer", playerlosttoplayer);
		playerlosttoothers = config.getString("playerlosttoothers", playerlosttoothers);
		playerfirstplace = config.getString("playerfirstplace", playerfirstplace);
		playersecondplace = config.getString("playersecondplace", playersecondplace);
		playerthirdplace = config.getString("playerthirdplace", playerthirdplace);
		playerrewardmessage = config.getString("playerrewardmessage", playerrewardmessage);
		playerrewardmaterial = config.getString("playerrewardmaterial", playerrewardmaterial);
		playerrewardcommand = config.getString("playerrewardcommand", playerrewardcommand);
		playerrewardmoney = config.getString("playerrewardmoney", playerrewardmoney);
		playerrewardxp = config.getString("playerrewardxp", playerrewardxp);
		playerboughtitem = config.getString("playerboughtitem", playerboughtitem);
		playerboughtwait = config.getString("playerboughtwait", playerboughtwait);
		playerkit = config.getString("playerkit", playerkit);
		notenoughmoney = config.getString("notenoughmoney", notenoughmoney);
		alreadyboughtitem = config.getString("alreadyboughtitem", alreadyboughtitem);
		shopnostock = config.getString("shopnostock", shopnostock);
		maxdoublejumpsexceeded = config.getString("maxdoublejumpsexceeded", maxdoublejumpsexceeded);
		playernotinarena = config.getString("playernotinarena", playernotinarena);
		gamesplayed = config.getString("gamesplayed", gamesplayed);
		gameswon = config.getString("gameswon", gameswon);
		gameslost = config.getString("gameslost", gameslost);
		statshead = config.getString("statshead", statshead);
		rewardshead = config.getString("rewardshead", rewardshead);
		leaderhead = config.getString("leaderhead", leaderhead);
		resultshead = config.getString("resultshead", resultshead);
		leaderboard = config.getString("leaderboard", leaderboard);
		leadersign = config.getString("leadersign", leadersign);
		shopmoneyheader = config.getString("shopmoneyheader", shopmoneyheader);
		shopmoneybalance = config.getString("shopmoneybalance", shopmoneybalance);
		statsdisabled = config.getString("statsdisabled", statsdisabled);
		helplobby = config.getString("helplobby", helplobby);
		helplist = config.getString("helplist", helplist);
		helplistkit = config.getString("helplistkit", helplistkit);
		helplistrewards = config.getString("helplistrewards", helplistrewards);
		helpjoin = config.getString("helpjoin", helpjoin);
		helpspectate = config.getString("helpspectate", helpspectate);
		helpautojoin = config.getString("helpautojoin", helpautojoin);
		helpleave = config.getString("helpleave", helpleave);
		helpvote = config.getString("helpvote", helpvote);
		helpcmds = config.getString("helpcmds", helpcmds);
		helpinfo = config.getString("helpinfo", helpinfo);
		helpstats = config.getString("helpstats", helpstats);
		helplb = config.getString("helplb", helplb);
		helpstart = config.getString("helpstart", helpstart);
		setuplobby = config.getString("setuplobby", setuplobby);
		setupdellobby = config.getString("setupdellobby", setupdellobby);
		setupcreate = config.getString("setupcreate", setupcreate);
		setupbounds = config.getString("setupbounds", setupbounds);
		setuploselevel = config.getString("setuploselevel", setuploselevel);
		setupspawn = config.getString("setupspawn", setupspawn);
		setupspectate = config.getString("setupspectate", setupspectate);
		setupaddspawn = config.getString("setupaddspawn", setupaddspawn);
		setupdelspawns = config.getString("setupdelspawns", setupdelspawns);
		setupfinish = config.getString("setupfinish", setupfinish);
		setupdelspectate = config.getString("setupdelspectate", setupdelspectate);
		setupdelay = config.getString("setupdelay", setupdelay);
		setupregendelay = config.getString("setupregendelay", setupregendelay);
		setupmax = config.getString("setupmax", setupmax);
		setupmin = config.getString("setupmin", setupmin);
		setupvote = config.getString("setupvote", setupvote);
		setuptimelimit = config.getString("setuptimelimit", setuptimelimit);
		setupcountdown = config.getString("setupcountdown", setupcountdown);
		setupmoney = config.getString("setupmoney", setupmoney);
		setupteleport = config.getString("setupteleport", setupteleport);
		setupdamage = config.getString("setupdamage", setupdamage);
		setupbarcolor = config.getString("setupbarcolor", setupbarcolor);
		setupreloadbars = config.getString("setupreloadbars", setupreloadbars);
		setupreloadtitles = config.getString("setupreloadtitles", setupreloadtitles);
		setupreloadmsg = config.getString("setupreloadmsg", setupreloadmsg);
		setupreloadconfig = config.getString("setupreloadconfig", setupreloadconfig);
		setupenable = config.getString("setupenable", setupenable);
		setupdisable = config.getString("setupdisable", setupdisable);
		setupdelete = config.getString("setupdelete", setupdelete);
		setupreward = config.getString("setupreward", setupreward);
		setupfee = config.getString("setupfee", setupfee);
		setupcurrency = config.getString("setupcurrency", setupcurrency);
		setuphelp = config.getString("setuphelp", setuphelp);
		setupenablekits = config.getString("setupenablekits", setupenablekits);
		setupdisablekits = config.getString("setupdisablekits", setupdisablekits);
		setupaddkit = config.getString("setupaddkit", setupaddkit);
		setupdelkit = config.getString("setupdelkit", setupaddkit);
		setupp1 = config.getString("setupp1", setupp1);
		setupp2 = config.getString("setupp2", setupp2);
		setupclear = config.getString("setupclear", setupclear);
		setupwhitelist = config.getString("setupwhitelist", setupwhitelist);
		kitexists = config.getString("kitexists", kitexists);
		kitnotexists = config.getString("kitnotexists", kitnotexists);
		saveMessages(messageconfig);
	}

	private static void saveMessages(File messageconfig) {
		FileConfiguration config = new YamlConfiguration();
		
		config.set("trprefix", trprefix);
		config.set("menutitle",  menutitle);
		config.set("menuarenaname", menuarenaname);
		config.set("menutext",  menutext);
		config.set("menufee",  menufee);
		config.set("menupvp",  menupvp);
		config.set("nopermission", nopermission);
		config.set("nolobby", nolobby);
		config.set("lobbyunloaded", lobbyunloaded);
		config.set("teleporttolobby", teleporttolobby);
		config.set("arenanotexist", arenanotexist);
		config.set("noarenas", noarenas);
		config.set("arenanotdisabled", arenanotdisabled);
		config.set("arenanospectatorspawn", arenanospectatorspawn);
		config.set("availablearenas", availablearenas);
		config.set("availablekits", availablekits);
		config.set("arenawolrdna", arenawolrdna);
		config.set("arenanobounds", arenanobounds);
		config.set("arenadisabled", arenadisabled);
		config.set("arenarunning", arenarunning);
		config.set("arenaregenerating", arenaregenerating);
		config.set("arenavehicle", arenavehicle);
		config.set("arenajoined", arenajoined);
		config.set("arenadisabling", arenadisabling);
		config.set("arenastarting", arenastarting);
		config.set("signcreate", signcreate);
		config.set("signremove", signremove);
		config.set("arenafee", arenafee);
		config.set("arenanofee", arenanofee);
		config.set("arenawaiting", arenawaiting);
		config.set("playerscountinarena", playerscountinarena);
		config.set("limitreached", limitreached);
		config.set("playerjoinedtoothers", playerjoinedtoothers);
		config.set("playerjoininvite", playerjoininvite);
		config.set("playerborderinvite", playerborderinvite);
		config.set("playerclickinvite", playerclickinvite);
		config.set("playerjoinedasspectator", playerjoinedasspectator);
		config.set("playerlefttoplayer", playerlefttoplayer);
		config.set("playerlefttoothers", playerlefttoothers);
		config.set("playervotedforstart", playervotedforstart);
		config.set("playeralreadyvotedforstart", playeralreadyvotedforstart);
		config.set("playercannotvote", playercannotvote);
		config.set("arenastarted", arenastarted);
		config.set("arenanolimit", arenanolimit);
		config.set("arenacountdown", arenacountdown);
		config.set("arenatimeout", arenatimeout);
		config.set("playerwontoplayer", playerwontoplayer);
		config.set("playerlosttoplayer", playerlosttoplayer);
		config.set("playerlosttoothers", playerlosttoothers);
		config.set("playerfirstplace", playerfirstplace);
		config.set("playersecondplace", playersecondplace);
		config.set("playerthirdplace", playerthirdplace);
		config.set("playerrewardmessage", playerrewardmessage);
		config.set("playerrewardmaterial", playerrewardmaterial);
		config.set("playerrewardcommand", playerrewardcommand);
		config.set("playerrewardmoney", playerrewardmoney);
		config.set("playerrewardxp", playerrewardxp);
		config.set("playerboughtitem", playerboughtitem);
		config.set("playerboughtwait", playerboughtwait);
		config.set("playerkit", playerkit);
		config.set("playersrequiredtostart", playersrequiredtostart);
		config.set("notenoughmoney", notenoughmoney);
		config.set("alreadyboughtitem", alreadyboughtitem);
		config.set("shopnostock", shopnostock);
		config.set("maxdoublejumpsexceeded", maxdoublejumpsexceeded);
		config.set("playernotinarena", playernotinarena);
		config.set("gamesplayed", gamesplayed);
		config.set("gameswon", gameswon);
		config.set("gameslost", gameslost);
		config.set("statshead", statshead);
		config.set("rewardshead", rewardshead);
		config.set("leaderhead", leaderhead);
		config.set("resultshead", resultshead);
		config.set("leaderboard", leaderboard);
		config.set("leadersign",  leadersign);
		config.set("shopmoneyheader", shopmoneyheader);
		config.set("shopmoneybalance", shopmoneybalance);
		config.set("statsdisabled", statsdisabled);
		config.set("helplobby", helplobby);
		config.set("helplist", helplist);
		config.set("helplistkit", helplistkit);
		config.set("helplistrewards", helplistrewards);
		config.set("helpjoin", helpjoin);
		config.set("helpspectate", helpspectate);
		config.set("helpautojoin", helpautojoin);
		config.set("helpleave", helpleave);
		config.set("helpvote", helpvote);
		config.set("helpcmds", helpcmds);
		config.set("helpinfo", helpinfo);
		config.set("helpstats", helpstats);
		config.set("helplb", helplb);
		config.set("helpstart",  helpstart);
		config.set("setuplobby", setuplobby);
		config.set("setupdellobby", setupdellobby);
		config.set("setupcreate", setupcreate);
		config.set("setupbounds", setupbounds);
		config.set("setuploselevel", setuploselevel);
		config.set("setupspawn", setupspawn);
		config.set("setupspectate", setupspectate);
		config.set("setupaddspawn", setupaddspawn);
		config.set("setupdelspawns", setupdelspawns);
		config.set("setupfinish", setupfinish);
		config.set("setupdelspectate", setupdelspectate);
		config.set("setupdelay", setupdelay);
		config.set("setupregendelay", setupregendelay);
		config.set("setupmax", setupmax);
		config.set("setupmin", setupmin);
		config.set("setupvote", setupvote);
		config.set("setuptimelimit", setuptimelimit);
		config.set("setupcountdown", setupcountdown);
		config.set("setupmoney", setupmoney);
		config.set("setupteleport", setupteleport);
		config.set("setupdamage", setupdamage);
		config.set("setupbarcolor", setupbarcolor);
		config.set("setupreloadbars", setupreloadbars);
		config.set("setupreloadtitles", setupreloadtitles);
		config.set("setupreloadmsg", setupreloadmsg);
		config.set("setupreloadconfig", setupreloadconfig);
		config.set("setupenable", setupenable);
		config.set("setupdisable", setupdisable);
		config.set("setupdelete", setupdelete);
		config.set("setupreward", setupreward);
		config.set("setupfee", setupfee);
		config.set("setupcurrency", setupcurrency);
		config.set("setuphelp", setuphelp);
		config.set("setupenablekits", setupenablekits);
		config.set("setupdisablekits", setupdisablekits);
		config.set("setupaddkit", setupaddkit);
		config.set("setupdelkit", setupdelkit);
		config.set("setupp1", setupp1);
		config.set("setupp2", setupp2);
		config.set("setupclear", setupclear);
		config.set("setupwhitelist", setupwhitelist);
		config.set("kitexists", kitexists);
		config.set("kitnotexists", kitnotexists);
		try {
			config.save(messageconfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
