package web.gaia.gaiaproject.model;

public class GameDetails {
    private String[][] mapsituation;
    private String gamestate;
    private String[] gamerecord;
    private String[] roundscore;
    private String[][] helptile;
    private boolean[] avarace;
    private String[] tt;
    private String currentuserid;

    public String[][] getMapsituation() {
        return mapsituation;
    }

    public void setMapsituation(String[][] mapsituation) {
        this.mapsituation = mapsituation;
    }

    public String getGamestate() {
        return gamestate;
    }

    public void setGamestate(String gamestate) {
        this.gamestate = gamestate;
    }

    public String[] getGamerecord() {
        return gamerecord;
    }

    public void setGamerecord(String[] gamerecord) {
        this.gamerecord = gamerecord;
    }

    public String[] getRoundscore() {
        return roundscore;
    }

    public void setRoundscore(String[] roundscore) {
        this.roundscore = roundscore;
    }

    public String[][] getHelptile() {
        return helptile;
    }

    public void setHelptile(String[][] helptile) {
        this.helptile = helptile;
    }

    public boolean[] getAvarace() {
        return avarace;
    }

    public void setAvarace(boolean[] avarace) {
        this.avarace = avarace;
    }

    public String[] getTt() {
        return tt;
    }

    public void setTt(String[] tt) {
        this.tt = tt;
    }

    public String getCurrentuserid() {
        return currentuserid;
    }

    public void setCurrentuserid(String currentuserid) {
        this.currentuserid = currentuserid;
    }
}
