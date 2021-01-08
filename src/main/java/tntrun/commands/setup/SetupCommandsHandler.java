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

package tntrun.commands.setup;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.commands.setup.arena.AddSpawn;
import tntrun.commands.setup.arena.CreateArena;
import tntrun.commands.setup.arena.DeleteArena;
import tntrun.commands.setup.arena.DeleteSpawnPoints;
import tntrun.commands.setup.arena.DeleteSpectatorSpawn;
import tntrun.commands.setup.arena.DisableArena;
import tntrun.commands.setup.arena.DisableKits;
import tntrun.commands.setup.arena.EnableArena;
import tntrun.commands.setup.arena.EnableKits;
import tntrun.commands.setup.arena.FinishArena;
import tntrun.commands.setup.arena.SetArena;
import tntrun.commands.setup.arena.SetBarColor;
import tntrun.commands.setup.arena.SetCountdown;
import tntrun.commands.setup.arena.SetCurrency;
import tntrun.commands.setup.arena.SetDamage;
import tntrun.commands.setup.arena.SetFee;
import tntrun.commands.setup.arena.SetGameLevelDestroyDelay;
import tntrun.commands.setup.arena.SetLoseLevel;
import tntrun.commands.setup.arena.SetMaxPlayers;
import tntrun.commands.setup.arena.SetMinPlayers;
import tntrun.commands.setup.arena.SetMoneyRewards;
import tntrun.commands.setup.arena.SetRegenerationDelay;
import tntrun.commands.setup.arena.SetReward;
import tntrun.commands.setup.arena.SetSpawn;
import tntrun.commands.setup.arena.SetSpectatorSpawn;
import tntrun.commands.setup.arena.SetTeleport;
import tntrun.commands.setup.arena.SetTimeLimit;
import tntrun.commands.setup.arena.SetVotePercent;
import tntrun.commands.setup.arena.SetupHelp;
import tntrun.commands.setup.kits.AddKit;
import tntrun.commands.setup.kits.DeleteKit;
import tntrun.commands.setup.kits.LinkKit;
import tntrun.commands.setup.kits.UnlinkKit;
import tntrun.commands.setup.lobby.DeleteLobby;
import tntrun.commands.setup.lobby.SetLobby;
import tntrun.commands.setup.other.AddToWhitelist;
import tntrun.commands.setup.other.SetLanguage;
import tntrun.commands.setup.reload.Migrate;
import tntrun.commands.setup.reload.ReloadBars;
import tntrun.commands.setup.reload.ReloadConfig;
import tntrun.commands.setup.reload.ReloadMSG;
import tntrun.commands.setup.reload.ReloadTitles;
import tntrun.commands.setup.selection.Clear;
import tntrun.commands.setup.selection.SetP1;
import tntrun.commands.setup.selection.SetP2;
import tntrun.messages.Messages;
import tntrun.selectionget.PlayerSelection;

public class SetupCommandsHandler implements CommandExecutor {

	private PlayerSelection plselection = new PlayerSelection();

	private HashMap<String, CommandHandlerInterface> commandHandlers = new HashMap<>();

	public SetupCommandsHandler(TNTRun plugin) {
		commandHandlers.put("setp1", new SetP1(plselection));
		commandHandlers.put("setp2", new SetP2(plselection));
		commandHandlers.put("clear", new Clear(plselection));
		commandHandlers.put("setlobby", new SetLobby(plugin));
		commandHandlers.put("deletelobby", new DeleteLobby(plugin));
		commandHandlers.put("reloadmsg", new ReloadMSG(plugin));
		commandHandlers.put("reloadbars", new ReloadBars(plugin));
		commandHandlers.put("reloadconfig", new ReloadConfig(plugin));
		commandHandlers.put("reloadtitles", new ReloadTitles(plugin));
		commandHandlers.put("create", new CreateArena(plugin));
		commandHandlers.put("delete", new DeleteArena(plugin));
		commandHandlers.put("setarena", new SetArena(plugin, plselection));
		commandHandlers.put("setgameleveldestroydelay", new SetGameLevelDestroyDelay(plugin));
		commandHandlers.put("setregenerationdelay", new SetRegenerationDelay(plugin));
		commandHandlers.put("setloselevel", new SetLoseLevel(plugin, plselection));
		commandHandlers.put("setspawn", new SetSpawn(plugin));
		commandHandlers.put("addspawn", new AddSpawn(plugin));
		commandHandlers.put("deletespawnpoints", new DeleteSpawnPoints(plugin));
		commandHandlers.put("setspectate", new SetSpectatorSpawn(plugin));
		commandHandlers.put("deletespectate", new DeleteSpectatorSpawn(plugin));
		commandHandlers.put("setmaxplayers", new SetMaxPlayers(plugin));
		commandHandlers.put("setminplayers", new SetMinPlayers(plugin));
		commandHandlers.put("setvotepercent", new SetVotePercent(plugin));
		commandHandlers.put("setcountdown", new SetCountdown(plugin));
		commandHandlers.put("setmoneyreward", new SetMoneyRewards(plugin));
		commandHandlers.put("settimelimit", new SetTimeLimit(plugin));
		commandHandlers.put("setteleport", new SetTeleport(plugin));
		commandHandlers.put("setdamage", new SetDamage(plugin));
		commandHandlers.put("finish", new FinishArena(plugin));
		commandHandlers.put("disable", new DisableArena(plugin));
		commandHandlers.put("enable", new EnableArena(plugin));
		commandHandlers.put("enablekits", new EnableKits(plugin));
		commandHandlers.put("disablekits", new DisableKits(plugin));
		commandHandlers.put("addkit", new AddKit(plugin));
		commandHandlers.put("deletekit", new DeleteKit(plugin));
		commandHandlers.put("linkkit", new LinkKit(plugin));
		commandHandlers.put("unlinkkit", new UnlinkKit(plugin));
		commandHandlers.put("setbarcolor", new SetBarColor(plugin));
		commandHandlers.put("setbarcolour", new SetBarColor(plugin));
		commandHandlers.put("setreward", new SetReward(plugin));
		commandHandlers.put("setfee", new SetFee(plugin));
		commandHandlers.put("setcurrency", new SetCurrency(plugin));
		commandHandlers.put("setlanguage", new SetLanguage(plugin));
		commandHandlers.put("addtowhitelist", new AddToWhitelist(plugin));
		commandHandlers.put("migrate", new Migrate(plugin));
		commandHandlers.put("help", new SetupHelp(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player is expected");
			return true;
		}
		Player player = (Player) sender;
		// check permissions
		if (!player.hasPermission("tntrun.setup")) {
			Messages.sendMessage(player, Messages.nopermission);
			return true;
		}
		// get command
		if (args.length > 0 && commandHandlers.containsKey(args[0])) {
			CommandHandlerInterface commandh = commandHandlers.get(args[0]);
			//check args length
			if (args.length - 1 < commandh.getMinArgsLength()) {
				Messages.sendMessage(player, "&c ERROR: Please use &6/tr cmds&c to view required arguments for all game commands");
				return false;
			}
			//execute command
			boolean result = commandh.handleCommand(player, Arrays.copyOfRange(args, 1, args.length));
			return result;
		} 
		Messages.sendMessage(player, "&c ERROR: Please use &6/tr cmds&c to view all valid game commands");
		return false;
	}
}
