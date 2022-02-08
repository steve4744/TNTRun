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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.Heads;
import tntrun.utils.Utils;

public class RestrictionHandler implements Listener {

	private TNTRun plugin;

	public RestrictionHandler(TNTRun plugin) {
		this.plugin = plugin;
	}

	private HashSet<String> allowedcommands = new HashSet<>(
		Arrays.asList("/tntrun leave", "/tntrun vote", "/tr leave", "/tr vote", "/tr help", "/tr info", "/tr stats", "/tntrun stats", "/tr", "/tntrun"));

	// player should not be able to issue any commands while in arena apart from the white list above
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		// allow use any command if player has permission
		if (player.hasPermission("tntrun.cmdblockbypass")) {
			return;
		}
		if (!allowedcommands.contains(e.getMessage().toLowerCase()) && !plugin.getConfig().getStringList("commandwhitelist").contains(e.getMessage().toLowerCase())) {
			Messages.sendMessage(player, Messages.nopermission);
			e.setCancelled(true);
		}
	}

	// player should not be able to break blocks while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}

	// player should not be able to place blocks while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}

	//player is not able to drop items while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}

	// player interaction with hotbar items
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.leave.material"))) {
			e.setCancelled(true);
			plugin.getSound().ITEM_SELECT(player);
			if (arena.getStatusManager().isArenaRunning()) {
				arena.getGameHandler().setPlaces(player.getName());
			}
			arena.getPlayerHandler().leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);

		} else if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.shop.material"))) {
			e.setCancelled(true);
			if (!plugin.isGlobalShop()) {
				return;
			}
			plugin.getSound().ITEM_SELECT(player);
			Inventory inv = Bukkit.createInventory(null, plugin.shop.getInvsize(), plugin.shop.getInvname());
			plugin.shop.setItems(inv, player);
			player.openInventory(inv);

		} else if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.info.material"))) {
			e.setCancelled(true);
			if (u.contains(player.getName())) {
				plugin.getSound().NOTE_PLING(player, 5, 999);
				return;
			}
			u.add(player.getName());
			coolDown(player);
			plugin.getSound().ITEM_SELECT(player);
			Utils.displayInfo(player);

		} else if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.vote.material"))) {
			e.setCancelled(true);
			if (u.contains(player.getName())) {
				plugin.getSound().NOTE_PLING(player, 5, 999);
				return;
			}
			plugin.getSound().ITEM_SELECT(player);
			u.add(player.getName());
			coolDown(player);

			if (arena.getStatusManager().isArenaStarting()) {
				Messages.sendMessage(player, Messages.arenastarting);
				return;
			}
			if (arena.getPlayerHandler().vote(player)) {
				Messages.sendMessage(player, Messages.playervotedforstart);
			} else {
				Messages.sendMessage(player, Messages.playeralreadyvotedforstart);
			}

		} else if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.stats.material"))) {
			e.setCancelled(true);
			if (u.contains(player.getName())) {
				plugin.getSound().NOTE_PLING(player, 5, 999);
				return;
			}
			u.add(player.getName());
			coolDown(player);
			plugin.getSound().ITEM_SELECT(player);
			player.chat("/tntrun stats");

		} else if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.heads.material"))) {
			e.setCancelled(true);
			if (!player.hasPermission("tntrun.heads")) {
				Messages.sendMessage(player, Messages.nopermission);
				return;
			}
			plugin.getSound().ITEM_SELECT(player);
			Heads.openMenu(player);

		} else if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.tracker.material"))) {
			e.setCancelled(true);
			plugin.getSound().ITEM_SELECT(player);
			plugin.getMenus().buildTrackerMenu(player, arena);
		}
	}

	private void coolDown(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				u.remove(player.getName());
			}
		}.runTaskLater(plugin, 40);
	}

	public ArrayList<String> u = new ArrayList<>();

	// handle fly and doublejumps
	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		final Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());

		if (arena == null) {
			return;
		}
		if (player.getGameMode() == GameMode.CREATIVE) {
			player.setAllowFlight(true);
			return;
		}
		if (arena.getPlayersManager().isSpectator(player.getName())) {
			e.setCancelled(false);
			player.setFlying(true);
			return;
		}
		e.setCancelled(true);
		if (!arena.getStatusManager().isArenaRunning()) {
			return;
		}
		if (!arena.getStructureManager().isAllowDoublejumps()) {
			return;
		}
		if (u.contains(player.getName())) {
			return;
		}
		if (!arena.getPlayerHandler().hasDoubleJumps(player)) {
			return;
		}

		arena.getPlayerHandler().decrementDoubleJumps(player);
		player.setFlying(false);
		player.setVelocity(player.getLocation().getDirection()
				.multiply(plugin.getConfig().getDouble("doublejumps.multiplier", 1.5D))
				.setY(plugin.getConfig().getDouble("doublejumps.height", 0.7D)));
		plugin.getSound().NOTE_PLING(player, 5, 999);
		u.add(player.getName());

		new BukkitRunnable() {
			@Override
			public void run() {
				u.remove(player.getName());
				if (!arena.getPlayerHandler().hasDoubleJumps(player)) {
					player.setAllowFlight(false);
				}
			}
		}.runTaskLater(plugin, 20);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player player = e.getPlayer();

		if (player.hasPermission("tntrun.version.check")) {
			if (plugin.needUpdate()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						Utils.displayUpdate(player);
					}
				}.runTaskLaterAsynchronously(plugin, 30L);
			}
		}
		if (!plugin.useStats() || plugin.isFile()) {
			return;
		}
		if (plugin.getStats().hasDatabaseEntry(player)) {
			return;
		}
		final String table = plugin.getConfig().getString("MySQL.table", "stats");
		new BukkitRunnable() {
			@Override
			public void run() {
				String uuid = plugin.useUuid() ? player.getUniqueId().toString() : player.getName();
				plugin.getMysql().query("INSERT IGNORE INTO `" + table + "` (`username`, `played`, "
							+ "`wins`, `looses`) VALUES "
							+ "('" + uuid + "', '0', '0', '0');");

			}
		}.runTaskAsynchronously(plugin);
	}

	// prevent firework damage
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}

		if (event.getDamager() instanceof Firework) {
			event.setCancelled(true);
		}
	}

	// prevent movement of items within inventory
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		event.setCancelled(true);
	}

	// remove arrows and tridents that have missed their target
	@EventHandler
	public void onProjectileImpact(ProjectileHitEvent e) {
		Projectile projectile = e.getEntity();
		if (!(projectile.getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) projectile.getShooter();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		if (!plugin.getConfig().getBoolean("removearrows")) {
			return;
		}
		if (e.getHitBlock() == null) {
			return;
		}
		if (projectile instanceof Arrow || projectile instanceof Trident) {
			projectile.remove();
		}
	}
}
