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

package tntrun.kits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tntrun.TNTRun;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class Kits {

	private HashMap<String, Kit> kits = new HashMap<>();
	
	private File kitsconfig = new File(TNTRun.getInstance().getDataFolder(), "kits.yml");
	private FileConfiguration config = YamlConfiguration.loadConfiguration(kitsconfig);

	public boolean kitExists(String name) {
		return kits.containsKey(name);
	}

	public HashSet<String> getKits() {
		return new HashSet<String>(kits.keySet());
	}
	
	public void registerKit(String name, Player player) {
		if (kitExists(name)) {
			Messages.sendMessage(player, Messages.kitexists.replace("{KIT}", name));
			return;
		}
		Kit kit = new Kit(player.getInventory().getContents(), player.getActivePotionEffects());
		registerKit(name, kit);
		Messages.sendMessage(player, Messages.kitadd.replace("{KIT}", name));
	}

	private void registerKit(String name, Kit kit) {
		kits.put(name, kit);
	}

	public void unregisterKit(String name, Player player) {
		if (!kitExists(name)) {
			Messages.sendMessage(player, Messages.kitnotexists.replace("{KIT}", name));
			return;
		}
		kits.remove(name);
		config.set("kits." + name, null);
		saveKits();
		Messages.sendMessage(player, Messages.kitdel.replace("{KIT}", name));
	}

	public void giveKit(String name, Player player) {
		try {
			kits.get(name).giveKit(player);
			Messages.sendMessage(player, Messages.playerkit.replace("{KIT}", name));
			if (Utils.debug()) {
				TNTRun.getInstance().getLogger().info(player.getName() + " has received kit " + name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Kit {

		private ItemStack[] items;
		private Collection<PotionEffect> effects;

		protected Kit() {
		}

		public Kit(ItemStack[] items, Collection<PotionEffect> effects) {
			this.items = items;
			this.effects = effects;
		}

		public void giveKit(Player player) {
			player.getInventory().setContents(items);
			if (!effects.contains(null)) {
				player.addPotionEffects(effects);
			}
		}

		public void loadFromConfig(FileConfiguration config, String path) {
			items = config.getList(path + ".items").toArray(new ItemStack[1]);
			effects = Arrays.asList(config.getList(path + ".effects").toArray(new PotionEffect[1]));
		}

		public void saveToConfig(FileConfiguration config, String path) {
			config.set(path + ".items", Arrays.asList(items));
			config.set(path + ".effects", new ArrayList<PotionEffect>(effects));
		}

	}

	public void loadFromConfig() {
		if (!kitsconfig.exists()) {
			try {
				kitsconfig.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ConfigurationSection cs = config.getConfigurationSection("kits");
		if (cs != null) {
			for (String name : cs.getKeys(false)) {
				Kit kit = new Kit();
				kit.loadFromConfig(config, "kits." + name);
				kits.put(name, kit);
			}
		}
	}

	public void saveToConfig() {
		for (String name : kits.keySet()) {
			kits.get(name).saveToConfig(config, "kits." + name);
		}
		saveKits();
	}
	
	private void saveKits() {
		try {
			config.save(kitsconfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listKit(String name, Player player) {
		if (!kitExists(name)) {
			Messages.sendMessage(player, Messages.kitnotexists.replace("{KIT}", name));
			return;
		}
		Messages.sendMessage(player, "&7============" + Messages.trprefix + "============", false);
		Messages.sendMessage(player, "&7Kit Details: &a" + name, false);

		for (ItemStack is : kits.get(name).items) {
			if (is == null || is.getType() == Material.AIR) {
				continue;
			}
			StringBuilder message = new StringBuilder(200);
			message.append("&6" + is.getType().toString());
			if (is.getAmount() > 1) {
				message.append("&7 x " + "&c" + is.getAmount());
			}
			Messages.sendMessage(player, message.toString(), false);
		}

		for (PotionEffect pe : kits.get(name).effects) {
			if (pe == null) {
				continue;
			}
			Messages.sendMessage(player, "&6Potion Effect&7 : &c" + pe.getType().getName(), false);
		}
	}

}