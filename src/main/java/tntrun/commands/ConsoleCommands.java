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

import java.util.StringJoiner;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.utils.Bars;
import tntrun.utils.Utils;
import tntrun.messages.Messages;

public class ConsoleCommands implements CommandExecutor {

	private TNTRun plugin;

	public ConsoleCommands(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender || sender instanceof BlockCommandSender)) {
			sender.sendMessage("Console is expected");
			return true;
		}
		if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
			Utils.displayInfo(sender);
			return true;
		}
		// disable arena
		if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena != null) {
				arena.getStatusManager().disableArena();
				sender.sendMessage("Arena disabled");
			} else {
				Messages.sendMessage(sender, Messages.arenanotexist.replace("{ARENA}", args[1]));
			}
			return true;
		}
		// enable arena
		else if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena == null) {
				Messages.sendMessage(sender, Messages.arenanotexist.replace("{ARENA}", args[1]));
				return true;
			}
			if (arena.getStatusManager().isArenaEnabled()) {
				sender.sendMessage("Arena already enabled.");
			} else {
				if (arena.getStatusManager().enableArena()) {
					sender.sendMessage("Arena enabled");
				} else {
					sender.sendMessage("Arena is not configured. Reason: "+ arena.getStructureManager().isArenaConfigured());
				}
			}
			return true;
		}
		// leader board
		else if (args.length >= 1 && args[0].equalsIgnoreCase("leaderboard")) {
			if (!plugin.useStats()) {
				Messages.sendMessage(sender, Messages.statsdisabled);
				return true;
			}
			int entries = plugin.getConfig().getInt("leaderboard.maxentries", 10);
			if (args.length >= 2) {
				if (Utils.isNumber(args[1]) && Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) <= entries) {
					entries = Integer.parseInt(args[1]);
				}
			}
			Messages.sendMessage(sender, Messages.leaderhead, false);
			plugin.stats.getLeaderboard(sender, entries);
			return true;
		}
		// list
		else if (args[0].equalsIgnoreCase("list")) {
			int arenacount = plugin.amanager.getArenas().size();
			Messages.sendMessage(sender, Messages.availablearenas.replace("{COUNT}", String.valueOf(arenacount)));
			if (arenacount == 0) {
				return true;
			}
			StringJoiner message = new StringJoiner(" : ");
			plugin.amanager.getArenasNames().stream().sorted().forEach(arenaname -> {
				if (plugin.amanager.getArenaByName(arenaname).getStatusManager().isArenaEnabled()) {
					message.add("&a" + arenaname);
				} else {
					message.add("&c" + arenaname + "&a");
				}
			});
			Messages.sendMessage(sender, message.toString(), false);
			return true;
		}
		// start
		else if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
			Arena arena = plugin.amanager.getArenaByName(args[1]);
			if (arena == null) {
				Messages.sendMessage(sender, Messages.arenanotexist.replace("{ARENA}", args[1]));
				return true;
			}
			if (arena.getPlayersManager().getPlayersCount() <= 1) {
				Messages.sendMessage(sender, Messages.playersrequiredtostart);
				return true;
			}
			if (!arena.getStatusManager().isArenaStarting()) {
				Messages.sendMessage(sender, "Arena " + arena.getArenaName() + " force-started by console");
				arena.getGameHandler().forceStartByCommand();
				return true;
			}
		}
		// help
		if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("cmds")) {
			displayConsoleCommands(sender);
		}
		// reload config
		else if (args[0].equalsIgnoreCase("reloadconfig")) {
			plugin.reloadConfig();
			plugin.signEditor.loadConfiguration();
			sender.sendMessage("Config reloaded");
			return true;
		}
		// reload messages
		else if (args[0].equalsIgnoreCase("reloadmsg")) {
			Messages.loadMessages(plugin);
			sender.sendMessage("Messages reloaded");
			return true;
		}
		// reload bars
		else if (args[0].equalsIgnoreCase("reloadbars")) {
			Bars.loadBars(plugin);
			sender.sendMessage("Bars reloaded");
			return true;
		}

		return false;
	}
	private void displayConsoleCommands(CommandSender sender) {
		Messages.sendMessage(sender, "trconsole help");
		Messages.sendMessage(sender, "trconsole list");
		Messages.sendMessage(sender, "trconsole info");
		Messages.sendMessage(sender, "trconsole enable {arena}");
		Messages.sendMessage(sender, "trconsole disable {arena}");
		Messages.sendMessage(sender, "trconsole start {arena}");
		Messages.sendMessage(sender, "trconsole reloadconfig");
		Messages.sendMessage(sender, "trconsole reloadmessages");
		Messages.sendMessage(sender, "trconsole reloadbars");
		Messages.sendMessage(sender, "trconsole leaderboard");
	}

}
