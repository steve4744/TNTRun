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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.Shop;
import tntrun.utils.Utils;

public class RestrictionHandler implements Listener {

	private TNTRun plugin;

	public RestrictionHandler(TNTRun plugin) {
		this.plugin = plugin;
	}

	private HashSet<String> allowedcommands = new HashSet<String>(
		Arrays.asList("/tntrun leave", "/tntrun vote", "/tr leave", "/tr vote", "/tr help", "/tr info", "/tr stats", "/tntrun stats", "/treffects", "/tr", "/tntrun")
	);

	// player should not be able to issue any commands besides /tr leave and /tr vote while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// ignore if player is not in arena
		if (arena == null) {
			return;
		}
		// allow use any command if player has permission
		if (player.hasPermission("tntrun.cmdblockbypass")) {
			return;
		}
		// now check command
		if (!allowedcommands.contains(e.getMessage().toLowerCase())) {
			Messages.sendMessage(player, Messages.nopermission);
			e.setCancelled(true);
		}
	}

	// player should not be able to break blocks while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// ignore if player is not in arena
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
		// ignore if player is not in arena
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
		// ignore if player is not in arena
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}
	
	//check interact
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		
		// check item
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
	        if (e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.leave.material"))) {
				if (arena != null) {
					TNTRun.getInstance().sound.WITHER_HURT(player, 5, 999);
					e.setCancelled(true);
					arena.getPlayerHandler().leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
				}
	        }
		}
        if(e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.shop.material"))){
    		if (arena != null) {
    			TNTRun.getInstance().sound.WITHER_HURT(player, 5, 999);
    			Inventory inv = Bukkit.createInventory(null, Shop.invsize, Shop.invname);
    			Shop.setItems(inv);
    			player.openInventory(inv);
        	}
		}
		
        if(e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.info.material"))){
            if (arena != null) {
       			if(u.contains(player)){
    				TNTRun.getInstance().sound.NOTE_PLING(player, 5, 999);
    				return;
    			}
       			u.add(player);
  			    Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    	public void run(){
			    		u.remove(player);
			    	}
			    }, 40);
            	TNTRun.getInstance().sound.WITHER_HURT(player, 5, 999);
            	Utils.displayInfo(player);
        	}
        }
        
        if(e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.vote.material"))){
            if (arena != null) {
    			if(u.contains(player)){
    				TNTRun.getInstance().sound.NOTE_PLING(player, 5, 999);
    				return;
    			}
            	TNTRun.getInstance().sound.WITHER_HURT(player, 5, 999);
            	u.add(player);
  			    Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    	public void run(){
			    		u.remove(player);
			    	}
			    }, 40);
            	if(arena.getStatusManager().isArenaStarting()){
            		player.sendMessage(Messages.arenastarting.replace("&", "§"));
            		return;
            	}
          	   	if(arena.getPlayerHandler().vote(player)){
          	   	     player.sendMessage(Messages.playervotedforstart.replace("&", "§"));
           	   	}else{
           	   	     player.sendMessage(Messages.playeralreadyvotedforstart.replace("&", "§"));
            	}
        	}
        }
        
        if(e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.stats.material"))){
            if (arena != null) {
            	e.setCancelled(true);
       			if(u.contains(player)){
    				TNTRun.getInstance().sound.NOTE_PLING(player, 5, 999);
    				return;
    			}
       			u.add(player);
  			    Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
  			    	public void run(){
  			    		u.remove(player);
  			    	}
			    }, 40);
            	TNTRun.getInstance().sound.WITHER_HURT(player, 5, 999);
         	   	player.chat("/tntrun stats");
        	}
        }
        if(e.getMaterial() == Material.getMaterial(plugin.getConfig().getString("items.effects.material"))){
            if (arena != null) {
       			if(u.contains(player)){
    				TNTRun.getInstance().sound.NOTE_PLING(player, 5, 999);
    				return;
    			}
       			u.add(player);
  			    Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    	public void run(){
			    		  u.remove(player);
			    	  }
			      	}, 40);
            	TNTRun.getInstance().sound.WITHER_HURT(player, 5, 999);
         	   	player.chat("/treffects");
        	}
        }
	}
	
	public ArrayList<Player> u = new ArrayList<Player>();
	
	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		final Player p = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(p.getName());
		
		if (arena == null) {
			return;
		}
		if (p.getGameMode() != GameMode.CREATIVE) {
			if(arena.getPlayersManager().isSpectator(p.getName())){
				e.setCancelled(false);
				p.setFlying(true);
				return;
			}
			if(!arena.getStatusManager().isArenaRunning()){
				e.setCancelled(true);
				return;
			}
			if(u.contains(p)){
				e.setCancelled(true);
				return;
			}
			if(plugin.getConfig().get("doublejumps." + p.getName()) == null || plugin.getConfig().getInt("doublejumps." + p.getName()) == 0){
				e.setCancelled(true);
				p.setAllowFlight(false);
				plugin.getConfig().set("doublejumps." + p.getName(), null);
				plugin.saveConfig();
				return;
			}else{
				plugin.getConfig().set("doublejumps." + p.getName(), plugin.getConfig().getInt("doublejumps." + p.getName()) - 1);
			}
			e.setCancelled(true);
			p.setFlying(false);
			p.setVelocity(p.getLocation().getDirection().multiply(1.5D).setY(0.7D));
			TNTRun.getInstance().sound.NOTE_PLING(p, 5, 999);
			plugin.saveConfig();
			u.add(p);
			      
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    public void run(){
			    	u.remove(p);
			    	p.setAllowFlight(true);
			    }
			}, 20);
		}else{
			p.setAllowFlight(true);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		
		if(p.hasPermission("tntrun.version.check")){
			if(TNTRun.getInstance().needUpdate){
				Bukkit.getScheduler().runTaskLaterAsynchronously(TNTRun.getInstance(), new Runnable(){
					public void run(){
						p.sendMessage(" ");
						p.sendMessage("§7[§6TNTRun§7] §6New update available!");
						p.sendMessage("§7[§6TNTRun§7] §7Your version: §6" + TNTRun.getInstance().getDescription().getVersion());
						p.sendMessage("§7[§6TNTRun§7] §7New version: §6" + TNTRun.getInstance().version[0] + " for Minecraft 1.13+");
						p.sendMessage("§7[§6TNTRun§7] §7New version available! Download now: §6https://www.spigotmc.org/resources/tntrun_reloaded.53359/");
					}
				}, 30L);
			}
		}
		
		if(!plugin.useStats()){
			return;
		}
		
		if(plugin.isFile()){
			return;
		}
		
		if(Bukkit.getOnlineMode()){
	        plugin.mysql.query("INSERT IGNORE INTO `stats` (`username`, `played`, "
	                + "`wins`, `looses`) VALUES " 
	        		+ "('" + p.getUniqueId().toString()
	                + "', '0', '0', '0');");
		}else{
	        plugin.mysql.query("INSERT IGNORE INTO `stats` (`username`, `played`, "
	                + "`wins`, `looses`) VALUES " 
	        		+ "('" + p.getName()
	                + "', '0', '0', '0');");
		}
	}
}
