package web.gaia.gaiaproject.model;

public class GameDetails {
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    private Game game;
    //地图信息
    private String[][] mapsituation;
    //地图建筑物信息
    private String[][] structure;
    //地图建筑物颜色
    private String[][] structurecolor;
    //首行待行动信息
    private String gamestate;
    //对局记录
    private String[] gamerecord;
    //每回合世界观
    private String[] roundscore;
    //回合助推板
    private String[][] helptile;
    //可选种族
    private boolean[] avarace;
    //低级、高级科技板,城顶城片
    private String[] tt;
    private String currentuserid;
    private String[][] resource;
    //第一位种族顺位，第二位科技类别，第三位科技等级，有值则说明是，对应前端页面显示
    private String[][][] sciencegrade;
    //玩家所有科技板/回合助推板/额外行动
    private String[][][] playeraction;

    public Power[] getPowerleech() {
        return powerleech;
    }

    public void setPowerleech(Power[] powerleech) {
        this.powerleech = powerleech;
    }

    //待吸收的魔力
    private Power[] powerleech;

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

    public String[][] getStructure() {
        return structure;
    }

    public void setStructure(String[][] structure) {
        this.structure = structure;
    }

    public String[][] getStructurecolor() {
        return structurecolor;
    }

    public void setStructurecolor(String[][] structurecolor) {
        this.structurecolor = structurecolor;
    }

    public String[][][] getSciencegrade() {
        return sciencegrade;
    }

    public void setSciencegrade(String[][][] sciencegrade) {
        this.sciencegrade = sciencegrade;
    }

    public String[][][] getPlayeraction() {
        return playeraction;
    }

    public void setPlayeraction(String[][][] playeraction) {
        this.playeraction = playeraction;
    }
}
