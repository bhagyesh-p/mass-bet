package dogboy602k.MassBet.Util;

/**
 * Created by dogboy on 7/12/2016.
 */
public class SetterAndGetter {

    private int time;
    private double maxbet;
    private double minbet;

    public SetterAndGetter(double maxbet , double minbet, int time){
        this.maxbet = maxbet;
        this.minbet = minbet;
        this.time = time;

    }

    public double getMaxbet(){
        return maxbet;
    }

    public double getMinbet(){
        return  minbet;
    }

    public int getTime(){
        return time;
    }
}
