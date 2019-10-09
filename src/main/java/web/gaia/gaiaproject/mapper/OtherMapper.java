package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import web.gaia.gaiaproject.model.HaveTt;
import web.gaia.gaiaproject.model.Power;

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
}
