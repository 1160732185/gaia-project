package web.gaia.gaiaproject.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
}
