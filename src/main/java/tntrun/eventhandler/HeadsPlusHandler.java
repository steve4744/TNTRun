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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.thatsmusic99.headsplus.api.events.HeadPurchaseEvent;
import tntrun.TNTRun;
import tntrun.arena.Arena;

public class HeadsPlusHandler implements Listener {
	
	private TNTRun plugin;
	
	public HeadsPlusHandler(TNTRun plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onHeadPurchase(HeadPurchaseEvent e) {
		if (e.isCancelled()) {
			return;
		}
		final Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		player.closeInventory();
		ItemStack itemStack = e.getItemStack();

		// need to delay equipping the head as the event is fired before the head is added to the inventory
		new BukkitRunnable() {
			@Override
			public void run() {
				for (int i = 0; i < 9; i++) {
					if (player.getInventory().getItem(i) == null) {
						continue;
					}
					if (player.getInventory().getItem(i).getType() == itemStack.getType()) {
						player.getInventory().setHelmet(player.getInventory().getItem(i));
						player.getInventory().setItem(i, null);
						break;
					}
				}
				player.updateInventory();
			}
		}.runTaskLater(plugin, 2L);
	}

}
