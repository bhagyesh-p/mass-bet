package dogboy602k.MassBet.Util;

import dogboy602k.MassBet.Main.MassBet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dogboy on 6/18/2016.
 */
public class Manager {

    private MassBet plugin;
    private HashMap<UUID, Double> playerBet;
    private HashMap<UUID, Double> Percentage;
    private HashMap<UUID, Integer> ArrayListPlaces;
    private List<PlayerStats> stats = new ArrayList();
    private List<UUID> UUIDs = new ArrayList();


    public Manager(MassBet plugin) {
        this.plugin = plugin;
        this.playerBet = new HashMap<>();
        this.Percentage = new HashMap<>();
        this.ArrayListPlaces = new HashMap<>();
    }



    public void returninfo(Player player) {
        UUID playerUUID = player.getUniqueId();
        String Player = player.getName();

        if (!playerBet.containsKey(playerUUID)) {
            double jackpot = getTotalPotAmount();
            int howManyBettors = getamountofPlayers();
            Util.sendMsg(player, ChatColor.RED + "[ERROR] Seems to be you havent betted, use " + ChatColor.GREEN + "/mass bet " + player.getName() + " <amount>" + ChatColor.RED + " to bet and to be added to the Jack pot");
            Util.sendMsg(player, ChatColor.AQUA + "[INFO] The Jackpot is " + ChatColor.GREEN + "$" + jackpot);
            Util.sendMsg(player, ChatColor.AQUA + "[INFO] There are " + ChatColor.GREEN + howManyBettors);
            return;
        } else if (playerBet.containsKey(playerUUID)) {
            NumberFormat defaultFormat = NumberFormat.getPercentInstance();
            defaultFormat.setMinimumFractionDigits(2);

            double bet = getPlayerBet(playerUUID);
            double jackpot = getTotalPotAmount();
            int howManyBettors = getamountofPlayers();
            double chance = bet / jackpot;
            Util.sendMsg(player, ChatColor.AQUA + "[INFO] Your chances are : " + defaultFormat.format(chance));
            Util.sendMsg(player, ChatColor.AQUA + "[INFO] You have bet " + ChatColor.GREEN + "$" + bet);
            Util.sendMsg(player, ChatColor.AQUA + "[INFO] The Jackpot is " + ChatColor.GREEN + "$" + jackpot);
            Util.sendMsg(player, ChatColor.AQUA + "[INFO] There are " + ChatColor.GREEN + howManyBettors);
        }


    }

    public void addArray(ArrayList<UUID> info) {
        for(UUID a: info ){
            UUIDs.add(a);
        }
    }

    public PlayerStats getPlayerStats(UUID owner) {
        for (PlayerStats playerStats : this.stats) {
            if (!playerStats.getPlayerUUID().equals(owner)) continue;
            return playerStats;
        }
        return null;
    }

    public List<PlayerStats> getStats() {
        return this.stats;
    }

    public void addStats(PlayerStats info) {
        this.stats.add(info);
    }

    public boolean hasRegistered(UUID playerUUID) {
        int count = 0;
        for(UUID a: UUIDs){
            if(playerUUID.equals(a)){
                count++;
            }
        }
        if(count > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public void SendBet(Player player, double betAmount) {
        UUID playerUUID = player.getUniqueId();
        String Player = player.getName();
        double maxbet = plugin.getFileManager().getMaxBet();
        double minbet = plugin.getFileManager().getMinBet();

        // check of the player got enough me money
        if (!hasEnoughMoney(player, betAmount)) {

            Util.sendMsg(player, ChatColor.RED + "Not Enough Money.");
        }

        if (hasEnoughMoney(player, betAmount)) {
            /**  To see  the Map printed put
             *
             * for (Map.Entry<UUID, Double> entry : playerBet.entrySet()) {
             *     String key = entry.getKey().toString();
             *     Double value = entry.getValue();
             *     System.out.println("key, " + key + " value " + value);
             *     }
             **/

            if (playerBet.containsKey(playerUUID)) {
                double bet = getPlayerBet(playerUUID);

                Util.sendMsg(player, ChatColor.RED + "[ERROR] You have betted already Use " + ChatColor.GREEN + "/mass retract " + player.getName() + ChatColor.RED + " to remove your bet Your previous  bet amount was: " + ChatColor.GREEN + "$" + bet);
                return;
            } else if (!playerBet.containsKey(playerUUID)) {
                double checkPercent =  betAmount/getTotalPotAmount();
                if (checkPercent < 0.01) {
                    double cashAmount = getTotalPotAmount() *.01;
                    Util.sendMsg(player, ChatColor.RED + "[ERROR] You must bet more then "+ cashAmount +" dollar");
                    return;
                }
                 else {
                    NumberFormat defaultFormat = NumberFormat.getPercentInstance();
                    defaultFormat.setMinimumFractionDigits(2);

                    addPlayerToPot(playerUUID, betAmount);
                    double bet = getPlayerBet(playerUUID);
                    double jackpot = getTotalPotAmount();
                    double chance = bet / jackpot;
                    Util.sendMsg(player, ChatColor.AQUA + " Your chances are : " + defaultFormat.format(chance));
                    Util.sendMsg(player, ChatColor.AQUA + " You have bet: " + ChatColor.GREEN + "$" + bet);
                    Util.sendMsg(player, ChatColor.AQUA + " " + getamountofPlayers() + " player(s) have bet, totaling :" + ChatColor.GREEN + "$" + jackpot);

                    plugin.getEconomy().withdrawPlayer(Player, betAmount);
                    DecimalFormat df = new DecimalFormat("#.######");
                    chance = Double.valueOf(df.format(chance));
                    addPlayerToPercentage(playerUUID, chance);
                    updatePercentages();
                    if(hasRegistered(playerUUID) == false) {
                        this.stats.add(new PlayerStats(playerUUID, player.getName(), 0, 0, 0.0));
                        UUIDs.add(playerUUID);
                    }
                }
                for (Map.Entry<UUID, Double> entry : Percentage.entrySet()) {
                    String key = entry.getKey().toString();
                    Double value = entry.getValue();
                }
            }
        }
    }

    public double getTotalPotAmount() {
        double amount = 0;
        for (Map.Entry<UUID, Double> values : playerBet.entrySet()) {
            amount = values.getValue() + amount;
        }
        return amount;
    }

    public boolean hasEnoughMoney(Player player, double amount) {
        if (plugin.getEconomy().getBalance(player.getName()) >= amount) {
            return true;
        }
        return false;
    }

    public void addPlayerToPot(UUID playerUUID, double amount) {
        this.playerBet.put(playerUUID, amount);
    }

    public void removePlayerFromPot(UUID playerUUID) {
        this.playerBet.remove(playerUUID);
        this.Percentage.remove(playerUUID);
    }

    public Double getPlayerBet(UUID playerUUID) {
        return this.playerBet.get(playerUUID);
    }

    public int getamountofPlayers() {
        return this.playerBet.size();
    }

    public void returnTheCashInRetract(Player player) {
        UUID playerUUID = player.getUniqueId();
        String Player = player.getName();
        if (playerBet.containsKey(playerUUID)) {
            double bet = getPlayerBet(playerUUID);
            plugin.getEconomy().depositPlayer(player, bet);
            Util.sendMsg(player, ChatColor.AQUA + "You have received " + ChatColor.GREEN + "$" + bet + ChatColor.AQUA + " due to your retract");
            Util.sendMsg(player, ChatColor.AQUA + "You have been removed from the Jackpot and game");
            return;
        } else {
            Util.sendMsg(player, ChatColor.RED + "[ERROR] Seems to be you havent betted, use " + ChatColor.GREEN + "/mass bet " + player.getName() + " <amount>" + ChatColor.RED + " to bet and to be added to the Jack pot");
            return;
        }
    }

    public void addPlayerToPercentage(UUID playerUUID, double percentage) {
        this.Percentage.put(playerUUID, percentage);
    }

    public void removePlayersAfterWinnerChosen() {
        playerBet.clear();
        Percentage.clear();
        ArrayListPlaces.clear();
    }

    public void updatePercentages() {
        //To see  the Map printed put

        for (Map.Entry<UUID, Double> entry : playerBet.entrySet()) {
            UUID key = entry.getKey();
            Double valueBet = entry.getValue();
            DecimalFormat df = new DecimalFormat("#.######");
            double percentageUpdated = Double.valueOf(df.format(valueBet / getTotalPotAmount()));
            //  double percentageUpdated = valueBet / getTotalPotAmount();
            this.Percentage.put(key, percentageUpdated);
        }

    }

    public void howManyPlaceEachPlayerGets() {
        int places;
        for (Map.Entry<UUID, Double> entry : Percentage.entrySet()) {
            UUID key = entry.getKey();
            Double value = entry.getValue() * 1000;
            String valueString = value.toString();
            int valueInt = (int) Math.round(value);
            ArrayListPlaces.put(key, valueInt);
        }
        for (Map.Entry<UUID, Integer> entry : ArrayListPlaces.entrySet()) {
            String key = entry.getKey().toString();
            int value = entry.getValue();
        }

    }

    public void putInArray() {
        ArrayList list = new ArrayList();
        Random r = new Random();
        for (Map.Entry<UUID, Integer> entry : ArrayListPlaces.entrySet()) {
            UUID key = entry.getKey();
            int value = entry.getValue();
            int h = 1;
            for (int i = 1; i < value; i++) {
                list.add(key);
                h++;
            }
        }
        UUID a = (UUID) list.get(r.nextInt(list.size()));
        String winner = Bukkit.getPlayer(a).getName();
        Player winnerPlayer = Bukkit.getPlayer(winner);

        for (Map.Entry<UUID, Double> entry : playerBet.entrySet()) {
            UUID key = entry.getKey();
            String bettor = Bukkit.getPlayer(key).getName();
            Player bettorPlayer = Bukkit.getPlayer(bettor);
            double value = entry.getValue();
            PlayerStats playerStats = getPlayerStats(key);
            Double Overallwins = playerStats.getOverall();
            int wins = playerStats.getWins();
            int loss = playerStats.getLoss();


            if (winnerPlayer == bettorPlayer) {
                double jackpot = this.getTotalPotAmount();
                double bet = this.getPlayerBet(key);
                double chance = bet / jackpot;


                NumberFormat defaultFormat = NumberFormat.getPercentInstance();
                defaultFormat.setMinimumFractionDigits(2);

                Util.sendMsg(bettorPlayer, ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟");
                Util.sendMsg(bettorPlayer, ChatColor.GREEN + "YOU HAVE WON");
                Util.sendMsg(bettorPlayer, ChatColor.AQUA + "You have won : " + ChatColor.GREEN + "$" + jackpot);
                Util.sendMsg(bettorPlayer, ChatColor.AQUA + "You had a : " + ChatColor.LIGHT_PURPLE + defaultFormat.format(chance) + ChatColor.AQUA + " of winning");
                Util.sendMsg(bettorPlayer, ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟" + ChatColor.RED + "⍟" + ChatColor.GOLD + "⍟" + ChatColor.YELLOW + "⍟" + ChatColor.GREEN + "⍟" + ChatColor.AQUA + "⍟" + ChatColor.BLUE + "⍟" + ChatColor.DARK_PURPLE + "⍟");
                wins ++;
                Overallwins = Overallwins+ jackpot ;


                playerStats.setWins(wins);
                playerStats.setOverAllWinl(Overallwins);



                plugin.getEconomy().depositPlayer(winnerPlayer, jackpot);


            }
            if (winnerPlayer != bettorPlayer) {
                double jackpot = this.getTotalPotAmount();
                double bet = this.getPlayerBet(key);
                double chance = bet / jackpot;


                NumberFormat defaultFormat = NumberFormat.getPercentInstance();
                defaultFormat.setMinimumFractionDigits(2);
                Bukkit.broadcastMessage(ChatColor.AQUA + "Winner is " + ChatColor.GREEN + winnerPlayer.getDisplayName() + ChatColor.AQUA + ". They have won " + ChatColor.GREEN + "$" + jackpot);

                Util.sendMsg(bettorPlayer, ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ ");
                Util.sendMsg(bettorPlayer, ChatColor.RED + "YOU HAVE LOST");
                Util.sendMsg(bettorPlayer, ChatColor.AQUA + "You have lost : " + ChatColor.GREEN + "$" + bet);
                Util.sendMsg(bettorPlayer, ChatColor.AQUA + "The Jack pot was : " + ChatColor.GREEN + "$" + jackpot);
                Util.sendMsg(bettorPlayer, ChatColor.AQUA + "You had a : " + ChatColor.LIGHT_PURPLE + defaultFormat.format(chance) + ChatColor.AQUA + " of winning");
                Util.sendMsg(bettorPlayer, ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ " + ChatColor.GREEN + "☠ " + ChatColor.AQUA + "☠ " + ChatColor.BLUE + "☠ " + ChatColor.DARK_PURPLE + "☠ " + ChatColor.RED + "☠ " + ChatColor.GOLD + "☠ " + ChatColor.YELLOW + "☠ ");
                loss++;

                Overallwins = Overallwins - this.getPlayerBet(bettorPlayer.getUniqueId());

                playerStats.setLoss(loss);
                playerStats.setOverAllWinl(Overallwins);




            }
        }

    }

    public void pickWinner() {
        final long gCD = 200L; //greatestCommonDenominator
        AtomicLong time = new AtomicLong();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            switch ((int) ((time.getAndAdd(gCD) % 800) / gCD)) { //800 is our total "execution loop" time, divided by our denominator
                case 0: // 0/200 == 0
                    break;
                case 3: // 600/200 == 3
                    if (playerBet.size() >= 2) {
                        howManyPlaceEachPlayerGets();
                        putInArray();
                        removePlayersAfterWinnerChosen();
                    }
                    break;
                default:
                    break;
            }
        }, 0L, gCD);
    }

    public void timeMessage() {
        int time = plugin.getFileManager().getTime();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.AQUA + "=====================================");
                Bukkit.broadcastMessage(ChatColor.GOLD + "[Mass]" + ChatColor.AQUA + " Bet in the Jack Pot, We know you want to win. Use " + ChatColor.GREEN + "/mass bet " + ChatColor.AQUA + "<username>" + ChatColor.GREEN + " <amount>");
                Bukkit.broadcastMessage(ChatColor.AQUA + "=====================================");
            }
        }, 90, time); // 600L (ticks) is equal to 30 seconds (20 ticks = 1 second)
    }

    public void stats(Player player , Player sendMsg){
        try {
            player.getUniqueId();
        }catch (NullPointerException e){
            Util.sendMsg(sendMsg, ChatColor.RED+ "[ERROR] That players stats are un obtainable ");
            return;
        }
        if(hasRegistered(player.getUniqueId()) ==  true) {
            PlayerStats p = getPlayerStats(player.getUniqueId());
            Util.sendMsg(sendMsg, "Your total amount of wins are: " +ChatColor.AQUA+ p.getWins());
            Util.sendMsg(sendMsg, "Your total amount of losses are: " +ChatColor.RED+ p.getLoss());
            Util.sendMsg(sendMsg, "Your net wins are : $" +ChatColor.GREEN+ p.getOverall());
        }

    }
}


