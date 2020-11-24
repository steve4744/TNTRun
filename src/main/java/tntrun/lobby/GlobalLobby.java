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

package tntrun.lobby;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class GlobalLobby {

	private File lobbyFile;
	private LobbyLocation lobbyLocation;

	public GlobalLobby(TNTRun plugin) {
		lobbyFile = new File(plugin.getDataFolder() + File.separator + "lobby.yml");
	}

	public boolean isLobbyLocationWorldAvailable() {
		if (isLobbyLocationSet()) {
			return lobbyLocation.isWorldAvailable();
		}
		return false;
	}

	public boolean isLobbyLocationSet() {
		return lobbyLocation != null;
	}

	public void joinLobby(Player player) {
		if (isLobbyLocationSet()) {
			if (isLobbyLocationWorldAvailable()) {
				player.teleport(getLobbyLocation());
				Messages.sendMessage(player, Messages.teleporttolobby);
			} else {
				Messages.sendMessage(player, Messages.lobbyunloaded);
			}
		} else {
			Messages.sendMessage(player, Messages.nolobby);
		}
	}

	public Location getLobbyLocation() {
		return lobbyLocation.getLocation();
	}

	public void setLobbyLocation(Location location) {
		if (location != null) {
			lobbyLocation = new LobbyLocation(location.getWorld().getName(), location.toVector(), location.getYaw(), location.getPitch());
			return;
		}

		FileConfiguration config = new YamlConfiguration();
		lobbyLocation = null;

		config.set("lobby", null);
		try {
			config.save(lobbyFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveToConfig() {
		FileConfiguration config = new YamlConfiguration();

		if (isLobbyLocationSet()) {
			config.set("lobby.world", lobbyLocation.getWorldName());
			config.set("lobby.vector", lobbyLocation.getVector());
			config.set("lobby.yaw", lobbyLocation.getYaw());
			config.set("lobby.pitch", lobbyLocation.getPitch());

			try {
				config.save(lobbyFile);
			} catch (IOException e) {
			}
		}
	}

	public void loadFromConfig() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(lobbyFile);

		String worldname = config.getString("lobby.world", null);
		Vector vector = config.getVector("lobby.vector", null);
		float yaw = (float) config.getDouble("lobby.yaw", 0.0);
		float pitch = (float) config.getDouble("lobby.pitch", 0.0);

		if (worldname != null && vector != null) {
			lobbyLocation = new LobbyLocation(worldname, vector, yaw, pitch);
		}
	}

}
