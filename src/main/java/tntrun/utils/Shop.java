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
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.eventhandler.ShopHandler;
import tntrun.messages.Messages;

public class Shop {

	private TNTRun plugin;
	private String invname;
	private int invsize;
	private int knockback;
	private ShopFiles shopFiles;
	private List<String> buyers = new ArrayList<>();
	private Map<Integer, Integer> itemSlot = new HashMap<>();
	private Map<String, ArrayList<ItemStack>> pitems = new HashMap<>(); // player-name -> items
	private Map<String, List<PotionEffect>> potionMap = new HashMap<>();  // player-name -> effects
	private Map<String, String> commandMap = new HashMap<>();  // player-name -> command
	private boolean doublejumpPurchase;
	private FileConfiguration cfg;

	public Shop(TNTRun plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(new ShopHandler(plugin), plugin);
		shopFiles = new ShopFiles(plugin);
		shopFiles.setShopItems();
		cfg = shopFiles.getShopConfiguration();
		invsize = getValidSize();
		invname = FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("shop.name"));
	}

	public void giveItem(int slot, Player player, String title) {
		int kit = itemSlot.get(slot);		
		ArrayList<ItemStack> item = new ArrayList<>();
		List<PotionEffect> pelist = new ArrayList<>();

		if (doublejumpPurchase) {
			int quantity = cfg.getInt(kit + ".items." + kit + ".amount", 1);
			giveDoubleJumps(player, quantity);
			return;
		}

		buyers.add(player.getName());

		if (isCommandPurchase(kit)) {
			List<String> lore = cfg.getStringList(kit + ".lore");
			String cmd = FormattingCodesParser.parseFormattingCodes(lore.get(0));
			commandMap.put(player.getName(), cmd);
			player.closeInventory();
			return;
		}
		for (String items : cfg.getConfigurationSection(kit + ".items").getKeys(false)) {
			try {				
				Material material = Material.getMaterial(cfg.getString(kit + ".items." + items + ".material"));
				int amount = cfg.getInt(kit + ".items." + items + ".amount");
				List<String> enchantments = cfg.getStringList(kit + ".items." + items + ".enchantments");

				// if the item is a potion, store the potion effect and skip to next item
				if (material.toString().equalsIgnoreCase("POTION")) {
					if (enchantments != null && !enchantments.isEmpty()) {
						for (String peffects : enchantments) {
							PotionEffect effect = createPotionEffect(peffects);
							if (effect != null) {
								pelist.add(effect);
							}
						}
					}
					continue;
				}
				String displayname = FormattingCodesParser.parseFormattingCodes(cfg.getString(kit + ".items." + items + ".displayname"));
				List<String> lore = cfg.getStringList(kit + ".items." + items + ".lore");

				if (material.toString().equalsIgnoreCase("SPLASH_POTION")) {
					item.add(getPotionItem(material, amount, displayname, lore, enchantments));
				} else {
					item.add(getItem(material, amount, displayname, lore, enchantments));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		player.updateInventory();
		player.closeInventory();
		pitems.put(player.getName(), item);
		potionMap.put(player.getName(), pelist);
	}

	/**
	 * Give the player shop-bought double jumps. If free double jumps are enabled 
	 * then store the purchase for later. Update player's balance item.
	 * @param player
	 * @param quantity
	 */
	private void giveDoubleJumps(Player player, int quantity) {
		if (plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
			quantity += plugin.getPData().getDoubleJumpsFromFile(player);
			plugin.getPData().saveDoubleJumpsToFile(player, quantity);

		} else {
			Arena arena = plugin.amanager.getPlayerArena(player.getName());
			arena.getPlayerHandler().incrementDoubleJumps(player, quantity);
			if(!plugin.getConfig().getBoolean("special.UseScoreboard")) {
				return;
			}
			if (!arena.getStatusManager().isArenaStarting() && plugin.getConfig().getBoolean("scoreboard.displaydoublejumps")) {
				arena.getScoreboardHandler().updateWaitingScoreboard(player);
			}
		}
		Inventory inv = player.getOpenInventory().getTopInventory();
		inv.setItem(getInvsize() -1, setMoneyItem(inv, player));
	}

	private void logPurchase(Player player, String item, int cost) {
		if (plugin.getConfig().getBoolean("shop.logpurchases")) {
			final ConsoleCommandSender console = plugin.getServer().getConsoleSender();
			console.sendMessage("[TNTRun_reloaded] " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + " has bought a " + ChatColor.RED + item +
					ChatColor.WHITE + " for " + ChatColor.RED + Utils.getFormattedCurrency(String.valueOf(cost)));
		}
	}

	private ItemStack getItem(Material material, int amount, String displayname, List<String> lore, List<String> enchantments) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);

		if (lore != null && !lore.isEmpty()) {
			meta.setLore(lore);
		}
		if (enchantments != null && !enchantments.isEmpty()) {
			for (String enchs : enchantments) {
				String[] array = enchs.split("#");
				String ench = array[0].toUpperCase();

				// get the enchantment level
				int level = 1;
				if (array.length > 1 && Utils.isNumber(array[1])) {
					level = Integer.valueOf(array[1]).intValue();
				}
				Enchantment realEnch = getEnchantmentFromString(ench);
				if (realEnch != null) {
					meta.addEnchant(realEnch, level, true);
				}
				if (material == Material.SNOWBALL && ench.equalsIgnoreCase("knockback")) {
					knockback = level;
				}
			}
		}
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack getPotionItem(Material material, int amount, String displayname, List<String> lore, List<String> enchantments) {
		ItemStack item = new ItemStack(material, amount);
		PotionMeta potionmeta = (PotionMeta) item.getItemMeta();

		potionmeta.setDisplayName(displayname);
		potionmeta.setColor(Color.RED);

		if (lore != null && !lore.isEmpty()) {
			potionmeta.setLore(lore);
		}
		if (enchantments != null && !enchantments.isEmpty()) {
			for (String peffects : enchantments) {
				PotionEffect effect = createPotionEffect(peffects);
				if (effect != null) {
					potionmeta.addCustomEffect(effect, true);
				}
			}
		}
		item.setItemMeta(potionmeta);
		return item;
	}

	private PotionEffect createPotionEffect(String effect) {
		String[] array = effect.split("#");
		String name = array[0].toUpperCase();
		int duration = 30;
		if (array.length > 1 && Utils.isNumber(array[1])) {
			duration = Integer.valueOf(array[1]).intValue();
		}
		int amplifier = 1;
		if (array.length > 2 && Utils.isNumber(array[2])) {
			amplifier = Integer.valueOf(array[2]).intValue();
		}
		PotionEffect peffect = new PotionEffect(PotionEffectType.getByName(name), duration * 20, amplifier);
		return peffect;
	}

	private boolean canBuyDoubleJumps(FileConfiguration cfg, Player p, int kit) {
		Arena arena = plugin.amanager.getPlayerArena(p.getName());
		int maxjumps = Utils.getAllowedDoubleJumps(p, plugin.getConfig().getInt("shop.doublejump.maxdoublejumps", 10));
		int quantity = cfg.getInt(kit + ".items." + kit + ".amount", 1);

		if (plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
			return maxjumps >= (plugin.getPData().getDoubleJumpsFromFile(p) + quantity);
		}
		return maxjumps >= (arena.getPlayerHandler().getDoubleJumps(p) + quantity);
	}

	public void setItems(Inventory inventory, Player player) {
		int slot = 0;
		for (String kitCounter : cfg.getConfigurationSection("").getKeys(false)) {
			String title = FormattingCodesParser.parseFormattingCodes(cfg.getString(kitCounter + ".name"));
			List<String> lore = new ArrayList<>();
			for (String loreLines : cfg.getStringList(kitCounter + ".lore")) {
				lore.add(FormattingCodesParser.parseFormattingCodes(loreLines));
			}
			Material material = Material.getMaterial(cfg.getString(kitCounter + ".material"));		      
			int amount = cfg.getInt(kitCounter + ".amount");

			if (material.toString().equalsIgnoreCase("POTION") || material.toString().equalsIgnoreCase("SPLASH_POTION")) {
				inventory.setItem(slot, getShopPotionItem(material, title, lore, amount));
			} else {
				inventory.setItem(slot, getShopItem(material, title, lore, amount));
			}
			itemSlot.put(Integer.valueOf(slot), Integer.valueOf(kitCounter));
			slot++;
		}
		inventory.setItem(getInvsize() -1, setMoneyItem(inventory, player));
	}

	private ItemStack setMoneyItem(Inventory inv, Player player) {
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		Material material = Material.getMaterial(plugin.getConfig().getString("shop.showmoneyitem", "GOLD_INGOT"));
		String title = FormattingCodesParser.parseFormattingCodes(Messages.shopmoneyheader);
		String balance = String.valueOf(arena.getArenaEconomy().getPlayerBalance(player));
		List<String> lore = new ArrayList<>();

		lore.add(FormattingCodesParser.parseFormattingCodes(Messages.shopmoneybalance).replace("{BAL}", Utils.getFormattedCurrency(balance)));
		return getShopItem(material, title, lore, 1);
	}

	private ItemStack getShopItem(Material material, String title, List<String> lore, int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		if (meta != null) {
			meta.setDisplayName(title);
			if ((lore != null) && (!lore.isEmpty())) {
				meta.setLore(lore);
			}
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getShopPotionItem(Material material, String title, List<String> lore, int amount) {
		ItemStack item = new ItemStack(material, amount);
		PotionMeta potionmeta = (PotionMeta) item.getItemMeta();

		potionmeta.setDisplayName(title);
		potionmeta.setColor(Color.BLUE);
		if (material.toString().equalsIgnoreCase("SPLASH_POTION")) {
			potionmeta.setColor(Color.RED);
		}
		potionmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		if ((lore != null) && (!lore.isEmpty())) {
			potionmeta.setLore(lore);
		}
		item.setItemMeta(potionmeta);
		return item;
	}

	private Enchantment getEnchantmentFromString(String enchantment) {		
		return Enchantment.getByKey(NamespacedKey.minecraft(enchantment.toLowerCase()));
	}

	public List<PotionEffect> getPotionEffects(Player player) {
		return potionMap.get(player.getName());
	}

	public void removePotionEffects(Player player) {
		potionMap.remove(player.getName());
	}

	public String getInvname() {
		return invname;
	}

	public int getInvsize() {
		return invsize;
	}

	public Map<String, ArrayList<ItemStack>> getPlayersItems() {
		return pitems;
	}

	public List<String> getBuyers() {
		return buyers;
	}

	public Map<String, String> getPurchasedCommands() {
		return commandMap;
	}

	public double getKnockback() {
		return Math.min(Math.max(knockback, 0), 5) * 0.4;
	}

	private int getValidSize() {
		int size = Math.max(plugin.getConfig().getInt("shop.size"), getShopFileEntries());
		if (size < 9 || size > 54) {
			return Math.min(Math.max(size, 9), 54);
		}
		return (int) (Math.ceil(size / 9.0) * 9);
	}

	private int getShopFileEntries() {
		return cfg.getConfigurationSection("").getKeys(false).size();
	}

	public ShopFiles getShopFiles() {
		return shopFiles;
	}

	public Map<Integer, Integer> getItemSlot() {
		return itemSlot;
	}

	public boolean validatePurchase(Player p, int kit, String title) {
		doublejumpPurchase = Material.getMaterial(cfg.getString(kit + ".material").toUpperCase()) == Material.FEATHER;
		if (!doublejumpPurchase && buyers.contains(p.getName())) {
			Messages.sendMessage(p, Messages.alreadyboughtitem);
			plugin.getSound().ITEM_SELECT(p);
			p.closeInventory();
			return false;
		}

		Arena arena = plugin.amanager.getPlayerArena(p.getName());
		if (doublejumpPurchase && !canBuyDoubleJumps(cfg, p, kit)) {
			Messages.sendMessage(p, Messages.maxdoublejumpsexceeded.replace("{MAXJUMPS}",
					Utils.getAllowedDoubleJumps(p, plugin.getConfig().getInt("shop.doublejump.maxdoublejumps", 10)) + ""));
			plugin.getSound().ITEM_SELECT(p);
			p.closeInventory();
			return false;
		}

		int cost = cfg.getInt(kit + ".cost");

		if (!arena.getArenaEconomy().hasMoney(cost, p)) {
			Messages.sendMessage(p, Messages.notenoughmoney.replace("{MONEY}", Utils.getFormattedCurrency(String.valueOf(cost))));
			plugin.getSound().ITEM_SELECT(p);
			return false;
		}

		Messages.sendMessage(p, Messages.playerboughtitem.replace("{ITEM}", title).replace("{MONEY}", Utils.getFormattedCurrency(String.valueOf(cost))));
		logPurchase(p, title, cost);
		if (!doublejumpPurchase) {
			Messages.sendMessage(p, Messages.playerboughtwait);
		}
		plugin.getSound().NOTE_PLING(p, 5, 10);
		return true;
	}

	private boolean isCommandPurchase(int kit) {
		return cfg.getString(kit + ".items.1.material").equalsIgnoreCase("command");
	}
}
