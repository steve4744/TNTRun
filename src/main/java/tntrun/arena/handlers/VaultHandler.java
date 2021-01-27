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

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import tntrun.TNTRun;

public class VaultHandler {

	private Economy economy;
	private Permission permission;
	private Chat chat;
	private final TNTRun plugin;

	public VaultHandler(TNTRun plugin) {
		this.plugin = plugin;

		Plugin Vault = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (Vault != null) {
			plugin.getLogger().info("Successfully linked with Vault, version " + Vault.getDescription().getVersion());
		} else {
			plugin.getLogger().info("Vault plugin not found, economy disabled");
			economy = null;
			permission = null;
			return;
		}
		setupVaultEconomy();
		setupVaultPermissions();
		setupVaultChat();
	}

	private void setupVaultEconomy() {
		final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp != null) {
            plugin.getLogger().info("Vault: economy enabled.");
            economy = rsp.getProvider();
        } else {
            plugin.getLogger().info("Vault: economy not detected.");
            economy = null;
        }
	}

	public boolean isEnabled() {
		return economy != null;
	}

	public Economy getEconomy() {
		return economy;
	}

	private void setupVaultPermissions() {
		final RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp != null) {
			permission = rsp.getProvider();
		} else {
			plugin.getLogger().info("Vault: permission plugin not detected.");
			permission = null;
		}
	}

	public Permission getPermissions() {
		return permission;
	}

	public boolean isPermissions() {
		return permission != null;
	}

	private void setupVaultChat() {
		final RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
		if (rsp != null) {
			chat = rsp.getProvider();
		} else {
			plugin.getLogger().info("Vault: chat plugin not detected.");
			chat = null;
		}
	}

	public Chat getChat() {
		return chat;
	}

	public boolean isChat() {
		return chat != null;
	}
}
