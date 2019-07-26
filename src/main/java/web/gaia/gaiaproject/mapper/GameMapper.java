package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import web.gaia.gaiaproject.model.Game;
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
    @Select("select * from tt")
    public TechTile[] getTTById(String gameid);
}
