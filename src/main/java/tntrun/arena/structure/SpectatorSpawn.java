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

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class SpectatorSpawn {

	private Vector p1 = null;
	private float yaw;
	private float pitch;

	protected Vector getVector() {
		return p1;
	}

	protected float getYaw() {
		return yaw;
	}

	protected float getPitch() {
		return pitch;
	}

	protected boolean isConfigured() {
		return p1 != null;
	}

	protected void setSpectatorSpawn(Location loc) {
		p1 = loc.toVector();
		yaw = loc.getYaw();
		pitch = loc.getPitch();
	}

	protected void remove() {
		p1 = null;
	}

	/**
	 * Save the spectator spawn vector.
	 *
	 * @param config
	 */
	protected void saveToConfig(FileConfiguration config) {
		if (!isConfigured()) {
			config.set("spectatorspawn", null);
			return;
		}
		config.set("spectatorspawn.p1", p1);
		config.set("spectatorspawn.yaw", yaw);
		config.set("spectatorspawn.pitch", pitch);
	}

	/**
	 * Load the spectator spawn vector. Older versions of the config will not have 
	 * p1 in the path the first time it is loaded.
	 *
	 * @param config
	 */
	protected void loadFromConfig(FileConfiguration config) {
		if (config.isSet("spectatorspawn.p1")) {
			p1 = config.getVector("spectatorspawn.p1");
		} else {
			p1 = config.getVector("spectatorspawn", null);
		}
		yaw = (float) config.getDouble("spectatorspawn.yaw", 0.0);
		pitch = (float) config.getDouble("spectatorspawn.pitch", 0.0);
	}
}
