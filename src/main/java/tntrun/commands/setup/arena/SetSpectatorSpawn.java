package tntrun.commands.setup.arena;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;

public class SetSpectatorSpawn implements CommandHandlerInterface {

	private TNTRun plugin;
	public SetSpectatorSpawn(TNTRun plugin) {
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
			if (!arena.getStructureManager().isArenaBoundsSet()) {
				player.sendMessage("§7[§6TNTRun§7] §cArena bounds not set");
				return true;
			}
			if (arena.getStructureManager().setSpectatorsSpawn(player.getLocation())) {
				player.sendMessage("§7[§6TNTRun§7] §7Arena §6" + args[0] + "§7 SpectatorSpawn set to §6X: §7" + Math.round(player.getLocation().getX()) + " §6Y: §7" + Math.round(player.getLocation().getY()) + " §6Z: §7" + Math.round(player.getLocation().getZ()));
			} else {
				player.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[0] + "§c SpectatorSpawn must be in arena bounds");
			}
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
