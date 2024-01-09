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
import tntrun.utils.Utils;

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

		inv.setMaxStackSize(256); // allow min and max players to go above 64
		Player player = (Player) e.getWhoClicked();
		boolean leftclick = e.getClick().isLeftClick();
		int page = Character.getNumericValue(title.charAt(title.indexOf("/") - 1));
		int slot = e.getRawSlot();
		String cmd = "trsetup ";
		switch (slot) {
			case 4:
				String status = arena.getStatusManager().isArenaEnabled() ? "disable " : "enable ";
				Bukkit.dispatchCommand(player, "trsetup " + status + arenaname);
				break;
			case 10:
				if (page == 1) {
					cmd += "setlobby";
				} else {
					int amount = leftclick ? (arena.getStructureManager().getCountdown() + 5) : (arena.getStructureManager().getCountdown() - 5);
					cmd += "setcountdown " + arenaname + " " + amount;
				}
				Bukkit.dispatchCommand(player, cmd);
				break;
			case 11:
				if (page == 1) {
					cmd += "setarena " + arenaname;
				} else {
					int amount = leftclick ? (arena.getStructureManager().getTimeLimit() + 10) : (arena.getStructureManager().getTimeLimit() - 10);
					cmd += "settimelimit " + arenaname + " " + amount;
				}
				Bukkit.dispatchCommand(player, cmd);
				break;
			case 12:
				if (page == 1) {
					cmd += "setloselevel " + arenaname;
					Bukkit.dispatchCommand(player, cmd);
				} else {
					if (arena.getStatusManager().isArenaEnabled()) {
						Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", arenaname));
						return;
					}
					int amount = leftclick ? (arena.getStructureManager().getStartVisibleCountdown() + 1) : (arena.getStructureManager().getStartVisibleCountdown() - 1);
					arena.getStructureManager().setStartVisibleCountdown(amount);
				}
				break;
			case 14:
				if (page == 1) {
					Bukkit.dispatchCommand(player, "trsetup setspawn " + arenaname);
				} else {
					if (arena.getStatusManager().isArenaEnabled()) {
						Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", arenaname));
						return;
					}
					arena.getStructureManager().toggleTestMode();
				}
				break;
			case 15:
				if (page == 1) {
					cmd += "setspectate " + arenaname;
				} else {
					int delay = leftclick ? (arena.getStructureManager().getGameLevelDestroyDelay() + 1) : (arena.getStructureManager().getGameLevelDestroyDelay() - 1);
					cmd += "setgameleveldestroydelay " + arenaname + " " + delay;
				}
				Bukkit.dispatchCommand(player, cmd);
				break;
			case 16:
				if (page == 1) {
					String dest = arena.getStructureManager().getTeleportDestination().toString();
					dest = dest.equalsIgnoreCase("LOBBY") ? " PREVIOUS" : " LOBBY";
					cmd += "setteleport " + arenaname + dest;
				} else {
					int delay = leftclick ? (arena.getStructureManager().getRegenerationDelay() + 1) : (arena.getStructureManager().getRegenerationDelay() - 1);
					cmd += "setregenerationdelay " + arenaname + " " + delay;
				}
				Bukkit.dispatchCommand(player, cmd);
				break;
			case 19:
				if (page == 1) {
					int minplayers = leftclick ? (arena.getStructureManager().getMinPlayers() + 1) : (arena.getStructureManager().getMinPlayers() - 1);
					cmd += "setminplayers " + arenaname + " " + minplayers;
				} else {
					String damage = arena.getStructureManager().getDamageEnabled().toString();
					if (damage.equalsIgnoreCase("NO")) {
						damage = " YES";
					} else if (damage.equalsIgnoreCase("YES")) {
						damage = " ZERO";
					} else {
						damage = " NO";
					}
					cmd += "setdamage " + arenaname + damage;
				}
				Bukkit.dispatchCommand(player, cmd);
				break;
			case 20:
				if (page == 1) {
					int maxplayers = leftclick ? (arena.getStructureManager().getMaxPlayers() + 1) : (arena.getStructureManager().getMaxPlayers() - 1);
					cmd += "setmaxplayers " + arenaname + " " + maxplayers;
					Bukkit.dispatchCommand(player, cmd);
				} else {
					if (arena.getStatusManager().isArenaEnabled()) {
						Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", arenaname));
						return;
					}
					arena.getStructureManager().togglePunchDamage();
				}
				break;
			case 21:
				if (page == 1) {
					double percent = leftclick ? (arena.getStructureManager().getVotePercent() + 0.05) : (arena.getStructureManager().getVotePercent() - 0.05);
					cmd += "setvotepercent " + arenaname + " " + Utils.getDecimalFormat(String.valueOf(percent));
				} else {
					String kits = arena.getStructureManager().isKitsEnabled() ? "disablekits " : "enablekits ";
					cmd += kits + arenaname;
				}
				Bukkit.dispatchCommand(player, cmd);
				break;
			case 23:
				if (page == 1) {
					Block block = player.getTargetBlock(null, 5);
					if (block.getState() instanceof Sign) {
						plugin.getSignEditor().createJoinSign(block, arenaname);
						Messages.sendMessage(player, Messages.signcreate);
					} else {
						Messages.sendMessage(player, Messages.signfail);
					}
					player.closeInventory();
				} else {
					if (arena.getStatusManager().isArenaEnabled()) {
						Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", arenaname));
						return;
					}
					arena.getStructureManager().toggleArenaStats();
				}
				break;
			case 24:
				if (page == 2) {
					if (arena.getStatusManager().isArenaEnabled()) {
						Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", arenaname));
						return;
					}
					int min = leftclick ? (arena.getStructureManager().getStatsMinPlayers() + 1) : (arena.getStructureManager().getStatsMinPlayers() - 1);
					arena.getStructureManager().setStatsMinPlayers(min);
				}
				break;
			case 25:
				if (page == 1) {
					Bukkit.dispatchCommand(player, "trsetup finish " + arenaname);
				} else {
					if (arena.getStatusManager().isArenaEnabled()) {
						Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", arenaname));
						return;
					}
					arena.getStructureManager().toggleShopEnabled();
				}
				break;
			case 27:
				if (page == 2) {
					player.closeInventory();
					plugin.getMenus().buildConfigMenu(player, arena, 1);
				}
				return;
			case 31:
				player.closeInventory();
				return;
			case 35:
				if (page == 1) {
					player.closeInventory();
					plugin.getMenus().buildConfigMenu(player, arena, 2);
				}
				return;
			default:
				return;
		}
		plugin.getMenus().updateConfigItem(inv, slot, arena, page);
		if (slot == 19 && page == 1) {
			// refresh vote percent if min players changes
			plugin.getMenus().updateConfigItem(inv, 21, arena, page);
		}
		player.updateInventory();
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
