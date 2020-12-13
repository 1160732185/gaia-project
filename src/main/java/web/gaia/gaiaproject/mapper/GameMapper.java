package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.*;
import web.gaia.gaiaproject.model.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface GameMapper {
    @Insert("insert into game (gameId,terratown,mapseed,otherseed,gamemode,lasttime,createtime,admin,blackstar) VALUES (#{gameId},#{terratown},#{mapseed},#{otherseed},#{gamemode},#{lasttime},#{createtime},#{admin},#{blackstar})")
    public void createGame(String gameId, int terratown, String mapseed, String otherseed, String gamemode, String lasttime, Date createtime, String admin, String blackstar);
    @Select("select * from game where gameId = #{gameId}")
    public Game getGameById(String gameId);
    @Update("update game set gamerecord = CONCAT(gamerecord,#{record}) where gameid = #{gameid}")
    public void updateRecordById(String gameid,String record);
    @Update("update game set gamerecord = #{record} where gameid = #{gameid}")
    public void updateLeechRecordById(String gameid,String record);
    @Select("select gamerecord from game where gameid = #{gameid}")
    public String getRecordById(String gameid);
    @Select("select * from tt where ttno like '%tt%'")
    public TechTile[] getTTById(String gameid);
    @Select("select towntypename from towntype where towntypeid=#{i}")
    public String getTownNameById(int i);
    @Update("update game set round = round+1,turn = 1,position = 1,pwa1 = '1',pwa2 = '1',pwa3 = '1',pwa4 = '1',pwa5 = '1',pwa6 = '1',pwa7 = '1',qa1 = '1',qa2 = '1',qa3 = '1' ,bon1 = 1,bon2 = 1 where gameid = #{gameid}")
    public void roundEnd(String gameid);
    @Update("update game set position = #{position} where gameid = #{gameid}")
    public void updatePositionById(String gameid,int position);
    @Update("update game set turn = #{turn} where gameid = #{gameid}")
    public void updateTurnById(String gameid,int turn);
    @Update({"update game set turn = #{game.turn},terratown = #{game.terratown},pwa1=#{game.pwa1},pwa2=#{game.pwa2},pwa3=#{game.pwa3},pwa4=#{game.pwa4},pwa5=#{game.pwa5},pwa6=#{game.pwa6},pwa7=#{game.pwa7},qa1=#{game.qa1},qa2=#{game.qa2},qa3=#{game.qa3},bon1=#{game.bon1},bon2=#{game.bon2} where gameid = #{game.gameId}"})
    public void updateGameById(@Param(value = "game") Game game);
    @Update("update game set blackstar = #{blackstar} where gameid = #{gameid}")
    public void gameEnd(String gameid,String blackstar);
    @Update("update game set gamerecord = #{record} where gameid = #{gameid}")
    void updateRecordByIdCR(String gameid, String record);
    @Update("update game set lasttime = #{time} where gameid = #{gameid}")
    void updateLasttime(String gameid,String time);
    @Select("select gameid from game")
    String[] getAllGames();
    @Select("select * from game")
    Game[] getAllGamesType();
    @Update("update game set mapseed = #{mapseed} where gameid = #{gameid}")
    void updateMapseed(String gameid, String mapseed);
    @Update("update game set blackstar = 'done' where gameid = #{gameid}")
    void updaterotate(String gameid);
    @Select("select * from pendinggame where gameid = #{gameid}")
    PendingGame getPGamebyId(String gameId);
    @Select("select * from pendinggame")
    ArrayList<PendingGame> getAPGamebyId();
    @Insert("insert into pendinggame(gameId,player1,player2,player3,player4,gamemode,des) VALUES (#{gameId},#{player1},#{player2},#{player3},#{player4},#{gamemode},#{des})")
    void addPGame(String gameId, String player1, String player2, String player3, String player4, String gamemode, String des);
    @Delete("delete from pendinggame where gameid = #{gameid}")
    void deletePGame(String gameid);
    @Update("update pendinggame set player2 = #{player2},player3 = #{player3},player4 = #{player4} where gameid = #{gameid}")
    void updatePGame(String gameid, String player2, String player3, String player4);
    @Select("select * from league where leagueid = #{leagueid}")
    League getPLeaguebyId(String leagueid);
    @Update("update league set player1 = #{player1},player2 = #{player2},player3 = #{player3},player4 = #{player4},player5 = #{player5},player6 = #{player6},player7 = #{player7} where leagueid = #{gameid}")
    void updatePLeague(String gameid, String player1, String player2, String player3, String player4, String player5, String player6, String player7);
}
