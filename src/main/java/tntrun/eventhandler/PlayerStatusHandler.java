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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.arena.structure.StructureManager.DamageEnabled;

public class PlayerStatusHandler implements Listener {

	private TNTRun plugin;
	private final Map<UUID, Integer> snowballLevels = new HashMap<>();

	public PlayerStatusHandler(TNTRun plugin) {
		this.plugin = plugin;
	}

	// handle damage based on arena settings
	// fall damage is always cancelled
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			if (arena == null) {
				return;
			}
			if (!arena.getStatusManager().isArenaRunning()) {
				e.setCancelled(true);
				return;
			}
			if (e.getCause() == DamageCause.FALL) {
				e.setCancelled(true);
				return;
			}
			DamageEnabled status = arena.getStructureManager().getDamageEnabled();
			switch (status) {
				case YES: {
					return;
				}
				case ZERO: {
					e.setDamage(0);
					return;
				}
				case NO: {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	// cancel all damage to and from spectators
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamageByPlayer(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player player = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			if (arena == null) {
				return;
			}
			if (!arena.getStructureManager().getDamageEnabled().toString().equals("NO")) {
				if (damager.getInventory().getItemInMainHand().getType() == Material.AIR && !arena.getStructureManager().isPunchDamage()) {
					e.setCancelled(true);
					return;
				}
			}
			if (arena.getPlayersManager().isSpectator(player.getName()) || arena.getPlayersManager().isSpectator(damager.getName())) {
				e.setCancelled(true);
			}
		}
	}

	// player should have infinite food while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamage(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (plugin.amanager.getPlayerArena(player.getName()) != null) {
				e.setCancelled(true);
			}
		}
	}

	// give snowballs an impact effect
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSnowballHit(ProjectileHitEvent e) {
		Projectile projectile = e.getEntity();
		if (!(projectile instanceof Snowball)) {
			return;
		}
		if (e.getHitEntity() == null || e.getHitEntity().getType() != EntityType.PLAYER) {
			return;
		}
		Player player = (Player) e.getHitEntity();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		if (!arena.getStatusManager().isArenaRunning()) {
			return;
		}
		player.damage(0.5, projectile);
		double knockback = getKnockbackLevel(projectile);
		if (knockback <= 0) {
			return;
		}
		player.setVelocity(projectile.getVelocity().multiply(knockback));
	}

	// store the knockback enchantment for each snowball
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSnowballThrow(ProjectileLaunchEvent e) {
		Projectile projectile = e.getEntity();
		if (!(projectile instanceof Snowball)) {
			return;
		}
		if (!(projectile.getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) projectile.getShooter();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		if (arena == null) {
			return;
		}
		if (!arena.getStatusManager().isArenaRunning()) {
			return;
		}
		ItemStack is = player.getInventory().getItemInMainHand();
		if (!is.hasItemMeta()) {
			return;
		}
		ItemMeta im = is.getItemMeta();
		if (im.hasEnchant(Enchantment.KNOCKBACK)) {
			snowballLevels.put(projectile.getUniqueId(), im.getEnchantLevel(Enchantment.KNOCKBACK));
		}
	}

	private double getKnockbackLevel(Projectile projectile) {
		Snowball snowball = (Snowball) projectile;
		int level = snowballLevels.getOrDefault(snowball.getUniqueId(), 0);
		snowballLevels.remove(snowball.getUniqueId());
		// snowball base knockback: 0.3
		double kb = 0.3 + (0.2 * level);
		return kb;
	}
}
