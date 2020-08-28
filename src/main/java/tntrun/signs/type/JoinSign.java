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

package tntrun.signs.type;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class JoinSign implements SignType {

	private TNTRun plugin;

	public JoinSign(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public void handleCreation(SignChangeEvent e) {
		final Arena arena = plugin.amanager.getArenaByName(e.getLine(2));
		if (arena != null) {
			e.setLine(0, plugin.getConfig().getString("signs.prefix").replace("&", "§"));
			e.getPlayer().sendMessage("Sign succesfully created");
			plugin.signEditor.addSign(e.getBlock(), arena.getArenaName());
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable() {
					@Override
					public void run() {
						plugin.signEditor.modifySigns(arena.getArenaName());
					}
				}
			);
		} else {
			e.getPlayer().sendMessage("Arena does not exist");
			e.setCancelled(true);
			e.getBlock().breakNaturally();
		}
	}

	@Override
	public void handleClick(PlayerInteractEvent e) {
		Arena arena = plugin.amanager.getArenaByName(((Sign) e.getClickedBlock().getState()).getLine(2));
		if (arena != null) {
			boolean canJoin = arena.getPlayerHandler().checkJoin(e.getPlayer());
			if (canJoin) {
				arena.getPlayerHandler().spawnPlayer(e.getPlayer(), Messages.playerjoinedtoplayer, Messages.playerjoinedtoothers);
			}
			e.setCancelled(true);
		} else {
			e.getPlayer().sendMessage("Arena does not exist");
			e.getClickedBlock().breakNaturally();
		}
	}

	@Override
	public void handleDestroy(BlockBreakEvent e) {
		Block b = e.getBlock();
		plugin.signEditor.removeSign(b, ((Sign) b.getState()).getLine(2));
		e.getPlayer().sendMessage("Sign succesfully removed");
	}

}
