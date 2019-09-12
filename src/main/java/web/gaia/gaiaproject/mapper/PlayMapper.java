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
    @Update("update play set o=#{o},c=#{c},k=#{k},q=#{q},p1=#{p1},p2=#{p2} where gameid = #{gameid} and userid = #{userid}")
    public void setInitResource(int o,int c,int k,int q,int p1,int p2,String gameid,String userid);
    @Update("update play set terralv = terralv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceTerra(String gameid,String userid);
    @Update("update play set shiplv = shiplv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceShip(String gameid,String userid);
    @Update("update play set qlv = qlv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceQ(String gameid,String userid);
    @Update("update play set gaialv = gaialv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceGaia(String gameid,String userid);
    @Update("update play set ecolv = ecolv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceEco(String gameid,String userid);
    @Update("update play set reslv = reslv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceRes(String gameid,String userid);
    @Select("select race from play where bonus = #{bonus} and gameid = #{gameid}")
    public String selectRaceByBonus(String gameid,int bonus);
    @Update("update play set bonus = #{bonus} where userid = #{userid} and gameid = #{gameid}")
    public void updateBonusById(String gameid,String userid,int bonus);
    @Select("select count(*) from play where gameid = #{gameid} and pass!=0")
    public int selectPassNo(String gameid);
    @Update("update play set pass = #{no} where gameid = #{gameid} and userid = #{userid}")
    public void updatePassNo(String gameid,String userid,int no);
    @Update("update play set pass = 0 where gameid = #{gameid}")
    public void roundEnd(String gameid);
}
