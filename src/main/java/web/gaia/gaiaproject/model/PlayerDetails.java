package web.gaia.gaiaproject.model;

import java.util.ArrayList;

public class PlayerDetails {
    int rungamenum;
    int finishgamenum;
    String[] otherinfo;
    ArrayList<Lobby> rungamedetail;
    ArrayList<Lobby> finishgamedetail;
    ArrayList<ArrayList<String>> raceinfo;
    ArrayList<ArrayList<String>> opponentinfo;

    public int getRungamenum() {
        return rungamenum;
    }

    public void setRungamenum(int rungamenum) {
        this.rungamenum = rungamenum;
    }

    public int getFinishgamenum() {
        return finishgamenum;
    }

    public void setFinishgamenum(int finishgamenum) {
        this.finishgamenum = finishgamenum;
    }

    public ArrayList<ArrayList<String>> getRaceinfo() {
        return raceinfo;
    }

    public void setRaceinfo(ArrayList<ArrayList<String>> raceinfo) {
        this.raceinfo = raceinfo;
    }

    public ArrayList<ArrayList<String>> getOpponentinfo() {
        return opponentinfo;
    }

    public void setOpponentinfo(ArrayList<ArrayList<String>> opponentinfo) {
        this.opponentinfo = opponentinfo;
    }

    public ArrayList<Lobby> getRungamedetail() {
        return rungamedetail;
    }

    public void setRungamedetail(ArrayList<Lobby> rungamedetail) {
        this.rungamedetail = rungamedetail;
    }

    public ArrayList<Lobby> getFinishgamedetail() {
        return finishgamedetail;
    }

    public void setFinishgamedetail(ArrayList<Lobby> finishgamedetail) {
        this.finishgamedetail = finishgamedetail;
    }

    public String[] getOtherinfo() {
        return otherinfo;
    }

    public void setOtherinfo(String[] otherinfo) {
        this.otherinfo = otherinfo;
    }
}
