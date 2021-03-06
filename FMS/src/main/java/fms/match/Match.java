package main.java.fms.match;

import main.java.UI.AudioManager;
import main.java.UI.text.UIStateMachine;
import main.java.fms.FMSStates;
import main.java.fms.scoring.ScoreConstants;
import main.java.fms.scoring.team.Team;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Match {

    private boolean scoresLocked = false;
    private static int matchID = 0;
    private int matchNum;
    private long startingTime = 0;
    private Alliance blue;
    private Alliance red;

    public enum MatchState{
        AUTO, BREAK, TELE, DONE
    }
    public MatchState matchState = MatchState.AUTO;

    public enum MatchType{
        QUAL, Q1, Q2, Q3, Q4, S1, S2, F
    }
    private MatchType matchType;

    private static int getNewMatchID(){
        matchID++;
        return matchID;
    }

    public Match(int matchNum_, MatchType matchType_, Team r1, Team r2, Team b1, Team b2){

        blue = new Alliance(Alliance.AllianceColor.BLUE);
        red = new Alliance(Alliance.AllianceColor.RED);

        red.setTeams(r1, r2);
        blue.setTeams(b1, b2);

        matchNum = matchNum_;
        matchType = matchType_;

    }

    private int getElapsedSeconds(){
        return (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startingTime);
    }

    public void start(){

        startingTime = System.currentTimeMillis();
        scoresLocked = false;
        UIStateMachine.setGameMode("AUTO");

    }

    public void lockScores(){

        scoresLocked = true;

    }

    public void connectTablets(){
        blue.linkTablets();
        red.linkTablets();
    }

    public void update(){

        switch(matchState){

            case AUTO:
                FMSStates.matchStatus = FMSStates.MatchStatus.AUTO;
                if(getElapsedSeconds() >= ScoreConstants.autoDuration){
                    matchState = MatchState.BREAK;
                    break;
                }
                break;
            case BREAK:
                System.out.println("[MATCH] Starting TeleOp");
                UIStateMachine.setGameMode("TELE");
                matchState = MatchState.TELE;
                FMSStates.matchStatus = FMSStates.MatchStatus.TELE;
                AudioManager.playTeleSound();
                break;
            case TELE:
                if(getElapsedSeconds() >= (ScoreConstants.autoDuration + ScoreConstants.teleDuration)) {
                    matchState = MatchState.DONE;
                }
                break;
            case DONE:
                blue.generateAllianceResults();
                red.generateAllianceResults();
                FMSStates.matchStatus = FMSStates.MatchStatus.PRE;
                break;
        }

        blue.calculateTotalScore();
        red.calculateTotalScore();
        updateScoreBoard();
    }

    public Alliance.AllianceColor calculateWinner(){
        if(red.getTotalScore() > blue.getTotalScore()){
            red.setWin(true);
            blue.setWin(false);
            return Alliance.AllianceColor.RED;
        }
        else if(blue.getTotalScore() > red.getTotalScore()){
            red.setWin(false);
            blue.setWin(true);
            return Alliance.AllianceColor.BLUE;
        }
        else{
            red.setWin(false);
            blue.setWin(false);
            red.setTie(true);
            blue.setTie(true);
            return Alliance.AllianceColor.NONE;
        }
    }

    public void updateTeamScores(){

        blue.updateTeamScores();
        red.updateTeamScores();

    }

    private void updateScoreBoard(){


        calculateWinner();
        if(!scoresLocked){

            //Update Time
            UIStateMachine.setTime((ScoreConstants.autoDuration + ScoreConstants.teleDuration) - getElapsedSeconds());

            //Update Blue Values
            UIStateMachine.setBlueScore(blue.getTotalScore());
            UIStateMachine.setBlueStandingRelic(blue.getStandingRelics());
            UIStateMachine.setBlueFallenRelic(blue.getFallenRelics());
            UIStateMachine.setBlueFlag(blue.getFlags());
            UIStateMachine.setBlueMoonRocks(blue.getMoonRocks());

            //Update Red Values
            UIStateMachine.setRedScore(red.getTotalScore());
            UIStateMachine.setRedStandingRelic(red.getStandingRelics());
            UIStateMachine.setRedFallenRelic(red.getFallenRelics());
            UIStateMachine.setRedFlag(red.getFlags());
            UIStateMachine.setRedMoonRocks(red.getMoonRocks());

            UIStateMachine.Results.setBlueWLT(blue.getWLT());
            UIStateMachine.Results.setRedWLT(red.getWLT());
            UIStateMachine.Results.setRedOneNum(red.getTeam(0));
            UIStateMachine.Results.setRedTwoNum(red.getTeam(1));
            UIStateMachine.Results.setBlueOneNum(blue.getTeam(0));
            UIStateMachine.Results.setBlueTwoNum(blue.getTeam(1));

            UIStateMachine.Results.setBlueOneName(blue.getTeamName(0));
            UIStateMachine.Results.setBlueTwoName(blue.getTeamName(1));
            UIStateMachine.Results.setRedOneName(red.getTeamName(0));
            UIStateMachine.Results.setRedTwoName(red.getTeamName(1));

            UIStateMachine.Results.setRedOneRank(red.getTeamRank(0));
            UIStateMachine.Results.setRedTwoRank(red.getTeamRank(1));
            UIStateMachine.Results.setBlueOneRank(blue.getTeamRank(0));
            UIStateMachine.Results.setBlueTwoRank(blue.getTeamRank(1));

        }
    }

    public String toString(){
        StringJoiner j = new StringJoiner(",");
        j.add(getNewMatchID() + "");
        j.add(matchType + "");
        j.add(matchNum + "");
        j.add(blue.toString());
        j.add(red.toString());
        return j.toString();
    }


    public String toPrint(){
        StringJoiner j = new StringJoiner(" ");
        j.add(matchType + "");
        j.add("#" + matchNum);
        j.add("Blue Teams: " + blue.getTeam(0) + " and " + blue.getTeam(1));
        j.add("Red Teams: " + red.getTeam(0) + " and " + red.getTeam(1));
        return j.toString();
    }

}
