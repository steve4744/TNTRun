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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
		TreeMap<String, Arena> arenas = getDisplayArenas();
		int size = getInventorySize(arenas.size());
		Inventory inv = Bukkit.createInventory(player, size, FormattingCodesParser.parseFormattingCodes(Messages.menutitle));

		keyPos = 9;
		//TODO provide permanent fix for > 28 arenas
		//arenas.forEach((arenaname, arena) -> {
		arenas.entrySet().stream().limit(28).forEach(e -> {
			Arena arena = e.getValue();
			boolean isPvp = !arena.getStructureManager().getDamageEnabled().toString().equalsIgnoreCase("no");
			List<String> lores = new ArrayList<>();
			ItemStack is = new ItemStack(getMenuItem(isPvp));
			ItemMeta im = is.getItemMeta();

			im.setDisplayName(FormattingCodesParser.parseFormattingCodes(Messages.menuarenaname).replace("{ARENA}", arena.getArenaName()));

			lores.add(FormattingCodesParser.parseFormattingCodes(Messages.menutext)
					.replace("{PS}", String.valueOf(arena.getPlayersManager().getPlayersCount()))
					.replace("{MPS}", String.valueOf(arena.getStructureManager().getMaxPlayers())));

			if (arena.getStructureManager().hasFee()) {
				lores.add(FormattingCodesParser.parseFormattingCodes(Messages.menufee.replace("{FEE}", arena.getStructureManager().getArenaCost())));
			}

			if (isPvp && Messages.menupvp.length() > 0) {
				lores.add(FormattingCodesParser.parseFormattingCodes(Messages.menupvp));
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

	private Material getMenuItem(boolean pvpEnabled) {
		String path = pvpEnabled ? "menu.pvpitem" : "menu.item";
		String item = plugin.getConfig().getString(path, "TNT").toUpperCase();

		return Material.getMaterial(item) != null ? Material.getMaterial(item) : Material.TNT;
	}

	public void autoJoin(Player player, String type) {
		if (plugin.amanager.getPlayerArena(player.getName()) != null) {
			Messages.sendMessage(player, Messages.arenajoined);
			return;
		}

		Arena autoArena = getAutoArena(player, type);
		if (autoArena == null) {
			Messages.sendMessage(player, Messages.noarenas);
			return;
		}

		if (autoArena.getPlayerHandler().processFee(player, false)) {
			autoArena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoothers);
		}
	}

	/**
	 * Select the arena to auto join. This will be the arena with the most players waiting to start.
	 * If all arenas are empty, then an arena is selected at random.
	 *
	 * @param player
	 * @return arena
	 */
	private Arena getAutoArena(Player player, String type) {
		Collection<Arena> arenas = new HashSet<>();
		Arena autoarena = null;
		int playercount = -1;

		switch (type.toLowerCase()) {
			case "pvp":
				arenas = plugin.amanager.getPvpArenas();
				break;
			case "nopvp":
				arenas = plugin.amanager.getNonPvpArenas();
				break;
			default:
				arenas = plugin.amanager.getArenas();
		}

		List<Arena> arenalist = new ArrayList<>(arenas);
		Collections.shuffle(arenalist);
		for (Arena arena : arenalist) {
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
		//} else if (size < 29) {
		} else {
			invsize = 54;
		}
		return invsize;
	}
}
