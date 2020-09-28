package web.gaia.gaiaproject.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import web.gaia.gaiaproject.model.*;

import java.util.ArrayList;

public interface PlayService {
    String[] showGames(String userid);
    //每天一小时清空一次缓存数据
    @Scheduled(cron="0 0 * * * ? ")
    @CacheEvict(cacheNames = {"player"},allEntries = true)
    void executeEvictCache();
    PlayerDetails getPlayer(String userid);
    boolean getPowerPendingLeech(String gameid, String userid);
    void turnEnd(String gameid, String bidid);
    String[][] topScore();
    ArrayList<Lobby> showLobby(String userid,String end);
    Vp[] getiniVP(String gameid);
    void saveLog(String gameid, String userid, String log);
    String showLog(String gameid, String userid);
    ArrayList<PendingGame> showPending();
    String getGameModeName(String gamemode);
    String joinGame(String gameid, String userid);
    ArrayList<ArrayList<League>> showPendingLeague();
    String joinLeague(String gameid, String userid);
    String[][] getLeaguedetail(String leagueid);
    Log[] getLogs(String gameid);
    ArrayList<Info> getinfo();
}
