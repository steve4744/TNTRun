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
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class Shop implements Listener{
	
	private static TNTRun pl;
	
	public Shop(TNTRun plugin){
		pl = plugin;
		ShopFiles shopFiles = new ShopFiles(pl);
		shopFiles.setShopItems();
		
		invsize = pl.getConfig().getInt("shop.size");
		invname = pl.getConfig().getString("shop.name").replace("&", "§");
	}  
	
	public static HashMap<Integer, Integer> itemSlot = new HashMap<Integer, Integer>();
	public static HashMap<Player, ArrayList<ItemStack>> pitems = new HashMap<Player, ArrayList<ItemStack>>();
	public static List<Player> bought = new ArrayList<Player>();
	public static String invname;
	public static int invsize; 
	
	private void giveItem(int slot, Player player, String title) {
		int kit = itemSlot.get(slot);		
		ArrayList<ItemStack> item = new ArrayList<ItemStack>();
		FileConfiguration cfg = ShopFiles.getShopConfiguration();
		
		for(String items : cfg.getConfigurationSection(kit + ".items").getKeys(false)) {
			try {				
				Material material = Material.getMaterial(cfg.getString(kit + ".items." + items + ".material"));
				int amount = Integer.valueOf(cfg.getInt(kit + ".items." + items + ".amount"));
				String displayname = cfg.getString(kit + ".items." + items + ".displayname").replace("&", "§");
				List<String> lore = cfg.getStringList(kit + ".items." + items + ".lore");
				List<String> enchantments = cfg.getStringList(kit + ".items." + items + ".enchantments");
				
				if(!bought.contains(player)){
					bought.add(player);
				}
				
				item.add(getItem(material, amount, displayname, lore, enchantments));				
				player.updateInventory();
				player.closeInventory();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		pitems.put(player, item);
	}

	private ItemStack getItem(Material material, int amount, String displayname, List<String> lore, List<String> enchantments){
		
		ItemStack item = new ItemStack(material, amount);
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(displayname);
	    
	    if ((lore != null) && (!lore.isEmpty())) {
	      meta.setLore(lore);
	    }
	    
	    if ((enchantments != null) && (!enchantments.isEmpty())) {
	      for (String enchs : enchantments) {
	        String[] array = enchs.split("#");
	        String ench = array[0].toUpperCase();
	        int level = Integer.valueOf(array[1]).intValue();
	        Enchantment realEnch = Enchantment.getByName(ench);
	        if (realEnch != null) {
	        	meta.addEnchant(realEnch, level, true);
	        }
	      }
	    }
	    item.setItemMeta(meta);
	    return item;
	}
	@EventHandler
	public void onClick(InventoryClickEvent e){
	    Player p = (Player)e.getWhoClicked();
	    
	    if (e.getInventory().getName().equals(invname)) { 	
	    	e.setCancelled(true);
	    	if ((e.getSlot() == e.getRawSlot()) && (e.getCurrentItem() != null)) {
	    		ItemStack current = e.getCurrentItem();
	    		if ((current.hasItemMeta()) && (current.getItemMeta().hasDisplayName())) {
	    			int kit = ((Integer)itemSlot.get(Integer.valueOf(e.getSlot()))).intValue();
	    			FileConfiguration cfg = ShopFiles.getShopConfiguration();
	    			String permission = cfg.getString(kit + ".permission");
	    			
	    			if(bought.contains(p)){
	    				p.sendMessage(Messages.alreadyboughtitem.replace("&", "§"));
	    				pl.getSoundHandler().playSound(p, "itemselect");
	    				return;
	    			}
	    			if (p.hasPermission(permission)) {
	    				String title = current.getItemMeta().getDisplayName();
	    				int cost = cfg.getInt(kit + ".cost");
	        	  
	    				if (Material.getMaterial(cfg.getString(kit + ".material").toUpperCase()) == Material.FEATHER) {
	    					if((pl.getConfig().getInt("shop.doublejump.maxdoublejumps") <= pl.getConfig().getInt("doublejumps." + p.getName()))){
	    						p.sendMessage(Messages.alreadyboughtitem.replace("&", "§"));
	    						pl.getSoundHandler().playSound(p, "itemselect");
	    						return;
	    					}
	    				}
	        	  
	    				if(hasMoney(cost, p)) {
	    					p.sendMessage(Messages.playerboughtitem.replace("&", "§").replace("{ITEM}", title).replace("{MONEY}", cost + ""));
	    					p.sendMessage(Messages.playerboughtwait.replace("&", "§"));
	    					pl.getSoundHandler().playPlingSound(p, 5, 2);
	    				}else{
	    					p.sendMessage(Messages.notenoughtmoney.replace("&", "§").replace("{MONEY}", cost + ""));
	    					pl.getSoundHandler().playSound(p, "itemselect");
	    					return;
	    				}
	    				if (Material.getMaterial(cfg.getString(kit + ".material").toUpperCase()) == Material.FEATHER) {
	    					if(pl.getConfig().get("doublejumps." + p.getName()) == null){
	    						pl.getConfig().set("doublejumps." + p.getName(), 1);
	    					}else{
	    						pl.getConfig().set("doublejumps." + p.getName(), pl.getConfig().getInt("doublejumps." + p.getName()) + 1);
	    					}
	    					pl.saveConfig();
	    					return;
	    				}
	    				giveItem(e.getSlot(), p, current.getItemMeta().getDisplayName());  
	    			} else {
	    				p.closeInventory();
	    				p.sendMessage(Messages.nopermission.replace("&", "§"));
	    				pl.getSoundHandler().playSound(p, "itemselect");
	    			}
	    		}
	    	}
	    }
	}

	//private Object economy = null;

	private boolean hasMoney(int moneyneed, Player player) {
		/*if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
		}*/
		Economy econ = pl.getVaultHandler().getEconomy();	 
		//if (economy != null) {
		if (econ != null) {
			OfflinePlayer offplayer = player.getPlayer();
			double pmoney = econ.getBalance(offplayer);
			if(pmoney >= moneyneed){
				econ.withdrawPlayer(offplayer, moneyneed);
				return true;
			}
		}
		return false;
	}
		
	public static void setItems(Inventory inventory){
		FileConfiguration cfg = ShopFiles.getShopConfiguration();
		int slot = 0;
		for (String kitCounter : cfg.getConfigurationSection("").getKeys(false)) {
		      String title = cfg.getString(kitCounter + ".name").replace("&", "§");
		      List<String> lore = new ArrayList<String>();
		      for (String loreLines : cfg.getStringList(kitCounter + ".lore")) {
		    	  loreLines = loreLines.replace("&", "§");
		          lore.add(loreLines);
		      }
		      Material material = Material.getMaterial(cfg.getString(kitCounter + ".material"));
		      int amount = cfg.getInt(kitCounter + ".amount");
		      inventory.setItem(slot, getItem(material, title, lore, amount));
		      itemSlot.put(Integer.valueOf(slot), Integer.valueOf(kitCounter));
		      slot++;
		}
	}

	private static ItemStack getItem(Material material, String title, List<String> lore, int amount){
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		
		if ((lore != null) && (!lore.isEmpty())) {
		    meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
}
