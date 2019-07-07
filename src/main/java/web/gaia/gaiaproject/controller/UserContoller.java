package web.gaia.gaiaproject.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.gaia.gaiaproject.mapper.UserMapper;
import web.gaia.gaiaproject.model.User;
import web.gaia.gaiaproject.service.UserService;

import java.util.List;

@Api
@RestController
@RequestMapping("api/v1")
public class UserContoller {
    @Autowired
    UserService userService;

    @ApiOperation(value = "获取用户列表", notes = "获取用户列表", produces = "application/json")
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }
}
