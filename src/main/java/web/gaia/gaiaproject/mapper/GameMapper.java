package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.*;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.Play;
import web.gaia.gaiaproject.model.TechTile;
import web.gaia.gaiaproject.model.User;

import java.util.List;

@Mapper
public interface GameMapper {
    @Insert("insert into game (gameId,terratown,mapseed,otherseed) VALUES (#{gameId},#{terratown},#{mapseed},#{otherseed})")
    public void createGame(String gameId,int terratown,String mapseed,String otherseed);
    @Select("select * from game where gameId = #{gameId}")
    public Game getGameById(String gameId);
    @Update("update game set gamerecord = CONCAT(gamerecord,#{record}) where gameid = #{gameid}")
    public void updateRecordById(String gameid,String record);
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
}
