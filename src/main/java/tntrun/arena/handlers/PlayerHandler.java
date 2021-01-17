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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.api.PartyAPI;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.arena.structure.StructureManager.DamageEnabled;
import tntrun.arena.structure.StructureManager.TeleportDestination;
import tntrun.events.PlayerJoinArenaEvent;
import tntrun.events.PlayerLeaveArenaEvent;
import tntrun.events.PlayerSpectateArenaEvent;
import tntrun.utils.Bars;
import tntrun.utils.FormattingCodesParser;
import tntrun.utils.TitleMsg;
import tntrun.utils.Utils;
import tntrun.messages.Messages;

public class PlayerHandler {

	private TNTRun plugin;
	private Arena arena;
	private Map<String, Integer> doublejumps = new HashMap<>();   // playername -> doublejumps
	private List<String> pparty = new ArrayList<>();
	private HashSet<String> votes = new HashSet<>();
	private Map<String, Location> spawnmap = new HashMap<>();
	private String linkedKitName;

	public PlayerHandler(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public boolean checkJoin(Player player) {
		return checkJoin(player, false);
	}

	/**
	 * Returns whether a player is able to join the arena at this time.
	 *
	 * @param player
	 * @param silent
	 * @return
	 */
	public boolean checkJoin(Player player, boolean silent) {
		if (!preJoinChecks(player, silent)) {
			return false;
		}
		if (arena.getStatusManager().isArenaRunning()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.arenarunning);
			}
			return false;
		}
		if (!player.hasPermission("tntrun.join")) {
			if (!silent) {
				Messages.sendMessage(player, Messages.nopermission);
			}
			return false;
		}
		if (arena.getPlayersManager().getPlayersCount() == arena.getStructureManager().getMaxPlayers()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.limitreached);
			}
			return false;
		}

		return processFee(player, silent);
	}

	/**
	 * Returns whether the player has the funds to pay the arena entry fee, if one exists.
	 * If silent is true, the fee is only checked but not taken from the player.
	 * If silent is false then the fee will be charged and removed from the player's balance
	 * or inventory.
	 *
	 * @param player
	 * @param silent
	 * @return
	 */
	public boolean processFee(Player player, boolean silent) {
		if (arena.getStructureManager().hasFee()) {
			double fee = arena.getStructureManager().getFee();
			if (!arena.getArenaEconomy().hasFunds(player, fee, silent)) {
				if (!silent) {
					Messages.sendMessage(player, Messages.arenanofee.replace("{FEE}", arena.getStructureManager().getArenaCost()));
				}
				return false;
			}
			if (!silent) {
				Messages.sendMessage(player, Messages.arenafee.replace("{FEE}", arena.getStructureManager().getArenaCost()));
			}
		}
		return true;
	}

	/**
	 * Returns whether a player is able to join the arena either as a spectator or player.
	 *
	 * @param player
	 * @param silent
	 * @return
	 */
	public boolean preJoinChecks(Player player, boolean silent) {
		if (!arena.getStatusManager().isArenaEnabled()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.arenadisabled);
			}
			return false;
		}
		if (arena.getStructureManager().getWorld() == null) {
			if (!silent) {
				Messages.sendMessage(player, Messages.arenawolrdna);
			}
			return false;
		}
		if (arena.getStatusManager().isArenaRegenerating()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.arenaregenerating);
			}
			return false;
		}
		if (player.isInsideVehicle()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.arenavehicle);
			}
			return false;
		}
		if (plugin.amanager.getPlayerArena(player.getName()) != null) {
			if (!silent) {
				Messages.sendMessage(player, Messages.arenajoined);
			}
			return false;
		}
		return true;
	}

	/**
	 * In a standard, single spawnpoint arena this method will simply return the arena spawnpoint location.
	 * In a multi-spawnpoint arena, the next available spawnpoint is allocated to the player and cached. If a player
	 * leaves the arena and then rejoins, he will be given his cached spawnpoint. If the cached spawnpoint has since
	 * been allocated to another player, then he will be given the next available spawnpoint.
	 *
	 * @param playerName
	 * @return the spawnpoint location
	 */
	private Location getSpawnPoint(String playerName) {
		Location loc = null;
		if (spawnmap.containsKey(playerName) && (arena.getStructureManager().getFreeSpawnList().contains(spawnmap.get(playerName).toVector()))) {
			loc = spawnmap.get(playerName);
			arena.getStructureManager().getFreeSpawnList().remove(loc.toVector());
		} else {
			loc = arena.getStructureManager().getSpawnPoint();
			if (arena.getStructureManager().hasAdditionalSpawnPoints()) {
				spawnmap.put(playerName, loc);
			}
		}
		return loc != null ? loc : arena.getStructureManager().getPrimarySpawnPoint();
	}

	/**
	 * Teleport the player to an arena spawn point.
	 * Store the player data and put the arena items into the hotbar.
	 *
	 * @param player
	 * @param msgtoarenaplayers Player join message sent to players in the arena
	 */
	public void spawnPlayer(final Player player, String msgtoarenaplayers) {
		plugin.getPData().storePlayerLocation(player);

		player.teleport(getSpawnPoint(player.getName()));

		for (Player aplayer : Bukkit.getOnlinePlayers()) {
			aplayer.showPlayer(plugin, player);
		}
		arena.getPlayersManager().add(player);
		if (Utils.debug()) {
			plugin.getLogger().info("Player " + player.getName() + " joined arena " + arena.getArenaName());
			plugin.getLogger().info("Players in arena: " + arena.getPlayersManager().getPlayersCount());
		}

		storePlayerData(player);

		if (plugin.isMCMMO() && !arena.getStructureManager().getDamageEnabled().equals(DamageEnabled.NO)) {
			allowFriendlyFire(player);
		}

		if (!arena.getStatusManager().isArenaStarting()) {
			arena.getGameHandler().count = arena.getStructureManager().getCountdown();
		}

		int playerCount = arena.getPlayersManager().getPlayersCount();
		if (playerCount == 1 && plugin.getConfig().getBoolean("invitationmessage.enabled")) {
			String welcomeJoinMessage = getFormattedMessage(player, Messages.playerjoininvite);

			for (Player aplayer : Bukkit.getOnlinePlayers()) {
				Utils.displayJoinMessage(aplayer, arena.getArenaName(), welcomeJoinMessage);
			}
		}

		for (Player oplayer : arena.getPlayersManager().getPlayers()) {
			TitleMsg.sendFullTitle(oplayer, TitleMsg.join.replace("{PLAYER}", player.getName()), TitleMsg.subjoin.replace("{PLAYER}", player.getName()), 10, 20, 20, plugin);
			if (playerCount != 1 || !plugin.getConfig().getBoolean("invitationmessage.enabled")) {
				Messages.sendMessage(oplayer, getFormattedMessage(player, msgtoarenaplayers));
			}
		}

		new BukkitRunnable() {
			@Override
			public void run(){
				if (plugin.getConfig().getBoolean("items.leave.use")) {
					addLeaveItem(player);
				}
				if (plugin.getConfig().getBoolean("items.vote.use")) {
					addVote(player);
				}
				if (plugin.getConfig().getBoolean("items.shop.use")) {
					addShop(player);
				}
				if (plugin.getConfig().getBoolean("items.info.use")) {
					addInfo(player);
				}
				if (plugin.getConfig().getBoolean("items.stats.use")) {
					addStats(player);
				}
				if (plugin.isHeadsPlus() && plugin.getConfig().getBoolean("items.heads.use")) {
					addHeads(player);
				}
			}
		}.runTaskLater(plugin, 5L);

		cacheDoubleJumps(player);

		if (plugin.getConfig().getBoolean("special.UseBossBar")) {
			Bars.addPlayerToBar(player, arena.getArenaName());
		} else {
			String message = Messages.playerscountinarena;
			message = message.replace("{COUNT}", String.valueOf(arena.getPlayersManager().getPlayersCount()));
			Messages.sendMessage(player, message);
		}

		plugin.signEditor.modifySigns(arena.getArenaName());
		arena.getScoreboardHandler().createWaitingScoreBoard();

		if (!arena.getStatusManager().isArenaStarting()) {
			double progress = (double) arena.getPlayersManager().getPlayersCount() / arena.getStructureManager().getMinPlayers(); 
			
			Bars.setBar(arena, Bars.waiting, arena.getPlayersManager().getPlayersCount(), 0, progress, plugin);
			for (Player oplayer : arena.getPlayersManager().getPlayers()) {
				plugin.getSound().NOTE_PLING(oplayer, 5, 999);
			}
		}

		plugin.getServer().getPluginManager().callEvent(new PlayerJoinArenaEvent(player, arena));

		// check for game start
		if (!arena.getStatusManager().isArenaStarting() && arena.getPlayersManager().getPlayersCount() == arena.getStructureManager().getMinPlayers()) {
			arena.getGameHandler().runArenaCountdown();
		}
	} 

	/**
	 * Teleport the player to the spectator spawn point. For players joining the arena as spectators, store their
	 * data for restore later.
	 *
	 * @param player
	 * @param msgtoplayer
	 * @param msgtoarenaplayers
	 */
	public void spectatePlayer(final Player player, String msgtoplayer, String msgtoarenaplayers) {
		// if existing spectator leaves bounds, send back to spectator spawn
		if (arena.getPlayersManager().isSpectator(player.getName())) {
			player.teleport(arena.getStructureManager().getSpectatorSpawn());
			return;
		}

		boolean isSpectatorOnly = false;
		if (!arena.getPlayersManager().getPlayers().contains(player)) {
			isSpectatorOnly = true;
			plugin.getPData().storePlayerLocation(player);
		}

		player.teleport(arena.getStructureManager().getSpectatorSpawn());

		if (!isSpectatorOnly) {
			arena.getPlayersManager().remove(player);
			arena.getGameHandler().lostPlayers++;
			arena.getScoreboardHandler().removeScoreboard(player);
		} else {
			storePlayerData(player);
			if (!arena.getStatusManager().isArenaStarting()) {
				arena.getGameHandler().count = arena.getStructureManager().getCountdown();
			}
			if (!arena.getStatusManager().isArenaRunning() && !arena.getStatusManager().isArenaRegenerating()) {
				arena.getScoreboardHandler().updateWaitingScoreboard(player);
			}
		}

		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		clearPotionEffects(player);
		player.setAllowFlight(true);
		player.setFlying(true);

		for (Player oplayer : Bukkit.getOnlinePlayers()) {
			oplayer.hidePlayer(plugin, player);
		}

		Messages.sendMessage(player, msgtoplayer.replace("{ARENA}", arena.getArenaName()));
		plugin.signEditor.modifySigns(arena.getArenaName());

		if (!isSpectatorOnly) {
			msgtoarenaplayers = msgtoarenaplayers.replace("{PLAYER}", player.getName()).replace("{RANK}", getDisplayName(player));
			for (Player oplayer : arena.getPlayersManager().getAllParticipantsCopy()) {
				Messages.sendMessage(oplayer, msgtoarenaplayers);
			}
		}
		arena.getPlayersManager().addSpectator(player);

		new BukkitRunnable() {
			@Override
			public void run(){
				if (plugin.getConfig().getBoolean("items.leave.use")) {
					addLeaveItem(player);
				}
				if (plugin.getConfig().getBoolean("items.info.use")) {
					addInfo(player);
				}
				if (plugin.getConfig().getBoolean("items.stats.use")) {
					addStats(player);
				}
			}
		}.runTaskLater(plugin, 5L);

		if (!isSpectatorOnly) {
			plugin.getServer().getPluginManager().callEvent(new PlayerSpectateArenaEvent(player, arena));
		}
	}
	/**
	 * If the winner attempts to leave, teleport to arena spawn.
	 * For other players, if we have a spectator spawn then we will move player to spectators, otherwise we will remove player from arena.
	 * Close inventory to prevent items being dragged out.
	 *
	 * @param player
	 */
	public void dispatchPlayer(Player player) {
		player.closeInventory();
		if (arena.getPlayersManager().getPlayersCount() == 1) {
			player.teleport(arena.getStructureManager().getSpawnPoint());
		} else if (arena.getStructureManager().getSpectatorSpawnVector() != null) {
			spectatePlayer(player, Messages.playerlosttoplayer, Messages.playerlosttoothers);
		} else {
			leavePlayer(player, Messages.playerlosttoplayer, Messages.playerlosttoothers);
		}
	}

	/**
	 * Remove the player from the arena. All players will be processed here except the winner.
	 * In a multi-spawnpoint arena, if a player leaves before the game starts, return the allocated
	 * spawnpoint to the free list.
	 *
	 * @param player
	 * @param msgtoplayer
	 * @param msgtoarenaplayers
	 */
	public void leavePlayer(Player player, String msgtoplayer, String msgtoarenaplayers) {
		// reset spectators
		boolean spectator = arena.getPlayersManager().isSpectator(player.getName());
		if (spectator) {
			arena.getPlayersManager().removeSpecator(player.getName());
			for (Player oplayer : Bukkit.getOnlinePlayers()) {
				oplayer.showPlayer(plugin, player);
			}
		} else if (arena.getStatusManager().isArenaRunning()) {
			arena.getGameHandler().lostPlayers++;
		}
		// disable flight for winner as well as spectators
		player.setAllowFlight(false);
		player.setFlying(false);

		arena.getScoreboardHandler().removeScoreboard(player);
		removePlayerFromArenaAndRestoreState(player, false);
		// should not send messages and other things when player is a spectator
		if (spectator) {
			return;
		}

		Messages.sendMessage(player, msgtoplayer);
		plugin.signEditor.modifySigns(arena.getArenaName());

		if (!arena.getStatusManager().isArenaRunning()) {
			arena.getScoreboardHandler().createWaitingScoreBoard();
			if (spawnmap.containsKey(player.getName())) {
				arena.getStructureManager().getFreeSpawnList().add(spawnmap.get(player.getName()).toVector());
			}
		}

		msgtoarenaplayers = msgtoarenaplayers
				.replace("{PLAYER}", player.getName())
				.replace("{RANK}", getDisplayName(player))
				.replace("{PS}", String.valueOf(arena.getPlayersManager().getPlayersCount()))
				.replace("{MPS}", String.valueOf(arena.getStructureManager().getMaxPlayers()));

		for (Player oplayer : arena.getPlayersManager().getAllParticipantsCopy()) {
			Messages.sendMessage(oplayer, msgtoarenaplayers);
			if (!arena.getStatusManager().isArenaStarting() && !arena.getStatusManager().isArenaRunning()) {
				double progress = (double) arena.getPlayersManager().getPlayersCount() / arena.getStructureManager().getMinPlayers();
				Bars.setBar(arena, Bars.waiting, arena.getPlayersManager().getPlayersCount(), 0, progress, plugin);
			}
		}
		plugin.getServer().getPluginManager().callEvent(new PlayerLeaveArenaEvent(player, arena));
	}

	/**
	 * The winner will leave the arena through this method.
	 *
	 * @param player
	 * @param msgtoplayer
	 */
	protected void leaveWinner(Player player, String msgtoplayer) {
		arena.getScoreboardHandler().removeScoreboard(player);
		player.setFlying(false);
		removePlayerFromArenaAndRestoreState(player, true);
		Messages.sendMessage(player, msgtoplayer);
		plugin.signEditor.modifySigns(arena.getArenaName());
		plugin.signEditor.refreshLeaderBoards();
		arena.getStructureManager().getFreeSpawnList().clear();
		spawnmap.clear();
		setLinkedKitName(null);
	}

	/**
	 * Restore each player's data and teleport to the lobby or previous location.
	 *
	 * @param player
	 * @param winner
	 */
	private void removePlayerFromArenaAndRestoreState(Player player, boolean winner) {
		votes.remove(player.getName());
		Bars.removeBar(player, arena.getArenaName());
		resetDoubleJumps(player);
		arena.getPlayersManager().remove(player);
		clearPotionEffects(player);
		if (arena.getStructureManager().hasCommandOnStop()) {
			arena.getGameHandler().executeCommandOnStop(player);
		}
		if (Utils.debug()) {
			plugin.getLogger().info("Player " + player.getName() + " left arena " + arena.getArenaName());
			plugin.getLogger().info("Players in arena: " + arena.getPlayersManager().getPlayersCount());
			plugin.getLogger().info("Spectators in arena: " + arena.getPlayersManager().getSpectators().size());
		}

		plugin.getPData().restorePlayerHunger(player);
		plugin.getPData().restorePlayerPotionEffects(player);
		plugin.getPData().restorePlayerArmor(player);
		plugin.getPData().restorePlayerInventory(player);
		plugin.getPData().restorePlayerLevel(player);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 80, true));
		arena.getScoreboardHandler().restorePrejoinScoreboard(player);

		if (plugin.isBungeecord()) {
			plugin.getBungeeHandler().connectToHub(player);
		} else {
			connectToLobby(player);
		}

		// reward players before restoring gamemode
		if (winner) {
			arena.getStructureManager().getRewards().rewardPlayer(player, 1);

		} else if (arena.getGameHandler().getPlaces().containsValue(player.getName())) {
			arena.getGameHandler().getPlaces().entrySet().forEach(e -> {
				if (e.getValue().equalsIgnoreCase(player.getName())) {
					arena.getStructureManager().getRewards().rewardPlayer(player, e.getKey());
				}
			});
		}

		plugin.getPData().restorePlayerGameMode(player);
		player.updateInventory();
		plugin.getPData().restorePlayerFlight(player);
		removeFriendlyFire(player);

		if (plugin.getConfig().getBoolean("shop.onleave.removepurchase")) {
			removePurchase(player);
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			player.setAllowFlight(true);
		}		

		if (arena.getStatusManager().isArenaRunning() && arena.getPlayersManager().getPlayersCount() == 0) {
			if (Utils.debug()) {
				plugin.getLogger().info("PH calling stopArena...");
			}
			arena.getGameHandler().stopArena();
		}
	}

	/**
	 * On a multiworld server, return the player to the lobby or previous location.
	 *
	 * @param player
	 */
	private void connectToLobby(Player player) {
		if (arena.getStructureManager().getTeleportDestination() == TeleportDestination.LOBBY && plugin.getGlobalLobby().isLobbyLocationWorldAvailable()) {
			player.teleport(plugin.getGlobalLobby().getLobbyLocation());
			plugin.getPData().clearPlayerLocation(player);
		} else {
			plugin.getPData().restorePlayerLocation(player);
		}
	}

	/**
	 * Players waiting for the game to start can vote to force start the game before the minimum number of players is reached.
	 * The number of votes required is determined by the value of the 'votepercent' setting.
	 * Players who have joined the arena as spectators are not allowed to vote.
	 *
	 * @param player
	 * @return true if player voted successfully
	 */
	public boolean vote(Player player) {
		if (!votes.contains(player.getName())) {
			votes.add(player.getName());

			arena.getScoreboardHandler().createWaitingScoreBoard();
			if (!arena.getStatusManager().isArenaStarting() && forceStart()) {
				arena.getGameHandler().runArenaCountdown();
			}
			return true;
		}
		return false;
	}

	public boolean forceStart() {
		if (arena.getPlayersManager().getPlayersCount() > 1 && votes.size() >= arena.getStructureManager().getMinPlayers() * arena.getStructureManager().getVotePercent()) {
			return true;
		}
		if (arena.getGameHandler().isForceStartByCommand()) {
			return true;
		}
		return false;
	}

	private void addInfo(Player player) {
		ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("items.info.material")));	     
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.info.name")));
		item.setItemMeta(meta);

		player.getInventory().setItem(plugin.getConfig().getInt("items.info.slot", 1), item);
	}

	private void addVote(Player player) {
		ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("items.vote.material")));     
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.vote.name")));
		item.setItemMeta(meta);

		player.getInventory().setItem(plugin.getConfig().getInt("items.vote.slot", 0), item);
	}

	private void addShop(Player player) {
		ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("items.shop.material"))); 
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.shop.name")));
		item.setItemMeta(meta);

		player.getInventory().setItem(plugin.getConfig().getInt("items.shop.slot", 2), item);
	}

	private void addStats(Player player) {
		Material statsMaterial = Material.getMaterial(plugin.getConfig().getString("items.stats.material"));
		ItemStack item = new ItemStack(statsMaterial);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.stats.name")));
		item.setItemMeta(meta);

		if (statsMaterial == Material.PLAYER_HEAD) {
			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			skullMeta.setOwningPlayer(player);
			item.setItemMeta(skullMeta);
		}

		player.getInventory().setItem(plugin.getConfig().getInt("items.stats.slot", 3), item);
	}

	private void addHeads(Player player) {
		ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("items.heads.material")));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.heads.name")));
		item.setItemMeta(meta);

		player.getInventory().setItem(plugin.getConfig().getInt("items.heads.slot", 4), item);
	}

	private void addLeaveItem(Player player) {
		// Old config files will have BED as leave item which is no longer valid on 1.13. Update any invalid material to valid one.
		Material leaveMaterial = Material.getMaterial(plugin.getConfig().getString("items.leave.material"));
		if (leaveMaterial == null) {
			leaveMaterial = Material.getMaterial("GREEN_BED");
			plugin.getConfig().set("items.leave.material", leaveMaterial.toString());
			plugin.saveConfig();
		}
		ItemStack item = new ItemStack(leaveMaterial);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.leave.name")));
		item.setItemMeta(im);

		player.getInventory().setItem(plugin.getConfig().getInt("items.leave.slot", 8), item);
	}

	public int getVotesCast() {
		return votes.size();
	}

	public void clearPotionEffects(Player player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	/**
	 * Allocate a kit to the player. If the arena has a linked kit, all players will
	 * receive that kit. If there a more than 1 linked kits then players will either
	 * receive the same linked kit or a random kit from the linked kit list, depending
	 * on whether "random" is true or false in the arena config file.
	 * If the arena has no linked kits, each player will receive a random kit.
	 * If the player has purchased a head, then this is preserved.
	 * The leave item is re-added to the inventory according to the config setting.
	 *
	 * @param player
	 */
	public void allocateKits(Player player) {
		String kit = null;
		List<String> kitnames = new ArrayList<>();
		if (arena.getStructureManager().hasLinkedKits()) {
			if (arena.getStructureManager().isRandomKit() || getLinkedKitName() == null) {
				kitnames = arena.getStructureManager().getLinkedKits();
			} else {
				kitnames.add(getLinkedKitName());
			}
		} else {
			kitnames.addAll(plugin.getKitManager().getKits());
		}
		if (Utils.debug()) {
			plugin.getLogger().info("kitnames = " + kitnames.toString());
		}

		kit = kitnames.size() > 1 ? getRandomKitName(kitnames) : kitnames.get(0);
		if (plugin.getKitManager().kitExists(kit)) {
			giveKitToPlayer(kit, player);
		} else {
			Messages.sendMessage(player, Messages.kitnotexists.replace("{KIT}", kit));
		}

		if (arena.getStructureManager().hasLinkedKits() && !arena.getStructureManager().isRandomKit()) {
			if (getLinkedKitName() == null) {
				setLinkedKitName(kit);
			}
		}
	}

	private String getRandomKitName(List<String> kits) {
		String[] kitnames = kits.toArray(new String[kits.size()]);
		Random rnd = new Random();
		return kitnames[rnd.nextInt(kitnames.length)];
	}

	private String getLinkedKitName() {
		return linkedKitName;
	}

	private void setLinkedKitName(String kitname) {
		linkedKitName = kitname;
	}

	private void giveKitToPlayer(String kitname, Player player) {
		ItemStack purchasedHead = null;
		if (player.getInventory().getHelmet() != null) {
			purchasedHead = new ItemStack(player.getInventory().getHelmet());
		}

		plugin.getKitManager().giveKit(kitname, player);

		if (plugin.getConfig().getBoolean("items.leave.use")) {
			addLeaveItem(player);
		}
		if (purchasedHead != null && purchasedHead.getType() == Material.PLAYER_HEAD) {
			player.getInventory().setHelmet(purchasedHead);
		}
	}

	private void cacheDoubleJumps(Player player) {
		int amount = 0;
		if (plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
			amount = plugin.shop.getAllowedDoubleJumps(player, plugin.getConfig().getInt("freedoublejumps.amount", 0));

		} else {
			if (plugin.shop.hasDoubleJumps(player)) {
				amount = plugin.getPData().getDoubleJumpsFromFile(player);
			}
		}
		if (amount > 0) {
			doublejumps.put(player.getName(), amount);
		}
	}

	public boolean hasDoubleJumps(Player player) {
		return getDoubleJumps(player) > 0;
	}

	public int getDoubleJumps(Player player) {
		return doublejumps.get(player.getName()) != null ? doublejumps.get(player.getName()) : 0;
	}

	public void decrementDoubleJumps(Player player) {
		if (hasDoubleJumps(player)) {
			doublejumps.put(player.getName(), getDoubleJumps(player) - 1);
		}
	}

	public void incrementDoubleJumps(Player player, Integer amount) {
		doublejumps.put(player.getName(), getDoubleJumps(player) + amount);
	}

	private void resetDoubleJumps(Player player) {
		if (!plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
			plugin.getPData().saveDoubleJumpsToFile(player, getDoubleJumps(player));
		}
		doublejumps.remove(player.getName());
	}

	/**
	 * Allow players in mcMMO parties to PVP.
	 * If vault has detected a permissions plugin, then give the player the mcMMO friendly fire permission.
	 *
	 * @param player
	 */
	private void allowFriendlyFire(Player player) {
		if (!plugin.getVaultHandler().isPermissions()) {
			return;
		}
		if (!PartyAPI.inParty(player)) {
			return;
		}
		if (!plugin.getVaultHandler().getPermissions().playerHas(player, "mcmmo.party.friendlyfire")) {
			plugin.getVaultHandler().getPermissions().playerAdd(player, "mcmmo.party.friendlyfire");
			if (!pparty.contains(player.getName())) {
				pparty.add(player.getName());
			}
		}
	}

	/**
	 * Restore the player's mcMMO friendly fire permission.
	 *
	 * @param player
	 */
	private void removeFriendlyFire(Player player) {
		if (pparty.contains(player.getName())) {
			pparty.remove(player.getName());
			plugin.getVaultHandler().getPermissions().playerRemove(player, "mcmmo.party.friendlyfire");
		}
	}

	/**
	 * Attempt to get a player's rank. This can be either the player's prefix or primary group.
	 *
	 * @param player
	 * @return rank
	 */
	private String getRank(Player player) {
		if (player == null || !plugin.getConfig().getBoolean("UseRankInChat.enabled")) {
			return null;
		}
		String rank = null;
		if (plugin.getVaultHandler().isPermissions()) {
			if (plugin.getConfig().getBoolean("UseRankInChat.usegroup")) {
				rank = plugin.getVaultHandler().getPermissions().getPrimaryGroup(player);
				if (rank != null) {
					rank = "[" + rank + "]";
				}
			}
		}
		if (plugin.getVaultHandler().isChat()) {
			if (plugin.getConfig().getBoolean("UseRankInChat.useprefix")) {
				rank = plugin.getVaultHandler().getChat().getPlayerPrefix(player);
			}
		}
		return rank;
	}

	/**
	 * Remove the cached purchase for the player. This can be when the game starts and the
	 * player receives the item, or if the player leaves the arena before the game starts.
	 *
	 * @param player
	 */
	public void removePurchase(Player player ) {
		if (plugin.shop.getPlayersItems().containsKey(player.getName())) {
			plugin.shop.getPlayersItems().remove(player.getName());
			plugin.shop.getBuyers().remove(player.getName());
		}
		if (plugin.shop.getPotionEffects(player) != null) {
			plugin.shop.removePotionEffects(player);
		}
	}

	/**
	 * Return the player's rank or an empty string for display purposes.
	 *
	 * @param player
	 * @return
	 */
	public String getDisplayName(Player player) {
		String rank = getRank(player);
		return rank == null ? "" : rank;
	}

	/**
	 * Store the players data before clearing the inventory and resetting the other
	 * data ready to play.
	 *
	 * @param player
	 */
	private void storePlayerData(Player player) {
		plugin.getPData().storePlayerGameMode(player);
		plugin.getPData().storePlayerFlight(player);
		player.setFlying(false);
		player.setAllowFlight(false);
		plugin.getPData().storePlayerLevel(player);
		plugin.getPData().storePlayerInventory(player);
		plugin.getPData().storePlayerArmor(player);
		plugin.getPData().storePlayerPotionEffects(player);
		plugin.getPData().storePlayerHunger(player);
		player.updateInventory();

		arena.getScoreboardHandler().storePrejoinScoreboard(player);
	}

	private String getFormattedMessage(Player player, String message) {
		return FormattingCodesParser.parseFormattingCodes(message)
				.replace("{PLAYER}", player.getName())
				.replace("{RANK}", getDisplayName(player))
				.replace("{ARENA}", arena.getArenaName())
				.replace("{PS}", String.valueOf(arena.getPlayersManager().getPlayersCount()))
				.replace("{MPS}", String.valueOf(arena.getStructureManager().getMaxPlayers()));
	}
}
