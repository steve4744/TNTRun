package tntrun.commands.setup.reload;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.utils.TitleMsg;

public class ReloadTitles  implements CommandHandlerInterface {

	private TNTRun plugin;
	
	public ReloadTitles(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		TitleMsg.loadTitles(plugin);
		player.sendMessage("Titles reloaded");
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 0;
	}

}
