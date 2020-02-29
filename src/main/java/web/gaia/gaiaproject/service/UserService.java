package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.controller.MessageBox;
import web.gaia.gaiaproject.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUser(String userid);
    User userLogin(String userid,String userpassword);
    MessageBox userSignin(String userid, String userpassword);
}
