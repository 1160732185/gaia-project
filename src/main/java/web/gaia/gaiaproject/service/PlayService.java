package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.League;
import web.gaia.gaiaproject.model.Lobby;
import web.gaia.gaiaproject.model.PendingGame;
import web.gaia.gaiaproject.model.Vp;

import java.util.ArrayList;

public interface PlayService {
    String[] showGames(String userid);

    boolean getPowerPendingLeech(String gameid, String userid);
    void turnEnd(String gameid, String bidid);
    String[][] topScore();
    ArrayList<Lobby> showLobby(String userid);

    Vp[] getiniVP(String gameid);

    void saveLog(String gameid, String userid, String log);

    String showLog(String gameid, String userid);

    ArrayList<PendingGame> showPending();
    String getGameModeName(String gamemode);
    String joinGame(String gameid, String userid);

    ArrayList<League> showPendingLeague();

    String joinLeague(String gameid, String userid);

    String[][] getLeaguedetail(String leagueid);
}
