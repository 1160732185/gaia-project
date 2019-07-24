package web.gaia.gaiaproject.model;

public class GameDetails {
    private String[][] mapsituation;
    private String gamestate;
    private String[] gamerecord;
    private String[] roundscore;
    private String[][] helptile;

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
}
