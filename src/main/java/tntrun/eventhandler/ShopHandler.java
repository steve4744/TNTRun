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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class ShopHandler implements Listener {

private TNTRun plugin;
	
	public ShopHandler(TNTRun plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(plugin.getShop().getInvname())) {
			return;
		}
		e.setCancelled(true);
		if (e.getRawSlot() == plugin.getShop().getInvsize() -1) {
			return;
		}
		Player p = (Player)e.getWhoClicked();
		if (e.getSlot() == e.getRawSlot() && e.getCurrentItem() != null) {
			ItemStack current = e.getCurrentItem();
			if (current.hasItemMeta() && current.getItemMeta().hasDisplayName()) {
				FileConfiguration cfg = plugin.getShop().getShopFiles().getShopConfiguration();
				int kit = plugin.getShop().getItemSlot().get(e.getSlot());
				if (cfg.getInt(kit + ".items.1.amount") <= 0) {
					Messages.sendMessage(p, Messages.shopnostock);
					return;
				}

				String permission = cfg.getString(kit + ".permission");
				if (!p.hasPermission(permission) && !p.hasPermission("tntrun.shop")) {
					p.closeInventory();
					Messages.sendMessage(p, Messages.nopermission);
					plugin.getSound().ITEM_SELECT(p);
					return;
				}

				String title = current.getItemMeta().getDisplayName();
				if (plugin.getShop().validatePurchase(p, kit, title)) {
					plugin.getShop().giveItem(e.getSlot(), p, title);
				}
			}
		}
	}

}
