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
import tntrun.utils.Utils;

public class SetSpawn implements CommandHandlerInterface {

	private TNTRun plugin;
	public SetSpawn(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getArenaByName(args[0]);
		if (arena != null) {
			if (arena.getStatusManager().isArenaEnabled()) {
				player.sendMessage("§7[§6TNTRun§7] §cPlease disable arena §6/trsetup disable " + args[0]);
				return true;
			}
			if (!arena.getStructureManager().isArenaBoundsSet()) {
				player.sendMessage("§7[§6TNTRun§7] §cArena bounds not set");
				return true;
			}
			if (arena.getStructureManager().setSpawnPoint(player.getLocation())) {
				player.sendMessage("§7[§6TNTRun§7] §7Arena §6" + args[0] + "§7 SpawnPoint set to §6X: §7" + Math.round(player.getLocation().getX()) + " §6Y: §7" + Math.round(player.getLocation().getY()) + " §6Z: §7" + Math.round(player.getLocation().getZ()));
			} else {
				player.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[0] + "§c SpawnPoint must be in arena bounds");
			}
			if (Utils.debug()) {
				plugin.getLogger().info("Arena " + arena.getArenaName() + " spawnpoint: " + player.getLocation().toVector().toString());
				plugin.getLogger().info("Min point: " + arena.getStructureManager().getP1().toString());
				plugin.getLogger().info("Max point: " + arena.getStructureManager().getP2().toString());
			}
		} else {
			player.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[0] + "§c doesn't exist");
		}
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}

}