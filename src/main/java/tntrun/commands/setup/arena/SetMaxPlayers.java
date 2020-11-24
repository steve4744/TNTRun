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

package tntrun.commands.setup.arena;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class SetMaxPlayers implements CommandHandlerInterface {

	private TNTRun plugin;
	public SetMaxPlayers(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getArenaByName(args[0]);
		if (arena != null) {
			if (arena.getStatusManager().isArenaEnabled()) {
				Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", args[0]));
				return true;
			}
			if (!Utils.isNumber(args[1]) || Integer.parseInt(args[1]) <= 0) {
				Messages.sendMessage(player, "&c MaxPlayers must be a positive integer");
				return true;
			}
			if (Integer.parseInt(args[1]) >= arena.getStructureManager().getMinPlayers()) {
				arena.getStructureManager().setMaxPlayers(Integer.parseInt(args[1]));
				Messages.sendMessage(player, "&7 Arena &6" + args[0] + "&7 MaxPlayers set to &6" + args[1]);
			} else {
				Messages.sendMessage(player, "&c MaxPlayers cannot be less than MinPlayers");
			}
		} else {
			Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[0]));
		}
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 2;
	}

}