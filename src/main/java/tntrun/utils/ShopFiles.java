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

	public static File getShopFile() {
		return new File(pl.getDataFolder(), "shop.yml");
	}

	public static FileConfiguration getShopConfiguration() {
		return YamlConfiguration.loadConfiguration(getShopFile());
	}

	public void setShopItems() {
		if (getShopFile().exists()) {
			return;
		}
		FileConfiguration cfg = getShopConfiguration();
		cfg.options().copyDefaults(true);
		/*
		 * first item
		 */
		cfg.addDefault("1.name", "&fDouble Jump");
		cfg.addDefault("1.cost", Integer.valueOf(100));
		cfg.addDefault("1.material", "FEATHER");
		cfg.addDefault("1.amount", Integer.valueOf(1));
		cfg.addDefault("1.permission", "tntrun.shop.1");
		List<String> lore = new ArrayList<>();
		lore.add("Double Jump");
		lore.add("Cost&6 100 &5coins");
		cfg.addDefault("1.lore", lore);
		cfg.addDefault("1.items.1.material", "");
		cfg.addDefault("1.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("1.items.1.displayname", "&cDouble jump");
		List<String> iLore = new ArrayList<>();
		iLore.add("Basic kit - double jump");
		cfg.addDefault("1.items.1.lore", iLore);
		List<String> iEnch = new ArrayList<>();
		iEnch.add("null");
		cfg.addDefault("1.items.1.enchantments", iEnch);
		/*
		 * second item
		 */
		cfg.addDefault("2.name", "&fSwordsman");
		cfg.addDefault("2.cost", Integer.valueOf(250));
		cfg.addDefault("2.material", "IRON_SWORD");
		cfg.addDefault("2.amount", Integer.valueOf(1));
		cfg.addDefault("2.permission", "tntrun.shop.2");
		lore = new ArrayList<String>();
		lore.add("Sword + Knockback#1");
		lore.add("Cost&6 250 &5coins");
		cfg.addDefault("2.lore", lore);
		cfg.addDefault("2.items.1.material", "IRON_SWORD");
		cfg.addDefault("2.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("2.items.1.displayname", "&cSword + Knockback1");
		iLore = new ArrayList<String>();
		iLore.add("Sword + Knockback#1");
		cfg.addDefault("2.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("KNOCKBACK#1");
		cfg.addDefault("2.items.1.enchantments", iEnch);
		/*
		 * third item
		 */
		cfg.addDefault("3.name", "&fProtection Plus");
		cfg.addDefault("3.cost", Integer.valueOf(300));
		cfg.addDefault("3.material", "GOLDEN_CHESTPLATE");
		cfg.addDefault("3.amount", Integer.valueOf(1));
		cfg.addDefault("3.permission", "tntrun.shop.3");
		lore = new ArrayList<String>();
		lore.add("Chestplate + Thorns#1");
		lore.add("Cost&6 300 &5coins");
		cfg.addDefault("3.lore", lore);
		cfg.addDefault("3.items.1.material", "GOLDEN_CHESTPLATE");
		cfg.addDefault("3.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("3.items.1.displayname", "&cChestplate + Thorns1");
		iLore = new ArrayList<String>();
		iLore.add("Chestplate + Thorns#1");
		cfg.addDefault("3.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("THORNS#1");
		cfg.addDefault("3.items.1.enchantments", iEnch);
		/*
		* fourth item
		*/
		cfg.addDefault("4.name", "&fSword and Helmet");
		cfg.addDefault("4.cost", Integer.valueOf(300));
		cfg.addDefault("4.material", "GOLDEN_SWORD");
		cfg.addDefault("4.amount", Integer.valueOf(1));
		cfg.addDefault("4.permission", "tntrun.shop.4");
		lore = new ArrayList<String>();
		lore.add("Golden Sword and Helmet");
		lore.add("Cost&6 300 &5coins");
		cfg.addDefault("4.lore", lore);
		cfg.addDefault("4.items.1.material", "GOLDEN_SWORD");
		cfg.addDefault("4.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("4.items.1.displayname", "&cGolden Sword");
		iLore = new ArrayList<String>();
		iLore.add("Golden Sword");
		cfg.addDefault("4.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add(null);
		cfg.addDefault("4.items.1.enchantments", iEnch);
		cfg.addDefault("4.items.2.material", "GOLDEN_HELMET");
		cfg.addDefault("4.items.2.amount", Integer.valueOf(1));
		cfg.addDefault("4.items.2.displayname", "&cGolden Helmet");
		iLore = new ArrayList<String>();
		iLore.add("Golden Helmet");
		cfg.addDefault("4.items.2.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("PROTECTION#1");
		cfg.addDefault("4.items.2.enchantments", iEnch);
		/*
		* fifth item
		*/
		cfg.addDefault("5.name", "&fSnowballs x 20");
		cfg.addDefault("5.cost", Integer.valueOf(50));
		cfg.addDefault("5.material", "SNOWBALL");
		cfg.addDefault("5.amount", Integer.valueOf(20));
		cfg.addDefault("5.permission", "tntrun.shop.5");
		lore = new ArrayList<String>();
		lore.add("Snowballs + Knockback2");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("5.lore", lore);
		cfg.addDefault("5.items.1.material", "SNOWBALL");
		cfg.addDefault("5.items.1.amount", Integer.valueOf(20));
		cfg.addDefault("5.items.1.displayname", "&cSnowballs");
		iLore = new ArrayList<String>();
		iLore.add("Snowballs");
		cfg.addDefault("5.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("KNOCKBACK#2");
		cfg.addDefault("5.items.1.enchantments", iEnch);
		/*
		* sixth item
		*/
		cfg.addDefault("6.name", "&fSpeed Potion");
		cfg.addDefault("6.cost", Integer.valueOf(50));
		cfg.addDefault("6.material", "POTION");
		cfg.addDefault("6.amount", Integer.valueOf(1));
		cfg.addDefault("6.permission", "tntrun.shop.6");
		lore = new ArrayList<String>();
		lore.add("Add&6 SPEED &5(1:00)");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("6.lore", lore);
		cfg.addDefault("6.items.1.material", "POTION");
		cfg.addDefault("6.items.1.amount", Integer.valueOf(1));
		iEnch = new ArrayList<String>();
		iEnch.add("SPEED#60");
		cfg.addDefault("6.items.1.enchantments", iEnch);
		/*
		* seventh item
		*/
		cfg.addDefault("7.name", "&fJump Potion");
		cfg.addDefault("7.cost", Integer.valueOf(50));
		cfg.addDefault("7.material", "POTION");
		cfg.addDefault("7.amount", Integer.valueOf(1));
		cfg.addDefault("7.permission", "tntrun.shop.7");
		lore = new ArrayList<String>();
		lore.add("Add&6 JUMP &5boost (0:45)");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("7.lore", lore);
		cfg.addDefault("7.items.1.material", "POTION");
		cfg.addDefault("7.items.1.amount", Integer.valueOf(1));
		iEnch = new ArrayList<String>();
		iEnch.add("JUMP#45");
		cfg.addDefault("7.items.1.enchantments", iEnch);
		/*
		* eighth item
		*/
		cfg.addDefault("8.name", "&fNight Vision Potion");
		cfg.addDefault("8.cost", Integer.valueOf(50));
		cfg.addDefault("8.material", "POTION");
		cfg.addDefault("8.amount", Integer.valueOf(1));
		cfg.addDefault("8.permission", "tntrun.shop.8");
		lore = new ArrayList<String>();
		lore.add("Add&6 NIGHT_VISION &5(2:00)");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("8.lore", lore);
		cfg.addDefault("8.items.1.material", "POTION");
		cfg.addDefault("8.items.1.amount", Integer.valueOf(1));
		iEnch = new ArrayList<String>();
		iEnch.add("NIGHT_VISION#120");
		cfg.addDefault("8.items.1.enchantments", iEnch);
		/*
		* ninth item
		*/
		cfg.addDefault("9.name", "&fInvisibility Potion");
		cfg.addDefault("9.cost", Integer.valueOf(50));
		cfg.addDefault("9.material", "POTION");
		cfg.addDefault("9.amount", Integer.valueOf(1));
		cfg.addDefault("9.permission", "tntrun.shop.9");
		lore = new ArrayList<String>();
		lore.add("Add&6 INVISIBILITY &5(1:00)");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("9.lore", lore);
		cfg.addDefault("9.items.1.material", "POTION");
		cfg.addDefault("9.items.1.amount", Integer.valueOf(1));
		iEnch = new ArrayList<String>();
		iEnch.add("INVISIBILITY#60");
		cfg.addDefault("9.items.1.enchantments", iEnch);
		/*
		* tenth item
		*/
		cfg.addDefault("10.name", "&fSplash Potion of Slowness");
		cfg.addDefault("10.cost", Integer.valueOf(50));
		cfg.addDefault("10.material", "SPLASH_POTION");
		cfg.addDefault("10.amount", Integer.valueOf(1));
		cfg.addDefault("10.permission", "tntrun.shop.10");
		lore = new ArrayList<String>();
		lore.add("Add&6 SLOWNESS &5(0:30)");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("10.lore", lore);
		cfg.addDefault("10.items.1.material", "SPLASH_POTION");
		cfg.addDefault("10.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("10.items.1.displayname", "&cSplash Potion of Slowness");
		iLore = new ArrayList<String>();
		iLore.add("Slowness");
		cfg.addDefault("10.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("SLOW#30");
		cfg.addDefault("10.items.1.enchantments", iEnch);
		/*
		* eleventh item
		*/
		cfg.addDefault("11.name", "&fSplash Potion of Confusion");
		cfg.addDefault("11.cost", Integer.valueOf(50));
		cfg.addDefault("11.material", "SPLASH_POTION");
		cfg.addDefault("11.amount", Integer.valueOf(1));
		cfg.addDefault("11.permission", "tntrun.shop.11");
		lore = new ArrayList<String>();
		lore.add("Add&6 CONFUSION &5(0:30)");
		lore.add("Cost&6 50 &5coins");
		cfg.addDefault("11.lore", lore);
		cfg.addDefault("11.items.1.material", "SPLASH_POTION");
		cfg.addDefault("11.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("11.items.1.displayname", "&cSplash Potion of Confusion");
		iLore = new ArrayList<String>();
		iLore.add("Confusion");
		cfg.addDefault("11.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("CONFUSION#30");
		cfg.addDefault("11.items.1.enchantments", iEnch);
		/*
		* twelfth item
		*/
		cfg.addDefault("12.name", "&fSplash Potion of Blindness");
		cfg.addDefault("12.cost", Integer.valueOf(50));
		cfg.addDefault("12.material", "SPLASH_POTION");
		cfg.addDefault("12.amount", Integer.valueOf(1));
		cfg.addDefault("12.permission", "tntrun.shop.12");
		lore = new ArrayList<String>();
		lore.add("Add&6 BLINDNESS &5(1:30)");
		lore.add("Cost&6 100 &5coins");
		cfg.addDefault("12.lore", lore);
		cfg.addDefault("12.items.1.material", "SPLASH_POTION");
		cfg.addDefault("12.items.1.amount", Integer.valueOf(1));
		cfg.addDefault("12.items.1.displayname", "&cSplash Potion of Blindness");
		iLore = new ArrayList<String>();
		iLore.add("Blindness");
		cfg.addDefault("12.items.1.lore", iLore);
		iEnch = new ArrayList<String>();
		iEnch.add("BLINDNESS#90");
		cfg.addDefault("12.items.1.enchantments", iEnch);
		/*
		* tidy up old IDs
		*/
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
