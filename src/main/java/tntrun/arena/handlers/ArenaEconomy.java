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
		return hasMoney(moneyneed, player, false);
	}

	public boolean hasMoney(double moneyneed, Player player, boolean checkonly) {
		Economy econ = plugin.getVaultHandler().getEconomy();
		if(econ == null) {
			return false;
		}
		OfflinePlayer offplayer = player.getPlayer();
		double pmoney = econ.getBalance(offplayer);
		if(pmoney >= moneyneed) {
			if (!checkonly) {
				econ.withdrawPlayer(offplayer, moneyneed);
			}
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

	private boolean hasItemCurrency(Player player, Material currency, int fee, boolean checkonly) {
		if (!player.getInventory().contains(currency, fee)) {
			return false;
		}
		if (!checkonly) {
			player.getInventory().removeItem(new ItemStack(currency, fee));
		}
		return true;
	}

	public boolean hasFunds(Player player, double fee, boolean checkonly) {
		if (arena.getStructureManager().isCurrencyEnabled()) {
			return hasItemCurrency(player, arena.getStructureManager().getCurrency(), (int)fee, checkonly);
		}
		return hasMoney(fee, player, checkonly);
	}
}
