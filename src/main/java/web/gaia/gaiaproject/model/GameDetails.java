package web.gaia.gaiaproject.model;

public class GameDetails {
    //地图信息
    private String[][] mapsituation;
    //地图建筑物信息
    private String[][] structuresituation;
    //地图建筑物颜色
    private String[][] structurecolor;
    //首行待行动信息
    private String gamestate;
    private String[] gamerecord;
    private String[] roundscore;
    private String[][] helptile;
    private boolean[] avarace;
    private String[] tt;
    private String currentuserid;
    private String[][] resource;


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

    public String[][] getResource() {
        return resource;
    }

    public void setResource(String[][] resource) {
        this.resource = resource;
    }

    public String[][] getStructuresituation() {
        return structuresituation;
    }

    public void setStructuresituation(String[][] structuresituation) {
        this.structuresituation = structuresituation;
    }

    public String[][] getStructurecolor() {
        return structurecolor;
    }

    public void setStructurecolor(String[][] structurecolor) {
        this.structurecolor = structurecolor;
    }
}
