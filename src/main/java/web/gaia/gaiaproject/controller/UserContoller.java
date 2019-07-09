package web.gaia.gaiaproject.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @ApiOperation(value = "用户登录", notes = "用户登录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userpassword", value = "userpassword", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/login",method = {RequestMethod.POST},produces = "application/json")
    public User login(@RequestParam("userid")String userid,@RequestParam("userpassword")String userpassword){
        return userService.userLogin(userid,userpassword);
    }
}
