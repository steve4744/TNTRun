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

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import tntrun.arena.Arena;

public class StructureManager {

	private Arena arena;
	public StructureManager(Arena arena) {
		this.arena = arena;
		gamezone = new GameZone(arena);
	}

	private String world;
	private Vector p1 = null;
	private Vector p2 = null;
	private GameZone gamezone;
	private int gameleveldestroydelay = 8;
	private LoseLevel loselevel = new LoseLevel();
	private Vector spectatorspawn = null;
	private Vector spawnpoint = null;
	private int minPlayers = 2;
	private int maxPlayers = 15;
	private double votesPercent = 0.75;
	private int timelimit = 300;
	private int countdown = 10;
	private Kits kits = new Kits();
	private Rewards rewards = new Rewards();
	private TeleportDestination teleportDest = TeleportDestination.PREVIOUS;
	private DamageEnabled damageEnabled = DamageEnabled.NO;
	private boolean testmode = false;
	private boolean finished = false;
	private boolean enableOnRestart = true;

	public String getWorldName() {
		return world;
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	public Vector getP1() {
		return p1;
	}


	public Vector getP2() {
		return p2;
	}

	public GameZone getGameZone() {
		return gamezone;
	}

	public int getGameLevelDestroyDelay() {
		return gameleveldestroydelay;
	}

	public LoseLevel getLoseLevel() {
		return loselevel;
	}

	public Vector getSpectatorSpawnVector() {
		return spectatorspawn;
	}

	public Location getSpectatorSpawn() {
		if (spectatorspawn != null) {
			return new Location(getWorld(), spectatorspawn.getX(), spectatorspawn.getY(), spectatorspawn.getZ());
		}
		return null;
	}

	public Vector getSpawnPointVector() {
		return spawnpoint;
	}

	public Location getSpawnPoint() {
		return new Location(getWorld(), spawnpoint.getX(), spawnpoint.getY(), spawnpoint.getZ());
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public double getVotePercent() {
		return votesPercent;
	}

	public int getTimeLimit() {
		return timelimit;
	}

	public int getCountdown() {
		return countdown;
	}

	public Kits getKits() {
		return kits;
	}

	public Rewards getRewards() {
		return rewards;
	}

	public TeleportDestination getTeleportDestination() {
		return teleportDest;
	}

	public static enum TeleportDestination {
		PREVIOUS, LOBBY;
	}

	public DamageEnabled getDamageEnabled() {
		return damageEnabled;
	}

	public static enum DamageEnabled {
		YES, ZERO, NO
	}

	public boolean isTestMode() {
		return testmode;
	}

	public boolean isInArenaBounds(Location loc) {
		if (loc.toVector().isInAABB(getP1(), getP2())) {
			return true;
		}
		return false;
	}

	public boolean isArenaBoundsSet() {
		if (getP1() == null || getP2() == null || world == null) {
			return false;
		}
		return true;
	}

	public boolean isArenaConfigured() {
		return isArenaConfiguredString().equals("yes");
	}

	public String isArenaConfiguredString() {
		if (getP1() == null || getP2() == null || world == null) {
			return "Arena bounds not set";
		}
		if (!loselevel.isConfigured()) {
			return "Arena loselevel not set";
		}
		if (spawnpoint == null) {
			return "Arena spawnpoint not set";
		}
		return "yes";
	}

	public boolean isArenaFinished() {
		return finished;
	}

	public boolean isEnableOnRestart() {
		return enableOnRestart;
	}

	public void setArenaFinished(boolean finished) {
		this.finished = finished;
	}

	public void setArenaPoints(Location loc1, Location loc2) {
		world = loc1.getWorld().getName();
		p1 = loc1.toVector();
		p2 = loc2.toVector();
	}

	public void setGameLevelDestroyDelay(int delay) {
		gameleveldestroydelay = delay;
	}

	public boolean setLooseLevel(Location loc1, Location loc2) {
		if (isInArenaBounds(loc1) && isInArenaBounds(loc2)) {
			loselevel.setLooseLocation(loc1, loc2);
			return true;
		}
		return false;
	}

	public boolean setSpawnPoint(Location loc) {
		if (isInArenaBounds(loc)) {
			spawnpoint = loc.toVector();
			return true;
		}
		return false;
	}

	public boolean setSpectatorsSpawn(Location loc) {
		if (isInArenaBounds(loc)) {
			spectatorspawn = loc.toVector();
			return true;
		}
		return false;
	}

	public void removeSpectatorsSpawn() {
		spectatorspawn = null;
	}

	public void setMaxPlayers(int maxplayers) {
		maxPlayers = maxplayers;
	}

	public void setMinPlayers(int minplayers) {
		minPlayers = minplayers;
	}

	public void setVotePercent(double votepercent) {
		votesPercent = votepercent;
	}

	public void setTimeLimit(int timelimit) {
		this.timelimit = timelimit;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}

	public void setTeleportDestination(TeleportDestination teleportDest) {
		this.teleportDest = teleportDest;
	}

	public void setDamageEnabled(DamageEnabled damageEnabled) {
		this.damageEnabled = damageEnabled;
	}

	public void saveToConfig() {
		FileConfiguration config = new YamlConfiguration();
		// save arena bounds
		try {
			config.set("world", world);
			config.set("p1", p1);
			config.set("p2", p2);
		} catch (Exception e) {
		}
		// save gamelevel destroy delay
		config.set("gameleveldestroydelay", gameleveldestroydelay);
		// save looselevel
		try {
			loselevel.saveToConfig(config);
		} catch (Exception e) {
		}
		// save spawnpoint
		try {
			config.set("spawnpoint", spawnpoint);
		} catch (Exception e) {
		}
		// save spectators spawn
		try {
			config.set("spectatorspawn", spectatorspawn);
		} catch (Exception e) {
		}
		// save maxplayers
		config.set("maxPlayers", maxPlayers);
		// save minplayers
		config.set("minPlayers", minPlayers);
		// save vote percent
		config.set("votePercent", votesPercent);
		// save timelimit
		config.set("timelimit", timelimit);
		// save countdown
		config.set("countdown", countdown);
		// save teleport destination
		config.set("teleportto", teleportDest.toString());
		// save damage enabled
		config.set("damageenabled", damageEnabled.toString());
		config.set("testmode", testmode);
		config.set("finished", finished);
		config.set("enableOnRestart", enableOnRestart);
		// save kits
		kits.saveToConfig(config);
		// save rewards
		rewards.saveToConfig(config);
		try {
			config.save(arena.getArenaFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromConfig() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
		// load arena world location
		world = config.getString("world", null);
		// load arena bounds
		p1 = config.getVector("p1", null);
		p2 = config.getVector("p2", null);
		// load gamelevel destroy delay
		gameleveldestroydelay = config.getInt("gameleveldestroydelay", gameleveldestroydelay);
		// load looselevel
		loselevel.loadFromConfig(config);
		// load spawnpoint
		spawnpoint = config.getVector("spawnpoint", null);
		// load spectators spawn
		spectatorspawn = config.getVector("spectatorspawn", null);
		// load maxplayers
		maxPlayers = config.getInt("maxPlayers", maxPlayers);
		// load minplayers
		minPlayers = config.getInt("minPlayers", minPlayers);
		// load vote percent
		votesPercent = config.getDouble("votePercent", votesPercent);
		// load timelimit
		timelimit = config.getInt("timelimit", timelimit);
		// load countdown
		countdown = config.getInt("countdown", countdown);
		// load teleport destination
		teleportDest = TeleportDestination.valueOf(config.getString("teleportto", TeleportDestination.PREVIOUS.toString()));
		// load damage enabled
		damageEnabled = DamageEnabled.valueOf(config.getString("damageenabled", DamageEnabled.NO.toString()));
		testmode = config.getBoolean("testmode");
		finished = config.getBoolean("finished");
		enableOnRestart = config.getBoolean("enableOnRestart");
		if (!finished && arena.getStructureManager().isArenaConfigured()) {
			finished = true;
		}
		// load kits
		kits.loadFromConfig(config);
		// load rewards
		rewards.loadFromConfig(config);
	}

}
