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

package tntrun.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.base.Enums;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;

public class Menus {

	private final TNTRun plugin;
	private int keyPos;

	public Menus(TNTRun plugin) {
		this.plugin = plugin;
	}

	/**
	 * Create the inventory menu for the player to select an arena to join.
	 *
	 * @param player
	 */
	public void buildJoinMenu(Player player) {
		TreeMap<String, Arena> arenas = getDisplayArenas();
		int size = getJoinMenuSize(arenas.size());
		Inventory inv = Bukkit.createInventory(player, size, FormattingCodesParser.parseFormattingCodes(Messages.menutitle));

		keyPos = 9;
		//TODO provide permanent fix for > 28 arenas
		arenas.entrySet().stream().limit(28).forEach(e -> {
			Arena arena = e.getValue();
			boolean isPvp = arena.getStructureManager().isPvpEnabled();
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

			switch (keyPos) {
				case 16, 25, 34, 43 -> keyPos+=3;
				default             -> keyPos++;
			};
			inv.setItem(keyPos,is);
		});

		fillEmptySlots(inv, size);
		player.openInventory(inv);
	}

	/**
	 * Create the inventory menu for spectators to teleport to players.
	 *
	 * @param player
	 * @param arena
	 */
	public void buildTrackerMenu(Player player, Arena arena) {
		int size = getTrackerMenuSize(arena.getPlayersManager().getPlayersCount());
		Inventory inv = Bukkit.createInventory(player, size, FormattingCodesParser.parseFormattingCodes(Messages.menutracker));

		for (Player p : arena.getPlayersManager().getPlayers()) {
			ItemStack is = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(p.getName());

			SkullMeta skullMeta = (SkullMeta) meta;
			skullMeta.setOwningPlayer(p);
			is.setItemMeta(skullMeta);

			inv.addItem(is);
		}

		fillEmptySlots(inv, size);
		player.openInventory(inv);
	}

	/**
	 * Create the inventory menu for the player to configure the arena.
	 *
	 * @param player
	 * @param arena
	 * @param page number
	 */
	public void buildConfigMenu(Player player, Arena arena, int page) {
		final int size = 36;
		String title = "TNTRun setup {PAGE}/2 - ".replace("{PAGE}", String.valueOf(page)) + arena.getArenaName();

		Inventory inv = Bukkit.createInventory(player, size, title);

		if (page == 1) {
			Stream.of(ConfigMenu.values()).forEach(item -> {
				int slot = item.getSlot();
				inv.setItem(slot, createConfigItem(Material.getMaterial(String.valueOf(item)), slot, arena, page));
			});
		} else {
			Stream.of(ConfigMenu2.values()).forEach(item -> {
				int slot = item.getSlot();
				inv.setItem(slot, createConfigItem(Material.getMaterial(String.valueOf(item)), slot, arena, page));
			});
		}

		fillEmptySlots(inv, size);
		player.openInventory(inv);
	}

	/**
	 * Create each configuration item with a custom display name showing the function of the item,
	 * and lore to give a description of what action the function performs.
	 *
	 * @param material
	 * @param slot
	 * @param arena
	 * @param page number
	 * @return ItemStack
	 */
	private ItemStack createConfigItem(Material material, int slot, Arena arena, int page) {
		String done = "Complete";
		String todo = ChatColor.RED + "Not set";
		String status = ChatColor.GOLD + "Status: " + ChatColor.GREEN;
		List<String> lores = new ArrayList<>();
		boolean showhelp = plugin.getConfig().getBoolean("configmenu.lore", true);

		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();

		switch (slot) {
			case 4:
				if (arena.getStatusManager().isArenaEnabled()) {
					is.setType(Material.LIME_WOOL);
				}
				im.setDisplayName(ChatColor.GREEN + "Set arena status");
				if (showhelp) {
					lores.add(ChatColor.GRAY + "Click to enable or disable the arena.");
				}
				lores.add(status + (arena.getStatusManager().isArenaEnabled() ? "Enabled" : "Disabled"));
				break;
			case 10:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set global lobby");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the TNTRun lobby at your current location.");
						lores.add(ChatColor.GRAY + "This is the lobby players will return to after the game.");
					}
					lores.add(status + (plugin.getGlobalLobby().isLobbyLocationSet() ? done : todo));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set arena countdown");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getCountdown());
				}
				break;
			case 11:
				if (page == 1) {
					im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					im.setDisplayName(ChatColor.GREEN + "Set arena bounds");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the corner points of a cuboid which");
						lores.add(ChatColor.GRAY + "completely encloses the arena.");
					}
					lores.add(status + (arena.getStructureManager().isArenaBoundsSet() ? done : todo));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set arena time limit");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getTimeLimit());
				}
				break;
			case 12:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set lose level");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the point at which players lose to your");
						lores.add(ChatColor.GRAY + "current Y location. You must be within");
						lores.add(ChatColor.GRAY + "the arena bounds to set the lose level.");
					}
					lores.add(status + (arena.getStructureManager().getLoseLevel().isConfigured() ? done : todo));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set start time of visible countdown");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the time at which the countdown is");
						lores.add(ChatColor.GRAY + "displayed continuously on the screen.");
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getStartVisibleCountdown());
				}
				break;
			case 14:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set arena spawn point");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the point that players joining the arena");
						lores.add(ChatColor.GRAY + "will spawn to your current location.");
					}
					lores.add(status + (arena.getStructureManager().isSpawnpointSet() ? done : todo));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set test mode status");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Click to enable or disable test mode.");
					}
					lores.add(status + (arena.getStructureManager().isTestMode() ? "Enabled" : "Disabled"));
				}
				break;
			case 15:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set spectator spawn point");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the point that spectators will spawn");
						lores.add(ChatColor.GRAY + "to your current location.");
					}
					lores.add(status + (arena.getStructureManager().isSpectatorSpawnSet() ? done : todo));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set gameleveldestroydelay");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the time before a block is broken after being");
						lores.add(ChatColor.GRAY + "stepped on by a player (default 8 ticks).");
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getGameLevelDestroyDelay());
				}
				break;
			case 16:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set teleport location");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "When the game ends players will teleport to either");
						lores.add(ChatColor.GRAY + "their previous location or to the lobby.");
						lores.add(ChatColor.GRAY + "Click to toggle between LOBBY and PREVIOUS location.");
					}
					lores.add(status + arena.getStructureManager().getTeleportDestination());
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set regeneration delay");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the time allowed for regeneration before players");
						lores.add(ChatColor.GRAY + "are allowed to re-join the arena (default 60 ticks).");
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getRegenerationDelay());
				}
				break;
			case 19:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set the minimum number of players");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getMinPlayers());
					is.setAmount(arena.getStructureManager().getMinPlayers());
				} else {
					im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					im.setDisplayName(ChatColor.GREEN + "Set damage (PVP)");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Enable or disable PVP in the arena by setting");
						lores.add(ChatColor.GRAY + "the damage indicator.");
						lores.add(ChatColor.GRAY + "Click to toggle between YES, NO and ZERO.");
					}
					lores.add(status + arena.getStructureManager().getDamageEnabled());
				}
				break;
			case 20:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set the maximum number of players");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getMaxPlayers());
					is.setAmount(arena.getStructureManager().getMaxPlayers());
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set punch damage status");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "In a PVP arena, inflicting damage with an");
						lores.add(ChatColor.GRAY + "empty hand (punch) can be disallowed. ");
						lores.add(ChatColor.GRAY + "Click to enable or disable punch damage.");
					}
					lores.add(status + (arena.getStructureManager().isPunchDamage() ? "Enabled" : "Disabled"));
				}
				break;
			case 21:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Set vote percentage");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Determine the votes needed to force-start");
						lores.add(ChatColor.GRAY + "the arena with < the minimum players.");
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getVotePercent() + ChatColor.GOLD +
							"  Votes Required: " + ChatColor.GREEN + arena.getPlayerHandler().getVotesRequired(arena));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set kit status");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Click to enable or disable kits.");
					}
					lores.add(status + (arena.getStructureManager().isKitsEnabled() ? "Enabled" : "Disabled"));
				}
				break;
			case 23:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Create a join sign");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Target a sign and click to create a join sign.");
					}
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set arena stats status");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Click to enable or disable arena stats.");
					}
					lores.add(status + (arena.getStructureManager().isArenaStatsEnabled() ? "Enabled" : "Disabled"));
				}
				break;
			case 24:
				if (page == 2) {
					im.setDisplayName(ChatColor.GREEN + "Set min players for stats");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Set the minimum number of players required");
						lores.add(ChatColor.GRAY + "for stats to be recorded.");
						lores.add(ChatColor.GRAY + "A value of zero means stats is always active.");
						lores.add(ChatColor.GRAY + "Left click to increase, right click to decrease.");
					}
					lores.add(status + arena.getStructureManager().getStatsMinPlayers());
				}
				break;
			case 25:
				if (page == 1) {
					im.setDisplayName(ChatColor.GREEN + "Finish configuring the arena");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Save the settings and enable the arena.");
					}
					lores.add(status + (arena.getStructureManager().isArenaFinished() ? done : todo));
				} else {
					im.setDisplayName(ChatColor.GREEN + "Set shop status");
					if (showhelp) {
						lores.add(ChatColor.GRAY + "Click to enable or disable the shop.");
					}
					lores.add(status + (arena.getStructureManager().isShopEnabled() ? "Enabled" : "Disabled"));
				}
				break;
			case 27:
				im.setDisplayName(ChatColor.GREEN + "<- Back");
				break;
			case 31:
				im.setDisplayName(ChatColor.GREEN + "Exit");
				break;
			case 35:
				im.setDisplayName(ChatColor.GREEN + "Next ->");
		}

		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}

	/**
	 * Update a single configuration item so that its new value is displayed immediately without
	 * the need to rebuild the whole inventory menu.
	 *
	 * @param inv
	 * @param slot
	 * @param arena
	 * @param page number
	 */
	public void updateConfigItem(Inventory inv, int slot, Arena arena, int page) {
		String itemName = (page == 1) ? String.valueOf(ConfigMenu.getName(slot)) : String.valueOf(ConfigMenu2.getName(slot));
		inv.setItem(slot, createConfigItem(Material.getMaterial(itemName), slot, arena, page));
	}

	/**
	 * Fill each unoccupied inventory slot with a coloured glass pane.
	 *
	 * @param inv
	 * @param size
	 */
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
		if (!player.hasPermission("tntrun.autojoin")) {
			Messages.sendMessage(player,  Messages.nopermission);
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

		arenas = switch (type.toLowerCase()) {
			case "pvp" -> {
				yield plugin.amanager.getPvpArenas();
			}
			case "nopvp" -> {
				yield plugin.amanager.getNonPvpArenas();
			}
			default -> {
				yield plugin.amanager.getArenas();
			}
		};

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

	private int getJoinMenuSize(int size) {
		int invsize = 54;
		if (size < 8) {
			invsize = 27;
		} else if (size < 15) {
			invsize = 36;
		} else if (size < 22) {
			invsize = 45;
		}
		return invsize;
	}

	private int getTrackerMenuSize(int size) {
		int invsize = 54;
		if (size < 10) {
			invsize = 9;
		} else if (size < 19) {
			invsize = 18;
		} else if (size < 28) {
			invsize = 27;
		} else if (size < 37) {
			invsize = 36;
		} else if (size < 46) {
			invsize = 45;
		}
		return invsize;
	}
}
