package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Bean;
import web.gaia.gaiaproject.model.User;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user")
    public List<User> getAllUsers();
    @Select("select * from user where userid = #{userid}")
    public User getUser(String userid);
    @Select("select * from user where userid = #{userid} and userpassword = #{userpassword}")
    public User userLogin(String userid,String userpassword);
    @Update("update user set userpassword = #{u.userpassword} where userid = #{u.userid}")
    public void userUpdate(@Param(value = "u") User u);
    @Insert("insert into user values(#{userid},#{userpassword})")
    public void userSignin(String userid,String userpassword);
}
