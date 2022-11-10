package web.gaia.gaiaproject.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.controller.MessageBox;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.OtherMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.mapper.UserMapper;
import web.gaia.gaiaproject.model.Lobby;
import web.gaia.gaiaproject.model.Play;
import web.gaia.gaiaproject.model.PlayerDetails;
import web.gaia.gaiaproject.model.User;
import web.gaia.gaiaproject.service.UserService;
import web.gaia.gaiaproject.serviceimpl.PlayServiceImpl;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    GameMapper gameMapper;
    @Autowired
    PlayMapper playMapper;
    @Autowired
    OtherMapper otherMapper;

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

    @Override
    public MessageBox userSignin(String userid, String userpassword) {
       User user = userMapper.getUser(userid);
       MessageBox result = new MessageBox();
       if(user!=null){result.setMessage("用户名已经存在");return result;}
       userMapper.userSignin(userid,userpassword);
        result.setMessage("注册成功");
        return result;
    }
    ;
}
