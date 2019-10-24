package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.*;
import web.gaia.gaiaproject.model.*;

@Mapper
public interface OtherMapper {
    @Select("select * from power where gameid = #{gameid}")
    public Power[] getAllPowerById(String gameid);
    @Select("select * from power where gameid = #{gameid} and receiverace = #{receiverace} and structure = #{structure} and location = #{location}")
    public Power getPowerById(String gameid,String receiverace,String location,String structure);
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
    @Insert("insert townbuilding(gameid,location) VALUES(#{gameid},#{location})")
    public void insertTB(String gameid,String location);
    @Insert("insert havetown(ttno,gameid,userid,ttstate) VALUES(#{towntype},#{gameid},#{userid},#{state})")
    public void insertHT(String gameid,String userid,int towntype,String state);
    @Select("select userid,location from satellite where gameid = #{gameid}")
    public Satellite[] getSatellite(String gameid);
    @Select("select ttno from havetown where gameid = #{gameid}")
    public String[] getHT(String gameid);
    @Select("select ttno from havetown where gameid = #{gameid} and userid = #{userid}")
    public String[] getHTTypeById(String gameid,String userid);
    @Select("select * from havetown where gameid = #{gameid} and userid = #{userid} and ttstate = '可用'")
    public HaveTown[] getAvaHTByGameIdUserId(String gameid, String userid);
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
    @Delete("delete from townbuilding where gameid = #{gameid}")
    public void deletetownbuilding(String gameid);
    @Delete("delete from vp where gameid = #{gameid}")
    public void deletevp(String gameid);
    @Delete("delete from game where gameid = #{gameid}")
    public void deletegame(String gameid);
    @Update({"update havetown set ttstate = #{haveTown.ttstate} where id = #{haveTown.id}"})
    public void updateHaveTownById(@Param(value = "haveTown") HaveTown haveTown);
    @Select ("select count(*) from havett where gameid = #{gameid} and userid = #{userid} and ttno = #{ttno} and ttstate = '可用'")
    public int getminusltt(String gameid,String userid,String ttno);
    @Update("update havett set ttstate = '被覆盖' where gameid = #{gameid} and userid = #{userid} and ttno = #{ttno}")
    public void lttfugai(String gameid,String userid,String ttno);
    @Select("select * from vp where gameid = #{gameid} and userid = #{userid}")
    public Vp[] getVpByGameidUserid(String gameid,String userid);
}
