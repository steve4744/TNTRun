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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tntrun.TNTRun;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class Rewards {

	private Economy econ;

	public Rewards() {
		econ = TNTRun.getInstance().getVaultHandler().getEconomy();
	}

	private Map<Integer, Integer> moneyreward = new HashMap<>();
	private Map<Integer, Integer> xpreward = new HashMap<>();
	private Map<Integer, List<String>> commandrewards = new HashMap<>();
	private Map<Integer, List<ItemStack>> materialrewards = new HashMap<>();
	private Map<Integer, Integer> minplayersrequired = new HashMap<>();
	private int startingPlayers;
	private int maxplaces;
	private int index;
	private String path;

	public List<ItemStack> getMaterialReward(int place) {
		return materialrewards.get(place);
	}

	public int getMoneyReward(int place) {
		return moneyreward.getOrDefault(place, 0);
	}

	public List<String> getCommandRewards(int place) {
		return commandrewards.getOrDefault(place, Collections.emptyList());
	}

	public int getXPReward(int place) {
		return xpreward.getOrDefault(place, 0);
	}

	public int getMinPlayersRequired(int place) {
		return minplayersrequired.getOrDefault(place, 0);
	}

	/**
	 * Check if the player's finishing position has a reward.
	 *
	 * @param place
	 * @return
	 */
	private boolean isActiveReward(int place) {
		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] place = " + place +", maxplaces = " + maxplaces +
					", starters = " + startingPlayers + ", min = " + getMinPlayersRequired(place));
		}
		return place <= maxplaces && startingPlayers >= getMinPlayersRequired(place);
	}

	public void setMaterialReward(String item, String amount, String label, boolean isFirstItem, int place) {
		if (isFirstItem) {
			materialrewards.remove(place);
		}
		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] reward(" + place + ") = " + materialrewards.toString());
		}

		ItemStack reward = new ItemStack(Material.getMaterial(item), Integer.valueOf(amount));
		if (!label.isEmpty()) {
			setMaterialDisplayName(reward, label);
		}
		materialrewards.computeIfAbsent(place, k -> new ArrayList<>()).add(reward);
		maxplaces = Math.max(maxplaces, place);

		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] reward(" + place + ") = " + materialrewards.toString());
		}
	}

	private void setMaterialDisplayName(ItemStack is, String label) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(label);
		is.setItemMeta(im);
	}

	public void setMoneyReward(int money, int place) {
		moneyreward.put(place, money);
		maxplaces = Math.max(maxplaces, place);
	}
	
	public void setCommandReward(String cmdreward, boolean isFirstCmd, int place) {
		if (isFirstCmd) {
			commandrewards.remove(place);
		}
		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] reward(" + place + ") = " + commandrewards.toString());
		}
		commandrewards.computeIfAbsent(place, k -> new ArrayList<>()).add(cmdreward);
		maxplaces = Math.max(maxplaces, place);

		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] reward(" + place + ") = " + commandrewards.toString());
		}
	}
	
	public void setCommandRewards(List<String> cmdreward, int place) {
		commandrewards.put(place, cmdreward);
	}

	public void setXPReward(int xprwd, int place) {
		xpreward.put(place, xprwd);
		maxplaces = Math.max(maxplaces, place);
	}

	public void deleteMaterialReward(int place) {
		materialrewards.remove(place);
	}

	public void deleteCommandReward(int place) {
		commandrewards.remove(place);
	}

	public void setMinPlayersRequired(int min, int place) {
		minplayersrequired.put(place, min);
	}

	public void rewardPlayer(Player player, int place) {
		if (!isActiveReward(place)) {
			return;
		}
		StringJoiner rewardmessage = new StringJoiner(", ");
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] Checking rewards for " + player.getName());
		}
		if (getMaterialReward(place) != null) {
			getMaterialReward(place).forEach(reward -> {
				if (player.getInventory().firstEmpty() != -1) {
					player.getInventory().addItem(reward);
					player.updateInventory();
				} else {
					player.getWorld().dropItemNaturally(player.getLocation(), reward);
				}
				rewardmessage.add(reward.getAmount() + " x " + reward.getType().toString());
			});
		}

		int moneyreward = getMoneyReward(place);
		if (moneyreward != 0) {
			OfflinePlayer offplayer = player.getPlayer();
			rewardMoney(offplayer, moneyreward);
			rewardmessage.add(Utils.getFormattedCurrency(String.valueOf(moneyreward)));
		}

		int xpreward = getXPReward(place);
		if (xpreward > 0) {
			player.giveExp(xpreward);
			rewardmessage.add(xpreward + " XP");
		}

		for (String commandreward : getCommandRewards(place)) {
			if (commandreward != null && commandreward.length() != 0) {
				Bukkit.getServer().dispatchCommand(console, commandreward.replace("%PLAYER%", player.getName()));
				console.sendMessage("[TNTRun_reloaded] Command " + ChatColor.GOLD + commandreward + ChatColor.WHITE + " has been executed for " + ChatColor.AQUA + player.getName());
			}
		}

		if (!rewardmessage.toString().isEmpty()) {
			console.sendMessage("[TNTRun_reloaded] " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + " has been rewarded " + ChatColor.GOLD + rewardmessage.toString());
			Messages.sendMessage(player, Messages.playerrewardmessage.replace("{REWARD}", rewardmessage.toString()));
		}
	}

	private void rewardMoney(OfflinePlayer offplayer, int money) {
		if(econ != null) {
			econ.depositPlayer(offplayer, money);
		}
	}

	public void setStartingPlayers(int starters) {
		startingPlayers = starters;
	}

	public void saveToConfig(FileConfiguration config) {
		path = "reward";
		IntStream.range(1, maxplaces + 1).forEach(index -> {

			config.set(path + ".minPlayers", getMinPlayersRequired(index));
			config.set(path + ".money", getMoneyReward(index));
			config.set(path + ".xp", getXPReward(index));
			config.set(path + ".command", getCommandRewards(index));
			if (getMaterialReward(index) != null) {
				getMaterialReward(index).forEach(is -> {
					config.set(path + ".material." + is.getType().toString()  + ".amount", is.getAmount());
				});
			}
			path = "places." + (index + 1);
		});
	}

	public void loadFromConfig(FileConfiguration config) {
		if (!config.isConfigurationSection("reward")) {
			return;
		}
		index = 1;
		getPlacesFromFile(config).stream().forEach(path -> {

			setMinPlayersRequired(config.getInt(path + ".minPlayers"), index);
			setMoneyReward(config.getInt(path + ".money"), index);
			setXPReward(config.getInt(path + ".xp"), index);
			setCommandRewards(config.getStringList(path + ".command"), index);
			if (config.getConfigurationSection(path + ".material") != null) {
				Set<String> materials = config.getConfigurationSection(path + ".material").getKeys(false);
				for (String material : materials) {
					if (isValidReward(material, config.getInt(path + ".material." + material  + ".amount"))) {
						ItemStack is = new ItemStack(Material.getMaterial(material), config.getInt(path + ".material." + material  + ".amount"));
						materialrewards.computeIfAbsent(index, k -> new ArrayList<>()).add(is);
					}
				}
			}
			index++;
		});

		maxplaces = index - 1;
	}

	private List<String> getPlacesFromFile(FileConfiguration config) {
		List<String> paths = new ArrayList<>(List.of("reward"));
		if (!config.isConfigurationSection("places")) {
			return paths;
		}
		for (String key : config.getConfigurationSection("places").getKeys(false)) {
			// temp code to rewrite config from v9.27
			if (key.equalsIgnoreCase("second")) {
				paths.add("places." + 2);
				continue;
			} else if (key.equalsIgnoreCase("third")) {
				paths.add("places." + 3);
				continue;
			}

			paths.add("places." + key);
		}
		if (Utils.debug()) {
			Bukkit.getLogger().info("[TNTRun] reward paths = " + paths.toString());
		}

		return paths;
	}

	private boolean isValidReward(String materialreward, int materialamount) {
		if (Material.getMaterial(materialreward) != null && materialamount > 0) {
			return true;
		}
		return false;
	}

	public void listRewards(Player player, String arenaName) {
		Messages.sendMessage(player, Messages.rewardshead.replace("{ARENA}", arenaName), false);

		IntStream.range(1, maxplaces + 1).forEach(i -> {

			StringBuilder sb = new StringBuilder(200);
			if (getXPReward(i) != 0) {
				sb.append("\n   " + Messages.playerrewardxp + getXPReward(i));
			}
			if (getMoneyReward(i) != 0) {
				sb.append("\n   " + Messages.playerrewardmoney + getMoneyReward(i));
			}
			if (getCommandRewards(i) != null) {
				getCommandRewards(i).forEach(reward -> {
					sb.append("\n   " + Messages.playerrewardcommand + reward);
				});
			}
			if (getMaterialReward(i) != null) {
				sb.append("\n   " + Messages.playerrewardmaterial);
				getMaterialReward(i).forEach(reward -> {
					sb.append(String.valueOf(reward.getAmount()) + " x " + reward.getType().toString() + ", ");
				});
				sb.setLength(sb.length() - 2);
			}
			if (sb.length() != 0) {
				sb.insert(0, Messages.rewardlistposition.replace("{POS}", String.valueOf(i)));
				Messages.sendMessage(player, sb.toString(), false);
			}
		});
	}
}
