package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import web.gaia.gaiaproject.model.Game;

@Mapper
public interface PlayMapper {
    @Insert("insert into play (gameId,userid,position) VALUES (#{gameId},#{player},#{position})")
    public void insertPlay(String gameId,String player,int position);
    @Select("select gameId from play where userid = #{userid}")
    public String[] showGames(String userid);
}
