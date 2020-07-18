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
import tntrun.arena.Arena;

public class SetupTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}

		if (!sender.hasPermission("tntrun.setup")) {
			return null;
		}

		List<String> list = new ArrayList<String>();
		List<String> auto = new ArrayList<String>();
		List<String> arenacommands = new ArrayList<String>();

		arenacommands.add("setarena");
		arenacommands.add("setloselevel");
		arenacommands.add("setspawn");
		arenacommands.add("addspawn");
		arenacommands.add("setspectate");
		arenacommands.add("finish");
		arenacommands.add("delspectate");
		arenacommands.add("deletespawnpoints");
		arenacommands.add("setgameleveldestroydelay");
		arenacommands.add("setregenerationdelay");
		arenacommands.add("setmaxplayers");
		arenacommands.add("setminplayers");
		arenacommands.add("setvotepercent");
		arenacommands.add("settimelimit");
		arenacommands.add("setcountdown");
		arenacommands.add("setmoneyreward");
		arenacommands.add("setteleport");
		arenacommands.add("enable");
		arenacommands.add("disable");
		arenacommands.add("delete");
		arenacommands.add("setreward");
		arenacommands.add("enablekits");
		arenacommands.add("disablekits");
		arenacommands.add("setdamage");
		arenacommands.add("setfee");
		arenacommands.add("setcurrency");

		if (args.length == 1) {
			list.add("help");
			list.add("create");  //because it doesn't take an existing arena name
			list.add("setlobby");
			list.add("reloadbars");
			list.add("reloadtitles");
			list.add("reloadmsg");
			list.add("reloadconfig");
			list.add("setbarcolor");
			list.add("addkit");
			list.add("deletekit");
			list.add("deletelobby");
			list.add("setp1");
			list.add("setp2");
			list.add("clear");

			list.addAll(arenacommands);

		} else if (args.length == 2) {
			if (arenacommands.contains(args[0])) {
				for (Arena arena : TNTRun.getInstance().amanager.getArenas()) {
					list.add(arena.getArenaName());
				}
			} else if (args[0].equalsIgnoreCase("setbarcolor") || args[0].equalsIgnoreCase("setbarcolour")) {
				for (BarColor color : Arrays.asList(BarColor.class.getEnumConstants())) {
					list.add(color.toString());
				}
				list.add("RANDOM");
			} else if (args[0].equalsIgnoreCase("deletekit")) {
				list.addAll(TNTRun.getInstance().kitmanager.getKits());	
			}

		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("setteleport")) {
				list.add("lobby");
				list.add("previous");
			} else if (args[0].equalsIgnoreCase("setdamage")) {
				list.add("yes");
				list.add("no");
				list.add("zero");
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
