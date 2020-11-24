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

import java.util.List;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;

public class AddToWhitelist implements CommandHandlerInterface {

	private TNTRun plugin;

	public AddToWhitelist(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		List<String> whitelist = plugin.getConfig().getStringList("commandwhitelist");
		String cmd = String.join(" ", args);
		if (whitelist.contains(cmd)) {
			Messages.sendMessage(player, "&7 Command is already whitelisted: &6" + cmd);
			return true;
		}
		whitelist.add(cmd);
		plugin.getConfig().set("commandwhitelist", whitelist);
		plugin.saveConfig();

		Messages.sendMessage(player, "&7 Command added to whitelist: &6" + cmd);
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}

}
