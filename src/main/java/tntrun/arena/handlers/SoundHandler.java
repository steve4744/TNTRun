package tntrun.arena.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.common.base.Enums;

import tntrun.TNTRun;

public class SoundHandler {

	private final TNTRun plugin;

	private String plingSound;

	public SoundHandler(TNTRun plugin) {
		this.plugin = plugin;
		this.plingSound = getPling();
	}

	public void playSound(Player player, String soundname) {
		if (isSoundEnabled(soundname)) {
			player.playSound(player.getLocation(), getSound(soundname), getVolume(soundname), getPitch(soundname));
		}
	}

	public void playBlockSound(Block block, String soundname) {
		if (isSoundEnabled(soundname)) {
			block.getWorld().playSound(block.getLocation(), getSound(soundname), getVolume(soundname), getPitch(soundname));
		}
	}

	public void playPlingSound(Player player, float volume, float pitch) {
		player.playSound(player.getLocation(), Sound.valueOf(plingSound), volume, pitch);
	}

	/**
	 * Get the sound to be played.
	 * Will return null if invalid.
	 *
	 * @param string path
	 * @return sound
	 */
	private Sound getSound(String path) {
		return Enums.getIfPresent(Sound.class, plugin.getConfig().getString("sounds." + path + ".sound")).orNull();
	}

	/**
	 * Get the volume of the sound to be played.
	 * Default is 1.0F
	 *
	 * @param string path
	 * @return volume
	 */
	private float getVolume(String path) {
		float volume = (float) plugin.getConfig().getDouble("sounds." + path + ".volume", 1.0);
		return volume > 0 ? volume : 1.0F;
	}

	/**
	 * Get the pitch of the sound to be played.
	 * Default is 1.0F
	 *
	 * @param string path
	 * @return pitch
	 */
	private float getPitch(String path) {
		float pitch = (float) plugin.getConfig().getDouble("sounds." + path + ".pitch", 1.0);
		return (pitch > 0.5 && pitch < 2.0) ? pitch : 1.0F;
	}

	public boolean isSoundEnabled(String path) {
		return plugin.getConfig().getBoolean("sounds." + path + ".enabled");
	}

	private String getPling() {
		String version = Bukkit.getBukkitVersion().split("-")[0];
		return (version.contains("1.8") || version.contains("1.7")) ? "NOTE_PLING" : "BLOCK_NOTE_PLING";
	}
}
