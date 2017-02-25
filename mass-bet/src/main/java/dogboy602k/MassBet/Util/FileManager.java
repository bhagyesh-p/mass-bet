package dogboy602k.MassBet.Util;


import dogboy602k.MassBet.Main.MassBet;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by dogboy on 7/11/2016.
 */
@SuppressWarnings("unused")
public class FileManager {
    private MassBet plugin;
    private ArrayList <UUID> list = new ArrayList();



    public FileManager(MassBet plugin){
        this.plugin=plugin;
    }

    public void saveDefaultConfiguration(File file) {
        if (file == null) {
            System.out.println("Error config file null");
            return;
        }
        if (!file.exists()) {
            this.plugin.saveResource(file.getName(), false);
        }
    }

    public void loadPlayerData() {
        Manager m = this.plugin.getManager();
        File playerDataFile = new File(this.plugin.getDataFolder(), "playerdata.yml");
        FileConfiguration playerData = this.plugin.getFileManager().getConfiguration(playerDataFile);
        ConfigurationSection sec = playerData.getConfigurationSection("Playerstats");
        if (sec == null) {
            return;
        }

        int loadedArenas = 0;
        Set PlayerstatsSet = sec.getKeys(false);
        if (PlayerstatsSet != null) {
            for (String aPlayerUUID : playerData.getConfigurationSection("Playerstats").getKeys(false)) {
                String path = "Playerstats." + aPlayerUUID + ".";
                UUID PlayerUUID = UUID.fromString(aPlayerUUID);

                int wins = playerData.getInt(path + "wins");
                int loss = playerData.getInt(path + "loss");
                String name = playerData.getString(path + "username");
                double OverAllWins= playerData.getDouble(path + "OverAllWins");
                list.add(PlayerUUID);
                SendConsoleMessage.debug("ON Load " +name + " WINS " + wins + " LOSS " +loss + " OVERALL " + OverAllWins);

                m.addStats(new PlayerStats(PlayerUUID,  name,  wins,  loss ,  OverAllWins));
                m.addArray(list);
                ++loadedArenas;
            }
        }

        SendConsoleMessage.info("Successfully loaded " + ChatColor.AQUA + loadedArenas + ChatColor.GREEN + " Player Stats.");
    }

    public void savePlayerData() {
        if (this.plugin.getManager().getStats().size() == 0) {
            SendConsoleMessage.info("No info to be stored");
            return;
        }
        File playerDataFile = new File(this.plugin.getDataFolder(), "playerdata.yml");
        FileConfiguration playerDataConfig = this.plugin.getFileManager().getConfiguration(playerDataFile);
        for (PlayerStats playerstats : this.plugin.getManager().getStats()) {
            SendConsoleMessage.debug("ON SAVE PART 1 " +playerstats.getName() + " WINS " + playerstats.getWins() + " LOSS " +playerstats.getLoss() + " OVERALL " + playerstats.getOverall ());
            String path = "Playerstats." + playerstats.getPlayerUUID().toString() + ".";
            playerDataConfig.set(path + "wins", playerstats.getWins());
            playerDataConfig.set(path + "loss", playerstats.getLoss());
            playerDataConfig.set(path + "username", playerstats.getName());
            playerDataConfig.set(path + "OverAllWins", playerstats.getOverall());
            SendConsoleMessage.debug("ON SAVE PART 2 " +playerstats.getName() + " WINS " + playerstats.getWins() + " LOSS " +playerstats.getLoss() + " OVERALL " + playerstats.getOverall ());


        }
        try {
            playerDataConfig.save(playerDataFile);
        }
        catch (IOException e) {
            e.printStackTrace();

        }
    }

    public FileConfiguration getConfiguration(File file) {
        if (file == null) {
            System.out.println("Error config file null");
            return null;
        }
        return YamlConfiguration.loadConfiguration((File)file);
    }

    public void saveConfiguration(File file) {
        if (file == null) {
            System.out.println("Error config file null");
            return;
        }
        try {
            this.getConfiguration(file).save(file);
        }
        catch (IOException e) {
            System.out.println("Error saving configuration file! " + e.getMessage());
        }
    }

    public int getTime() {
        return plugin.getConfig().getInt("messagerepeter");
    }

    public double getMaxBet(){
        return plugin.getConfig().getDouble("maxBet");
    }

    public double getMinBet(){
        return plugin.getConfig().getDouble("minBet");
    }

}

