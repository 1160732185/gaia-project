package web.gaia.gaiaproject.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import web.gaia.gaiaproject.exception.CreateGameException;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.GameDetails;
import web.gaia.gaiaproject.model.Play;
import web.gaia.gaiaproject.model.User;
import web.gaia.gaiaproject.service.GameService;
import web.gaia.gaiaproject.service.PlayService;
import web.gaia.gaiaproject.service.UserService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            @ApiImplicitParam(name = "player4", value = "player4", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "gamemode", value = "gamemode", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/game",method = {RequestMethod.POST},produces = "application/json")
    public MessageBox login(@RequestParam("gameId")String gameId, @RequestParam("player1")String player1, @RequestParam("player2")String player2,
                            @RequestParam("player3")String player3,@RequestParam("player4")String player4,@RequestParam("gamemode")String gamemode)
    throws Exception{
        logger.info("注册新对局："+gameId+"玩家1id："+player1+"玩家2id："+player2+"玩家3id："+player3+"玩家4id："+player4+"游戏模式："+gamemode);
        MessageBox messageBox = new MessageBox();
        User user1 = userService.getUser(player1);
        if(user1==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE);
        messageBox.setMessage("玩家1id不存在！"); return messageBox;}
        User user2 = userService.getUser(player2);
        if(user2==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家2id不存在！"); return messageBox;}
        User user3 = userService.getUser(player3);
        if(user3==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家3id不存在！"); return messageBox;}
        User user4 = userService.getUser(player4);
        if(user4==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家4id不存在！"); return messageBox;}
        Game hasgame = gameService.getGameById(gameId);
        if(hasgame!=null){messageBox.setStatus(MessageBox.NEW_GAME_EXIST_CODE); messageBox.setMessage("该对局id已经存在！"); return messageBox;}
        gameService.createGame(gameId,player1,player2,player3,player4,gamemode);
        messageBox.setStatus(MessageBox.NEW_GAME_CREATE_SUCCESS_CODE); messageBox.setMessage("对局创建成功"); return messageBox;
    }

    @ApiOperation(value = "删除指定对局", notes = "删除指定对局", produces = "application/json")
    @RequestMapping(value = "/game/{gameid}",method = {RequestMethod.DELETE},produces = "application/json")
    public void deleteGame(@PathVariable("gameid")String gameid ){
        logger.info(gameid+"被删除");
        gameService.deleteGame(gameid);
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
        if(game!=null) {
            if (game.getPwa1().equals("1")) {
                game.setPwa1("orange");
            } else {
                game.setPwa1("grey");
            }
            if (game.getPwa2().equals("1")) {
                game.setPwa2("orange");
            } else {
                game.setPwa2("grey");
            }
            if (game.getPwa3().equals("1")) {
                game.setPwa3("orange");
            } else {
                game.setPwa3("grey");
            }
            if (game.getPwa4().equals("1")) {
                game.setPwa4("orange");
            } else {
                game.setPwa4("grey");
            }
            if (game.getPwa5().equals("1")) {
                game.setPwa5("orange");
            } else {
                game.setPwa5("grey");
            }
            if (game.getPwa6().equals("1")) {
                game.setPwa6("orange");
            } else {
                game.setPwa6("grey");
            }
            if (game.getPwa7().equals("1")) {
                game.setPwa7("orange");
            } else {
                game.setPwa7("grey");
            }
            if (game.getQa1().equals("1")) {
                game.setQa1("green");
            } else {
                game.setQa1("grey");
            }
            if (game.getQa2().equals("1")) {
                game.setQa2("green");
            } else {
                game.setQa2("grey");
            }
            if (game.getQa3().equals("1")) {
                game.setQa3("green");
            } else {
                game.setQa3("grey");
            }
        }
        GameDetails gameDetails = new GameDetails();
        if(game==null){gameDetails.setGamestate("此对局未被创建");return gameDetails;}
        String[][] mapdetail = new String[21][15];
        gameService.setMapDetail(mapdetail,gameid);
        gameDetails.setMapsituation(mapdetail);
        gameDetails.setGame(game);
        //接下来轮到的行动
        gameDetails.setGamestate(gameService.getGameStateById(gameid));
        String[] records = gameService.getGameById(gameid).getGamerecord().split("\\.");
        ArrayList<String> record = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (String str : records){
            list.add(str);
        }
        Collections.reverse(list);
        for (int i = 0; i < records.length; i++) {
            if(i==50)break;
            record.add(list.get(i));
        }
        gameDetails.setGamerecord(record);
        gameDetails.setRoundscore(gameService.getRoundScoreById(gameid));
        gameDetails.setHelptile(gameService.getHelpTileById(gameid));
        gameDetails.setAvarace(gameService.getAvaraceById(gameid));
        gameDetails.setTt(gameService.getTTByid(gameid));
        gameDetails.setCurrentuserid(gameService.getCurrentUserIdById(gameid));
        gameDetails.setResource(gameService.getResourceById(gameid));
        gameDetails.setStructure(gameService.getStructureSituationById(gameid));
        gameDetails.setStructurecolor(gameService.getStructureColorById(gameid));
        gameDetails.setSciencegrade(gameService.getScienceGrade(gameid));
        gameDetails.setPowerleech(gameService.getPowerLeech(gameid));
        gameDetails.setBuildingcount(gameService.getBuildingcount(gameid));
        gameDetails.setIncome(gameService.income(gameid,false));
        gameDetails.setPlayeraction(gameService.getPlayerAction(gameid));
        gameDetails.setTownremain(gameService.getTownremain(gameid));
        gameDetails.setSatellite(gameService.getSatellite(gameid));
        gameDetails.setVpdetail(gameService.getVpDetail(gameid));
        gameDetails.setJisheng(gameService.getJisheng(gameid));
        gameDetails.setTownbuilding(gameService.getTownBuilding(gameid));
        return gameDetails;
    }
    
    @ApiOperation(value = "执行行动", notes = "执行行动", produces = "application/json")
    @RequestMapping(value = "/action",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "action", value = "action", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query")
    })
    public MessageBox doAction(@RequestParam("gameid")String gameid,@RequestParam("action")String action) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MessageBox messageBox = new MessageBox();
        Game game = gameService.getGameById(gameid);
        action = action.replaceAll("%2B","+");
        System.out.println("行动"+action);
        String userid = gameService.getCurrentUserIdById(gameid);
        boolean ok = playService.getPowerPendingLeech(gameid,userid);
        if(!ok) {messageBox.setMessage("请先蹭魔"); return messageBox;}
        String[] actions = action.split("\\.");
        for (String act:actions) {
            if (act.length() >= 7 && act.substring(0, 7).equals("convert"))
                messageBox.setMessage(gameService.convert(gameid, userid, act.substring(8)));
            if (act.length() >= 12 && act.substring(0, 11).equals("choose race"))
                messageBox.setMessage(gameService.chooseRace(gameid, userid, act.substring(13)));
            if (act.length() >= 6 && act.substring(0, 5).equals("build")) {
                messageBox.setMessage(gameService.buildMine(gameid, userid, act.substring(6), ""));
            }
            if (act.length() >= 4 && act.substring(0, 4).equals("pass")) {
                messageBox.setMessage(gameService.pass(gameid, userid, act.substring(8)));
            }
            if (act.length() >= 7 && act.substring(0, 7).equals("upgrade")) {
                messageBox.setMessage(gameService.upgrade(gameid, userid, act.substring(8)));
            }
            if (act.length() >= 7 && act.substring(0, 7).equals("advance")) {
                messageBox.setMessage(gameService.advance(gameid, userid, act.substring(8), true));
            }
            if (act.length() >= 6 && act.substring(0, 6).equals("action")) {
                messageBox.setMessage(gameService.action(gameid, userid, act.substring(6)));
            }
            if (act.length() >= 4 && act.substring(0, 4).equals("gaia")) {
                messageBox.setMessage(gameService.gaia(gameid, userid, act.substring(5), ""));
            }
            if (act.length() >= 4 && act.substring(0, 4).equals("form")) {
                messageBox.setMessage(gameService.form(gameid, userid, act.substring(5)));
            }
            if (act.length() >= 1 && act.charAt(0) == '+') {
                messageBox.setMessage(gameService.roundbeginaction(gameid, userid, act));
            }
            playService.turnEnd(gameid);
            Play play = gameService.getPlayByGameidUserid(gameid, userid);
            if (messageBox.getMessage() != null && messageBox.getMessage().equals("成功")) {
                if (act.length() >= 12 && act.substring(0, 11).equals("choose race")) {
                    gameService.updateRecordById(gameid, play.getUserid() + ":" + act + ".");
                } else if (act.length() >= 7 && act.substring(0, 7).equals("convert")) {
                    gameService.updateConvertRecordById(gameid, play.getRace(), act);
                } else if (game.getRound() == 0) {
                    gameService.updateRecordById(gameid, play.getRace() + ":" + act + ".");
                } else {
                    gameService.updateRRecordById(gameid, "R" + game.getRound() + "T" + game.getTurn() + play.getRace() + ":",act + ".");
                }
            }
        }
        return messageBox;
    }

    @ApiOperation(value = "蹭得魔力", notes = "蹭得魔力", produces = "application/json")
    @RequestMapping(value = "/power",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "receiverace", value = "receiverace", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "location", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "structure", value = "structure", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "accept", value = "accept", dataType = "String", paramType = "query")
    })
    public MessageBox LeechPower(@RequestParam("gameid")String gameid,@RequestParam("receiverace")String receiverace,@RequestParam("location")String location,@RequestParam("structure")String structure,@RequestParam("accept")String accept){
        MessageBox messageBox = new MessageBox();
        System.out.println(receiverace+"从"+location+"升级为"+structure+"蹭魔："+accept);
        messageBox.setMessage(gameService.leechPower(gameid,receiverace,location,structure,accept));
        return messageBox;
    }
}
