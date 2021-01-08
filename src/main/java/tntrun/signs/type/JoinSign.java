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
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;

public class JoinSign implements SignType {

	private TNTRun plugin;

	public JoinSign(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public void handleCreation(SignChangeEvent e) {
		String arenaname = ChatColor.stripColor(FormattingCodesParser.parseFormattingCodes(e.getLine(2)));
		final Arena arena = plugin.amanager.getArenaByName(arenaname);
		if (arena != null) {
			e.setLine(0, FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.prefix")));
			e.setLine(1, FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.join")));
			e.setLine(2, FormattingCodesParser.parseFormattingCodes(e.getLine(2)));
			Messages.sendMessage(e.getPlayer(), Messages.signcreate);
			plugin.signEditor.addSign(e.getBlock(), arenaname);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable() {
					@Override
					public void run() {
						plugin.signEditor.modifySigns(arenaname);
					}
				}
			);
		} else {
			Messages.sendMessage(e.getPlayer(), Messages.arenanotexist.replace("{ARENA}", arenaname));
			e.setCancelled(true);
			e.getBlock().breakNaturally();
		}
	}

	@Override
	public void handleClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getArenaByName(ChatColor.stripColor(((Sign) e.getClickedBlock().getState()).getLine(2)));
		if (arena != null) {
			if (arena.getPlayerHandler().checkJoin(player)) {
				arena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoothers);
				//attempt to cache the sign location as a fix for lost signinfo
				plugin.signEditor.addSign(e.getClickedBlock(), arena.getArenaName());
			}
			e.setCancelled(true);
		} else {
			Messages.sendMessage(player, Messages.arenanotexist);
			e.getClickedBlock().breakNaturally();
		}
	}

	@Override
	public void handleDestroy(BlockBreakEvent e) {
		Block b = e.getBlock();
		plugin.signEditor.removeSign(b, ChatColor.stripColor(((Sign) b.getState()).getLine(2)));
		Messages.sendMessage(e.getPlayer(), Messages.signremove);
	}

}
