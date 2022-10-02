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
	private Map<String, List<ItemStack>> pitems = new HashMap<>(); // player-name -> items
	private Map<String, List<PotionEffect>> potionMap = new HashMap<>();  // player-name -> effects
	private Map<String, List<String>> commandMap = new HashMap<>();  // player-name -> commands
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

		if (doublejumpPurchase) {
			int quantity = cfg.getInt(kit + ".items." + kit + ".amount", 1);
			giveDoubleJumps(player, quantity);
			return;
		}

		buyers.add(player.getName());

		if (isCommandPurchase(kit)) {
			List<String> cmds = new ArrayList<>();
			List<String> lore = cfg.getStringList(kit + ".lore");
			lore.stream()
				.limit(cfg.getInt(kit + ".items.1.amount"))
				.forEachOrdered(e -> {
					cmds.add(ChatColor.stripColor(FormattingCodesParser.parseFormattingCodes(e)));
			});
			commandMap.put(player.getName(), cmds);
			player.closeInventory();
			return;
		}

		List<ItemStack> itemlist = new ArrayList<>();
		List<PotionEffect> pelist = new ArrayList<>();

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
					itemlist.add(getPotionItem(material, amount, displayname, lore, enchantments));
				} else {
					itemlist.add(getItem(material, amount, displayname, lore, enchantments));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		player.updateInventory();
		player.closeInventory();
		pitems.put(player.getName(), itemlist);
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
			arena.getPlayerHandler().incrementDoubleJumps(player.getName(), quantity);
			if (!arena.getStatusManager().isArenaStarting() && plugin.getConfig().getBoolean("scoreboard.displaydoublejumps")) {
				if(plugin.getConfig().getBoolean("special.UseScoreboard")) {
					arena.getScoreboardHandler().updateWaitingScoreboard(player);
				}
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
			meta.setLore(getFormattedLore(lore));
		}
		if (enchantments != null && !enchantments.isEmpty()) {
			addEnchantmentsToMeta(material, enchantments, meta);
		}
		item.setItemMeta(meta);
		return item;
	}

	private void addEnchantmentsToMeta(Material material, List<String> enchantments, ItemMeta meta) {
		for (String enchs : enchantments) {
			String ench = getEnchantmentName(enchs);
			int level = getEnchantmentLevel(enchs);

			Enchantment realEnch = getEnchantmentFromString(ench);
			if (realEnch != null) {
				meta.addEnchant(realEnch, level, true);
			}
			if (material == Material.SNOWBALL && ench.equalsIgnoreCase("knockback")) {
				knockback = level;
			}
		}
	}

	/**
	 * Get the splash potion item to give to the player.
	 *
	 * @param material
	 * @param amount
	 * @param displayname
	 * @param lore
	 * @param enchantments
	 * @return
	 */
	private ItemStack getPotionItem(Material material, int amount, String displayname, List<String> lore, List<String> enchantments) {
		ItemStack item = new ItemStack(material, amount);
		PotionMeta potionmeta = (PotionMeta) item.getItemMeta();

		potionmeta.setDisplayName(displayname);

		if (lore != null && !lore.isEmpty()) {
			potionmeta.setLore(getFormattedLore(lore));
		}

		if (enchantments != null && !enchantments.isEmpty()) {
			for (String peffects : enchantments) {
				PotionEffect effect = createPotionEffect(peffects);
				if (effect != null) {
					potionmeta.addCustomEffect(effect, true);
					potionmeta.setColor(PotionEffectType.getByName(getEnchantmentName(peffects)).getColor());
				}
			}
		}
		item.setItemMeta(potionmeta);
		return item;
	}

	private PotionEffect createPotionEffect(String effect) {
		String name = getEnchantmentName(effect);
		int duration = getEnchantmentDuration(effect);
		int amplifier = getEnchantmentAmplifier(effect);

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
		return maxjumps >= (arena.getPlayerHandler().getDoubleJumps(p.getName()) + quantity);
	}

	/**
	 * Set the display items in the shop menu.
	 *
	 * @param inventory
	 * @param player
	 */
	public void setItems(Inventory inventory, Player player) {
		int slot = 0;
		for (String kitCounter : cfg.getConfigurationSection("").getKeys(false)) {
			String title = FormattingCodesParser.parseFormattingCodes(cfg.getString(kitCounter + ".name"));
			List<String> lore = cfg.getStringList(kitCounter + ".lore");

			List<String> enchantments = cfg.getStringList(kitCounter + ".items.1.enchantments");
			String firstEnchantment = (enchantments != null && !enchantments.isEmpty()) ? enchantments.get(0) : "";

			boolean isGlowing = cfg.getBoolean(kitCounter + ".glow");
			Material material = Material.getMaterial(cfg.getString(kitCounter + ".material"));		      
			int amount = cfg.getInt(kitCounter + ".amount");

			if (material.toString().equalsIgnoreCase("POTION") || material.toString().equalsIgnoreCase("SPLASH_POTION")) {
				inventory.setItem(slot, getShopPotionItem(material, title, lore, amount, firstEnchantment));
			} else {
				inventory.setItem(slot, getShopItem(material, title, lore, amount, isGlowing));
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
		return getShopItem(material, title, lore, 1, false);
	}

	private ItemStack getShopItem(Material material, String title, List<String> lore, int amount, boolean isGlowing) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		if (meta != null) {
			meta.setDisplayName(title);
			if (lore != null && !lore.isEmpty()) {
				meta.setLore(getFormattedLore(lore));
			}
			if (isGlowing) {
				meta.addEnchant(Enchantment.DURABILITY, 2, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getShopPotionItem(Material material, String title, List<String> lore, int amount, String enchantment) {
		ItemStack item = new ItemStack(material, amount);
		PotionMeta potionmeta = (PotionMeta) item.getItemMeta();

		potionmeta.setDisplayName(title);

		PotionEffect effect = createPotionEffect(enchantment);
		if (effect != null) {
			potionmeta.addCustomEffect(effect, true);
			potionmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			potionmeta.setColor(PotionEffectType.getByName(getEnchantmentName(enchantment)).getColor());
		}

		if ((lore != null) && (!lore.isEmpty())) {
			potionmeta.setLore(getFormattedLore(lore));
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

	public Map<String, List<ItemStack>> getPlayersItems() {
		return pitems;
	}

	public List<String> getBuyers() {
		return buyers;
	}

	public Map<String, List<String>> getPurchasedCommands() {
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

	private List<String> getFormattedLore(List<String> lore) {
		List<String> formattedLore = new ArrayList<>();
		for (String loreline : lore) {
			formattedLore.add(FormattingCodesParser.parseFormattingCodes(loreline));
		}
		return formattedLore;
	}

	private String getEnchantmentName(String enchant) {
		String[] array = enchant.split("#");
		return array[0].toUpperCase();
	}

	private int getEnchantmentLevel(String enchant) {
		String[] array = enchant.split("#");
		return (array.length > 1 && Utils.isNumber(array[1])) ? Integer.valueOf(array[1]) : 1;
	}

	private int getEnchantmentDuration(String enchant) {
		String[] array = enchant.split("#");
		return (array.length > 1 && Utils.isNumber(array[1])) ? Integer.valueOf(array[1]) : 30;
	}

	private int getEnchantmentAmplifier(String enchant) {
		String[] array = enchant.split("#");
		return (array.length > 2 && Utils.isNumber(array[2])) ? Integer.valueOf(array[2]) : 1;
	}
}
