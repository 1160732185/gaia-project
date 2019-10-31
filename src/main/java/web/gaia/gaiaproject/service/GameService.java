package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.Play;
import web.gaia.gaiaproject.model.Power;

import java.util.ArrayList;

public interface GameService {
    void deleteGame(String gameid);
    void createGame(String gameId, String player1, String player2, String player3, String player4);
    Game getGameById(String gameId);
    void setMapDetail(String[][] mapDetail,String mapseed);
    void updateRecordById(String gameid,String record);
    String getGameStateById(String gameid);
    String[] getRoundScoreById(String gameid);
    String[][] getHelpTileById(String gameid);
    boolean[] getAvaraceById(String gameid);
    String[] getTTByid(String gameid);
    String getCurrentUserIdById(String gameid);
    String[][] getResourceById(String gameid);
    String[][] getStructureSituationById(String gameid);
    String[][] getStructureColorById(String gameid);
    void chooseRace(String gameid,String userid,String race);
    String buildMine(String gameid,String userid,String location,String action);
    String pass(String gameid,String userid,String bon);
    String[][][] getScienceGrade(String gameid);
    Power[] getPowerLeech(String gameid);
    String leechPower(String gameid, String receiverace, String location, String structure, String accept);
    String upgrade(String gameid, String userid, String substring);
    int[][] income(String gameid,boolean b);
    int[][] getBuildingcount(String gameid);
    boolean takett(String gameid, String userid, String str, String str1);
    boolean takeatt(String gameid, String userid, String str, String str1, String str2);
    String[][][] getPlayerAction(String gameid);
    String advance(String gameid, String userid, String substring,boolean needk);
    int[] getTownremain(String gameid);
    String action(String gameid, String userid, String substring);
    String gaia(String gameid, String userid, String substring,String action);
    boolean canArrive(String gameid,String userid,String location,String action);
    String form(String gameid, String userid, String substring);
    ArrayList<String>[][] getSatellite(String gameid);
    ArrayList<String>[] getVpDetail(String gameid);
    String convert(String gameid, String userid, String substring);
    Play getPlayByGameidUserid(String gameid,String userid);
    String roundbeginaction(String gameid, String userid, String act);
}
