package web.gaia.gaiaproject.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.GameDetails;
import web.gaia.gaiaproject.model.User;
import web.gaia.gaiaproject.service.GameService;
import web.gaia.gaiaproject.service.PlayService;
import web.gaia.gaiaproject.service.UserService;

@Api
@RestController
@RequestMapping("api/v1")
public class GameController {
    @Autowired
    UserService userService;
    @Autowired
    GameService gameService;
    @Autowired
    PlayService playService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation(value = "创建新对局", notes = "创建新对局", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameId", value = "gameId", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player1", value = "player1", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player2", value = "player2", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player3", value = "player3", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player4", value = "player4", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/game",method = {RequestMethod.POST},produces = "application/json")
    public MessageBox login(@RequestParam("gameId")String gameId, @RequestParam("player1")String player1,
                      @RequestParam("player2")String player2,@RequestParam("player3")String player3,@RequestParam("player4")String player4){
        logger.info("注册新对局："+gameId+"玩家1id："+player1+"玩家2id："+player2+"玩家3id："+player3+"玩家4id："+player4);
        MessageBox messageBox = new MessageBox();
        User user1 = userService.getUser(player1);
        if(user1==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家1id不存在！"); return messageBox;}
        User user2 = userService.getUser(player2);
        if(user2==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家2id不存在！"); return messageBox;}
        User user3 = userService.getUser(player3);
        if(user3==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家3id不存在！"); return messageBox;}
        User user4 = userService.getUser(player4);
        if(user4==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家4id不存在！"); return messageBox;}
        Game hasgame = gameService.getGameById(gameId);
        if(hasgame!=null){messageBox.setStatus(MessageBox.NEW_GAME_EXIST_CODE); messageBox.setMessage("该对局id已经存在！"); return messageBox;}
        gameService.createGame(gameId,player1,player2,player3,player4);
        messageBox.setStatus(MessageBox.NEW_GAME_CREATE_SUCCESS_CODE); messageBox.setMessage("对局创建成功"); return messageBox;
    }

    @ApiOperation(value = "根据userid查看对局", notes = "根据userid查看对局", produces = "application/json")
    @RequestMapping(value = "/game/userid/{userid}",method = {RequestMethod.GET},produces = "application/json")
    public String[] showGames(@PathVariable("userid")String userid ){
        logger.info(userid+"查看自己的所有对局");
        return playService.showGames(userid);
    }

    @ApiOperation(value = "根据gameid进入对局页面", notes = "根据gameid进入对局页面", produces = "application/json")
    @RequestMapping(value = "/game/{gameid}",method = {RequestMethod.GET},produces = "application/json")
    public GameDetails showGame(@PathVariable("gameid")String gameid ){
        Game game = gameService.getGameById(gameid);
        GameDetails gameDetails = new GameDetails();
        if(game==null){gameDetails.setGamestate("此对局未被创建");return gameDetails;}
        String[][] mapdetail = new String[21][15];
        gameService.setMapDetail(mapdetail,game.getMapseed(),game.getGamerecord());
        gameDetails.setMapsituation(mapdetail);
        gameDetails.setGamestate(gameService.getGameStateById(gameid));
        gameDetails.setGamerecord(gameService.getGameById(gameid).getGamerecord().split("\\."));
        gameDetails.setRoundscore(gameService.getRoundScoreById(gameid));
        gameDetails.setHelptile(gameService.getHelpTileById(gameid));
        return gameDetails;
    }


}
