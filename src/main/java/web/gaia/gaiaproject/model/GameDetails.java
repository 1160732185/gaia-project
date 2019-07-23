package web.gaia.gaiaproject.model;

public class GameDetails {
    private String[][] mapsituation;
    private String gamestate;

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
}
