package dogboy602k.MassBet.Main;

//remember to set up the xml correctly for the imports
import dogboy602k.MassBet.Commands.MassBetCommands;
import dogboy602k.MassBet.Util.FileManager;
import dogboy602k.MassBet.Util.Manager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by dogboy on 6/18/2016.
 */
public class MassBet extends JavaPlugin {

    private FileManager fileManager;
    private Manager manager;
    private Economy economy = null;
    private MassBetCommands MassBetCommands;


    @Override
    public void onEnable() {
        this.fileManager = new FileManager(this);

        // setting the commands such as the command, go to the command file
        this.MassBetCommands = new MassBetCommands(this);
        setupEconomy ();
        this.manager = new Manager(this);
        // the "Commands" in 26:44, is in the Command file; in the "public Commands(MassBet plugin)" method

        getCommand("Mass").setExecutor(new MassBetCommands(this));

        File playerDataFile = new File(this.getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            this.getFileManager().saveDefaultConfiguration(playerDataFile);
        }

        File Configfile = new File(getDataFolder(), "config.yml");
        if (!Configfile.exists()) {//if Configfile.yml file does not exist in plugins/CreditCard folder
            this.saveDefaultConfig();
        }
        this.fileManager.loadPlayerData();



        //fileManager.loadPlayerData(); // put the load AT THE END
        this.getManager().timeMessage();
        this.getManager().pickWinner();
    }

    @Override
    public void onDisable() {
       this.fileManager.savePlayerData();

    }

    public Manager getManager() {
        return manager;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);

    }

    public Economy getEconomy() {
        return economy;
    }

    public MassBetCommands getMassBetCommands() {
        return MassBetCommands;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

}

