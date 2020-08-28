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

import java.util.HashMap;
import java.util.Map;
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
import tntrun.TNTRun;
import tntrun.messages.Messages;

public class Rewards {

	private Economy econ;

	public Rewards() {
		econ = TNTRun.getInstance().getVaultHandler().getEconomy();
	}

	private Map<String, Integer> materialrewards = new HashMap<String, Integer>();
	private int moneyreward = 0;
	private int xpreward = 0;
	private String commandreward;

	public Map<String, Integer> getMaterialReward() {
		return materialrewards;
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
	
	public void setMaterialReward(String item, String amount, Boolean isFirstItem) {
		if (isFirstItem) {
			materialrewards.clear();
		}
		materialrewards.put(item, Integer.valueOf(amount));
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

		for (Map.Entry<String, Integer> entry : materialrewards.entrySet()) {
			if (isValidReward(entry.getKey(), entry.getValue())) {
				ItemStack reward = new ItemStack(Material.getMaterial(entry.getKey()), entry.getValue());
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
			rewardmessage += moneyreward + " coins, ";
		}
		if (xpreward > 0) {
			player.giveExp(xpreward);
			rewardmessage += xpreward + " XP";
		}
		if (commandreward != null && commandreward.length() != 0) {
			Bukkit.getServer().dispatchCommand(console, commandreward.replace("%PLAYER%", player.getName()));
			console.sendMessage("[TNTRun_reloaded] Command " + ChatColor.GOLD + commandreward + ChatColor.WHITE + " has been executed for " + ChatColor.AQUA + player.getName());
		}
		
		if (rewardmessage.endsWith(", ")) {
			rewardmessage = rewardmessage.substring(0, rewardmessage.length() - 2);
		}
		if (!rewardmessage.isEmpty()) {
			console.sendMessage("[TNTRun_reloaded] " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + " has been rewarded " + ChatColor.GOLD + rewardmessage);
			rewardmessage = Messages.playerrewardmessage.replace("{REWARD}", rewardmessage);
			Messages.sendMessage(player, Messages.trprefix + rewardmessage);
		}
	}

	private void rewardMoney(OfflinePlayer offplayer, int money) {
		if(econ != null) {
			econ.depositPlayer(offplayer, money);
		}
	}

	public void saveToConfig(FileConfiguration config) {
		config.set("reward.money", moneyreward);
		config.set("reward.command", commandreward);
		config.set("reward.xp", xpreward);
		
		String path = "";
		for (Map.Entry<String, Integer> entry : materialrewards.entrySet()) {
			path = "reward.material." + entry.getKey() + ".amount";
			config.set(path, entry.getValue());
		}
	}

	public void loadFromConfig(FileConfiguration config) {
		moneyreward = config.getInt("reward.money", moneyreward);
		xpreward = config.getInt("reward.xp", xpreward);
		commandreward = config.getString("reward.command", commandreward);
		
		if (config.getConfigurationSection("reward.material") != null) {
			Set<String> materials = config.getConfigurationSection("reward.material").getKeys(false);
			for (String material : materials) {
				materialrewards.put(material, config.getInt("reward.material." + material  + ".amount"));
			}
		}
	}

	public boolean isValidReward(String materialreward, int materialamount) {
		if (Material.getMaterial(materialreward) != null && materialamount > 0) {
			return true;
		}
		return false;
	}

}
