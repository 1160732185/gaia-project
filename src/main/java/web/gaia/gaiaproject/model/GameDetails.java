package web.gaia.gaiaproject.model;

import java.util.ArrayList;

public class GameDetails {
    public Game getGame() {
        return game;
    }

    private  String[] bid;
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

    public ArrayList<String> getGamerecord() {
        return gamerecord;
    }

    public void setGamerecord(ArrayList<String> gamerecord) {
        this.gamerecord = gamerecord;
    }

    //对局记录
    private ArrayList<String> gamerecord;
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
    private ArrayList<String>[] vpdetail;
    private ArrayList<String>[][] satellite;
    private int[] townremain;
    //玩家所有科技板/回合助推板/额外行动
    private String[][][] playeraction;
    private int[][] income;
    //建筑数量
    private int[][] buildingcount;
    //待吸收的魔力
    private Power[] powerleech;
    private boolean[][] jisheng;
    private boolean[][][] townbuilding;
    private ArrayList<String> fasts;
    public int[] getTownremain() {
        return townremain;
    }

    public void setTownremain(int[] townremain) {
        this.townremain = townremain;
    }

    public int[][] getBuildingcount() {
        return buildingcount;
    }

    public void setBuildingcount(int[][] buildingcount) {
        this.buildingcount = buildingcount;
    }

    public int[][] getIncome() {
        return income;
    }

    public void setIncome(int[][] income) {
        this.income = income;
    }

    public Power[] getPowerleech() {
        return powerleech;
    }

    public void setPowerleech(Power[] powerleech) {
        this.powerleech = powerleech;
    }

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

    public ArrayList<String>[][] getSatellite() {
        return satellite;
    }

    public void setSatellite(ArrayList<String>[][] satellite) {
        this.satellite = satellite;
    }

    public ArrayList<String>[] getVpdetail() {
        return vpdetail;
    }

    public void setVpdetail(ArrayList<String>[] vpdetail) {
        this.vpdetail = vpdetail;
    }

    public boolean[][] getJisheng() {
        return jisheng;
    }

    public void setJisheng(boolean[][] jisheng) {
        this.jisheng = jisheng;
    }

    public boolean[][][] getTownbuilding() {
        return townbuilding;
    }

    public void setTownbuilding(boolean[][][] townbuilding) {
        this.townbuilding = townbuilding;
    }

    public ArrayList<String> getFasts() {
        return fasts;
    }

    public void setFasts(ArrayList<String> fasts) {
        this.fasts = fasts;
    }

    public String[] getBid() {
        return bid;
    }

    public void setBid(String[] bid) {
        this.bid = bid;
    }
}
