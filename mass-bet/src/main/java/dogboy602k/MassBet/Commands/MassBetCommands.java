package dogboy602k.MassBet.Commands;

import dogboy602k.MassBet.Main.MassBet;
import dogboy602k.MassBet.Util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Created by dogboy on 6/18/2016.
 */
public class MassBetCommands implements CommandExecutor {
    private MassBet plugin;

        public MassBetCommands(MassBet plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0 || args.length > 3) {
            Util.sendEmptyMsg((Player) sender, ChatColor.AQUA + "=====================================================");
            Util.sendMsg((Player) sender, ChatColor.AQUA + "Usage /mass bet "+ ChatColor.AQUA+"<username>"+ ChatColor.GREEN+" <amount>");
            Util.sendMsg((Player) sender, ChatColor.AQUA + "Usage /mass retract "+ ChatColor.AQUA+"<username>");
            //Util.sendMsg((Player) sender, ChatColor.AQUA + "Usage /mass wins "+ ChatColor.AQUA+"<username>" );
            Util.sendMsg((Player) sender, ChatColor.AQUA + "Usage /mass info" + ChatColor.AQUA+"<username>" );
            Util.sendEmptyMsg((Player) sender, ChatColor.AQUA + "=====================================================");
            return true;
        }
        else if(args.length == 3) {
            if (args[0].equalsIgnoreCase("bet")) {
                //to do BETTING
                String playerName = args[1];
                Player player = Bukkit.getPlayer(playerName);
                if (sender.equals(player)) {
                    double betAmount = 0;

                    //having the bet looked at from string to double
                    try {
                        betAmount = Double.valueOf(args[2]);
                    } catch (NumberFormatException e) {
                        Util.sendMsg(sender, ChatColor.RED + "Error:  You must enter a number for this value");
                        return true;
                    }
                    //Checking if the betAmount is less then 0
                    if (betAmount <= 0) {

                        Util.sendMsg(sender, ChatColor.RED + (" ERROR:  you must input a bet amount greater than 0"));
                        return true;
                    }

                    if (player != null) {
                        plugin.getManager().SendBet(player, betAmount);
                    } else {
                        Util.sendMsg((Player) sender, ChatColor.RED + "The player " + ChatColor.GOLD + playerName + ChatColor.RED + " is not online!");
                    }
                }
                else {
                    Util.sendMsg(sender, ChatColor.RED+"[ERROR] You are not "+ChatColor.GREEN+ player.getName() +ChatColor.RED+ " hence you can not bet");
                }
            }
        }
        else if(args.length ==2 && args[0].equalsIgnoreCase("retract")) {
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if (sender.equals(player)) {
                {
                    plugin.getManager().returnTheCashInRetract(player);
                    plugin.getManager().removePlayerFromPot(player.getUniqueId());

                }
            }
        }
        else if(args.length ==2 && args[0].equalsIgnoreCase("wins")) {
                String playerName = args[1];
                Player player = Bukkit.getPlayer(playerName);
                if (sender.equals(player)) {
                    {
                        //to do wins
                    }

                }
            }
        else if(args.length ==2 && args[0].equalsIgnoreCase("info")){

            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if (sender.equals(player)) {
                {
                    plugin.getManager().returninfo(player);
                    //to do info on current pot
                }

            }
        }

        else if(args.length ==2 && args[0].equalsIgnoreCase("stats")){
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            Player player2 = Bukkit.getPlayer(sender.getName());
            plugin.getManager().stats(player,player2);

        }
    return true;
    }
}
