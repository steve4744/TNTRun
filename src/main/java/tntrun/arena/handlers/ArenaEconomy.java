package tntrun.arena.handlers;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;
import tntrun.TNTRun;
import tntrun.arena.Arena;

public class ArenaEconomy {

	private final TNTRun plugin;
	private Arena arena;

	public ArenaEconomy(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public boolean hasMoney(double moneyneed, Player player) {
		Economy econ = plugin.getVaultHandler().getEconomy();
		if(econ == null) {
			return false;
		}
		OfflinePlayer offplayer = player.getPlayer();
		double pmoney = econ.getBalance(offplayer);
		if(pmoney >= moneyneed) {
			econ.withdrawPlayer(offplayer, moneyneed);
			return true;
		}
		return false;
	}

	public double getPlayerBalance(Player player) {
		Economy econ = plugin.getVaultHandler().getEconomy();
		if(econ == null) {
			return 0.0;
		}
		OfflinePlayer offplayer = player.getPlayer();
		return econ.getBalance(offplayer);
	}

	private boolean hasItemCurrency(Player player, Material currency, int fee) {
		if (!player.getInventory().contains(currency, fee)) {
			return false;
		}
		player.getInventory().removeItem(new ItemStack(currency, fee));	
		return true;
	}

	public boolean hasFunds(Player player, double fee) {
		if (arena.getStructureManager().isCurrencyEnabled()) {
			return hasItemCurrency(player, arena.getStructureManager().getCurrency(), (int)fee);
		}
		if (!hasMoney(fee, player)) {
			return false;
		}
		return true;
	}
}
