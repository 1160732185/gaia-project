package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import web.gaia.gaiaproject.model.HaveTt;
import web.gaia.gaiaproject.model.Power;
import web.gaia.gaiaproject.model.Satellite;

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
    @Select("select ttno,ttstate,gameid,userid from havetown where gameid = #{gameid}")
    public HaveTt[] getAllHT(String gameid);
}
