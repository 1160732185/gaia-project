package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User userLogin(String userid,String userpassword);
}
