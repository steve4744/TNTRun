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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.events.ArenaStartEvent;
import tntrun.events.ArenaTimeoutEvent;
import tntrun.events.PlayerWinArenaEvent;
import tntrun.utils.Bars;
import tntrun.utils.TitleMsg;
import tntrun.utils.Utils;
import tntrun.messages.Messages;
import tntrun.signs.editor.SignEditor;

public class GameHandler {

	private TNTRun plugin;
	private Arena arena;
	public int lostPlayers = 0;
	private boolean activeStats = true;
	private Map<Integer, String> places = new HashMap<>();
	private SignEditor signEditor;

	public GameHandler(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
		count = arena.getStructureManager().getCountdown();
		signEditor = plugin.getSignEditor();
	}

	private int leavetaskid;

	public void startArenaAntiLeaveHandler() {
		leavetaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(
			plugin,
			new Runnable() {
				@Override
				public void run() {
					for (Player player : arena.getPlayersManager().getPlayersCopy()) {
						if (!arena.getStructureManager().isInArenaBounds(player.getLocation())) {
							//remove player during countdown, otherwise spectate
							if (arena.getStatusManager().isArenaStarting()) {
								arena.getPlayerHandler().leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
							} else {
								setPlaces(player.getName());
								arena.getPlayerHandler().dispatchPlayer(player);
							}
						}
					}
					for (Player player : arena.getPlayersManager().getSpectatorsCopy()) {
						if (!arena.getStructureManager().isInArenaBounds(player.getLocation())) {
							arena.getPlayerHandler().spectatePlayer(player, "", "");
						}
					}
				}
			},
			0, 1
		);
	}

	public void stopArenaAntiLeaveHandler() {
		Bukkit.getScheduler().cancelTask(leavetaskid);
	}

	// arena start handler (running status updater)
	int runtaskid;
	public int count;

	/**
	 * Set the arena status to "starting", change colour of glass block behind sign, and start
	 * the countdown. The countdown will stop if the number of players drops below the minimum required
	 * to start the game, unless the arena has been force started.
	 */
	public void runArenaCountdown() {
		count = arena.getStructureManager().getCountdown();
		arena.getStatusManager().setStarting(true);
		signEditor.modifySigns(arena.getArenaName());
		int antiCamping = Math.max(plugin.getConfig().getInt("anticamping.teleporttime"), 5);
		int startVisibleCountdown = arena.getStructureManager().getStartVisibleCountdown();
		runtaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// check if countdown should be stopped for various reasons
				if (arena.getPlayersManager().getPlayersCount() < arena.getStructureManager().getMinPlayers() && !arena.getPlayerHandler().forceStart()) {
					double progress = (double) arena.getPlayersManager().getPlayersCount() / arena.getStructureManager().getMinPlayers();
					Bars.setBar(arena, Bars.waiting, arena.getPlayersManager().getPlayersCount(), 0, progress, plugin);
					arena.getScoreboardHandler().createWaitingScoreBoard();
					stopArenaCountdown();
				} else
				// start arena if countdown is 0
				if (count == 0) {
					stopArenaCountdown();
					startArena();
					return;

				} else if(count == antiCamping) {
					String message = Messages.arenacountdown.replace("{COUNTDOWN}", String.valueOf(count));
					for (Player player : arena.getPlayersManager().getPlayers()) {
						if (isAntiCamping() && !arena.getStructureManager().hasAdditionalSpawnPoints()) {
							player.teleport(arena.getStructureManager().getSpawnPoint());
						}
						displayCountdown(player, count, message);
					}

				} else if (count <= startVisibleCountdown || count % 10 == 0) {
					String message = Messages.arenacountdown.replace("{COUNTDOWN}", String.valueOf(count));
					for (Player player : arena.getPlayersManager().getPlayers()) {
						displayCountdown(player, count, message);
					}
				}

				arena.getScoreboardHandler().createWaitingScoreBoard();
				double progressbar = (double) count / arena.getStructureManager().getCountdown();
				Bars.setBar(arena, Bars.starting, 0, count, progressbar, plugin);

				if (plugin.getConfig().getBoolean("usexpbar.countdown")) {
					for (Player player : arena.getPlayersManager().getPlayers()) {
						player.setLevel(count);
					}
				}
				count--;
			}
		}, 0, 20);
	}

	/**
	 * Stop the arena countdown, updating the arena status and join signs.
	 */
	public void stopArenaCountdown() {
		arena.getStatusManager().setStarting(false);
		signEditor.modifySigns(arena.getArenaName());
		Bukkit.getScheduler().cancelTask(runtaskid);
		count = arena.getStructureManager().getCountdown();
	}

	// main arena handler
	private int timeremaining;
	private int arenahandler;
	private int startingPlayers;
	private boolean forceStartByCmd;
	private boolean hasTimeLimit;

	/**
	 * Start the arena, removing the waiting scoreboard from spectator-only players.
	 */
	private void startArena() {
		arena.getStatusManager().setRunning(true);
		startingPlayers = arena.getPlayersManager().getPlayersCount();
		if (Utils.debug()) {
			plugin.getLogger().info("Arena " + arena.getArenaName() + " started");
			plugin.getLogger().info("Players in arena: " + startingPlayers);
		}

		plugin.getServer().getPluginManager().callEvent(new ArenaStartEvent(arena));

		for (Player player : arena.getPlayersManager().getSpectators()) {
			arena.getScoreboardHandler().removeScoreboard(player);
		}

		arena.getStructureManager().getRewards().setStartingPlayers(startingPlayers);
		setActiveStats(startingPlayers);

		String message = Messages.trprefix;
		int limit = arena.getStructureManager().getTimeLimit();
		if (limit != 0) {
			hasTimeLimit = true;
			message = message + Messages.arenastarted;
			message = message.replace("{TIMELIMIT}", String.valueOf(arena.getStructureManager().getTimeLimit()));
		} else {
			hasTimeLimit = false;
			message = message + Messages.arenanolimit;
		}

		for (Player player : arena.getPlayersManager().getPlayers()) {
			player.closeInventory();
			player.setLevel(0);
			if (plugin.useStats() && isStatsActive()) {
				plugin.getStats().addPlayedGames(player, 1);
			}
			if (arena.getPlayerHandler().hasDoubleJumps(player.getName())) {
				player.setAllowFlight(true);
			}

			Messages.sendMessage(player, message, false);
			plugin.getSound().ARENA_START(player);
			setGameInventory(player);

			TitleMsg.sendFullTitle(player, TitleMsg.start, TitleMsg.substart, 20, 20, 20, plugin);

			if (arena.getStructureManager().hasCommandOnStart()) {
				executeCommandOnStart(player);
			}
		}

		if (plugin.useStats() && isStatsActive()) {
			plugin.getStats().clearPlayedList();
		}
		signEditor.modifySigns(arena.getArenaName());

		timeremaining = limit * 20;
		arena.getScoreboardHandler().createPlayingScoreBoard();
		arenahandler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// stop arena if player count is 0
				if (arena.getPlayersManager().getPlayersCount() == 0) {
					if (Utils.debug()) {
						plugin.getLogger().info("GH calling stopArena...");
					}
					stopArena();
					return;
				}
				// kick all players if time is out
				if (isTimedOut()) {
					plugin.getServer().getPluginManager().callEvent(new ArenaTimeoutEvent(arena));
					places.clear();
					for (Player player : arena.getPlayersManager().getPlayersCopy()) {
						arena.getPlayerHandler().leavePlayer(player, Messages.arenatimeout, "");
					}
					return;
				}
				double progress = 1.0;
				int seconds = 0;
				if (hasTimeLimit) {
					progress = (double) timeremaining / (arena.getStructureManager().getTimeLimit() * 20);
					seconds = (int) Math.ceil((double)timeremaining / 20);
				}
				Bars.setBar(arena, Bars.playing, arena.getPlayersManager().getPlayersCount(), seconds, progress, plugin);
				for (Player player : arena.getPlayersManager().getPlayersCopy()) {
					if (plugin.getConfig().getBoolean("usexpbar.timelimit")) {
						player.setLevel(seconds);
					}
					handlePlayer(player);
				}
				timeremaining--;
			}
		}, plugin.getConfig().getInt("onstart.delay", 0) * 20, 1);
	}

	/**
	 * Stop the arena, removing any remaining players/spectators.
	 */
	public void stopArena() {
		if (!arena.getStatusManager().isArenaRunning()) {
			if (Utils.debug()) {
				plugin.getLogger().info("stopArena: arena not running. Exiting...");
			}
			return;
		}
		arena.getStatusManager().setRunning(false);
		for (Player player : arena.getPlayersManager().getAllParticipantsCopy()) {
			if (Utils.debug()) {
				plugin.getLogger().info("stopArena is removing player " + player.getName());
			}
			if (arena.getStructureManager().hasCommandOnStop()) {
				executeCommandOnStop(player);
			}
			arena.getScoreboardHandler().removeScoreboard(player);
			arena.getPlayerHandler().leavePlayer(player, "", "");
		}
		lostPlayers = 0;
		timeremaining = 0;
		forceStartByCmd = false;
		places.clear();
		arena.getPlayerHandler().clearRewardedPlayers();
		arena.getPlayersManager().setWinner(null);
		Bukkit.getScheduler().cancelTask(arenahandler);
		Bukkit.getScheduler().cancelTask(arena.getScoreboardHandler().getPlayingTask());
		signEditor.modifySigns(arena.getArenaName());
		if (arena.getStatusManager().isArenaEnabled()) {
			startArenaRegen();
		}
	}

	/**
	 * Remove the block under the player's feet, monitor player's position for lose.
	 * Check for winner, i.e. last player remaining, and for other places.
	 *
	 * @param player
	 */
	private void handlePlayer(final Player player) {
		Location plloc = player.getLocation();
		Location plufloc = plloc.clone().add(0, -1, 0);

		arena.getStructureManager().getGameZone().destroyBlock(plufloc);

		if (arena.getPlayersManager().getPlayersCount() == 1  && !arena.getStructureManager().isTestMode()) {
			startEnding(player);
			return;
		}

		if (arena.getStructureManager().getLoseLevel().isLoseLocation(plloc)) {
			if (arena.getPlayersManager().getPlayersCount() == 1) {
				// arena must be in test mode
				startEnding(player);
				return;
			}
			setPlaces(player.getName());
			arena.getPlayerHandler().dispatchPlayer(player);
		}
	}

	/**
	 * Get map containing the player names for 2nd, 3rd and other places.
	 *
	 * @return player names finishing in places to be displayed.
	 */
	public Map<Integer, String> getPlaces() {
		return places;
	}

	/**
	 * Store the names of the players finishing in the top places (2nd and 3rd by default).
	 * The players can be at the lose level or can have quit the game.
	 * Exit if the map already contains the player - 3rd placed spectator could
	 * subsequently quit and be rewarded multiple prizes.
	 *
	 * @param playerName
	 */
	public void setPlaces(String playerName) {
		if (places.containsValue(playerName) || arena.getPlayersManager().isSpectator(playerName) || !arena.getStatusManager().isArenaRunning()) {
			return;
		}
		int remainingPlayers = arena.getPlayersManager().getPlayersCount();
		places.put(remainingPlayers, playerName);
	}

	/**
	 * Regenerate the arena at the end of a game.
	 */
	private void startArenaRegen() {
		if (arena.getStatusManager().isArenaRegenerating()) {
			return;
		}
		arena.getStatusManager().setRegenerating(true);
		if (Utils.debug()) {
			plugin.getLogger().info("Arena regen started");
		}
		signEditor.modifySigns(arena.getArenaName());

		int delay = arena.getStructureManager().getGameZone().regen();

		new BukkitRunnable() {
			@Override
			public void run() {
				arena.getStatusManager().setRegenerating(false);
				signEditor.modifySigns(arena.getArenaName());

				if (!plugin.isBungeecord()) {
					cancel();
					return;
				}
				if (plugin.getConfig().getBoolean("bungeecord.randomarena")) {
					plugin.amanager.setBungeeArena();
				}
				if (plugin.getConfig().getBoolean("bungeecord.stopserver")) {
					new BukkitRunnable() {
						@Override
						public void run() {
							Bukkit.shutdown();
						}
					}.runTaskLater(plugin, 20);
				}
			}
		}.runTaskLater(plugin, delay);
	}

	/**
	 * Called when there is only 1 player left to update winner stats and
	 * teleport winner and spectators to the arena spawn point. It determines who
	 * should receive the broadcast results and then stops the arena.
	 *
	 * @param player winner
	 */
	private void startEnding(final Player player) {
		if (plugin.useStats() && isStatsActive()) {
			plugin.getStats().addWins(player, 1);
		}
		arena.getPlayersManager().setWinner(player.getName());
		TitleMsg.sendFullTitle(player, TitleMsg.win, TitleMsg.subwin, 20, 60, 20, plugin);
		arena.getPlayerHandler().clearPotionEffects(player);

		String message = getPodiumPlaces(player);
		if (plugin.getConfig().getInt("broadcastwinlevel") == 1) {
			for (Player all : arena.getPlayersManager().getAllParticipantsCopy()) {
				Messages.sendMessage(all, message, false);
			}
		} else if (plugin.getConfig().getInt("broadcastwinlevel") >= 2) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				Messages.sendMessage(all, message, false);
			}
		}

		plugin.getLogger().info("1. " + player.getName() + ", 2. " + getPlaces().get(2) + ", 3. " + getPlaces().get(3));

		// allow winner to fly at arena spawn
		player.setAllowFlight(true);
		player.setFlying(true);

		for(Player p : arena.getPlayersManager().getAllParticipantsCopy()) {
			plugin.getSound().ARENA_START(p);
			p.teleport(arena.getStructureManager().getSpawnPoint());
			p.closeInventory();
			p.getInventory().clear();
		}

		Bukkit.getScheduler().cancelTask(arenahandler);
		Bukkit.getScheduler().cancelTask(arena.getScoreboardHandler().getPlayingTask());

		plugin.getServer().getPluginManager().callEvent(new PlayerWinArenaEvent(player, arena));

		if (plugin.getConfig().getBoolean("fireworksonwin.enabled")) {
			new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					//cancel on duration -1 to avoid firework overrun
					if (i >= ((getFireworkDuration() * 2) - 1) || arena.getPlayersManager().getPlayersCount() == 0) {
						this.cancel();
					}
					Firework f = player.getWorld().spawn(arena.getStructureManager().getSpawnPoint(), Firework.class);
					FireworkMeta fm = f.getFireworkMeta();
					fm.addEffect(FireworkEffect.builder()
								.withColor(Color.GREEN).withColor(Color.RED)
								.withColor(Color.PURPLE)
								.with(Type.BALL_LARGE)
								.withFlicker()
								.build());
					fm.setPower(1);
					f.setFireworkMeta(fm);
					i++;
				}	
			}.runTaskTimer(plugin, 0, 10);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					//check if winner has not left the arena
					if (arena.getPlayersManager().getPlayersCount() == 1) {
						String msg = arena.getStructureManager().isTestMode() ? Messages.playerfinishedtestmode : Messages.playerwontoplayer;
						arena.getPlayerHandler().leaveWinner(player, msg);
						if (arena.getStructureManager().hasCommandOnStop()) {
							executeCommandOnStop(player);
						}
					}
					if (Utils.debug()) {
						plugin.getLogger().info("GH StartEnding calling stopArena...");
					}
					stopArena();

					if (plugin.getConfig().getStringList("commandsonwin").isEmpty()) {
						return;
					}

					final ConsoleCommandSender console = Bukkit.getConsoleSender();
					for(String commands : plugin.getConfig().getStringList("commandsonwin")) {
						Bukkit.dispatchCommand(console, commands.replace("{PLAYER}", player.getName()).replace("{ARENA}", arena.getArenaName()));
					}
				} catch (NullPointerException ex) {

				}
			}
		}.runTaskLater(plugin, 40 + (getFireworkDuration() * 20));
	}

	/**
	 * Get the number of seconds to run the fireworks task for.
	 * Default is 4 seconds.
	 *
	 * @return number of seconds
	 */
	private int getFireworkDuration() {
		return plugin.getConfig().getInt("fireworksonwin.duration", 4);
	}

	/**
	 * Is anti-camping enabled. If true players are teleported to arena spawn when
	 * countdown hits 5 seconds.
	 *
	 * @return boolean anti-camping
	 */
	private boolean isAntiCamping() {
		return plugin.getConfig().getBoolean("anticamping.enabled", true);
	}
	/**
	 * Displays the current value of countdown on the screen.
	 *
	 * @param player
	 * @param count
	 * @param message
	 */
	private void displayCountdown(Player player, int count, String message) {
		plugin.getSound().NOTE_PLING(player, 1, 999);
		if (!plugin.getConfig().getBoolean("special.UseTitle")) {
			Messages.sendMessage(player, message);
			return;
		} 
		TitleMsg.sendFullTitle(player, TitleMsg.starting.replace("{COUNT}", count + ""), TitleMsg.substarting.replace("{COUNT}", count + ""), 0, 40, 20, plugin);
	}

	/**
	 * Remove the inventory items and optionally give the player a kit and any items bought in the shop.
	 *
	 * @param player
	 */
	private void setGameInventory(Player player) {
		player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.shop.material")));
		player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.vote.material")));
		player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.info.material")));
		player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.stats.material")));
		player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.heads.material")));
		player.getInventory().setItemInOffHand(null);

		if (arena.getStructureManager().isKitsEnabled() && plugin.getKitManager().getKits().size() > 0) {
			arena.getPlayerHandler().allocateKits(player);
			if (plugin.getConfig().getBoolean("items.leave.use")) {
				arena.getPlayerHandler().addLeaveItem(player);
			}
		}
		if (plugin.getConfig().getBoolean("items.doublejump.use")) {
			arena.getPlayerHandler().addDoubleJumpItem(player);
		}
		givePlayerPurchasedItems(player);
	}

	private void givePlayerPurchasedItems(Player player) {
		if (!plugin.isGlobalShop() || !arena.getStructureManager().isShopEnabled()) {
			return;
		}
		if (plugin.getShop().getPlayersItems().containsKey(player.getName())) {
			List<ItemStack> items = plugin.getShop().getPlayersItems().get(player.getName());
			if (items != null) {
				for (ItemStack item : items) {
					if (isArmor(item)) {
						setArmorItem(player,item);
					} else {
						player.getInventory().addItem(item);
					}
				}
			}
			player.updateInventory();
		}
		if (plugin.getShop().getPotionEffects(player) != null) {
			for (PotionEffect pe : plugin.getShop().getPotionEffects(player)) {
				player.addPotionEffect(pe);
			}
		}
		List<String> cmds = plugin.getShop().getPurchasedCommands().get(player.getName());
		if (cmds != null) {
			final ConsoleCommandSender console = Bukkit.getConsoleSender();
			cmds.stream().forEach(cmd -> {
				Bukkit.dispatchCommand(console, cmd.replace("{PLAYER}", player.getName()).replace("%PLAYER%", player.getName()));
			});
		}

		arena.getPlayerHandler().removePurchase(player);
	}

	/**
	 * Validate ItemStack is an item of armour.
	 *
	 * @param item
	 * @return boolean
	 */
	private boolean isArmor(ItemStack item) {
		final List<String> armor = List.of("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
		return armor.stream().anyMatch(item.getType().toString()::contains);
	}

	/**
	 * Equip the armour item.
	 *
	 * @param player
	 * @param item
	 */
	private void setArmorItem(Player player, ItemStack item) {
		if (item.toString().contains("BOOTS")) {
			player.getInventory().setBoots(item);
		} else if (item.toString().contains("LEGGINGS")) {
			player.getInventory().setLeggings(item);
		} else if (item.toString().contains("CHESTPLATE")) {
			player.getInventory().setChestplate(item);
		} else if (item.toString().contains("HELMET")) {
			player.getInventory().setHelmet(item);
		}
	}

	public void forceStartByCommand() {
		forceStartByCmd = true;
		runArenaCountdown();
	}

	public boolean isForceStartByCommand() {
		return forceStartByCmd;
	}

	public int getTimeRemaining() {
		return hasTimeLimit ? timeremaining : 0;
	}

	public boolean isTimedOut() {
		return hasTimeLimit && timeremaining < 0;
	}

	/**
	 * Gets the player positions at the end of the game up to the maximum set in the configuration.
	 *
	 * @param winner player
	 * @return string places
	 */
	private String getPodiumPlaces(Player winner) {
		StringBuilder sb = new StringBuilder(200);
		String header = Messages.resultshead.replace("{ARENA}", arena.getArenaName());
		sb.append("\n" + header);
		sb.append("\n ");
		sb.append("\n" + Messages.playerposition.replace("{POS}", "1").replace("{RANK}", Utils.getRank(winner.getName()))
								.replace("{COLOR}", Utils.getColourMeta(winner)).replace("{PLAYER}", winner.getName()));

		places.entrySet().stream()
			.sorted(Entry.comparingByKey())
			.forEach(e -> {
				if (e.getKey() <= Math.min(arena.getStructureManager().getMaxFinalPositions(), startingPlayers)) {
					String playerName = e.getValue();
					sb.append("\n" + Messages.playerposition.replace("{POS}", String.valueOf(e.getKey()))
									.replace("{RANK}", Utils.getRank(playerName))
									.replace("{COLOR}", Utils.getColourMeta(playerName))
									.replace("{PLAYER}", playerName));
				}
			}
		);

		sb.append("\n ");
		sb.append("\n" + header);

		return sb.toString();
	}

	private void executeCommandOnStart(Player player) {
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
				arena.getStructureManager().getCommandOnStart().replace("%PLAYER%", player.getName()));
	}

	private void executeCommandOnStop(Player player) {
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
				arena.getStructureManager().getCommandOnStop().replace("%PLAYER%", player.getName()));
	}

	private void setActiveStats(int playercount) {
		if (!arena.getStructureManager().isArenaStatsEnabled()) {
			activeStats = false;
			return;
		}
		activeStats = playercount >= arena.getStructureManager().getStatsMinPlayers();
		if (Utils.debug()) {
			plugin.getLogger().info("Stats active: " + activeStats + ", min players = " + arena.getStructureManager().getStatsMinPlayers());
		}
	}

	protected boolean isStatsActive() {
		return activeStats;
	}
}
