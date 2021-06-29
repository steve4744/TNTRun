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
		if (!e.getView().getTitle().equals(plugin.shop.getInvname())) {
			return;
		}
		e.setCancelled(true);
		if (e.getRawSlot() == plugin.shop.getInvsize() -1) {
			return;
		}
		Player p = (Player)e.getWhoClicked();
		if (e.getSlot() == e.getRawSlot() && e.getCurrentItem() != null) {
			ItemStack current = e.getCurrentItem();
			if (current.hasItemMeta() && current.getItemMeta().hasDisplayName()) {
				FileConfiguration cfg = plugin.shop.getShopFiles().getShopConfiguration();
				int kit = plugin.shop.getItemSlot().get(e.getSlot());
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
				if (plugin.shop.validatePurchase(p, kit, title)) {
					plugin.shop.giveItem(e.getSlot(), p, title);
				}
			}
		}
	}

}
