package tntrun.commands.setup.arena;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;

public class DeleteSpectatorSpawn implements CommandHandlerInterface {

	private TNTRun plugin;
	public DeleteSpectatorSpawn(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getArenaByName(args[0]);
		if (arena != null) {
			if (arena.getStatusManager().isArenaEnabled()) {
				player.sendMessage("§7[§6TNTRun§7] §cPlease disable arena §6/trsetup disable " + args[0]);
				return true;
			}
			arena.getStructureManager().removeSpectatorsSpawn();
			player.sendMessage("§7[§6TNTRun§7] §7Spectator spawn for arena §6" + args[0] + "§7 deleted");
		} else {
			player.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[0] + "§c doesn't exist");
		}
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}

}
