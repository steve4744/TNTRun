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

package tntrun.utils;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class Stats {
	
	public static TNTRun pl;
	public static File file;

	public Stats(TNTRun plugin){
		pl = plugin;
		file = new File(pl.getDataFolder(), "stats.yml");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addPlayedGames(Player player, int value){
		if(pl.file){
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(Bukkit.getOnlineMode()){
				if(config.get("stats." + player.getUniqueId().toString() + ".played") == null){
					config.set("stats." + player.getUniqueId().toString() + ".played", value);
				}else{
					config.set("stats." + player.getUniqueId().toString() + ".played", config.getInt("stats." + player.getUniqueId().toString() + ".played") + value);
				}
			}else{
				if(config.get("stats." + player.getName() + ".played") == null){
					config.set("stats." + player.getName() + ".played", value);
				}else{
					config.set("stats." + player.getName() + ".played", config.getInt("stats." + player.getName() + ".played") + value);
				}
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			setValue("played",  player,  getStat("played", player) + 1);	
		}
	}
	
	public static void addWins(Player player, int value){
		if(pl.file){
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(Bukkit.getOnlineMode()){
				if(config.get("stats." + player.getUniqueId().toString() + ".wins") == null){
					config.set("stats." + player.getUniqueId().toString() + ".wins", value);
				}else{
					config.set("stats." + player.getUniqueId().toString() + ".wins", config.getInt("stats." + player.getUniqueId().toString() + ".wins") + value);
				}
			}else{
				if(config.get("stats." + player.getName() + ".wins") == null){
					config.set("stats." + player.getName() + ".wins", value);
				}else{
					config.set("stats." + player.getName() + ".wins", config.getInt("stats." + player.getName() + ".wins") + value);
				}
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			setValue("wins",  player,  getStat("wins", player) + 1);	
		}
	}
	
	public static void addLoses(Player player, int value){
		if(pl.file){
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(Bukkit.getOnlineMode()){
				if(config.get("stats." + player.getUniqueId().toString() + ".looses") == null){
					config.set("stats." + player.getUniqueId().toString() + ".looses", value);
				}else{
					config.set("stats." + player.getUniqueId().toString() + ".looses", config.getInt("stats." + player.getUniqueId().toString() + ".looses") + value);
				}
			}else{
				if(config.get("stats." + player.getName() + ".looses") == null){
					config.set("stats." + player.getName() + ".looses", value);
				}else{
					config.set("stats." + player.getName() + ".looses", config.getInt("stats." + player.getName() + ".looses") + value);
				}
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			setValue("looses",  player,  getStat("looses", player) + 1);	
		}
	}
	
	
	public static int getLooses(Player player){
		if(pl.file){
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(Bukkit.getOnlineMode()){
				if(config.get("stats." + player.getUniqueId().toString() + ".looses") == null){
					return 0;
				}else{
					return config.getInt("stats." + player.getUniqueId().toString() + ".looses");
				}
			}else{
				if(config.get("stats." + player.getName() + ".looses") == null){
					return 0;
				}else{
					return config.getInt("stats." + player.getName() + ".looses");
				}
			}
		}
		return getStat("looses", player);
	}
	
	
	public static int getWins(Player player){
		if(pl.file){
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(Bukkit.getOnlineMode()){
				if(config.get("stats." + player.getUniqueId().toString() + ".wins") == null){
					return 0;
				}else{
					return config.getInt("stats." + player.getUniqueId().toString() + ".wins");
				}
			}else{
				if(config.get("stats." + player.getName() + ".wins") == null){
					return 0;
				}else{
					return config.getInt("stats." + player.getName() + ".wins");
				}
			}
		}
		return getStat("wins", player);
	}
	
	
	public static int getPlayedGames(Player player){
		if(pl.file){
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(Bukkit.getOnlineMode()){
				if(config.get("stats." + player.getUniqueId().toString() + ".played") == null){
					return 0;
				}else{
					return config.getInt("stats." + player.getUniqueId().toString() + ".played");
				}
			}else{
				if(config.get("stats." + player.getName() + ".played") == null){
					return 0;
				}else{
					return config.getInt("stats." + player.getName() + ".played");
				}
			}
		}
		return getStat("played", player);
	}
	
    private static int getStat(String statname, Player player) {
        try {
            int stat = 0;
            ResultSet rs;
            
            if(Bukkit.getOnlineMode()){
            	rs = pl.mysql.query("SELECT * FROM `stats` WHERE `username`='" + player.getUniqueId().toString() + "'").getResultSet();
            }else{
            	rs = pl.mysql.query("SELECT * FROM `stats` WHERE `username`='" + player.getName() + "'").getResultSet();
            }
            
            while (rs.next()) {
               	stat = rs.getInt(statname);
            }

            return stat;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 999;
	}
    
    private static void setValue(String statname, Player p, int value) {    
        if (!pl.usestats) {
        	return;
        }
        
        if(Bukkit.getOnlineMode()){
            pl.mysql.query("UPDATE `stats` SET `" + statname
                    + "`='" + value + "' WHERE `username`='" + p.getUniqueId().toString() + "';");
        }else{
            pl.mysql.query("UPDATE `stats` SET `" + statname
                    + "`='" + value + "' WHERE `username`='" + p.getName() + "';");
        }
    }
	
}
