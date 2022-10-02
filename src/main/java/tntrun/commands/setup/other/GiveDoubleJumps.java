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
package tntrun.commands.setup.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class GiveDoubleJumps implements CommandHandlerInterface {

	private TNTRun plugin;

	public GiveDoubleJumps(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getPlayerArena(args[0]);
		if (arena == null) {
			Messages.sendMessage(player, "&7 " + args[0] + "&c is not in a TNTRun arena");
			return false;
		}
		if (!Utils.isNumber(args[1]) || Integer.parseInt(args[1]) <= 0) {
			Messages.sendMessage(player, "&c DoubleJumps must be a positive integer");
			return false;
		}
		arena.getPlayerHandler().incrementDoubleJumps(args[0], Integer.parseInt(args[1]));
		Messages.sendMessage(player, "&7 " + args[1] + " doublejumps given to: &6" + args[0]);

		if (!arena.getStatusManager().isArenaStarting() && plugin.getConfig().getBoolean("scoreboard.displaydoublejumps")) {
			if(plugin.getConfig().getBoolean("special.UseScoreboard")) {
				arena.getScoreboardHandler().updateWaitingScoreboard(Bukkit.getPlayer(args[0]));
			}
		}
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 2;
	}

}
