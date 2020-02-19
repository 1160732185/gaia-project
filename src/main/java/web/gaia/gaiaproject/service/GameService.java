package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.Play;
import web.gaia.gaiaproject.model.Power;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public interface GameService {
    void deleteGame(String gameid);
    void createGame(String gameId, String player1, String player2, String player3, String player4,String gamemode);
    void changeGame(String gameId, String player1, String player2, String player3, String player4,String gamemode,int terratown,String mapseed,String otherseed);
    Game getGameById(String gameId);
    void setMapDetail(String[][] mapDetail,String mapseed);
    void updateRecordById(String gameid,String record);
    void updateRRecordById(String gameid,String tr,String record);
    void updateConvertRecordById(String gameid,String race,String convert);
    String getGameStateById(String gameid);
    String[] getRoundScoreById(String gameid);
    String[][] getHelpTileById(String gameid);
    boolean[] getAvaraceById(String gameid);
    String[] getTTByid(String gameid);
    String getCurrentUserIdById(String gameid);
    String[][] getResourceById(String gameid);
    String[][] getStructureSituationById(String gameid);
    String[][] getStructureColorById(String gameid);
    String chooseRace(String gameid,String userid,String race);
    String buildMine(String gameid,String userid,String location,String action);
    String pass(String gameid,String userid,String bon);
    String[][][] getScienceGrade(String gameid);
    Power[] getPowerLeech(String gameid);
    String leechPower(String gameid,String giverace, String receiverace, String location, String structure, String accept);
    String upgrade(String gameid, String userid, String substring);
    int[][] income(String gameid,boolean b);
    int[][] getBuildingcount(String gameid);
    boolean takett(String gameid, String userid, String str, String str1);
    boolean takeatt(String gameid, String userid, String str, String str1, String str2);
    String[][][] getPlayerAction(String gameid);
    String advance(String gameid, String userid, String substring,boolean needk);
    int[] getTownremain(String gameid);
    String action(String gameid, String userid, String substring) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    String gaia(String gameid, String userid, String substring,String action);
    boolean canArrive(String gameid,String userid,String location,String action);
    String form(String gameid, String userid, String substring);
    ArrayList<String>[][] getSatellite(String gameid);
    ArrayList<String>[] getVpDetail(String gameid);
    String convert(String gameid, String userid, String substring);
    Play getPlayByGameidUserid(String gameid,String userid);
    String roundbeginaction(String gameid, String userid, String act);
    boolean[][] getJisheng(String gameid);
    boolean[][][] getTownBuilding(String gameid);
    int gethiveno(String gameid,String userid);
    void LeechPower(String gameid, String giverace, String substring);
    void deletePower(String gameid);

    void updateRecordByIdCR(String gameid, String record);

    void updateLasttime(String gameid);
}
