package web.gaia.gaiaproject.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.UserMapper;
import web.gaia.gaiaproject.model.User;
import web.gaia.gaiaproject.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    public List<User> getAllUsers(){
        return userMapper.getAllUsers();
    }

    @Override
    public User getUser(String userid) {
        return userMapper.getUser(userid);
    }

    @Override
    public User userLogin(String userid, String userpassword) {
        return userMapper.userLogin(userid,userpassword);
    }

    ;
}
