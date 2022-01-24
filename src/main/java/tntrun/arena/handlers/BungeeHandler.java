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
		Arena arena = plugin.amanager.getBungeeArena();
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
		Arena arena = plugin.amanager.getBungeeArena();
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
		if (!plugin.getConfig().getBoolean("bungeecord.teleporttohub")) {
			return;
		}
		Arena arena = plugin.amanager.getBungeeArena();
		if (arena == null || (!event.getPlayer().hasPermission("tntrun.spectate") && !arena.getPlayerHandler().checkJoin(event.getPlayer())) ){
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You cannot join the arena at this time");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!plugin.isBungeecord()) {
			return;
		}
		if (!plugin.getConfig().getBoolean("bungeecord.teleporttohub")) {
			plugin.getGlobalLobby().joinLobby(event.getPlayer());
			return;
		}
		Arena arena = plugin.amanager.getBungeeArena();
		if (arena == null) {
			return;
		}
		Player player = event.getPlayer();
		// player doesn't have permission and we know he can join because of onLogin check
		if (!player.hasPermission("tntrun.spectate")) {
			arena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoothers);
			return;
		}
		// player has permission and has the 'playorspectate' option, so do join checks
		if (plugin.getConfig().getBoolean("bungeecord.playorspectate") && arena.getPlayerHandler().checkJoin(player)) {
			arena.getPlayerHandler().spawnPlayer(player, Messages.playerjoinedtoothers);
			return;
		}

		if (!arena.getPlayerHandler().canSpectate(player)) {
			plugin.getServer().getScheduler().runTaskLater(plugin, () ->
					connectToHub(player), 20L);
			return;
		}

		arena.getPlayerHandler().spectatePlayer(player, Messages.playerjoinedasspectator, "");
	}
}
