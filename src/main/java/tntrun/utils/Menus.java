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
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.google.common.base.Enums;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class Menus {

	private final TNTRun plugin;
	private int keyPos;

	public Menus(TNTRun plugin) {
		this.plugin = plugin;
	}

	public void buildJoinMenu(Player player) {
		TreeMap<String, Arena> arenas = getDisplayArenas();
		int size = getInventorySize(arenas.size());
		Inventory inv = Bukkit.createInventory(player, size, FormattingCodesParser.parseFormattingCodes(Messages.menutitle));

		keyPos = 9;
		//TODO provide permanent fix for > 28 arenas
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

	public void buildConfigMenu(Player player, Arena arena) {
		final int size = 36;
		Inventory inv = Bukkit.createInventory(player, size, "TNTRun setup - " + arena.getArenaName());

		//enable/disable
		inv.setItem(4, createItem(Material.RED_WOOL, 4, arena));

		//setlobby
		inv.setItem(10, createItem(Material.LAPIS_LAZULI, 10, arena));

		//setbounds
		inv.setItem(11, createItem(Material.ARROW, 11, arena));

		//setloselevel
		inv.setItem(12, createItem(Material.BONE, 12, arena));

		//setspawn
		inv.setItem(14, createItem(Material.APPLE, 14, arena));

		//setspectate
		inv.setItem(15, createItem(Material.NETHER_STAR, 15, arena));

		//setteleport
		inv.setItem(16, createItem(Material.ENDER_PEARL, 16, arena));

		//setdamage
		inv.setItem(19, createItem(Material.BLAZE_ROD, 19, arena));

		//setminplayers
		inv.setItem(20, createItem(Material.REDSTONE, 20, arena));

		//setmaxplayers
		inv.setItem(21, createItem(Material.GLOWSTONE_DUST, 21, arena));

		//create sign
		inv.setItem(23, createItem(Material.OAK_SIGN, 23, arena));

		//finish
		inv.setItem(25, createItem(Material.DIAMOND, 25, arena));

		fillEmptySlots(inv, size);
		player.openInventory(inv);
	}

	private ItemStack createItem(Material material, int slot, Arena arena) {
		String done = ChatColor.GREEN + "Complete";
		String todo = ChatColor.RED + "Not set";
		String status = ChatColor.GOLD + "Status: ";
		List<String> lores = new ArrayList<>();
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		switch (slot) {
			case 4:
				if (arena.getStatusManager().isArenaEnabled()) {
					is.setType(Material.LIME_WOOL);
				}
				im.setDisplayName(ChatColor.GREEN + "Set arena status");
				lores.add(ChatColor.GRAY + "Click to Enable or Disable the arena.");
				lores.add(status + ChatColor.WHITE + (arena.getStatusManager().isArenaEnabled() ? "Enabled" : "Disabled"));
				break;
			case 10:
				im.setDisplayName(ChatColor.GREEN + "Set Lobby at your current location");
				lores.add(ChatColor.GRAY + "This is the lobby players will return to after the game.");
				lores.add(status + ChatColor.WHITE + (plugin.getGlobalLobby().isLobbyLocationSet() ? done : todo));
				break;
			case 11:
				im.setDisplayName(ChatColor.GREEN + "Set arena bounds");
				lores.add(ChatColor.GRAY + "Set the corner points of a cuboid which completely encloses the arena.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().isArenaBoundsSet() ? done : todo));
				break;
			case 12:
				im.setDisplayName(ChatColor.GREEN + "Set lose level");
				lores.add(ChatColor.GRAY + "Set the point at which players lose to your current Y location.");
				lores.add(ChatColor.GRAY + "You must be within the arena bounds to set the lose level.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().getLoseLevel().isConfigured() ? done : todo));
				break;
			case 14:
				im.setDisplayName(ChatColor.GREEN + "Set arena spawn point");
				lores.add(ChatColor.GRAY + "Set the arena spawn point to your current location.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().isSpawnpointSet() ? done : todo));
				break;
			case 15:
				im.setDisplayName(ChatColor.GREEN + "Set spectator spawn point");
				lores.add(ChatColor.GRAY + "Set the spectator spawn point to your current location.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().isSpectatorSpawnSet() ? done : todo));
				break;
			case 16:
				im.setDisplayName(ChatColor.GREEN + "Set teleport location");
				lores.add(ChatColor.GRAY + "Set the teleport destination when the game ends.");
				lores.add(ChatColor.GRAY + "Click to toggle between LOBBY and PREVIOUS location.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().getTeleportDestination()));
				break;
			case 19:
				im.setDisplayName(ChatColor.GREEN + "Set damage (PVP)");
				lores.add(ChatColor.GRAY + "Enable or disable PVP in the arena by setting the damage.");
				lores.add(ChatColor.GRAY + "Click to toggle between YES, NO and ZERO.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().getDamageEnabled()));
				break;
			case 20:
				im.setDisplayName(ChatColor.GREEN + "Set the minimum number of players");
				lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().getMinPlayers()));
				is.setAmount(arena.getStructureManager().getMinPlayers());
				break;
			case 21:
				im.setDisplayName(ChatColor.GREEN + "Set the maximum number of players");
				lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().getMaxPlayers()));
				is.setAmount(arena.getStructureManager().getMaxPlayers());
				break;
			case 23:
				im.setDisplayName(ChatColor.GREEN + "Create a join sign");
				lores.add(ChatColor.GRAY + "Target a sign and click to create a join sign");
				break;
			case 24:
			case 25:
				im.setDisplayName(ChatColor.GREEN + "Finish configuring the arena");
				lores.add(ChatColor.GRAY + "Save the settings and enable the arena.");
				lores.add(status + ChatColor.WHITE + (arena.getStructureManager().isArenaFinished() ? done : todo));
		}

		im.setLore(lores);
		is.setItemMeta(im);
		return is;
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
