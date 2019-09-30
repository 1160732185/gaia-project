package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.*;
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
    @Select("select * from play where gameid = #{gameid} and race = #{race}")
    public Play getPlayByGameIdRace(String gameid,String race);
    @Update("update play set m1=#{m1} where gameid = #{gameid} and userid = #{userid}")
    public void updateM1ByGameIdUserid(String gameid,String userid,String m1);
    @Update("update play set m2=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM2ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set m3=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM3ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set m4=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM4ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set m5=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM5ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set m6=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM6ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set m7=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM7ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set m8=#{m2} where gameid = #{gameid} and userid = #{userid}")
    public void updateM8ByGameIdUserid(String gameid,String userid,String m2);
    @Update("update play set o=o-1,c=c-2 where gameid = #{gameid} and userid = #{userid}")
    public void buildMine(String gameid,String userid);
    @Update("update play set o=o-2,c=c-3 where gameid = #{gameid} and userid = #{userid}")
    public void upgradeTc(String gameid,String userid);
    @Update("update play set o=o-3,c=c-5 where gameid = #{gameid} and userid = #{userid}")
    public void upgradeRl(String gameid,String userid);
    @Update("update play set o=o-4,c=c-6 where gameid = #{gameid} and userid = #{userid}")
    public void upgradeSh(String gameid,String userid);
    @Update("update play set o=o-6,c=c-6 where gameid = #{gameid} and userid = #{userid}")
    public void upgradeAc(String gameid,String userid);
    @Update("update play set o=#{o} where gameid = #{gameid} and userid = #{userid}")
    public void updateO(String gameid,String userid,int o);
    @Update("update play set q=#{q} where gameid = #{gameid} and userid = #{userid}")
    public void updateQ(String gameid,String userid,int q);
    @Update("update play set c=#{c} where gameid = #{gameid} and userid = #{userid}")
    public void updateC(String gameid,String userid,int c);
    @Update("update play set p1=#{power1},p2=#{power2},p3=#{power3},pg=#{powerG} where gameid = #{gameid} and userid = #{userid}")
    public void updatePower(String gameid,String userid,int power1,int power2,int power3,int powerG);
    @Update("update play set vp = #{vp} where gameid = #{gameid} and userid = #{userid}")
    public void updateVp(String gameid,String userid,int vp);
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
    @Update("update play set position = pass where gameid = #{gameid}")
    public void roundEnd(String gameid);
    @Update("update play set pass = 0 where gameid = #{gameid}")
    public void roundEnd2(String gameid);
    @Update("update play set position = #{position} where gameid = #{gameid} and userid = #{userid}")
    public void updatePosition(String gameid,String userid,int position);
    @Select("select userid from play where gameid = #{gameid} and race = #{race}")
    public String getUseridByRace(String gameid,String race);
    //Todo
    @Update({"update play set m1 = #{play.m1},m2 = #{play.m2},m3 = #{play.m3},m4 = #{play.m4},m5 = #{play.m5},m6 = #{play.m6},m7 = #{play.m7},m8 = #{play.m8},tc1 = #{play.tc1},tc2 = #{play.tc2},tc3 = #{play.tc3},tc4 = #{play.tc4} where gameid = #{play.gameid} and userid = #{play.userid}"})
    public void updatePlayById(@Param (value = "play")Play play);
}
