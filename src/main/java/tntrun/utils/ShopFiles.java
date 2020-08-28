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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tntrun.TNTRun;

public class ShopFiles {
	private static TNTRun pl;
	public ShopFiles(TNTRun plugin) {
		pl = plugin;
	}

	public static File getShopFile(){
	   return new File(pl.getDataFolder(), "shop.yml");
	}

	public static FileConfiguration getShopConfiguration() {
	   return YamlConfiguration.loadConfiguration(getShopFile());
	}

	public void setShopItems() {
	   FileConfiguration cfg = getShopConfiguration();
	   cfg.options().copyDefaults(true);
	   cfg.addDefault("1.name", "&7Default double jump");
	   cfg.addDefault("1.cost", Integer.valueOf(100));
	   cfg.addDefault("1.material", "FEATHER");
	   cfg.addDefault("1.amount", Integer.valueOf(1));
	   cfg.addDefault("1.permission", "shop.default");
	   List<String> lore = new ArrayList<String>();
	   lore.add("Basic kit");
	   lore.add("Cost 100 coins");
	   cfg.addDefault("1.lore", lore);
	   cfg.addDefault("1.items.1.material", "");
	   cfg.addDefault("1.items.1.amount", Integer.valueOf(1));
	   cfg.addDefault("1.items.1.displayname", "&cDefault");
	   List<String> iLore = new ArrayList<String>();
	   iLore.add("Basic kit - double jump");
	   cfg.addDefault("1.items.1.lore", iLore);
	   List<String> iEnch = new ArrayList<String>();
	   iEnch.add("KNOCKBACK#1");
	   cfg.addDefault("1.items.1.enchantments", iEnch);
	   //tidy up old IDs
	   cfg.set("1.ID", null);
	   cfg.set("1.subID", null);
	   cfg.set("1.items.1.ID", null);
	   cfg.set("1.items.1.subID", null);
	   try{
	     cfg.save(getShopFile());
	   } catch (IOException e) {
	     e.printStackTrace();
	   }
    }
}
