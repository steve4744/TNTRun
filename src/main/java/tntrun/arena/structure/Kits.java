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

package tntrun.arena.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Kits {

	private HashMap<String, Kit> kits = new HashMap<String, Kit>();

	public boolean isKitExist(String name) {
		return kits.containsKey(name);
	}

	public HashSet<String> getKits() {
		return new HashSet<String>(kits.keySet());
	}

	public void registerKit(String name, Player player) {
		Kit kit = new Kit(player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getActivePotionEffects());
		registerKit(name, kit);
	}

	public void registerKit(String name, Kit kit) {
		kits.put(name, kit);
	}

	public void unregisterKit(String name) {
		kits.remove(name);
	}

	public void giveKit(String name, Player player) {
		try {
			kits.get(name).giveKit(player);
		} catch (Exception e) {
		}
	}

	public static class Kit {

		private ItemStack[] armor;
		private ItemStack[] items;
		private Collection<PotionEffect> effects;

		protected Kit() {
		}

		public Kit(ItemStack[] armor, ItemStack[] items, Collection<PotionEffect> effects) {
			this.armor = armor;
			this.items = items;
			this.effects = effects;
		}

		public void giveKit(Player player) {
			player.getInventory().setArmorContents(armor);
			player.getInventory().setContents(items);
			player.addPotionEffects(effects);
		}

		public void loadFromConfig(FileConfiguration config, String path) {
			armor = config.getList(path+".armor").toArray(new ItemStack[1]);
			items = config.getList(path+".items").toArray(new ItemStack[1]);
			effects = Arrays.asList(config.getList(path+".effects").toArray(new PotionEffect[1]));
		}

		public void saveToConfig(FileConfiguration config, String path) {
			config.set(path+".armor", Arrays.asList(armor));
			config.set(path+".items", Arrays.asList(items));
			config.set(path+".effects", new ArrayList<PotionEffect>(effects));
		}

	}

	public void loadFromConfig(FileConfiguration config) {
		ConfigurationSection cs = config.getConfigurationSection("kits");
		if (cs != null) {
			for (String name : cs.getKeys(false)) {
				Kit kit = new Kit();
				kit.loadFromConfig(config, "kits."+name);
				kits.put(name, kit);
			}
		}
	}

	public void saveToConfig(FileConfiguration config) {
		for (String name : kits.keySet()) {
			kits.get(name).saveToConfig(config, "kits."+name);
		}
	}

}