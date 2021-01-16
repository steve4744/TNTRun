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

package tntrun.commands.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class SetupTabCompleter implements TabCompleter {

	private static final List<String> COMMANDS = Arrays.asList("help", "create", "setlobby", "reloadbars", "reloadtitles", "reloadmsg",
			"reloadconfig", "setbarcolor", "addkit", "deletekit", "deletelobby", "setp1", "setp2", "clear", "addtowhitelist", "setlanguage");

	private static final List<String> ARENA_COMMANDS = Arrays.asList("setarena", "setloselevel", "setspawn", "addspawn", "setspectate", "finish",
			"deletespectate", "deletespawnpoints", "setgameleveldestroydelay", "setregenerationdelay", "setmaxplayers", "setminplayers", "setvotepercent",
			"settimelimit", "setcountdown", "setmoneyreward", "setteleport", "enable", "disable", "setreward", "enablekits", "disablekits", "linkkit",
			"unlinkkit", "delete", "setdamage", "setfee", "setcurrency");

	private static final List<String> TELEPORT_COMMANDS = Arrays.asList("lobby", "previous");

	private static final List<String> DAMAGE_COMMANDS = Arrays.asList("yes", "no", "zero");

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}

		if (!sender.hasPermission("tntrun.setup")) {
			return null;
		}

		List<String> list = new ArrayList<>();
		List<String> auto = new ArrayList<>();

		if (args.length == 1) {
			list.addAll(COMMANDS);
			list.addAll(ARENA_COMMANDS);

		} else if (args.length == 2) {
			if (ARENA_COMMANDS.contains(args[0])) {
				list.addAll(TNTRun.getInstance().amanager.getArenasNames());

			} else if (args[0].equalsIgnoreCase("setbarcolor") || args[0].equalsIgnoreCase("setbarcolour")) {
				for (BarColor color : Arrays.asList(BarColor.class.getEnumConstants())) {
					list.add(color.toString());
				}
				list.add("RANDOM");

			} else if (args[0].equalsIgnoreCase("deletekit")) {
				list.addAll(TNTRun.getInstance().getKitManager().getKits());

			} else if (args[0].equalsIgnoreCase("setlanguage")) {
				list.addAll(TNTRun.getInstance().getLanguage().getTranslatedLanguages());
			}

		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("setteleport")) {
				list.addAll(TELEPORT_COMMANDS);

			} else if (args[0].equalsIgnoreCase("setdamage")) {
				list.addAll(DAMAGE_COMMANDS);

			} else if (args[0].equalsIgnoreCase("linkkit")) {
				list.addAll(TNTRun.getInstance().getKitManager().getKits());

			} else if (args[0].equalsIgnoreCase("unlinkkit")) {
				list.addAll(TNTRun.getInstance().amanager.getArenaByName(args[1]).getStructureManager().getLinkedKits());
			}
		}
		for (String s : list) {
			if (s.startsWith(args[args.length - 1])) {
				auto.add(s);
			}
		}

		return auto.isEmpty() ? list : auto;
	}

}
