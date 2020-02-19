package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.Lobby;

import java.util.ArrayList;

public interface PlayService {
    String[] showGames(String userid);

    boolean getPowerPendingLeech(String gameid, String userid);
    void turnEnd(String gameid);
    String[][] topScore();
    ArrayList<Lobby> showLobby(String userid);
}
