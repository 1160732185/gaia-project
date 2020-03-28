package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.*;
import web.gaia.gaiaproject.model.*;

import java.util.ArrayList;

@Mapper
public interface OtherMapper {
    @Select("select * from power where gameid = #{gameid}")
    public Power[] getAllPowerById(String gameid);
    @Select("select * from power where gameid = #{gameid} and receiverace = #{receiverace} order by num")
    public Power[] getPowerByGameIdUserId(String gameid,String receiverace);
    @Select("select count(*) from power where gameid = #{gameid} and receiverace = #{receiverace}")
    public int getPowerPendingLeech(String gameid,String receiverace);
    @Select("select * from power where gameid = #{gameid} and giverace = #{giverace} and receiverace = #{receiverace} and structure = #{structure} and location = #{location}")
    public Power getPowerById(String gameid,String giverace,String receiverace,String location,String structure);
    @Delete("delete from power where gameid = #{gameid} and receiverace = #{receiverace} and structure = #{structure} and location = #{location}")
    public void deletePowerById(String gameid,String receiverace,String location,String structure);
    @Insert("insert into power(gameid,giverace,receiverace,location,structure,power) VALUES(#{gameid},#{giverace},#{receiverace},#{location},#{structure},#{power})")
    public void insertPower(String gameid,String giverace,String receiverace,String location,String structure,int power);
    @Insert("insert into havett(gameid,userid,ttno,ttstate) VALUES(#{gameid},#{userid},#{ttno},#{ttstate})")
    public void insertHaveTt(String gameid,String userid,String ttno,String ttstate);
    @Select("select * from havett where gameid = #{gameid}")
    public HaveTt[] getHaveTt(String gameid);
    @Select("select * from havett where gameid = #{gameid} and userid = #{userid}")
    public HaveTt[] getHaveTtByUserid(String gameid,String userid);
    @Select("select count(*) from gaia where gameid = #{gameid} and location = #{location}")
    public int getGaia(String gameid,String location);
    @Insert("insert into gaia(gameid,location) VALUES(#{gameid},#{location})")
    public void insertGaia(String gameid,String location);
    @Select("select location from gaia where gameid = #{gameid}")
    public String[] getAllGaia(String gameid);
    @Insert("insert satellite(gameid,userid,location) VALUES(#{gameid},#{userid},#{location})")
    public void insertSate(String gameid,String userid,String location);
    @Insert("insert townbuilding(gameid,userid,location) VALUES(#{gameid},#{userid},#{location})")
    public void insertTB(String gameid,String userid,String location);
    @Insert("insert havetown(ttno,gameid,userid,ttstate) VALUES(#{towntype},#{gameid},#{userid},#{state})")
    public void insertHT(String gameid,String userid,int towntype,String state);
    @Select("select userid,location from satellite where gameid = #{gameid}")
    public Satellite[] getSatellite(String gameid);
    @Select("select location from satellite where gameid = #{gameid} and userid = #{userid}")
    public String[] getSatelliteByUserid(String gameid,String userid);
    @Select("select ttno from havetown where gameid = #{gameid}")
    public String[] getHT(String gameid);
    @Select("select ttno from havetown where gameid = #{gameid} and userid = #{userid}")
    public String[] getHTTypeById(String gameid,String userid);
    @Select("select * from havetown where gameid = #{gameid} and userid = #{userid} and ttstate = '可用'")
    public HaveTown[] getAvaHTByGameIdUserId(String gameid, String userid);
    @Select("select * from havetown where gameid = #{gameid} and userid = #{userid}")
    public HaveTown[] getHTByGameIdUserId(String gameid, String userid);
    @Select("select * from havetown where gameid = #{gameid}")
    public HaveTown[] getHTByGameId(String gameid);
    @Select("select ttno,ttstate,gameid,userid from havetown where gameid = #{gameid}")
    public HaveTt[] getAllHT(String gameid);
    @Insert("insert into vp(gameid,userid,gainvp,reason) VALUES(#{gameid},#{userid},#{gainvp},#{reason})")
    public void gainVp(String gameid,String userid,int gainvp,String reason);
    @Select("select sum(gainvp) from vp where gameid = #{gameid} and userid = #{userid} ")
    public int getvp(String gameid,String userid);
    @Delete("delete from gaia where gameid = #{gameid}")
    public void deletegaia(String gameid);
    @Delete("delete from havetown where gameid = #{gameid}")
    public void deletehavetown(String gameid);
    @Delete("delete from havett where gameid = #{gameid}")
    public void deletehavett(String gameid);
    @Delete("delete from play where gameid = #{gameid}")
    public void deleteplay(String gameid);
    @Delete("delete from power where gameid = #{gameid}")
    public void deletepower(String gameid);
    @Delete("delete from satellite where gameid = #{gameid}")
    public void deletesatellite(String gameid);
    @Select("select count(*) from townbuilding where gameid = #{gameid} and userid = #{userid} and location = #{location}")
    public int gettownbuilding(String gameid,String userid,String location);
    @Delete("delete from townbuilding where gameid = #{gameid}")
    public void deletetownbuilding(String gameid);
    @Delete("delete from vp where gameid = #{gameid}")
    public void deletevp(String gameid);
    @Delete("delete from game where gameid = #{gameid}")
    public void deletegame(String gameid);
    @Update({"update havetown set ttstate = #{haveTown.ttstate} where id = #{haveTown.id}"})
    public void updateHaveTownById(@Param(value = "haveTown") HaveTown haveTown);
    @Select ("select count(*) from havett where gameid = #{gameid} and userid = #{userid} and ttno = #{ttno} and ttstate != '被覆盖'")
    public int getminusltt(String gameid,String userid,String ttno);
    @Update("update havett set ttstate = '被覆盖' where gameid = #{gameid} and userid = #{userid} and ttno = #{ttno}")
    public void lttfugai(String gameid,String userid,String ttno);
    @Update("update havett set ttstate = '已使用' where gameid = #{gameid} and userid = #{userid} and ttno = #{ttno}")
    public void ttuse(String gameid,String userid,String ttno);
    @Select("select * from vp where gameid = #{gameid} and userid = #{userid}")
    public Vp[] getVpByGameidUserid(String gameid,String userid);
    @Select("select * from havett where gameid = #{gameid} and userid = #{userid} and ttno = #{ttno}")
    public HaveTt getTtByGameidUseridTtno(String gameid,String userid,String ttno);
    @Update("update havett set ttstate = '可用' where gameid = #{gameid} and ttstate = '已使用'")
    public void roundEnd(String gameid);
    @Select("select count(*) from satellite where gameid = #{gameid} and location = #{location}")
    public int hassatellite(String gameid, String location);
    @Select("select count(*) from townbuilding where gameid = #{gameid} and userid = #{userid}")
    public int zjjf2(String gameid,String userid);
    @Select("select count(*) from satellite where gameid = #{gameid} and userid = #{userid}")
    public int zjjf6(String gameid,String userid);
    @Select("select count(*) from havetown where gameid = #{gameid} and userid = #{userid}")
    public int gettownnum(String gameid,String userid);
    @Select("select * from townbuilding where gameid = #{gameid}")
    public TownBuilding[] getAllTownBuilding(String gameid);
    @Select("select location from townbuilding where gameid = #{gameid} and userid = #{userid}")
    public String[] getTownBuildingByUserid(String gameid,String userid);
    @Select("select * from play")
    public Play[] getAllPlay();
    @Select("select * from power where gameid = #{gameid} and giverace = #{giverace} and receiverace = #{receiverace} ")
    Power[] getPowerByIdCR(String gameid, String giverace, String receiverace);
    @Delete("delete from power where gameid = #{gameid} and receiverace = #{receiverace}")
    void deletePowerByIdCR(String gameid, String receiverace);
    @Delete("delete from power where gameid = #{gameid}")
    void deleteAllPower(String gameid);
    @Delete("delete from vp where gameid = #{gameid} and reason = '起始分'")
    void deleteiniscore(String gameid);
    @Select("select * from vp where gameid = #{gameid} and reason = '起始分'")
    Vp[] getiniVps(String gameid);
    @Select("select gainvp from vp where gameid = #{gameid} and reason = '起始分' and userid = #{userid}")
    int getiniVpByUserid(String gameid,String userid);
    @Select("select * from power where gameid = #{gameid}")
    Power[] getPowerByGameId(String gameid);
    @Select("select * from league")
    ArrayList<League> getPLeagues();

}
