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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
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
	private Map<String, Integer> doublejumps = new HashMap<String, Integer>();   // playername -> number_of_doublejumps
	private List<String> pparty = new ArrayList<String>();
	private HashSet<String> votes = new HashSet<String>();

	public PlayerHandler(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public boolean checkJoin(Player player) {
		return checkJoin(player, false);
	}

	/**
	 * Returns whether a player is able to join the arena at this time.
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
				Messages.sendMessage(player, Messages.trprefix + Messages.arenarunning);
			}
			return false;
		}
		if (!player.hasPermission("tntrun.join")) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.nopermission);
			}
			return false;
		}
		if (arena.getPlayersManager().getPlayersCount() == arena.getStructureManager().getMaxPlayers()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.limitreached);
			}
			return false;
		}
		if (arena.getStructureManager().hasFee()) {
			double fee = arena.getStructureManager().getFee();
			if (!arena.getArenaEconomy().hasFunds(player, fee)) {
				if (!silent) {
					Messages.sendMessage(player, Messages.trprefix + Messages.arenanofee.replace("{FEE}", arena.getStructureManager().getArenaCost(arena)));
				}
				return false;
			}
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.arenafee.replace("{FEE}", arena.getStructureManager().getArenaCost(arena)));
			}
		}
		return true;
	}

	/**
	 * Returns whether a player is able to join the arena either as a spectator or player.
	 * @param player
	 * @param silent
	 * @return
	 */
	public boolean preJoinChecks(Player player, boolean silent) {
		if (!arena.getStatusManager().isArenaEnabled()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.arenadisabled);
			}
			return false;
		}
		if (arena.getStructureManager().getWorld() == null) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.arenawolrdna);
			}
			return false;
		}
		if (arena.getStatusManager().isArenaRegenerating()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.arenaregenerating);
			}
			return false;
		}
		if (player.isInsideVehicle()) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.arenavehicle);
			}
			return false;
		}
		if (plugin.amanager.getPlayerArena(player.getName()) != null) {
			if (!silent) {
				Messages.sendMessage(player, Messages.trprefix + Messages.arenajoined);
			}
			return false;
		}
		return true;
	}

	public void spawnPlayer(final Player player, String msgtoplayer, String msgtoarenaplayers) {
		plugin.getPData().storePlayerLocation(player);
		player.teleport(arena.getStructureManager().getSpawnPoint());
		for (Player aplayer : Bukkit.getOnlinePlayers()) {
			aplayer.showPlayer(plugin, player);
		}

		storePlayerData(player);

		if (plugin.isMCMMO() && !arena.getStructureManager().getDamageEnabled().equals(DamageEnabled.NO)) {
			allowFriendlyFire(player);
		}

		if (!arena.getStatusManager().isArenaStarting()) {
			arena.getGameHandler().count = arena.getStructureManager().getCountdown();
		}

		if (!plugin.getConfig().getBoolean("special.UseTitle")) {
			Messages.sendMessage(player, Messages.trprefix + msgtoplayer);
		}	

		arena.getPlayersManager().add(player);

		msgtoarenaplayers = FormattingCodesParser.parseFormattingCodes(msgtoarenaplayers).replace("{PLAYER}", player.getName()).replace("{RANK}", getDisplayName(player));;

		for (Player oplayer : arena.getPlayersManager().getPlayers()) {
			Messages.sendMessage(oplayer, Messages.trprefix + msgtoarenaplayers);
			TitleMsg.sendFullTitle(oplayer, TitleMsg.join.replace("{PLAYER}", player.getName()), TitleMsg.subjoin.replace("{PLAYER}", player.getName()), 10, 20, 20, plugin);
		}

		new BukkitRunnable() {
			@Override
			public void run(){
				addLeaveItem(player);
				
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

		if (plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
			int amount = getAllowedDoubleJumps(player, plugin.getConfig().getInt("freedoublejumps.amount", 0));
			if (amount > 0) {
				doublejumps.put(player.getName(), amount);
			}
		} else {
			if (plugin.shop.hasDoubleJumps(player)) {
				doublejumps.put(player.getName(), plugin.getConfig().getInt("doublejumps." + player.getName()));
			}
		}

		if (plugin.getConfig().getBoolean("special.UseBossBar")) {
			Bars.addPlayerToBar(player, arena.getArenaName());
		} else {
			String message = Messages.playerscountinarena;
			message = message.replace("{COUNT}", String.valueOf(arena.getPlayersManager().getPlayersCount()));
			Messages.sendMessage(player, Messages.trprefix + message);
		}

		plugin.signEditor.modifySigns(arena.getArenaName());
		arena.getScoreboardHandler().createWaitingScoreBoard();

		if (!arena.getStatusManager().isArenaStarting()) {
			double progress = (double) arena.getPlayersManager().getPlayersCount() / arena.getStructureManager().getMinPlayers(); 
			
			Bars.setBar(arena, Bars.waiting, arena.getPlayersManager().getPlayersCount(), 0, progress, plugin);
			for (Player oplayer : arena.getPlayersManager().getPlayers()) {
				plugin.sound.NOTE_PLING(oplayer, 5, 999);
			}
		}

		plugin.getServer().getPluginManager().callEvent(new PlayerJoinArenaEvent(player, arena));

		// check for game start
		if (!arena.getStatusManager().isArenaStarting() && arena.getPlayersManager().getPlayersCount() == arena.getStructureManager().getMinPlayers()) {
			arena.getGameHandler().runArenaCountdown();
		}
	} 

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
		}

		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		clearPotionEffects(player);
		player.setAllowFlight(true);
		player.setFlying(true);

		for (Player oplayer : Bukkit.getOnlinePlayers()) {
			oplayer.hidePlayer(plugin, player);
		}

		Messages.sendMessage(player, Messages.trprefix + msgtoplayer.replace("{ARENA}", arena.getArenaName()));
		plugin.signEditor.modifySigns(arena.getArenaName());

		msgtoarenaplayers = msgtoarenaplayers.replace("{PLAYER}", player.getName()).replace("{RANK}", getDisplayName(player));
		for (Player oplayer : arena.getPlayersManager().getAllParticipantsCopy()) {
			Messages.sendMessage(oplayer, Messages.trprefix + msgtoarenaplayers);
		}

		arena.getPlayersManager().addSpectator(player);

		new BukkitRunnable() {
			@Override
			public void run(){
				addLeaveItem(player);

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
	 * @param player
	 */
	public void dispatchPlayer(Player player) {
		if (arena.getPlayersManager().getPlayersCount() == 1) {
			player.teleport(arena.getStructureManager().getSpawnPoint());
		} else if (arena.getStructureManager().getSpectatorSpawnVector() != null) {
			spectatePlayer(player, Messages.playerlosttoplayer, Messages.playerlosttoothers);
		} else {
			leavePlayer(player, Messages.playerlosttoplayer, Messages.playerlosttoothers);
		}
	}

	// remove player from arena
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
		Messages.sendMessage(player, Messages.trprefix + msgtoplayer);
		plugin.signEditor.modifySigns(arena.getArenaName());
		if (!arena.getStatusManager().isArenaRunning()) {
			arena.getScoreboardHandler().createWaitingScoreBoard();
		}

		msgtoarenaplayers = msgtoarenaplayers.replace("{PLAYER}", player.getName()).replace("{RANK}", getDisplayName(player));
		for (Player oplayer : arena.getPlayersManager().getAllParticipantsCopy()) {
			Messages.sendMessage(oplayer, Messages.trprefix + msgtoarenaplayers);
			if (!arena.getStatusManager().isArenaStarting() && !arena.getStatusManager().isArenaRunning()) {
				double progress = (double) arena.getPlayersManager().getPlayersCount() / arena.getStructureManager().getMinPlayers();
				Bars.setBar(arena, Bars.waiting, arena.getPlayersManager().getPlayersCount(), 0, progress, plugin);
			}
		}
		plugin.getServer().getPluginManager().callEvent(new PlayerLeaveArenaEvent(player, arena));
	}

	protected void leaveWinner(Player player, String msgtoplayer) {
		arena.getScoreboardHandler().removeScoreboard(player);
		player.setFlying(false);
		removePlayerFromArenaAndRestoreState(player, true);
		Messages.sendMessage(player, Messages.trprefix + msgtoplayer);
		plugin.signEditor.modifySigns(arena.getArenaName());
		plugin.signEditor.refreshLeaderBoards();
	}

	private void removePlayerFromArenaAndRestoreState(Player player, boolean winner) {
		votes.remove(player.getName());
		Bars.removeBar(player, arena.getArenaName());
		resetDoubleJumps(player);
		arena.getPlayersManager().remove(player);
		clearPotionEffects(player);

		plugin.getPData().restorePlayerHunger(player);
		plugin.getPData().restorePlayerPotionEffects(player);
		plugin.getPData().restorePlayerArmor(player);
		plugin.getPData().restorePlayerInventory(player);
		plugin.getPData().restorePlayerLevel(player);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 80, true));

		if (plugin.isBungeecord()) {
			plugin.getBungeeHandler().connectToHub(player);
		} else {
			connectToLobby(player);
		}

		// reward player before restoring gamemode if player is winner
		if (winner) {
			arena.getStructureManager().getRewards().rewardPlayer(player);
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
			arena.getGameHandler().stopArena();
		}
	}

	/**
	 * On a multiworld server, return the player to the lobby or previous location.
	 * @param player
	 */
	private void connectToLobby(Player player) {
		if (arena.getStructureManager().getTeleportDestination() == TeleportDestination.LOBBY && plugin.globallobby.isLobbyLocationWorldAvailable()) {
			player.teleport(plugin.globallobby.getLobbyLocation());
			plugin.getPData().clearPlayerLocation(player);
		} else {
			plugin.getPData().restorePlayerLocation(player);
		}
	}

	/**
	 * Players waiting for the game to start can vote to force start the game before the minimum number of players is reached.
	 * The number of votes required is determined by the value of the 'votepercent' setting.
	 * Players who have joined the arena as spectators are not allowed to vote.
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
		ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("items.stats.material")));
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("items.stats.name")));
	    item.setItemMeta(meta);

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
		Material leaveItem = Material.getMaterial(plugin.getConfig().getString("items.leave.material"));
		if (leaveItem == null) {
			leaveItem = Material.getMaterial("GREEN_BED");
			plugin.getConfig().set("items.leave.material", leaveItem.toString());
			plugin.saveConfig();
		}
		ItemStack item = new ItemStack(leaveItem);
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

	public void allocateKits() {
		Random rnd = new Random();
		HashSet<String> kits = plugin.kitmanager.getKits();
		if (kits.size() > 0) {
			String[] kitnames = kits.toArray(new String[kits.size()]);
			for (Player player : arena.getPlayersManager().getPlayers()) {
				plugin.kitmanager.giveKit(kitnames[rnd.nextInt(kitnames.length)], player);
				//kits will replace the GUI items, so give each player the leave item again
				addLeaveItem(player);
			}
		}
	}

	public boolean hasDoubleJumps(Player player) {
		return getDoubleJumps(player) > 0;
	}

	public int getDoubleJumps(Player player) {
		return doublejumps.get(player.getName()) != null ? doublejumps.get(player.getName()) : 0;
	}

	public void decrementDoubleJumps(Player player) {
		if (getDoubleJumps(player) > 0) {
			doublejumps.put(player.getName(), getDoubleJumps(player) - 1);
		}
	}

	public void incrementDoubleJumps(Player player, Integer amount) {
		doublejumps.put(player.getName(), getDoubleJumps(player) + amount);
	}

	private void resetDoubleJumps(Player player) {
		if (!plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
			if (hasDoubleJumps(player)) {
				plugin.getConfig().set("doublejumps." + player.getName(), getDoubleJumps(player));
			} else {
				plugin.getConfig().set("doublejumps." + player.getName(), null);
			}
			plugin.saveConfig();
		}
		doublejumps.remove(player.getName());
	}

	/**
	 * The maximum number of double jumps the player is allowed. If permissions are used,
	 * return the lower number of the maximum and number allowed by the permission node.
	 * @param player
	 * @param max allowed double jumps
	 * @return integer representing the number of double jumps to give player
	 */
	public int getAllowedDoubleJumps(Player player, Integer max) {
		if (!plugin.getConfig().getBoolean("special.UseDoubleJumpPermissions") || max <= 0) {
			return max;
		}
		String permissionPrefix = "tntrun.doublejumps.";
		for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
				String permission = attachmentInfo.getPermission();
				if (!Utils.isNumber(permission.substring(permission.lastIndexOf(".") + 1))) {
					return 0;
				}
				return Math.min(Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1)), max);
			}
		}
		return max;
	}

	/**
	 * Allow players in mcMMO parties to PVP.
	 * If vault has detected a permissions plugin, then give the player the mcMMO friendly fire permission.
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
	 * @param player
	 * @return rank
	 */
	private String getRank(Player player) {
		if (!plugin.getConfig().getBoolean("UseRankInChat.enabled")) {
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

	public String getDisplayName(Player player) {
		return getRank(player) == null ? "" : getRank(player);
	}

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
	}
}
