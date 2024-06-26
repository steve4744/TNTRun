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
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;

public class ResetStats implements CommandHandlerInterface {

	private TNTRun plugin;

	public ResetStats(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		if (!plugin.useStats()) {
			Messages.sendMessage(player, Messages.statsdisabled);
			return false;
		}
		@SuppressWarnings("deprecation")
		String uuid = plugin.useUuid() ? Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString() : args[0];
		if (!plugin.getStats().hasStats(uuid)) {
			Messages.sendMessage(player, "&7 No stats exist for player: &c" + args[0]);
			return false;
		}
		plugin.getStats().resetStats(uuid);
		Messages.sendMessage(player, "&7 Stats reset for player: &6" + args[0]);
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}
}
