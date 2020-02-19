package web.gaia.gaiaproject.model;

public class Lobby {
    String gameid;
    String currentuserid;
    String round;
    String turn;
    String race;
    String lasttime;

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getCurrentuserid() {
        return currentuserid;
    }

    public void setCurrentuserid(String currentuserid) {
        this.currentuserid = currentuserid;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }
}
