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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class AutoTabCompleter implements TabCompleter {

	private static final List<String> COMMANDS = Arrays.asList(
			"help", "lobby", "list", "join", "leave", "vote", "cmds", "info", "stats", "listkits", "autojoin", "leaderboard");
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("tntrun") || cmd.getName().equalsIgnoreCase("tr")) {
			if (!(sender instanceof Player)) {
				return null;
			}

			List<String> list = new ArrayList<>();
			List<String> auto = new ArrayList<>();

			if (args.length == 1) {
				list.addAll(COMMANDS);

				if (sender.hasPermission("tntrun.start")) {
					list.add("start");
				}
				if (sender.hasPermission("tntrun.spectate")) {
					list.add("spectate");
				}
				if (sender.hasPermission("tntrun.listrewards")) {
					list.add("listrewards");
				}

			} else if (args.length == 2) {
				if (Stream.of("join", "list", "start", "spectate", "listrewards").anyMatch(s -> s.equalsIgnoreCase(args[0]))) {
					list.addAll(TNTRun.getInstance().amanager.getArenasNames());

				} else if (args[0].equalsIgnoreCase("listkits") || args[0].equalsIgnoreCase("listkit")) {
					list.addAll(TNTRun.getInstance().getKitManager().getKits());

				} else if (args[0].equalsIgnoreCase("autojoin")) {
					list.add("nopvp");
					list.add("pvp");
				}
			}
			for (String s : list) {
				if (s.startsWith(args[args.length - 1])) {
					auto.add(s);
				}
			}
			return auto.isEmpty() ? list : auto;

		}
		return null;
	}
}
