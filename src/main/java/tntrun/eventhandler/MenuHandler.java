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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tntrun.TNTRun;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;

public class MenuHandler implements Listener {

	private TNTRun plugin;

	public MenuHandler(TNTRun plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onArenaSelect(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		ItemStack is = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		String title = FormattingCodesParser.parseFormattingCodes(Messages.menutitle);

		if (inv == null) {
			return;
		}
		if (!e.getView().getTitle().equals(title)) {
			return;
		}
		if (e.getRawSlot() >= e.getView().getTopInventory().getSize()) {
			e.setCancelled(true);
			return;
		}
		if (is == null) {
			e.setCancelled(true);
			return;
		}
		if (is.getType() != Material.getMaterial(plugin.getConfig().getString("menu.item")) &&
				is.getType() != Material.getMaterial(plugin.getConfig().getString("menu.pvpitem"))) {
			e.setCancelled(true);
			return;
		}
		if (e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			e.setCancelled(true);
			return;
		}		
		e.setCancelled(true);

		ItemMeta im = is.getItemMeta();
		String arenaname = im.getDisplayName();
		String cmd = "tntrun join " + ChatColor.stripColor(arenaname);
	
		Bukkit.dispatchCommand(player, cmd);
		player.closeInventory();
	}

}
