package tntrun.commands.setup.reload;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.commands.setup.CommandHandlerInterface;

public class Migrate implements CommandHandlerInterface {

	private TNTRun plugin;

	public Migrate(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("doublejumps");
		if (section == null) {
			player.sendMessage("[TNTRun_reloaded] There is nothing to migrate");
			return true;
		}
		
		for (String name : section.getKeys(false)) {
			int amount = plugin.getConfig().getInt("doublejumps." + name, 0);
			if (amount <= 0) {
				continue;
			}

			/* Check if anything already in players.yml */
			if (Bukkit.getOnlineMode()) {
				@SuppressWarnings("deprecation")
				OfflinePlayer oplayer = Bukkit.getOfflinePlayer(name);

				amount += plugin.getPData().getDoubleJumpsFromFile(oplayer);
				plugin.getPData().saveDoubleJumpsToFile(oplayer, amount);

			} else {
				amount += plugin.getPData().getDoubleJumpsFromFile(name);
				plugin.getPData().saveDoubleJumpsToFile(name, amount);
			}
		}

		plugin.getConfig().set("doublejumps", null);
		plugin.saveConfig();
		player.sendMessage("[TNTRun_reloaded] All doublejump entries have been migrated");

		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 0;
	}
}
