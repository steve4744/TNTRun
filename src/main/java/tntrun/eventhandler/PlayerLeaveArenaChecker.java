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

package tntrun.eventhandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class PlayerLeaveArenaChecker implements Listener {

	private TNTRun plugin;

	public PlayerLeaveArenaChecker(TNTRun plugin) {
		this.plugin = plugin;
	}

	// remove player from arena on quit
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		arena.getGameHandler().setPlaces(player.getName());
		arena.getPlayerHandler().leavePlayer(player, "", Messages.playerlefttoothers);
		
		if (arena.getPlayersManager().getPlayersCount() == 0) {
			arena.getGameHandler().stopArena();
		}
	}

	// remove player from arena if he died
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		arena.getGameHandler().setPlaces(player.getName());
		arena.getPlayerHandler().dispatchPlayer(player);
	}

}
