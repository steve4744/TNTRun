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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class GameCommands implements CommandExecutor {

	private TNTRun plugin;

	public GameCommands(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§7[§6TNTRun§7] §cYou must be player");
			return true;
		}
		Player player = (Player) sender;
		if (args.length < 1){
			sender.sendMessage("§7============[§6TNTRun§7]§7============");
			sender.sendMessage("§7[§6TNTRun§7] §cPlease use §6/tr help");
			return true;
		}
		// help command
		if (args[0].equalsIgnoreCase("help")) {
			player.sendMessage("§7============[§6TNTRun§7]§7============");
			Messages.sendMessage(player, "§6/tr lobby §f- §c" + Messages.helplobby);
			Messages.sendMessage(player, "§6/tr list [arena] §f- §c" + Messages.helplist);
			Messages.sendMessage(player, "§6/tr join {arena} §f- §c" + Messages.helpjoin);
			Messages.sendMessage(player, "§6/tr leave §f- §c" + Messages.helpleave);
			Messages.sendMessage(player, "§6/tr vote §f- §c" + Messages.helpvote);
			Messages.sendMessage(player, "§6/tr cmds §f- §c" + Messages.helpcmds);
			Messages.sendMessage(player, "§6/tr info §f- §c" + Messages.helpinfo);
			Messages.sendMessage(player, "§6/tr stats §f- §c" + Messages.helpstats);
			Messages.sendMessage(player, "§6/tr leaderboard [size] §f- §c" + Messages.helplb);
			return true;
		} else if (args[0].equalsIgnoreCase("lobby")) {
			if (plugin.globallobby.isLobbyLocationSet()) {
				if (plugin.globallobby.isLobbyLocationWorldAvailable()) {
					player.teleport(plugin.globallobby.getLobbyLocation());
					Messages.sendMessage(player, Messages.teleporttolobby);
				} else {
					player.sendMessage("§7[§6TNTRun§7] §cLobby world is unloaded or doesn't exist");
				}
			} else {
				sender.sendMessage("§7[§6TNTRun§7] §cLobby isn't set");

			}
			return true;
		}
		// list arenas
		else if (args[0].equalsIgnoreCase("list")) {
			if (args.length >= 2) {
				Arena arena = plugin.amanager.getArenaByName(args[1]);
				if (arena == null) {
					sender.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[1] + "§c doesn't exist");
					return true;
				}
				//list arena details
				sender.sendMessage("§7============[§6TNTRun§7]§7============");
				sender.sendMessage("§7Arena Details: §a" + arena.getArenaName());
				
				String arenaStatus = "Enabled";
				if (!arena.getStatusManager().isArenaEnabled()) {
					arenaStatus = "Disabled";
				}
				sender.sendMessage("§6Status §f- §c " + arenaStatus);
				sender.sendMessage("§6Min Players §f- §c " + arena.getStructureManager().getMinPlayers());
				sender.sendMessage("§6Max Players §f- §c " + arena.getStructureManager().getMaxPlayers());
				sender.sendMessage("§6Time Limit §f- §c " + arena.getStructureManager().getTimeLimit() + " seconds");
				sender.sendMessage("§6Countdown §f- §c " + arena.getStructureManager().getCountdown() + " seconds");
				sender.sendMessage("§6Teleport to §f- §c " + arena.getStructureManager().getTeleportDestination());
				sender.sendMessage("§6Player Count §f- §c " + arena.getPlayersManager().getPlayersCount());
				sender.sendMessage("§6Vote Percent §f- §c " + arena.getStructureManager().getVotePercent());
				if (arena.getStructureManager().getRewards().getXPReward() != 0) {
					sender.sendMessage("§6XP Reward §f- §c " + arena.getStructureManager().getRewards().getXPReward());
				}
				if (arena.getStructureManager().getRewards().getMoneyReward() != 0) {
					sender.sendMessage("§6Money Reward §f- §c " + arena.getStructureManager().getRewards().getMoneyReward());
				}
				List<String> materialrewards = arena.getStructureManager().getRewards().getMaterialReward();
				List<String> materialamounts = arena.getStructureManager().getRewards().getMaterialAmount();
						
				String rewardmessage = "";
				for (int i=0; i < materialrewards.size(); i++) {
					if (arena.getStructureManager().getRewards().isValidReward(materialrewards.get(i), materialamounts.get(i))) {
						rewardmessage += materialamounts.get(i) + "§6 x §c" + materialrewards.get(i) + ", ";
					}
				}
				if (rewardmessage.length() > 0) {
					sender.sendMessage("§6Material Reward §f- §c " + rewardmessage.substring(0, rewardmessage.length() - 2));
				}

				if (arena.getStructureManager().getRewards().getCommandReward() != null) {
					sender.sendMessage("§6Command Reward §f- §6\"§c" + arena.getStructureManager().getRewards().getCommandReward() + "§6\"");
				}

				if (arena.getStructureManager().isTestMode()) {
					player.sendMessage("§6Test Mode §f- §c Enabled");
				}

				return true;
			}
			StringBuilder message = new StringBuilder(200);
			message.append(Messages.availablearenas);
			for (Arena arena : plugin.amanager.getArenas()) {
				if (arena.getStatusManager().isArenaEnabled()) {
					message.append("&a" + arena.getArenaName() + " ; ");
				} else {
					message.append("&c" + arena.getArenaName() + " ; ");
				}
			}
			if (message.length() > 0) {
				message.setLength(message.length() - 2);
			}
			Messages.sendMessage(player, message.toString());
			return true;
		}
		// join arena
		else if (args[0].equalsIgnoreCase("join")) {
			if (args.length != 2) {
				player.sendMessage("§7[§6TNTRun§7] §cInvalid number of arguments supplied");
				return false;
			}
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena != null) {
				boolean canJoin = arena.getPlayerHandler().checkJoin(player);
				if (canJoin) {
					arena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoplayer, Messages.playerjoinedtoothers);
				}
				return true;
			} else {
				sender.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[1] + "§c doesn't exist");
				return true;
			}
		}
		// tntrun_reloaded info
		else if (args[0].equalsIgnoreCase("info")) {
			Utils.displayInfo(player);
		}
		// player stats
		else if (args[0].equalsIgnoreCase("stats")) {
			if(!plugin.useStats()){
				Messages.sendMessage(player, Messages.statsdisabled);
				return true;
			}
			player.sendMessage("§7============[§6TNTRun§7]§7============");
			Messages.sendMessage(player, Messages.gamesplayed + plugin.stats.getPlayedGames(player));
			Messages.sendMessage(player, Messages.gameswon + plugin.stats.getWins(player));
			Messages.sendMessage(player, Messages.gameslost + plugin.stats.getLosses(player));
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
			Messages.sendMessage(player, Messages.leaderhead);
			plugin.stats.getLeaderboard(player, entries);
		}
		// leave arena
		else if (args[0].equalsIgnoreCase("leave")) {
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			if (arena != null) {
				arena.getPlayerHandler().leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
				return true;
			} else {
				Messages.sendMessage(player, Messages.playernotinarena);
				return true;
			}
		}
		// all commands
		else if (args[0].equalsIgnoreCase("cmds")) {
			sender.sendMessage("§7============[§6TNTRun§7]============");
			Messages.sendMessage(player, "§6/trsetup setlobby §f- §c" + Messages.setuplobby);
			Messages.sendMessage(player, "§6/trsetup create {arena} §f- §c" + Messages.setupcreate);
			Messages.sendMessage(player, "§6/trsetup setarena {arena} §f- §c" + Messages.setupbounds);
			Messages.sendMessage(player, "§6/trsetup setloselevel {arena} §f- §c" + Messages.setuploselevel);
			Messages.sendMessage(player, "§6/trsetup setspawn {arena} §f- §c" + Messages.setupspawn);
			Messages.sendMessage(player, "§6/trsetup setspectate {arena} §f- §c" + Messages.setupspectate);
			Messages.sendMessage(player, "§6/trsetup finish {arena} §f- §c" + Messages.setupfinish);
			sender.sendMessage("§7============[§6Other commands§7]============");
			Messages.sendMessage(player, "§6/trsetup delspectate {arena} §f- §c" + Messages.setupdelspectate);
			Messages.sendMessage(player, "§6/trsetup setgameleveldestroydelay {arena} {ticks} §f- §c" + Messages.setupdelay);
			Messages.sendMessage(player, "§6/trsetup setmaxplayers {arena} {players} §f- §c" + Messages.setupmax);
			Messages.sendMessage(player, "§6/trsetup setminplayers {arena} {players} §f- §c" + Messages.setupmin);
			Messages.sendMessage(player, "§6/trsetup setvotepercent {arena} {0<votepercent<1} §f- §c" + Messages.setupvote);
			Messages.sendMessage(player, "§6/trsetup settimelimit {arena} {seconds} §f- §c" + Messages.setuptimelimit);
			Messages.sendMessage(player, "§6/trsetup setcountdown {arena} {seconds} §f- §c" + Messages.setupcountdown);
			Messages.sendMessage(player, "§6/trsetup setmoneyreward {arena} {amount} §f- §c" + Messages.setupmoney);
			Messages.sendMessage(player, "§6/trsetup setteleport {arena} {previous/lobby} §f- §c" + Messages.setupteleport);
			Messages.sendMessage(player, "§6/trsetup setdamage {arena} {on/off/zero} §f- §c" + Messages.setupdamage);
			Messages.sendMessage(player, "§6/trsetup reloadbars §f- §c" + Messages.setupreloadbars);
			Messages.sendMessage(player, "§6/trsetup reloadtitles §f- §c" + Messages.setupreloadtitles);
			Messages.sendMessage(player, "§6/trsetup reloadmsg §f- §c" + Messages.setupreloadmsg);
			Messages.sendMessage(player, "§6/trsetup reloadconfig §f- §c" + Messages.setupreloadconfig);
			Messages.sendMessage(player, "§6/trsetup enable {arena} §f- §c" + Messages.setupenable);
			Messages.sendMessage(player, "§6/trsetup disable {arena} §f- §c" + Messages.setupdisable);
			Messages.sendMessage(player, "§6/trsetup delete {arena} §f- §c" + Messages.setupdelete);
			Messages.sendMessage(player, "§6/trsetup setreward {arena} §f- §c" + Messages.setupreward);
		}
		// vote
		else if (args[0].equalsIgnoreCase("vote")) {
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			if (arena != null) {
				if (arena.getPlayerHandler().vote(player)) {
					Messages.sendMessage(player, Messages.playervotedforstart);
				} else {
					Messages.sendMessage(player, Messages.playeralreadyvotedforstart);
				}
				return true;
			} else {
				Messages.sendMessage(player, Messages.playernotinarena);
				return true;
			}
		} 
		else {
			sender.sendMessage("§7[§6TNTRun§7] §cInvalid argument supplied, please use §6/tr help");
			return true;
		}	
		return false;
	}

}
