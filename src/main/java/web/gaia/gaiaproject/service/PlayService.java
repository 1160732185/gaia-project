package web.gaia.gaiaproject.service;

public interface PlayService {
    String[] showGames(String userid);

    boolean getPowerPendingLeech(String gameid, String userid);
    void turnEnd(String gameid);
}
