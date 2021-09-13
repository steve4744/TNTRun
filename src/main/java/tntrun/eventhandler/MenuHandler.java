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
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;

public class MenuHandler implements Listener {

	private TNTRun plugin;

	public MenuHandler(TNTRun plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onTrackerSelect(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (inv == null) {
			return;
		}
		String title = FormattingCodesParser.parseFormattingCodes(Messages.menutracker);
		if (!e.getView().getTitle().equals(title)) {
			return;
		}
		if (!isValidClick(e)) {
			return;
		}
		ItemStack is = e.getCurrentItem();
		if (is == null || is.getType() != Material.PLAYER_HEAD) {
			return;
		}

		Player player = (Player) e.getWhoClicked();
		String target = is.getItemMeta().getDisplayName();
		Player targetPlayer = Bukkit.getPlayer(target);
		Arena arena = plugin.amanager.getPlayerArena(player.getName());

		if (targetPlayer == null || !arena.getPlayersManager().getPlayers().contains(targetPlayer)) {
			Messages.sendMessage(player, Messages.playernotplaying);
			return;
		}

		player.teleport(targetPlayer.getLocation());
		player.closeInventory();
	}

	@EventHandler
	public void onArenaSelect(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (inv == null) {
			return;
		}
		String title = FormattingCodesParser.parseFormattingCodes(Messages.menutitle);
		if (!e.getView().getTitle().equals(title)) {
			return;
		}
		if (!isValidClick(e)) {
			return;
		}
		ItemStack is = e.getCurrentItem();
		if (is == null) {
			return;
		}
		if (is.getType() != Material.getMaterial(plugin.getConfig().getString("menu.item")) &&
				is.getType() != Material.getMaterial(plugin.getConfig().getString("menu.pvpitem"))) {
			return;
		}

		Player player = (Player) e.getWhoClicked();
		String arenaname = is.getItemMeta().getDisplayName();
		String cmd = "tntrun join " + ChatColor.stripColor(arenaname);
	
		Bukkit.dispatchCommand(player, cmd);
		player.closeInventory();
	}

	@EventHandler
	public void onItemSelect(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (inv == null) {
			return;
		}
		String title = e.getView().getTitle();
		if (!title.startsWith("TNTRun setup")) {
			return;
		}
		if (!isValidClick(e)) {
			return;
		}
		String arenaname = ChatColor.stripColor(title.substring(title.lastIndexOf(" ") + 1));
		Arena arena = plugin.amanager.getArenaByName(arenaname);
		if (arena == null) {
			return;
		}
		ItemStack is = e.getCurrentItem();
		if (is == null) {
			return;
		}

		Player player = (Player) e.getWhoClicked();
		boolean leftclick = e.getClick().isLeftClick();
		int slot = e.getRawSlot();
		switch (slot) {
			case 4:
				String status = arena.getStatusManager().isArenaEnabled() ? "Enabled" : "Disabled";
				status = status.equalsIgnoreCase("Enabled") ? "disable " : "enable ";
				Bukkit.dispatchCommand(player, "trsetup " + status + arenaname);
				plugin.getMenus().updateConfigItem(inv, slot, arena);
				player.updateInventory();
				break;
			case 10:
				Bukkit.dispatchCommand(player, "trsetup setlobby");
				player.closeInventory();
				break;
			case 11:
				Bukkit.dispatchCommand(player, "trsetup setarena " + arenaname);
				player.closeInventory();
				break;
			case 12:
				Bukkit.dispatchCommand(player, "trsetup setloselevel " + arenaname);
				player.closeInventory();
				break;
			case 14:
				Bukkit.dispatchCommand(player, "trsetup setspawn " + arenaname);
				player.closeInventory();
				break;
			case 15:
				Bukkit.dispatchCommand(player, "trsetup setspectate " + arenaname);
				player.closeInventory();
				break;
			case 16:
				String dest = arena.getStructureManager().getTeleportDestination().toString();
				dest = dest.equalsIgnoreCase("LOBBY") ? " PREVIOUS" : " LOBBY";
				Bukkit.dispatchCommand(player, "trsetup setteleport " + arenaname + dest);
				plugin.getMenus().updateConfigItem(inv, slot, arena);
				player.updateInventory();
				break;
			case 19:
				String damage = arena.getStructureManager().getDamageEnabled().toString();
				if (damage.equalsIgnoreCase("NO")) {
					damage = " YES";
				} else if (damage.equalsIgnoreCase("YES")) {
					damage = " ZERO";
				} else {
					damage = " NO";
				}
				Bukkit.dispatchCommand(player, "trsetup setdamage " + arenaname + damage);
				plugin.getMenus().updateConfigItem(inv, slot, arena);
				player.updateInventory();
				break;
			case 20:
				int minplayers = leftclick ? (is.getAmount() + 1) : (is.getAmount() - 1);
				Bukkit.dispatchCommand(player, "trsetup setminplayers " + arenaname + " " + minplayers);
				plugin.getMenus().updateConfigItem(inv, slot, arena);
				player.updateInventory();
				break;
			case 21:
				int maxplayers = leftclick ? (is.getAmount() + 1) : (is.getAmount() - 1);
				Bukkit.dispatchCommand(player, "trsetup setmaxplayers " + arenaname + " " + maxplayers);
				plugin.getMenus().updateConfigItem(inv, slot, arena);
				player.updateInventory();
				break;
			case 23:
				Block block = player.getTargetBlock(null, 5);
				if (block.getState() instanceof Sign) {
					plugin.getSignEditor().createJoinSign(block, arenaname);
					Messages.sendMessage(player, Messages.signcreate);
				} else {
					Messages.sendMessage(player, Messages.signfail);
				}
				player.closeInventory();
				break;
			case 25:
				Bukkit.dispatchCommand(player, "trsetup finish " + arenaname);
				player.closeInventory();
		}
	}

	private boolean isValidClick(InventoryClickEvent e) {
		e.setCancelled(true);
		if (e.getRawSlot() >= e.getView().getTopInventory().getSize()) {
			return false;
		}
		if (e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			return false;
		}
		return true;
	}
}
