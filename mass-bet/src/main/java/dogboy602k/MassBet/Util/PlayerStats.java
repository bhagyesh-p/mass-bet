package dogboy602k.MassBet.Util;

import java.util.UUID;

/**
 * Created by dogboy on 7/29/2016.
 */
public class PlayerStats {
    private int wins;
    private int loss;
    private String name;
    private UUID PlayerUUID;
    private double overAllWin;

    public PlayerStats(UUID PlayerUUID, String name, int wins, int loss , double overAllWin ) {
        this.wins = wins;
        this.loss = loss;
        this.name = name;
        this.PlayerUUID = PlayerUUID;
        this.overAllWin = overAllWin;
    }


    public int getWins() {
        return this.wins;
    }

    public int getLoss() {
        return this.loss;
    }

    public double getOverall() {
        return this.overAllWin;
    }

    public void setWins(int wins) {
         this.wins = wins;
    }

    public void setLoss(int loss) {
         this.loss = loss;
    }

    public void setOverAllWinl(double overAllWin) {
        this.overAllWin = overAllWin;
    }

    public String getName() {
        return this.name;
    }

    public UUID getPlayerUUID() {
        return this.PlayerUUID;
    }




}
