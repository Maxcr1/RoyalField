package main.java.networkHandler.tabletHandler;

import main.java.fms.match.Alliance;
import main.java.networkHandler.clientBase.Client;

import java.net.Socket;

public class FieldTablet extends Client {

    private double standingRelics = 0;
    private double fallenRelics = 0;
    private double flags = 0;

    private double ID = 0;
    private Alliance alliance;
    private boolean linked = false;

    public FieldTablet(Socket s){
        super(s);
        System.out.println("Created Field Tablet");
    }

    public void link(Alliance a){
        a.attemptToLink();
        System.out.println("Linking");
        alliance  = a;
        linked = true;
        ID = (int) (Math.random() * 100);

    }

    public Alliance.AllianceColor getAllianceColor(){
        return alliance.getColor();
    }

    public double getStandingRelics() {
        return standingRelics;
    }

    private void setStandingRelics(double standingRelics) {
        this.standingRelics = standingRelics;
    }

    public double getFallenRelics() {
        return fallenRelics;
    }

    private void setFallenRelics(double fallenRelics) {
        this.fallenRelics = fallenRelics;
    }

    public double getFlags() {
        return flags;
    }

    private void setFlags(double flags) {
        this.flags = flags;
    }

    private void parseScoreData(String s) throws IndexOutOfBoundsException{
        String data[] = s.split(";");
        setFlags(Integer.parseInt(data[1]));
        setFallenRelics(Integer.parseInt(data[2]));
        setStandingRelics(Integer.parseInt(data[3]));
    }

    protected void useData(String s){
        if(alliance.isLinked()){
            //TODO This tabletHandler is now linked to a robot.  Score stuff
            try{
                parseScoreData(s);
            }
            catch(IndexOutOfBoundsException e){
                System.out.println("[ERROR] Error parsing score data in " + toString());
            }
        }
    }

    protected void disconnect(){
        linked = false;
        alliance = null;
        System.out.println("Connection to " + ID + " lost");
    }

    boolean isLinked(){
        return linked;
    }

    public String toString(){
        if(linked){
            return "" +ID + ": " + alliance;
        }
        else{
            return "not linked";
        }
    }

}
