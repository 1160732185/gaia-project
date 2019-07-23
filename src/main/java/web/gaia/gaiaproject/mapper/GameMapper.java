package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.User;

import java.util.List;

@Mapper
public interface GameMapper {
    @Insert("insert into game (gameId,terratown,mapseed,otherseed) VALUES (#{gameId},#{terratown},#{mapseed},#{otherseed})")
    public void createGame(String gameId,int terratown,String mapseed,String otherseed);
    @Select("select * from game where gameId = #{gameId}")
    public Game getGameById(String gameId);

}
