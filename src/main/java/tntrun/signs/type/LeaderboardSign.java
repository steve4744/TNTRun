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

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tntrun.TNTRun;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;

public class LeaderboardSign implements SignType {

	private TNTRun plugin;

	public LeaderboardSign(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public void handleCreation(SignChangeEvent e) {
		if (!plugin.useStats()) {
			Messages.sendMessage(e.getPlayer(), Messages.statsdisabled);
			e.setCancelled(true);
			e.getBlock().breakNaturally();
			return;
		}
		e.setLine(0, FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.prefix")));
		plugin.signEditor.addLeaderboardSign(e.getBlock());
		Messages.sendMessage(e.getPlayer(), Messages.signcreate);
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.signEditor.modifyLeaderboardSign(e.getBlock());
			}
		}.runTask(plugin);
	}

	@Override
	public void handleClick(PlayerInteractEvent e) {
		//TODO refresh sign on click?
	}

	@Override
	public void handleDestroy(BlockBreakEvent e) {
		plugin.signEditor.removeLeaderboardSign(e.getBlock());
		Messages.sendMessage(e.getPlayer(), Messages.signremove);
	}
}
