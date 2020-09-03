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
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import tntrun.FormattingCodesParser;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.arena.structure.Kits;
import tntrun.utils.Bars;
import tntrun.utils.Shop;
import tntrun.utils.TitleMsg;
import tntrun.utils.Utils;
import tntrun.messages.Messages;

public class GameHandler {

	private TNTRun plugin;
	private Arena arena;
	public int lostPlayers = 0;

	public GameHandler(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
		count = arena.getStructureManager().getCountdown();
	}

	private Scoreboard scoreboard = buildScoreboard();

	// arena leave handler
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
	public static int count;

	public void runArenaCountdown() {
		count = arena.getStructureManager().getCountdown();
		arena.getStatusManager().setStarting(true);
		runtaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(
			plugin,
			new Runnable() {
				@Override
				public void run() {
					// check if countdown should be stopped for some various reasons
					if (arena.getPlayersManager().getPlayersCount() < arena.getStructureManager().getMinPlayers() && !arena.getPlayerHandler().forceStart()) {
						for (Player player : arena.getPlayersManager().getPlayers()) {
							Bars.setBar(player, Bars.waiting, arena.getPlayersManager().getPlayersCount(), 0, arena.getPlayersManager().getPlayersCount() * 100 / arena.getStructureManager().getMinPlayers(), plugin);
							createWaitingScoreBoard();
						}
						stopArenaCountdown();
					} else
					// start arena if countdown is 0
					if (count == 0) {
						stopArenaCountdown();
						startArena();
					} else if(count == 5) {
						String message = Messages.arenacountdown;
						message = message.replace("{COUNTDOWN}", String.valueOf(count));
						for (Player player : arena.getPlayersManager().getPlayers()) {
							player.teleport(arena.getStructureManager().getSpawnPoint());
							TNTRun.getInstance().sound.NOTE_PLING(player, 1, 999);
							if(plugin.getConfig().getBoolean("special.UseTitle") == false){
								Messages.sendMessage(player, message);
							} 
							TitleMsg.sendFullTitle(player, TitleMsg.starting.replace("{COUNT}", count + ""), TitleMsg.substarting.replace("{COUNT}", count + ""), 0, 40, 20, plugin);
						}
					} else if (count < 11) {
						String message = Messages.arenacountdown;
						message = message.replace("{COUNTDOWN}", String.valueOf(count));
						for (Player player : arena.getPlayersManager().getPlayers()) {
							TNTRun.getInstance().sound.NOTE_PLING(player, 1, 999);
							if(plugin.getConfig().getBoolean("special.UseTitle") == false){
								Messages.sendMessage(player, message);
							} 
							TitleMsg.sendFullTitle(player, TitleMsg.starting.replace("{COUNT}", count + ""), TitleMsg.substarting.replace("{COUNT}", count + ""), 0, 40, 20, plugin);
						}
					} else if (count % 10 == 0) {
						String message = Messages.arenacountdown;
						message = message.replace("{COUNTDOWN}", String.valueOf(count));
				        for (Player all : arena.getPlayersManager().getPlayers()) {
				        	TNTRun.getInstance().sound.NOTE_PLING(all, 1, 999);
				        	if(plugin.getConfig().getBoolean("special.UseTitle") == false){
				        		Messages.sendMessage(all, message);
				        	} 
				        	TitleMsg.sendFullTitle(all, TitleMsg.starting.replace("{COUNT}", count + ""), TitleMsg.substarting.replace("{COUNT}", count + ""), 0, 40, 20, plugin);
				        }
				    }
					if(count == 5) {
						for (Player player : arena.getPlayersManager().getPlayers()) {
							player.teleport(arena.getStructureManager().getSpawnPoint());
							TNTRun.getInstance().sound.NOTE_PLING(player, 1, 999);
						}
					}
					// scoreboard
					createWaitingScoreBoard();
					// sending bars
					for (Player player : arena.getPlayersManager().getPlayers()) {
						player.setLevel(count);
						Bars.setBar(player, Bars.starting, 0, count, count * 100 / arena.getStructureManager().getCountdown(), plugin);
				    }
					count--;
				}
			},
			0, 20
		);
	}

	public void stopArenaCountdown() {
		arena.getStatusManager().setStarting(false);
		count = arena.getStructureManager().getCountdown();
		Bukkit.getScheduler().cancelTask(runtaskid);
	}

	// main arena handler
	private int timelimit;
	private int arenahandler;
	private int playingtask;

	Random rnd = new Random();
	public void startArena() {
		arena.getStatusManager().setRunning(true);
		if (Utils.debug() ) {
			plugin.getLogger().info("Arena " + arena.getArenaName() + " started");
			plugin.getLogger().info("Players in arena: " + arena.getPlayersManager().getPlayersCount());
		}
		String message = Messages.arenastarted;
		message = message.replace("{TIMELIMIT}", String.valueOf(arena.getStructureManager().getTimeLimit()));
		for (Player player : arena.getPlayersManager().getPlayers()) {
			player.closeInventory();
			//Stats.addPlayedGames(player, 1);
			if (plugin.useStats()) {
				plugin.stats.addPlayedGames(player, 1);
			}
			player.setAllowFlight(true);
			Messages.sendMessage(player, message);
			TNTRun.getInstance().sound.ENDER_DRAGON(player, 1, 999);
			
			player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.shop.material")));
			player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.vote.material")));
			player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.info.material")));
			player.getInventory().remove(Material.getMaterial(plugin.getConfig().getString("items.stats.material")));
			
            if (Shop.pitems.containsKey(player)) {
            	ArrayList<ItemStack> items = Shop.pitems.get(player);
                Shop.pitems.remove(player);
                Shop.bought.remove(player);
 
                if(items != null){
                    for (ItemStack item : items) {
                        player.getInventory().addItem(item);
                    }	
                }
                player.updateInventory();
            }
			TitleMsg.sendFullTitle(player, TitleMsg.start, TitleMsg.substart, 20, 20, 20, plugin);
		}
		plugin.signEditor.modifySigns(arena.getArenaName());
		Kits kits = arena.getStructureManager().getKits();
		if (kits.getKits().size() > 0) {
			String[] kitnames = kits.getKits().toArray(new String[kits.getKits().size()]);
			for (Player player : arena.getPlayersManager().getPlayers()) {
				kits.giveKit(kitnames[rnd.nextInt(kitnames.length)], player);
			}
		}
		resetScoreboard();
		createPlayingScoreBoard();
		timelimit = arena.getStructureManager().getTimeLimit() * 20; // timelimit is in ticks
		arenahandler = Bukkit.getScheduler().scheduleSyncRepeatingTask(
			plugin,
			new Runnable() {
				@Override
				public void run() {
					// stop arena if player count is 0
					if (arena.getPlayersManager().getPlayersCount() == 0) {
						// stop arena
						if (Utils.debug()) {
							plugin.getLogger().info("GH calling stopArena...");
						}
						stopArena();
						return;
					}
					// kick all players if time is out
					if (timelimit < 0) {
						for (Player player : arena.getPlayersManager().getPlayersCopy()) {
							arena.getPlayerHandler().leavePlayer(player,Messages.arenatimeout, "");
						}
						return;
					}
					// handle players
					for (Player player : arena.getPlayersManager().getPlayersCopy()) {
						// Xp level
						player.setLevel(timelimit/20);
						// update bar
						Bars.setBar(player, Bars.playing, arena.getPlayersManager().getPlayersCount(), timelimit / 20, timelimit * 5 / arena.getStructureManager().getTimeLimit(), plugin);
						// handle player
						handlePlayer(player);
					}
					// update bars for spectators too
					for (Player player : arena.getPlayersManager().getSpectators()) {
						Bars.setBar(player, Bars.playing, arena.getPlayersManager().getPlayersCount(), timelimit / 20, timelimit * 5 / arena.getStructureManager().getTimeLimit(), plugin);
					}
					// decrease timelimit
					timelimit--;
				}
			},
			0, 1
		);
	}

	public void stopArena() {
		if (!arena.getStatusManager().isArenaRunning()) {
			if (Utils.debug()) {
				plugin.getLogger().info("stopArena arena not running. Exiting....");
			}
			return;
		}
		arena.getStatusManager().setRunning(false);
		resetScoreboard();

		for (Player player : arena.getPlayersManager().getAllParticipantsCopy()) {
			if (Utils.debug()) {
				plugin.getLogger().info("stopArena is removing player " + player.getName());
			}
			arena.getPlayerHandler().leavePlayer(player, "", "");
		}
		lostPlayers = 0;
		Bukkit.getScheduler().cancelTask(arenahandler);
		Bukkit.getScheduler().cancelTask(playingtask);

		plugin.signEditor.modifySigns(arena.getArenaName());
		if (arena.getStatusManager().isArenaEnabled()) {
			startArenaRegen();
		}
	}

	// player handlers
	public void handlePlayer(final Player player) {
		Location plloc = player.getLocation();
		Location plufloc = plloc.clone().add(0, -1, 0);
		// remove block under player feet
		arena.getStructureManager().getGameZone().destroyBlock(plufloc);
		// check for win
		if (arena.getPlayersManager().getPlayersCount() == 1 && !arena.getStructureManager().isTestMode()) {
			// last player wins
			startEnding(player);
			return;
		}
		// check for lose
		if (arena.getStructureManager().getLoseLevel().isLooseLocation(plloc)) {
			if (arena.getPlayersManager().getPlayersCount() == 1) {
				// must be test mode
				startEnding(player);
				return;
			}
			arena.getPlayerHandler().dispatchPlayer(player);
			return;
		}
	}

	public Scoreboard buildScoreboard() {
		
		FileConfiguration config = TNTRun.getInstance().getConfig();
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		if (config.getBoolean("special.UseScoreboard")) {
			Objective o = scoreboard.registerNewObjective("TNTRun", "waiting");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			String header = FormattingCodesParser.parseFormattingCodes(config.getString("scoreboard.header", ChatColor.GOLD.toString() + ChatColor.BOLD + "TNTRUN"));
			o.setDisplayName(header);
		}
		return scoreboard;
	} 
	
	public void createWaitingScoreBoard() {
		if(!plugin.getConfig().getBoolean("special.UseScoreboard")){
			return;
		}
		resetScoreboard();
		Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		try{
			int size = plugin.getConfig().getStringList("scoreboard.waiting").size();
			for(String s : plugin.getConfig().getStringList("scoreboard.waiting")){
				s = s.replace("&", "§");
				s = s.replace("{ARENA}", arena.getArenaName());
				s = s.replace("{PS}", arena.getPlayersManager().getAllParticipantsCopy().size() + "");
				s = s.replace("{MPS}", arena.getStructureManager().getMaxPlayers() + "");
				s = s.replace("{COUNT}", count + "");
				o.getScore(s).setScore(size);
				size--;
			}
			for (Player p : arena.getPlayersManager().getPlayers()) {
				p.setScoreboard(scoreboard);
			}
		}catch (NullPointerException ex){
			
		}
	}

	public void resetScoreboard() {
		for (String entry : new ArrayList<String>(scoreboard.getEntries())) {
			scoreboard.resetScores(entry);
		}
	}

	public void createPlayingScoreBoard() {
		if(!plugin.getConfig().getBoolean("special.UseScoreboard")){
			return;	
		}
		playingtask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				resetScoreboard();
				Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
				
				int size = plugin.getConfig().getStringList("scoreboard.playing").size();
				for(String s : plugin.getConfig().getStringList("scoreboard.playing")){
					s = s.replace("&", "§");
					s = s.replace("{ARENA}", arena.getArenaName());
					s = s.replace("{PS}", arena.getPlayersManager().getAllParticipantsCopy().size() + "");		
					s = s.replace("{MPS}", arena.getStructureManager().getMaxPlayers() + "");
					s = s.replace("{LOST}", lostPlayers + "");
					s = s.replace("{LIMIT}", timelimit/20 + "");
					o.getScore(s).setScore(size);
					size--;
				}
			}
		}, 0, 20);
	}

	private void startArenaRegen() {
		if(arena.getStatusManager().isArenaRegenerating()){
			return;
		}
		// set arena is regenerating status
		arena.getStatusManager().setRegenerating(true);
		if (Utils.debug()) {
			plugin.getLogger().info("Arena regen started");
		}
		// modify signs
		plugin.signEditor.modifySigns(arena.getArenaName());
		// schedule gamezone regen
		int delay = arena.getStructureManager().getGameZone().regen();
		// regen finished
		Bukkit.getScheduler().scheduleSyncDelayedTask(
			arena.plugin,
			new Runnable() {
				@Override
				public void run() {
					// set not regenerating status
					arena.getStatusManager().setRegenerating(false);
					// modify signs
					plugin.signEditor.modifySigns(arena.getArenaName());
				}
			},
			delay
		);
	}
	
	/**
	 * Called when there is only 1 player left, to update winner stats and
	 * teleport winner and spectators to the arena spawn point. It then
	 * stops the arena.
	 * @param player
	 */
	public void startEnding(final Player player){
		//Stats.addWins(player, 1);
		if (plugin.useStats()) {
			plugin.stats.addWins(player, 1);
		}
		TitleMsg.sendFullTitle(player, TitleMsg.win, TitleMsg.subwin, 20, 60, 20, plugin);
		plugin.getLogger().info("Player " + player.getName() + " won arena " + arena.getArenaName());
		
		String message = Messages.playerwonbroadcast;
		message = message.replace("{PLAYER}", player.getName());
		message = message.replace("{ARENA}", arena.getArenaName());
		
		/* Determine who should receive notification of win (0 suppresses broadcast) */
		if (plugin.getConfig().getInt("broadcastwinlevel") == 1) {
			for (Player all : arena.getPlayersManager().getAllParticipantsCopy()) {
				all.sendMessage(message.replace("&", "§"));
			}
		} else if (plugin.getConfig().getInt("broadcastwinlevel") >= 2) {
			for (Player all : Bukkit.getOnlinePlayers()){
				all.sendMessage(message.replace("&", "§"));
			}
		}
		// allow winner to fly at arena spawn
		player.setAllowFlight(true);
		player.setFlying(true);
		// teleport winner and spectators to arena spawn
		for(Player p : arena.getPlayersManager().getAllParticipantsCopy()) {
			TNTRun.getInstance().sound.ENDER_DRAGON(p, 5, 999);
			p.teleport(arena.getStructureManager().getSpawnPoint());
			p.getInventory().clear();
		}
				
		Bukkit.getScheduler().cancelTask(arenahandler);
		Bukkit.getScheduler().cancelTask(playingtask);
		
		if (plugin.getConfig().getBoolean("fireworksonwin")) {
				
			new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					if (i == 8) {
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
			public void run(){
				try{
					arena.getPlayerHandler().leaveWinner(player, Messages.playerwontoplayer);
					stopArena();
						
					final ConsoleCommandSender console = Bukkit.getConsoleSender();
						
					if(plugin.getConfig().getStringList("commandsonwin") == null){
						return;
					}
					for(String commands : plugin.getConfig().getStringList("commandsonwin")){
						Bukkit.dispatchCommand(console, commands.replace("{PLAYER}", player.getName()));
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			
		}.runTaskLater(plugin, 120);
	}
}