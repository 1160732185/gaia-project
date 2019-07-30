package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.Play;

@Mapper
public interface PlayMapper {
    @Insert("insert into play (gameId,userid,position) VALUES (#{gameId},#{player},#{position})")
    public void insertPlay(String gameId,String player,int position);
    @Select("select gameId from play where userid = #{userid}")
    public String[] showGames(String userid);
    @Select("select userid from play where gameid = #{gameid} order by position")
    public String[] getUseridByGameId(String gameid);
    @Update("update play set race = #{race} where gameid = #{gameid} and userid = #{userid}")
    public void playerChooseRace(String gameid,String userid,String race);
    @Select("select * from play where gameid = #{gameid} order by position")
    public Play[] getPlayByGameId(String gameid);
    @Select("select * from play where gameid = #{gameid} and userid = #{userid}")
    public Play getPlayByGameIdUserid(String gameid,String userid);
    @Update("update play set m1=#{m1} where gameid = #{gameid} and userid = #{userid}")
    public void updateM1ByGameIdUserid(String gameid,String userid,String m1);
    @Update("update play set m2=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM2ByGameIdUserid(String gameid,String userid,String m2);
}
