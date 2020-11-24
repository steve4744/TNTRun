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

import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import com.google.common.base.Enums;

import tntrun.TNTRun;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;

public class SetBarColor implements CommandHandlerInterface {
	
	private TNTRun plugin;
	public SetBarColor(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {

		String colour = args[0].toUpperCase();
		if (colour.equals("RANDOM") || Enums.getIfPresent(BarColor.class, colour).orNull() != null) {
			plugin.getConfig().set("special.BossBarColor", colour);
			plugin.saveConfig();
		} else {
			Messages.sendMessage(player, "&c Invalid bar colour: &6" + args[0]);
			return true;
		}
		Messages.sendMessage(player, "&7 Bar colour set to: &6" + colour);

		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}

}
