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

package tntrun.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Enums;

import net.md_5.bungee.api.ChatColor;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class JoinMenu {

	private final TNTRun plugin;
	private int keyPos;

	public JoinMenu(TNTRun plugin) {
		this.plugin = plugin;
	}

	public void buildMenu(Player player) {
		ItemStack is = new ItemStack(getMenuItem());
		ItemMeta im = is.getItemMeta();	

		TreeMap<String, Arena> arenas = getDisplayArenas();

		int size = getInventorySize(arenas.size());
		Inventory inv = Bukkit.createInventory(player, size, FormattingCodesParser.parseFormattingCodes(Messages.menutitle));

		keyPos = 9;
		arenas.forEach((arenaname, arena) -> {
			List<String> lores = new ArrayList<>();
			im.setDisplayName(FormattingCodesParser.parseFormattingCodes(Messages.menuarenaname).replace("{ARENA}", arena.getArenaName()));

			lores.add(FormattingCodesParser.parseFormattingCodes(Messages.menutext) + " " + getArenaCount(arena));
			if (arena.getStructureManager().hasFee()) {
				lores.add(FormattingCodesParser.parseFormattingCodes(Messages.menufee.replace("{FEE}", arena.getStructureManager().getArenaCost(arena))));
			}
			im.setLore(lores);
			is.setItemMeta(im);

			// put the arenas in the centre rows of the inventory
			switch (keyPos) {
				case 16 : case 25 : case 34 : case 43 :
					keyPos+=3;
					break;
				default :  keyPos++;
			}
			inv.setItem(keyPos,is);
		});

		fillEmptySlots(inv, size);
		player.openInventory(inv);
	}

	private void fillEmptySlots(Inventory inv, Integer size) {
		ItemStack is = new ItemStack(getPane());
		if (is.getType() == Material.AIR) {
			return;
		}
		for (int i = 0; i < size; i++) {
			if (inv.getItem(i) == null) {
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.RED + "");
				is.setItemMeta(im);
				inv.setItem(i, is);
			}
		}
	}

	private Material getPane() {
		String colour = plugin.getConfig().getString("menu.panecolor", "LIGHT_BLUE").toUpperCase();
		if (colour == "NONE" || colour == "AIR" || Enums.getIfPresent(DyeColor.class, colour).orNull() == null) {
			return Material.AIR;
		}
		return Material.getMaterial(colour + "_STAINED_GLASS_PANE");
	}

	private Material getMenuItem() {
		String item = plugin.getConfig().getString("menu.item", "TNT").toUpperCase();
		return Material.getMaterial(item) != null ? Material.getMaterial(item) : Material.TNT;
	}

	private String getArenaCount(Arena arena) {
		int maxPlayers = arena.getStructureManager().getMaxPlayers();
		int players = arena.getPlayersManager().getPlayersCount();
		return players + " / " + maxPlayers;
	}

	public void autoJoin(Player player) {
		if (plugin.amanager.getPlayerArena(player.getName()) != null) {
			Messages.sendMessage(player, Messages.trprefix + Messages.arenajoined);
			return;
		}

		Arena autoArena = getAutoArena(player);
		if (autoArena == null) {
			Messages.sendMessage(player, Messages.trprefix + Messages.noarenas);
			return;
		}

		autoArena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoplayer, Messages.playerjoinedtoothers);
	}

	/**
	 * Select the arena to auto join. This will be the arena with the most players waiting to start.
	 * @param player
	 * @return arena
	 */
	private Arena getAutoArena(Player player) {
		Arena autoarena = null;
		int playercount = -1;
		for (Arena arena : plugin.amanager.getArenas()) {
			if (arena.getPlayerHandler().checkJoin(player, true)) {
				if (arena.getPlayersManager().getPlayersCount() > playercount) {
					autoarena = arena;
					playercount = arena.getPlayersManager().getPlayersCount();
				}
			}
		}
		return autoarena;
	}

	/**
	 * Get the list of arenas, by default excluding disabled arenas, in alphabetical order.
	 * @return Sorted map of arenas
	 */
	private TreeMap<String, Arena> getDisplayArenas() {
		TreeMap<String, Arena> arenas = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Arena arena : plugin.amanager.getArenas()) {
			if (!arena.getStatusManager().isArenaEnabled() && !plugin.getConfig().getBoolean("menu.includedisabled")) {
				continue;
			}
			arenas.put(arena.getArenaName(), arena);
		}
		return arenas;
	}

	private int getInventorySize(int size) {
		int invsize = 0;
		if (size < 8) {
			invsize = 27;
		} else if (size < 15) {
			invsize = 36;
		} else if (size < 22) {
			invsize = 45;
		} else if (size < 29) {
			invsize = 54;
		}
		return invsize;
	}
}
