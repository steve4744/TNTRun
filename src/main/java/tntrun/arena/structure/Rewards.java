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
import java.util.List;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import tntrun.messages.Messages;

public class Rewards {

	private Object economy = null;

	public Rewards() {
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
		}
	}

	private List<String> materialrewards = new ArrayList<String>();
	private List<String> materialamounts = new ArrayList<String>();
	private int moneyreward = 0;
	private int xpreward = 0;
	private String commandreward;

	public List<String> getMaterialReward() {
		return materialrewards;
	}
	
	public List<String> getMaterialAmount() {
		return materialamounts;
	}

	public int getMoneyReward() {
		return moneyreward;
	}
	
	public String getCommandReward() {
		return commandreward;
	}
	
	public int getXPReward() {
		return xpreward;
	}
	
	public void setMaterialReward(String block, String amount, Boolean isFirstItem) {
		if (isFirstItem) {
			materialrewards.clear();
			materialamounts.clear();
		}
		materialrewards.add(block);
		materialamounts.add(amount);
	}

	public void setMoneyReward(int money) {
		moneyreward = money;
	}
	
	public void setCommandReward(String cmdreward) {
		commandreward = cmdreward;
	}
	
	public void setXPReward(int xprwd) {
		xpreward = xprwd;
	}

	public void rewardPlayer(Player player) {
		String rewardmessage = "";
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		for (int i=0; i < materialrewards.size(); i++) {
			if (isValidReward(materialrewards.get(i), materialamounts.get(i))) {
				ItemStack reward = new ItemStack(Material.getMaterial(materialrewards.get(i)), Integer.parseInt(materialamounts.get(i)));
				if (player.getInventory().firstEmpty() != -1) {
					player.getInventory().addItem(reward);
					player.updateInventory();
				} else {
					player.getWorld().dropItemNaturally(player.getLocation(),reward);
				}
				rewardmessage += reward.getAmount() + " x " + reward.getType().toString() + ", ";
			}
		}
		
		if (moneyreward != 0) {
			OfflinePlayer offplayer = player.getPlayer();
			rewardMoney(offplayer, moneyreward);
			rewardmessage += ChatColor.GOLD.toString() + moneyreward + " coins, ";
		}
		if (xpreward > 0) {
			player.giveExp(xpreward);
			rewardmessage += ChatColor.GOLD.toString() + xpreward + " XP";
		}
		if (commandreward != null && commandreward.length() != 0) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getServer().getConsoleSender(), commandreward.replace("%PLAYER%", player.getName()));
			console.sendMessage("[TNTRun_reloaded] Command " + ChatColor.GOLD + commandreward + ChatColor.WHITE + " has been executed for " + ChatColor.AQUA + player.getName());
		}
		
		if (rewardmessage.endsWith(", ")) {
			rewardmessage = rewardmessage.substring(0, rewardmessage.length() - 2);
		}
		if (!rewardmessage.isEmpty()) {
			console.sendMessage("[TNTRun_reloaded] " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + " has been rewarded " + ChatColor.GOLD + rewardmessage);
			rewardmessage = Messages.playerrewardmessage.replace("{REWARD}", rewardmessage);
			Messages.sendMessage(player, rewardmessage);
		}
	}

	private void rewardMoney(OfflinePlayer offplayer, int money) {
		if (economy != null) {
			Economy econ = (Economy) economy;
			econ.depositPlayer(offplayer, money);
		}
	}

	public void saveToConfig(FileConfiguration config) {
		config.set("reward.money", moneyreward);
		config.set("reward.command", commandreward);
		config.set("reward.xp", xpreward);
		
		String path = "";
		for (int i=0; i < materialrewards.size(); i++) {
			path = "reward.material." + materialrewards.get(i) + ".amount";
			config.set(path, Integer.parseInt(materialamounts.get(i)));
		}
	}

	public void loadFromConfig(FileConfiguration config) {
		moneyreward = config.getInt("reward.money", moneyreward);
		xpreward = config.getInt("reward.xp", xpreward);
		commandreward = config.getString("reward.command", commandreward);
		
		//check that path exists
		if (config.getConfigurationSection("reward.material") != null) {
			Set<String> materials = config.getConfigurationSection("reward.material").getKeys(false);
			for (String material : materials) {
				materialrewards.add(material);
				materialamounts.add(String.valueOf(config.getInt("reward.material." + material  + ".amount")));
			}
		}
	}

	public boolean isValidReward(String materialreward, String materialamount) {		
		if (Material.getMaterial(materialreward) != null && Integer.parseInt(materialamount) > 0) {
			return true;
		}
		return false;
	}

}
