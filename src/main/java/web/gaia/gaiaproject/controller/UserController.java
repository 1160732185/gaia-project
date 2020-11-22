package web.gaia.gaiaproject.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.gaia.gaiaproject.aop.GaiaController;
import web.gaia.gaiaproject.model.PlayerDetails;
import web.gaia.gaiaproject.model.User;
import web.gaia.gaiaproject.service.PlayService;
import web.gaia.gaiaproject.service.UserService;

import java.util.List;

@Api
@RestController
@GaiaController
@RequestMapping("api/v1")
public class UserController {
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation(value = "获取用户列表", notes = "获取用户列表", produces = "application/json")
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ApiOperation(value = "查看指定用户信息", notes = "查看指定用户信息", produces = "application/json")
    @RequestMapping(value = "/user/{userid}", method = RequestMethod.GET, produces = "application/json")
    public User getUser(@PathVariable("userid")String userid ) {
        return userService.getUser(userid);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userpassword", value = "userpassword", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/login",method = {RequestMethod.POST},produces = "application/json")
    public User login(@RequestParam("userid")String userid,@RequestParam("userpassword")String userpassword){
        logger.info("尝试登录用户名："+userid+"尝试登录密码"+userpassword);
        User user = userService.userLogin(userid,userpassword);
        if(user==null) user = new User("用户名或密码错误!","");
        return user;
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userpassword", value = "userpassword", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/signin",method = {RequestMethod.POST},produces = "application/json")
    public MessageBox signin(@RequestParam("userid")String userid,@RequestParam("userpassword")String userpassword){
        logger.info("尝试注册用户名："+userid+"尝试注册密码"+userpassword);
        return userService.userSignin(userid,userpassword);
    }
}
