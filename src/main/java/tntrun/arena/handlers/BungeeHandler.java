package tntrun.arena.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class BungeeHandler implements Listener {

	private TNTRun plugin;

	public BungeeHandler(TNTRun plugin) {
		this.plugin = plugin;
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Teleport player to the Bungeecord server at the end of the game.
	 * @param player
	 */
	public void connectToHub(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(getHubServerName());
		player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	private String getHubServerName() {
		return plugin.getConfig().getString("bungeecord.hub");
	}

	private String getMOTD() {
		Arena arena = plugin.getBungeeArena();
		if (arena == null) {
			return "";
		}
		if (arena.getStatusManager().isArenaStarting() && (arena.getGameHandler().count <= 3)) {
			return Messages.arenarunning;
		}
		return arena.getStatusManager().getArenaStatusMesssage();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerListPing(ServerListPingEvent event) {
		Arena arena = plugin.getBungeeArena();
		if (arena == null || !plugin.getConfig().getBoolean("bungeecord.useMOTD")) {
			return;
		}
		event.setMaxPlayers(arena.getStructureManager().getMaxPlayers());
		event.setMotd(this.getMOTD());
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if (!plugin.isBungeecord()) {
			return;
		}
		Arena arena = plugin.getBungeeArena();
		if (arena == null || !arena.getPlayerHandler().checkJoin(event.getPlayer())) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You cannot join the arena at this time");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (plugin.isBungeecord()) {
			Arena arena = plugin.getBungeeArena();
			if (arena == null) {
				return;
			}
			arena.getPlayerHandler().spawnPlayer(event.getPlayer(), Messages.playerjoinedtoothers);
		}
	}
}
