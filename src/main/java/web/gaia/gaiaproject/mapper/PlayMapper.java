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
    @Select("select * from play where gameid = #{gameid} order by pass,position")
    public Play[] getPlayByGameId(String gameid);
    @Select("select * from play where gameid = #{gameid} and userid = #{userid}")
    public Play getPlayByGameIdUserid(String gameid,String userid);
    @Select("select * from play where gameid = #{gameid} and race = #{race}")
    public Play getPlayByGameIdRace(String gameid,String race);
    @Update("update play set o=#{o} where gameid = #{gameid} and userid = #{userid}")
    public void updateO(String gameid,String userid,int o);
    @Update("update play set q=#{q} where gameid = #{gameid} and userid = #{userid}")
    public void updateQ(String gameid,String userid,int q);
    @Update("update play set c=#{c} where gameid = #{gameid} and userid = #{userid}")
    public void updateC(String gameid,String userid,int c);
    @Update("update play set k=#{k} where gameid = #{gameid} and userid = #{userid}")
    public void updateK(String gameid,String userid,int k);
    @Update("update play set p1=#{power1},p2=#{power2},p3=#{power3},pg=#{powerG},racea1 = #{racea1} where gameid = #{gameid} and userid = #{userid}")
    public void updatePower(String gameid,String userid,int power1,int power2,int power3,int powerG,String racea1);
    @Update("update play set p1=#{power1},p2=#{power2},p3=#{power3},pg=#{powerG} where gameid = #{gameid} and userid = #{userid}")
    public void updatePowerOld(String gameid,String userid,int power1,int power2,int power3,int powerG);
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
    @Update("update play set scilv = scilv+1 where gameid = #{gameid} and userid = #{userid}")
    public void advanceSci(String gameid,String userid);
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
    @Update("update play set o = #{o},c = #{c},k = #{k},q = #{q},where gameid = #{gameid} and userid = #{userid}")
    public void income(String gameid,String userid,int o,int c,int k,int q,int p1,int p2,int p3);
    @Update("update play set position = #{position} where gameid = #{gameid} and userid = #{userid}")
    public void updatePosition(String gameid,String userid,int position);
    @Select("select userid from play where gameid = #{gameid} and race = #{race}")
    public String getUseridByRace(String gameid,String race);
    //Todo
    @Update({"update play set blackstar = #{play.blackstar},o=#{play.o},c=#{play.c},k=#{play.k},q=#{play.q},p1=#{play.p1},p2=#{play.p2},p3=#{play.p3},pg=#{play.pg},m1 = #{play.m1},m2 = #{play.m2},m3 = #{play.m3},m4 = #{play.m4},m5 = #{play.m5},m6 = #{play.m6},m7 = #{play.m7},m8 = #{play.m8},tc1 = #{play.tc1},tc2 = #{play.tc2},tc3 = #{play.tc3},tc4 = #{play.tc4},rl1 = #{play.rl1},rl2 = #{play.rl2},rl3 = #{play.rl3},sh = #{play.sh},ac1 = #{play.ac1},ac2 = #{play.ac2},gtu1=#{play.gtu1},gtu3=#{play.gtu3},gtu2=#{play.gtu2},racea1 = #{play.racea1},racea2 = #{play.racea2},racea3 = #{play.racea3},racea4 = #{play.racea4},racea5 = #{play.racea5},racea6 = #{play.racea6} where gameid = #{play.gameid} and userid = #{play.userid}"})
    public void updatePlayById(@Param (value = "play")Play play);
    @Select("select * from play where gameid = #{gameid} and position = #{position}")
    public Play selectPlayByGameIdPosition(String gameid,int position);
    @Update("update play set racea1 = '0' where userid = #{userid} and gameid = #{gameid}")
    void updateRacea1(String gameid,String userid);
    @Select("select q from play where userid = #{userid} and gameid = #{gameid}")
    int getQById(String gameid,String userid);
    @Update("update play set q = #{q} where userid = #{userid} and gameid = #{gameid}")
    int updateQById(String gameid,String userid,int q);
}
