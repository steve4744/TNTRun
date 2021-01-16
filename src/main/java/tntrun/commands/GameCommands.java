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

package tntrun.commands;

import java.util.List;
import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;
import tntrun.utils.Utils;

public class GameCommands implements CommandExecutor {

	private TNTRun plugin;

	public GameCommands(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FormattingCodesParser.parseFormattingCodes(Messages.trprefix + "&c You must be a player"));
			return true;
		}
		Player player = (Player) sender;
		if (args.length < 1) {
			Messages.sendMessage(player, "&7============" + Messages.trprefix + "============", false);
			Messages.sendMessage(player, "&c Please use &6/tr help");
			return true;
		}
		// help command
		if (args[0].equalsIgnoreCase("help")) {
			Messages.sendMessage(player, "&7============" + Messages.trprefix + "============", false);
			player.spigot().sendMessage(Utils.getTextComponent("/tr lobby", true), Utils.getTextComponent(Messages.helplobby));
			player.spigot().sendMessage(Utils.getTextComponent("/tr list [arena]", true), Utils.getTextComponent(Messages.helplist));
			player.spigot().sendMessage(Utils.getTextComponent("/tr join [arena]", true), Utils.getTextComponent(Messages.helpjoin));
			player.spigot().sendMessage(Utils.getTextComponent("/tr spectate {arena}", true), Utils.getTextComponent(Messages.helpspectate));
			player.spigot().sendMessage(Utils.getTextComponent("/tr autojoin [pvp|nopvp]", true), Utils.getTextComponent(Messages.helpautojoin));
			player.spigot().sendMessage(Utils.getTextComponent("/tr leave", true), Utils.getTextComponent(Messages.helpleave));
			player.spigot().sendMessage(Utils.getTextComponent("/tr vote", true), Utils.getTextComponent(Messages.helpvote));
			player.spigot().sendMessage(Utils.getTextComponent("/tr info", true), Utils.getTextComponent(Messages.helpinfo));
			player.spigot().sendMessage(Utils.getTextComponent("/tr stats", true), Utils.getTextComponent(Messages.helpstats));
			player.spigot().sendMessage(Utils.getTextComponent("/tr leaderboard [size]", true), Utils.getTextComponent(Messages.helplb));
			player.spigot().sendMessage(Utils.getTextComponent("/tr listkit [kit]", true), Utils.getTextComponent(Messages.helplistkit));
			player.spigot().sendMessage(Utils.getTextComponent("/tr listrewards {arena}", true), Utils.getTextComponent(Messages.helplistrewards));
			player.spigot().sendMessage(Utils.getTextComponent("/tr start {arena}", true), Utils.getTextComponent(Messages.helpstart));
			player.spigot().sendMessage(Utils.getTextComponent("/tr cmds", true), Utils.getTextComponent(Messages.helpcmds));

		} else if (args[0].equalsIgnoreCase("lobby")) {
			plugin.getGlobalLobby().joinLobby(player);
		}

		// list arenas
		else if (args[0].equalsIgnoreCase("list")) {
			if (args.length >= 2) {
				Arena arena = plugin.amanager.getArenaByName(args[1]);
				if (arena == null) {
					Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[1]));
					return true;
				}
				//list arena details
				Messages.sendMessage(player, "&7============" + Messages.trprefix + "============", false);
				Messages.sendMessage(player, "&7Arena Details: &a" + arena.getArenaName(), false);

				String arenaStatus = arena.getStatusManager().isArenaEnabled() ? "Enabled" : "Disabled";
				player.sendMessage(ChatColor.GOLD + "Status " + ChatColor.DARK_GRAY + "........................ " + ChatColor.RED + arenaStatus);
				player.sendMessage(ChatColor.GOLD + "Min Players " + ChatColor.DARK_GRAY + "........... " + ChatColor.RED + + arena.getStructureManager().getMinPlayers());
				player.sendMessage(ChatColor.GOLD + "Max Players " + ChatColor.DARK_GRAY + "......... " + ChatColor.RED + arena.getStructureManager().getMaxPlayers());
				player.sendMessage(ChatColor.GOLD + "Time Limit " + ChatColor.DARK_GRAY + ".................. " + ChatColor.RED + arena.getStructureManager().getTimeLimit() + " seconds");
				player.sendMessage(ChatColor.GOLD + "Countdown " + ChatColor.DARK_GRAY + ".............. " + ChatColor.RED + arena.getStructureManager().getCountdown() + " seconds");
				player.sendMessage(ChatColor.GOLD + "Teleport to " + ChatColor.DARK_GRAY + "............ " + ChatColor.RED + Utils.getTitleCase(arena.getStructureManager().getTeleportDestination().toString()));
				player.sendMessage(ChatColor.GOLD + "Player Count " + ChatColor.DARK_GRAY + "........ " + ChatColor.RED + arena.getPlayersManager().getPlayersCount());
				player.sendMessage(ChatColor.GOLD + "Vote Percent " + ChatColor.DARK_GRAY + "....... " + ChatColor.RED + arena.getStructureManager().getVotePercent());
				player.sendMessage(ChatColor.GOLD + "PVP Damage " + ChatColor.DARK_GRAY + "........... " + ChatColor.RED + Utils.getTitleCase(arena.getStructureManager().getDamageEnabled().toString()));

				String result = arena.getStructureManager().isKitsEnabled() ? "Yes" : "No";
				player.sendMessage(ChatColor.GOLD + "Kits Enabled " + ChatColor.DARK_GRAY + "......... " + ChatColor.RED + result);

				List<String> kitnames = arena.getStructureManager().getLinkedKits();
				if (kitnames.size() > 0) {
					player.sendMessage(ChatColor.GOLD + "Linked Kits " + ChatColor.DARK_GRAY + "............. " + ChatColor.RED + String.join(", ", kitnames));
				}

				player.sendMessage(ChatColor.GOLD + "Rewards " + ChatColor.DARK_GRAY + "................... " + ChatColor.RED + "Use command '/tr listrewards {arena}'");

				if (arena.getStructureManager().getFee() > 0) {
					player.sendMessage(ChatColor.GOLD + "Join Fee " + ChatColor.DARK_GRAY + "................... " + ChatColor.RED + arena.getStructureManager().getArenaCost());
				}

				if (arena.getStructureManager().isTestMode()) {
					player.sendMessage(ChatColor.GOLD + "Test Mode " + ChatColor.DARK_GRAY + "............... " + ChatColor.RED + "Enabled");
				}
				return false;
			}

			int arenacount = plugin.amanager.getArenas().size();
			Messages.sendMessage(player, Messages.availablearenas.replace("{COUNT}", String.valueOf(arenacount)));
			if (arenacount == 0) {
				return false;
			}
			StringJoiner message = new StringJoiner(" : ");
			plugin.amanager.getArenasNames().stream().sorted().forEach(arenaname -> {
				if (plugin.amanager.getArenaByName(arenaname).getStatusManager().isArenaEnabled()) {
					message.add("&a" + arenaname);
				} else {
					message.add("&c" + arenaname + "&a");
				}
			});
			Messages.sendMessage(player, message.toString(), false);
		}

		// join arena
		else if (args[0].equalsIgnoreCase("join")) {
			if (args.length == 1 && player.hasPermission("tntrun.joinmenu")) {
				plugin.getJoinMenu().buildMenu(player);
				return false;
			}
			if (args.length != 2) {
				Messages.sendMessage(player, "&c Invalid number of arguments supplied");
				return false;
			}
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena != null) {
				if (arena.getPlayerHandler().checkJoin(player)) {
					arena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoothers);
				}
			} else {
				Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[1]));
				return true;
			}
		}

		// spectate arena
		else if (args[0].equalsIgnoreCase("spectate")) {
			if (!player.hasPermission("tntrun.spectate")) {
				Messages.sendMessage(player, Messages.nopermission);
				return true;
			}
			if (args.length != 2) {
				Messages.sendMessage(player, "&c Invalid number of arguments supplied");
				return false;
			}
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena == null) {
				Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[1]));
				return true;
			}
			if (arena.getStructureManager().getSpectatorSpawnVector() == null) {
				Messages.sendMessage(player, Messages.arenanospectatorspawn.replace("{ARENA}", args[1]));
				return true;
			}
			if (!arena.getPlayerHandler().preJoinChecks(player, false)) {
				return true;
			}
			arena.getPlayerHandler().spectatePlayer(player, Messages.playerjoinedasspectator, "");
			if (Utils.debug()) {
				plugin.getLogger().info("Player " + player.getName() + " joined arena " + arena.getArenaName() + " as a spectator");
			}
		}

		// autojoin
		else if (args[0].equalsIgnoreCase("autojoin")) {
			String arenatype = "";
			if (args.length >= 2) {
				if (!args[1].equalsIgnoreCase("pvp") && !args[1].equalsIgnoreCase("nopvp")) {
					Messages.sendMessage(player, "&c Invalid argument supplied");
					return true;
				}
				arenatype = args[1];
			}
			plugin.getJoinMenu().autoJoin(player, arenatype);
		}

		// tntrun_reloaded info
		else if (args[0].equalsIgnoreCase("info")) {
			Utils.displayInfo(player);
		}

		// player stats
		else if (args[0].equalsIgnoreCase("stats")) {
			if (!plugin.useStats()) {
				Messages.sendMessage(player, Messages.statsdisabled);
				return true;
			}
			Messages.sendMessage(player, Messages.statshead, false);
			Messages.sendMessage(player, Messages.gamesplayed + plugin.stats.getPlayedGames(player), false);
			Messages.sendMessage(player, Messages.gameswon + plugin.stats.getWins(player), false);
			Messages.sendMessage(player, Messages.gameslost + plugin.stats.getLosses(player), false);
		}

		// leaderboard
		else if (args[0].equalsIgnoreCase("leaderboard")) {
			if (!plugin.useStats()) {
				Messages.sendMessage(player, Messages.statsdisabled);
				return true;
			}
			int entries = plugin.getConfig().getInt("leaderboard.maxentries", 10);
			if (args.length > 1) {
				if (Utils.isNumber(args[1]) && Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) <= entries) {
					entries = Integer.parseInt(args[1]);
				}
			}
			Messages.sendMessage(player, Messages.leaderhead, false);
			plugin.stats.getLeaderboard(player, entries);
		}

		// leave arena
		else if (args[0].equalsIgnoreCase("leave")) {
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			if (arena != null) {
				arena.getPlayerHandler().leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
			} else {
				Messages.sendMessage(player, Messages.playernotinarena);
				return true;
			}
		}

		// all commands
		else if (args[0].equalsIgnoreCase("cmds")) {
			Messages.sendMessage(player, "&7============" + Messages.trprefix + "============", false);
			Utils.displayHelp(player);
			Messages.sendMessage(player, "&7============[&6Other commands&7]============", false);
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup deletespectate {arena}", true), Utils.getTextComponent(Messages.setupdelspectate));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setgameleveldestroydelay {arena} {ticks}", true), Utils.getTextComponent(Messages.setupdelay));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setregenerationdelay {arena} {ticks}", true), Utils.getTextComponent(Messages.setupregendelay));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setmaxplayers {arena} {players}", true), Utils.getTextComponent(Messages.setupmax));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setminplayers {arena} {players}", true), Utils.getTextComponent(Messages.setupmin));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setvotepercent {arena} {0<votepercent<1}", true), Utils.getTextComponent(Messages.setupvote));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup settimelimit {arena} {seconds}", true), Utils.getTextComponent(Messages.setuptimelimit));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setcountdown {arena} {seconds}", true), Utils.getTextComponent(Messages.setupcountdown));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setmoneyreward {arena} {amount}", true), Utils.getTextComponent(Messages.setupmoney));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setteleport {arena} {previous/lobby}", true), Utils.getTextComponent(Messages.setupteleport));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setdamage {arena} {yes/no/zero}", true), Utils.getTextComponent(Messages.setupdamage));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup addkit {kitname}", true), Utils.getTextComponent(Messages.setupaddkit));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup deletekit {kitname}", true), Utils.getTextComponent(Messages.setupdelkit));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup enablekits {arena}", true), Utils.getTextComponent(Messages.setupenablekits));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup disablekits {arena}", true), Utils.getTextComponent(Messages.setupdisablekits));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setbarcolor", true), Utils.getTextComponent(Messages.setupbarcolor));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setP1", true), Utils.getTextComponent(Messages.setupp1));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setP2", true), Utils.getTextComponent(Messages.setupp2));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup clear", true), Utils.getTextComponent(Messages.setupclear));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup reloadbars", true), Utils.getTextComponent(Messages.setupreloadbars));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup reloadtitles", true), Utils.getTextComponent(Messages.setupreloadtitles));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup reloadmsg", true), Utils.getTextComponent(Messages.setupreloadmsg));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup reloadconfig", true), Utils.getTextComponent(Messages.setupreloadconfig));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup enable {arena}", true), Utils.getTextComponent(Messages.setupenable));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup disable {arena}", true), Utils.getTextComponent(Messages.setupdisable));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup delete {arena}", true), Utils.getTextComponent(Messages.setupdelete));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setreward {arena}", true), Utils.getTextComponent(Messages.setupreward));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setfee {arena} {amount}", true), Utils.getTextComponent(Messages.setupfee));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setcurrency {arena} {item}", true), Utils.getTextComponent(Messages.setupcurrency));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup setlobby", true), Utils.getTextComponent(Messages.setuplobby));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup deletelobby", true), Utils.getTextComponent(Messages.setupdellobby));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup addspawn", true), Utils.getTextComponent(Messages.setupaddspawn));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup deletespawnpoints", true), Utils.getTextComponent(Messages.setupdelspawns));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup addtowhitelist", true), Utils.getTextComponent(Messages.setupwhitelist));
			player.spigot().sendMessage(Utils.getTextComponent("/trsetup help", true), Utils.getTextComponent(Messages.setuphelp));
		}

		// vote
		else if (args[0].equalsIgnoreCase("vote")) {
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			if (arena == null) {
				Messages.sendMessage(player, Messages.playernotinarena);
				return true;
			}
			if (!arena.getPlayersManager().getPlayers().contains(player)) {
				Messages.sendMessage(player, Messages.playercannotvote);
				return true;
			}
			if (arena.getPlayerHandler().vote(player)) {
				Messages.sendMessage(player, Messages.playervotedforstart);
			} else {
				Messages.sendMessage(player, Messages.playeralreadyvotedforstart);
				return true;
			}
		}

		// listkits
		else if (args[0].equalsIgnoreCase("listkit") || args[0].equalsIgnoreCase("listkits")) {
			if (args.length >= 2) {
				plugin.getKitManager().listKit(args[1], player);
				return true;
			}

			int kitcount = plugin.getKitManager().getKits().size();
			Messages.sendMessage(player, Messages.availablekits.replace("{COUNT}", String.valueOf(kitcount)));
			if (kitcount == 0) {
				return false;
			}
			StringJoiner message = new StringJoiner(" : ");
			plugin.getKitManager().getKits().stream().sorted().forEach(kit -> {
				message.add("&a" + kit);
			});
			Messages.sendMessage(player, message.toString(), false);
		}

		// listrewards
		else if (args[0].equalsIgnoreCase("listrewards")) {
			if (!player.hasPermission("tntrun.listrewards")) {
				Messages.sendMessage(player, Messages.nopermission);
				return true;
			}
			if (args.length != 2) {
				Messages.sendMessage(player, "&c Invalid number of arguments supplied");
				return true;
			}
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena == null) {
				Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[1]));
				return true;
			}
			arena.getStructureManager().getRewards().listRewards(player, args[1]);
		}

		// start
		else if (args[0].equalsIgnoreCase("start")) {
			if (!player.hasPermission("tntrun.start")) {
				Messages.sendMessage(player, Messages.nopermission);
				return true;
			}
			if (args.length != 2) {
				Messages.sendMessage(player, "&c Invalid number of arguments supplied");
				return true;
			}
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena == null) {
				Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[1]));
				return true;
			}
			if (arena.getPlayersManager().getPlayersCount() <= 1) {
				Messages.sendMessage(player, Messages.playersrequiredtostart);
				return true;
			}
			if (!arena.getStatusManager().isArenaStarting()) {
				plugin.getServer().getConsoleSender().sendMessage("[TNTRun] Arena " + ChatColor.GOLD + arena.getArenaName() + ChatColor.WHITE + " force-started by " + ChatColor.AQUA + player.getName());
				arena.getGameHandler().forceStartByCommand();
			} else {
				Messages.sendMessage(player, Messages.arenastarting.replace("{ARENA}", args[1]));
				return true;
			}
		}

		else {
			Messages.sendMessage(player, "&c Invalid argument supplied, please use &6/tr help");
			return true;
		}	
		return false;
	}

}
