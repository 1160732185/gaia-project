package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.Play;

public interface GameService {
    void createGame(String gameId,String player1,String player2,String player3,String player4);
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
    void chooseRace(String gameid,String userid,String race);
    String buildMine(String gameid,String userid,String location);
    String[][] getStructureSituationById(String gameid);
    String[][] getStructureColorById(String gameid);
}
