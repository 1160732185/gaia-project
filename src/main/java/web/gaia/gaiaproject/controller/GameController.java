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
import web.gaia.gaiaproject.aop.GaiaController;
import web.gaia.gaiaproject.exception.CreateGameException;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.model.*;
import web.gaia.gaiaproject.service.GameService;
import web.gaia.gaiaproject.service.PlayService;
import web.gaia.gaiaproject.service.UserService;
import static web.gaia.gaiaproject.controller.MessageBox.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Api
@RestController
@GaiaController
@RequestMapping("api/v1")
public class GameController {

    @Autowired
    UserService userService;
    @Autowired
    GameService gameService;
    @Autowired
    PlayService playService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static ConcurrentHashMap<String, String> goingGameId = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> changingGameId = new ConcurrentHashMap<>();


    @ApiOperation(value = "查看玩家游戏信息", notes = "查看玩家信息", produces = "application/json")
    @RequestMapping(value = "/player/{userid}", method = RequestMethod.GET, produces = "application/json")
    public PlayerDetails getPlayer(@PathVariable("userid")String userid ) {
        PlayerDetails playerDetails = playService.getPlayer(userid);
        return playService.getPlayer(userid);
    }

    @ApiOperation(value = "创建新对局", notes = "创建新对局", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameId", value = "gameId", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player1", value = "player1", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player2", value = "player2", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player3", value = "player3", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "player4", value = "player4", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "gamemode", value = "gamemode", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "gamebalance", value = "gamebalance", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "describe", value = "describe", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/game",method = {RequestMethod.POST},produces = "application/json")
    public MessageBox login(@RequestParam("gameId")String gameId, @RequestParam("player1")String player1, @RequestParam("player2")String player2,
                            @RequestParam("player3")String player3,@RequestParam("player4")String player4,@RequestParam("gamemode")String gamemode,@RequestParam("gamebalance")String gamebalance,@RequestParam("describe")String describe)
    throws Exception{
        System.out.println("MASTER测试");
        logger.info("注册新对局："+gameId+"玩家1id："+player1+"玩家2id："+player2+"玩家3id："+player3+"玩家4id："+player4+"游戏模式："+gamemode);
        MessageBox messageBox = new MessageBox();
        if(gameId.equals("")||gamemode.equals("undefined")||gamebalance.equals("undefined")) {messageBox.setMessage("请选择游戏模式");return messageBox;}
        if(gameId.contains("%")||gameId.contains("/")||gameId.contains("\\"))
        {messageBox.setMessage("非法局名字符");return messageBox;}
        if(player2.equals("")||player3.equals("")||player4.equals("")){
            if(player1.equals("")){messageBox.setMessage("必须指定ADMIN");return messageBox;}
            User user1 = userService.getUser(player1);
            if(user1==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE);messageBox.setMessage("玩家1id不存在！"); return messageBox;}
            if(!player2.equals("")){
                User user2 = userService.getUser(player2);
                if(user2==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家2id不存在！"); return messageBox;}
            }
            if(!player3.equals("")){
                User user3 = userService.getUser(player3);
                if(user3==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家3id不存在！"); return messageBox;}
            }
            if(!player4.equals("")){
                User user4 = userService.getUser(player4);
                if(user4==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家4id不存在！"); return messageBox;}
            }
            Game hasgame = gameService.getGameById(gameId);
            PendingGame pendinggame = gameService.getPGameById(gameId);
            if(hasgame!=null||pendinggame!=null){messageBox.setStatus(MessageBox.NEW_GAME_EXIST_CODE); messageBox.setMessage("该对局id已经存在！"); return messageBox;}
            gamemode = gamemode.substring(0,1)+'.'+gamebalance.substring(0,1)+gamemode.substring(1)+gamebalance.substring(1);
            if(gamemode.charAt(2) == '3') {
                messageBox.setMessage("单人游戏请输入测试1、测试2、测试3"); return messageBox;
            }
            gameService.createPGame(gameId,player1,player2,player3,player4,gamemode,describe);
        }else {
            if(player1.equals(player2)||player1.equals(player3)||player1.equals(player4)||player2.equals(player3)||player2.equals(player4)||player3.equals(player4)){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE);messageBox.setMessage("玩家名重复");return messageBox;}
            User user1 = userService.getUser(player1);
            if(user1==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE);messageBox.setMessage("玩家1id不存在！"); return messageBox;}
            User user2 = userService.getUser(player2);
            if(user2==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家2id不存在！"); return messageBox;}
            User user3 = userService.getUser(player3);
            if(user3==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家3id不存在！"); return messageBox;}
            User user4 = userService.getUser(player4);
            if(user4==null){messageBox.setStatus(MessageBox.PLAYER_NOT_EXIST_CODE); messageBox.setMessage("玩家4id不存在！"); return messageBox;}
            Game hasgame = gameService.getGameById(gameId); PendingGame pendinggame = gameService.getPGameById(gameId);
            if(hasgame!=null||pendinggame!=null){messageBox.setStatus(MessageBox.NEW_GAME_EXIST_CODE); messageBox.setMessage("该对局id已经存在！"); return messageBox;}
            gamemode = gamemode.substring(0,1)+'.'+gamebalance.substring(0,1)+gamemode.substring(1)+gamebalance.substring(1);
            gameService.createGame(gameId,player1,player2,player3,player4,gamemode);
        }
        messageBox.setStatus(MessageBox.NEW_GAME_CREATE_SUCCESS_CODE); messageBox.setMessage("对局创建成功"); return messageBox;
    }

    @ApiOperation(value = "删除指定对局", notes = "删除指定对局", produces = "application/json")
    @RequestMapping(value = "/game/{gameid}",method = {RequestMethod.DELETE},produces = "application/json")
    public void deleteGame(@PathVariable("gameid")String gameid ){
        System.out.println("DEV测试");
        logger.info(gameid+"被删除");
        gameService.deleteGame(gameid);
    }

    @ApiOperation(value = "删除Pending对局", notes = "删除Pending对局", produces = "application/json")
    @RequestMapping(value = "/deletePG/{gameid}",method = {RequestMethod.DELETE},produces = "application/json")
    public void deletePGGame(@PathVariable("gameid")String gameid ){
        logger.info(gameid+"被删除");
        gameService.deletePGGame(gameid);
    }

    @ApiOperation(value = "根据userid查看对局", notes = "根据userid查看对局", produces = "application/json")
    @RequestMapping(value = "/game/userid/{userid}",method = {RequestMethod.GET},produces = "application/json")
    public String[] showGames(@PathVariable("userid")String userid ){
        return playService.showGames(userid);
    }

    @ApiOperation(value = "根据userid查看大厅", notes = "根据userid查看大厅", produces = "application/json")
    @RequestMapping(value = "/lobby/userid/{userid}",method = {RequestMethod.GET},produces = "application/json")
    public ArrayList<Lobby> showLobby(@PathVariable("userid")String userid ){
        return playService.showLobby(userid,"active");
    }

    @ApiOperation(value = "根据userid查看结束游戏", notes = "根据userid查看结束游戏", produces = "application/json")
    @RequestMapping(value = "/endlobby/userid/{userid}",method = {RequestMethod.GET},produces = "application/json")
    public ArrayList<Lobby> showEndLobby(@PathVariable("userid")String userid ){
        return playService.showLobby(userid,"end");
    }

    @ApiOperation(value = "查看Pending大厅", notes = "查看Pending大厅", produces = "application/json")
    @RequestMapping(value = "/pending",method = {RequestMethod.GET},produces = "application/json")
    public ArrayList<PendingGame> showPending(){
        ArrayList<PendingGame> result =  playService.showPending();
        return playService.showPending();
    }

    @ApiOperation(value = "查看PendingLeague大厅", notes = "查看PendingLeague大厅", produces = "application/json")
    @RequestMapping(value = "/pendingleague",method = {RequestMethod.GET},produces = "application/json")
    public ArrayList<ArrayList<League>> showPendingLeague(){
        ArrayList<ArrayList<League>> result = playService.showPendingLeague();
        return playService.showPendingLeague();
    }

    @ApiOperation(value = "加入Pending游戏", notes = "加入Pending游戏", produces = "application/json")
    @RequestMapping(value = "/joinPG",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query"),
    })
    public MessageBox joinGame(@RequestParam("gameid")String gameid,@RequestParam("userid")String userid){
        if(userid==null||userid.equals("")||userid.equals("null")) return new MessageBox();
        MessageBox messageBox = new MessageBox();
        messageBox.setMessage(playService.joinGame(gameid,userid));
        return messageBox;
    }

    @ApiOperation(value = "加入Pending联赛", notes = "加入Pending联赛", produces = "application/json")
    @RequestMapping(value = "/joinPL",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query"),
    })
    public MessageBox joinLeague(@RequestParam("gameid")String gameid,@RequestParam("userid")String userid){
        if(userid==null||userid.equals("")||userid.equals("null")) return new MessageBox();
        MessageBox messageBox = new MessageBox();
        messageBox.setMessage(playService.joinLeague(gameid,userid));
        return messageBox;
    }

    @ApiOperation(value = "查看种族最高分", notes = "查看种族最高分", produces = "application/json")
    @RequestMapping(value = "/topscore",method = {RequestMethod.GET},produces = "application/json")
    public String[][] topScore(){
        String[][] result = playService.topScore();
        return playService.topScore();
    }

    @ApiOperation(value = "查看统计信息", notes = "查看统计信息", produces = "application/json")
    @RequestMapping(value = "/info",method = {RequestMethod.GET},produces = "application/json")
    public ArrayList<Info> getinfo(){
        return playService.getinfo();
    }

    @ApiOperation(value = "根据leagueid进入联赛页面", notes = "根据leagueid进入联赛页面", produces = "application/json")
    @RequestMapping(value = "league/{leagueid}",method = {RequestMethod.GET},produces = "application/json")
    public String[][] showLeague(@PathVariable("leagueid")String leagueid){
         String[][] leaguedetail = playService.getLeaguedetail(leagueid);
         return  leaguedetail;
    }

    @ApiOperation(value = "根据gameid进入对局页面", notes = "根据gameid进入对局页面", produces = "application/json")
    @RequestMapping(value = "/game/{gameid}/{userid}/{row}",method = {RequestMethod.GET},produces = "application/json")
    public GameDetails showGame(@PathVariable("gameid")String gameid,@PathVariable("userid")String userid,@PathVariable("row")int row ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        if(row!=0&&row>5){
            Game game = gameService.getGameById(gameid);
            this.changeRecord(gameid,game.getGamerecord(),row);
            GameDetails result = this.showGame(gameid+row,"",0);
            deleteGame(gameid+row);
            return result;
        }
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
        gameDetails.setFasts(gameService.getFasts(gameid,userid));
 /*       gameDetails.setBugs(gameService.getBugs(gameid,userid));*/
        gameDetails.setGamemodename(playService.getGameModeName(game.getGamemode()));
        String[] records = gameService.getGameById(gameid).getGamerecord().split("\\.");
        ArrayList<String> record = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (String str : records){
            list.add(str);
        }
        Collections.reverse(list);
        for (int i = 0; i < records.length; i++) {
            if(i==333)break;
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
        boolean[][][] abc = gameService.getTownBuilding(gameid);
        gameDetails.setTownbuilding(abc);
        gameDetails.setBid(gameService.getBid(gameid));
        return gameDetails;
    }


    @ApiOperation(value = "查看游戏记录", notes = "查看游戏记录", produces = "application/json")
    @RequestMapping(value = "/record/{gameid}",method = {RequestMethod.GET},produces = "application/json")
    public Game getRecord(@PathVariable("gameid")String gameid){
        Game game = gameService.getGameById(gameid);
        game.setGamerecord(game.getGamerecord().replace('.','\n'));
        return game;
    }

    @ApiOperation(value = "rollBack游戏最新一动", notes = "rollBack游戏最新一动", produces = "application/json")
    @RequestMapping(value = "/rollBack",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query")
    })
    public MessageBox rollBack(@RequestParam("gameid")String gameid) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        MessageBox messageBox = new MessageBox();
        Game game = gameService.getGameById(gameid);
        String gamerecord = game.getGamerecord();
        String[] records = gamerecord.split("\\.");
        String panduan = records[records.length-1];
        int kuohao = 0;
        String race = "";
        while(kuohao<panduan.length()&&panduan.charAt(kuohao)!=':'){
            kuohao++;
        }
        panduan = panduan.substring(0,kuohao);
        for(int p=kuohao-1;p>0;p--){
            if( racecolormap.containsKey(panduan.substring(p,kuohao))) {race = panduan.substring(p,kuohao);break;}
        }
        if(gamerecord.charAt(gamerecord.length()-1)=='.'){
            for(int i=gamerecord.length()-2;i>0;i--){
                if(gamerecord.charAt(i)=='.'){
                    gamerecord=gamerecord.substring(0,i+1);
                    records = gamerecord.split("\\.");
                    int round = game.getRound();
                    for (int j=records.length-1;j>0;j--){
                        if(!records[j].substring(0,2).equals("R"+String.valueOf(round)))break;
                        if(records[j].length()<11||records[j].substring(0,11).contains(race))break;
                        for (int k=7;k<records[j].length();k++){
                            if(records[j].charAt(k)=='('){
                                int right = k;
                                while(records[j].charAt(right)!=')') right++;
                                if(records[j].substring(k+1,right).contains(race)){
                                    if(right==records[j].length()) {records[j] = records[j].substring(0,k);}else {
                                        records[j] = records[j].substring(0,k)+records[j].substring(right+1);
                                    }
                                }
                            }
                        }
                    }
                    gamerecord = "";
                    for (String re:records){
                        gamerecord+=re;
                        gamerecord+='.';
                    }
                    this.changeRecord(gameid,gamerecord,0);
                    break;
                }
            }
        }
        return messageBox;
    }

    @ApiOperation(value = "修改游戏记录", notes = "修改游戏记录", produces = "application/json")
    @RequestMapping(value = "/lobby/cc",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "record", value = "record", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query")
    })public MessageBox changeRecord(@RequestParam("gameid")String gameid,@RequestParam("record")String record,Integer history) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        while (changingGameId.contains(gameid)) {
            Thread.sleep(1000);
        }
        String trueGameId = gameid;
        try {
        changingGameId.put(gameid, gameid);
        record = record.replace('\n','.');
        record = record.replaceAll("%2B","+");
        System.out.println(record);
        String[] actions = record.split("\\.");
        ArrayList<String> acts = new ArrayList<>();
        for (int i=0;i<actions.length;i++){
            if(!actions[i].equals(""))acts.add(actions[i]);
        }
        Game game = gameService.getGameById(gameid);
        String lastMove = game.getLasttime();
        String currentUser = gameService.getCurrentUserIdById(gameid);
        Power[] powers = gameService.getAllpower(gameid);
        Vp[] vps = playService.getiniVP(gameid);
        Log[] getlogs = playService.getLogs(gameid);
        Time[] getTimes = playService.getTimes(gameid);
        String[] logs = new String[4];
        String[] times = new String[4];
        for (Log log:getlogs){
            if(log.getUserid().equals(acts.get(1).substring(9))) logs[0]=log.getLog();
            if(log.getUserid().equals(acts.get(2).substring(9))) logs[1]=log.getLog();
            if(log.getUserid().equals(acts.get(3).substring(9))) logs[2]=log.getLog();
            if(log.getUserid().equals(acts.get(4).substring(9))) logs[3]=log.getLog();
        }
            for (Time log:getTimes){
                if(log.getUserid().equals(acts.get(1).substring(9))) times[0]=log.getTime();
                if(log.getUserid().equals(acts.get(2).substring(9))) times[1]=log.getTime();
                if(log.getUserid().equals(acts.get(3).substring(9))) times[2]=log.getTime();
                if(log.getUserid().equals(acts.get(4).substring(9))) times[3]=log.getTime();
            }
        //添加log不删除
        boolean error = false;
        String oldRecord = gameService.getRecordByGameid(gameid);
        if(history!=null&&history!=0){
            if(game.getGamemode().length()>=7&&game.getGamemode().charAt(2)=='0'&&game.getGamemode().charAt(6)=='1'){
                game.setGamemode(game.getGamemode().substring(0,6)+"0");
            }
            gameService.changeGame(gameid+history,acts.get(1).substring(9),acts.get(2).substring(9),acts.get(3).substring(9),acts.get(4).substring(9),game.getGamemode(),game.getTerratown(),game.getMapseed(),game.getOtherseed(),vps,game.getAdmin(),game.getBlackstar(),logs,times,game.getCreatetime());
            gameid = gameid+history;
        }else {
            deleteGame(gameid);
            gameService.changeGame(gameid,acts.get(1).substring(9),acts.get(2).substring(9),acts.get(3).substring(9),acts.get(4).substring(9),game.getGamemode(),game.getTerratown(),game.getMapseed(),game.getOtherseed(),vps,game.getAdmin(),game.getBlackstar(),logs,times,game.getCreatetime());
        }
        int length = acts.size();
        if(history!=null&&history!=0) length = history-2;
        outloop: for (int i=5;i<length;i++){
            String action = acts.get(i);
            if(action.equals(""))continue ;
            int x = 0;
            if(i==acts.size()-1&&!action.contains(":")){
               acts.set(i,""); continue ;
            }
            while(action.charAt(x)!=':') {x++;if(x==action.length()){error=true;break;}}
            logger.info(action);
            if(action.equals("Game Start")){
                //测试错误
            }
            int racestart = 0;
            while(action.charAt(racestart)!='晶'&&action.charAt(racestart)!='蜂'&&action.charAt(racestart)!='亚'&&action.charAt(racestart)!='人'&&action.charAt(racestart)!='格'&&action.charAt(racestart)!='超'&&action.charAt(racestart)!='章'&&action.charAt(racestart)!='疯'
                    &&action.charAt(racestart)!='大'&&action.charAt(racestart)!='圣'&&action.charAt(racestart)!='利'&&action.charAt(racestart)!='翼'&&action.charAt(racestart)!='伊'&&action.charAt(racestart)!='炽'&&action.charAt(racestart)!='魔'&&action.charAt(racestart)!='熊'&&action.charAt(racestart)!='蜥'&&action.charAt(racestart)!='天'&&action.charAt(racestart)!='织'&&action.charAt(racestart)!='殖'&&action.charAt(racestart)!='混'&&action.charAt(racestart)!='猎'){racestart++;}
            String giverace = "";
            if(racestart<action.length()&&i>10)  giverace = action.substring(racestart,x);
            action = action.substring(x+1);
            //主行动前的所有快速行动
            boolean finish = false;
            MessageBox messageBox = new MessageBox();
            while(action.charAt(0)=='<'){
                int right = 1;
                while(action.charAt(right)!='>') right++;
                messageBox = this.doAction(gameid,action.substring(1,right),"admin");
                System.out.println(messageBox.getMessage());
                if(right==action.length()-1) break outloop;
                action = action.substring(right+1);
            }
            //主行动
            int left = 0;
                while(true){
                    if(action.charAt(left)=='<'||action.charAt(left)=='('){
                        messageBox = this.doAction(gameid,action.substring(0,left),"admin");
                        if(!messageBox.getMessage().equals("成功")){
                            System.out.println("这里错了：：："+messageBox.getMessage());
                            error = true;
                            break outloop;
                        }
                        break;
                    }
                    if(left==action.length()-1){
                        messageBox = this.doAction(gameid,action,"admin");
                        if(messageBox.getMessage()==null||!messageBox.getMessage().equals("成功")){
                            error = true;
                            break outloop;
                        }
                        break;
                    }
                    left++;
                }
            //主行动之后的快速行动or蹭魔
            if (action.equals("upgrade P11 to rl advance shipQ5 +att7 -ltt4(炽炎族蹭2魔)(伊塔星人蹭0魔)(伊塔星人蹭0魔)")){
                System.out.println("aaa");
            }
            if(left!=action.length()-1){
                    action = action.substring(left);
                    left = 0;
                while(action.length()>0&&action.charAt(0)=='<'){
                    int right = left;
                    while(action.charAt(right)!='>') {right++;}
                    String s = this.doBConvert(gameid,action.substring(1,right),giverace);
                    if(!s.equals("成功")){
                        error = true;
                        break outloop;
                    }
                    action = action.substring(right+1);
                    left = 0;
                }
                while(action.length()>0&&action.charAt(0)=='('){
                    int right = 0;
                    int ceng = 0;
                    while(action.charAt(right)!=')') {right++;}
                    int num = 0;
                    if(!action.substring(0,right).contains("拒绝")&&!action.substring(0,right).contains("蹭")){
                        error = true;
                    }else if(!action.substring(0,right).contains("拒绝")){
                        while(action.charAt(ceng)!='蹭') {ceng++;}
                        num = action.charAt(ceng+1)-48;
                    }else {
                        while(action.charAt(ceng)!='拒') {ceng++;}
                    }
                    String s = gameService.LeechPower(gameid,giverace,action.substring(1,ceng),num);
                    if(!s.equals("成功")){
                        System.out.println("这里错了：：："+s);
                        error = true;
                        break outloop;
                    }
                    action = action.substring(right+1);
                }
            }
                if(game.getGamemode().charAt(0)=='1'){
                    gameService.deletePower(gameid);
                }
        }
        if(error){
            changingGameId.remove(trueGameId);
            changeRecord(gameid,oldRecord,0);
        }else if(history!=null&&history!=0){
            record = "";
            for (int k = 0;k<=history-3;k++) {
                if(!acts.get(k).equals("")){
                    record += acts.get(k);
                    record += ".";
                }
            }
            gameService.updateRecordByIdCR(gameid, record);
        }else {
            record = "";
            for (String s : acts) {
                if(!s.equals("")){
                    record += s;
                    record += ".";
                }
            }
            gameService.updateRecordByIdCR(gameid, record);
            gameService.updateTime(gameid,lastMove,currentUser);
        }
        changingGameId.remove(trueGameId);
        } catch (Exception e) {
            e.printStackTrace();
            changingGameId.remove(trueGameId);
        }
        return new MessageBox();
    }

    private String doBConvert(String gameid, String substring, String giverace) {
        String userid = gameService.getuseridByGameidRace(gameid,giverace);
        return gameService.convert(gameid,userid,substring.substring(8));
    }


    @ApiOperation(value = "执行行动", notes = "执行行动", produces = "application/json")
    @RequestMapping(value = "/action",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "action", value = "action", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "bid", value = "userid", dataType = "String", paramType = "query")
    })
    public MessageBox doAction(@RequestParam("gameid")String gameid,@RequestParam("action")String action,@RequestParam("bid")String bidid) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        while (goingGameId.contains(gameid)) {
            Thread.sleep(300);
        }
        goingGameId.put(gameid,gameid);
        try {
            MessageBox messageBox = new MessageBox();
            Game game = gameService.getGameById(gameid);
            StringBuilder sb = new StringBuilder(action);
            for (int i=1;i<sb.length();i++){
                if(sb.charAt(i-1)==' '&&sb.charAt(i)==' ') {sb.deleteCharAt(i);i--;}
            }
            action = sb.toString();
            String[] bid = gameService.getBid(gameid);
            if(game.getGamemode().charAt(2)=='0'&&bid[0].equals("t")){
                gameService.bid(gameid,bidid,action);
                goingGameId.remove(gameid);
                return messageBox;
            }
            if(game.getGamemode().charAt(2)=='2'&&game.getBlackstar()==null){
                if(action.equals("ok")||Integer.parseInt(action)<10&&Integer.parseInt(action)>=0){
                    gameService.rotate(gameid,action);
                }else {
                    messageBox.setMessage("命令错误");
                }
                goingGameId.remove(gameid);
                return messageBox;
            }

            action = action.replaceAll("%2B","+");
            String userid = gameService.getCurrentUserIdById(gameid);
            if(!bidid.equals(userid)&&!userid.equals("all")&&game.getGamemode().charAt(2) != '3'&&!bidid.equals("admin")){
                goingGameId.remove(gameid);return new MessageBox();
            }
            boolean ok = playService.getPowerPendingLeech(gameid,userid);
            if(!ok) {messageBox.setMessage("请先蹭魔");goingGameId.remove(gameid); return messageBox;}
            String[] actions = action.split("\\.");
            for (String act:actions) {
                messageBox.setMessage(null);
                if(act.equals("")) continue;
                while(act.charAt(act.length()-1)==' ') {act = act.substring(0,act.length()-1);}
                while(act.charAt(0)==' ') {act = act.substring(1);}
                if(game.getTurn()==0){
                    if (act.charAt(0) == '+') {
                        messageBox.setMessage(gameService.roundbeginaction(gameid, userid, act));
                    }
                }
                if(game.getTurn()!=0){
                    if (act.length() >= 6 && act.substring(0, 5).equals("build")) {
                        messageBox.setMessage(gameService.buildMine(gameid, userid, act.substring(6), ""));
                    }
                    if (act.length() >= 4 && act.substring(0, 4).equals("pass")) {
                        messageBox.setMessage(gameService.pass(gameid, userid, act.substring(8)));
                    }
                    if (act.length() >= 4 && act.substring(0, 4).equals("drop")) {
                        messageBox.setMessage(gameService.drop(gameid, userid));
                    }
                    if(game.getRound()==0){
                        if (act.length() >= 12 && act.substring(0, 11).equals("choose race"))
                            if(game.getGamemode().charAt(2)=='3'){messageBox.setMessage(gameService.chooseRace(gameid, userid, act.substring(13)));}else {
                                messageBox.setMessage(gameService.chooseRace(gameid, userid, act.substring(13)));
                            }
                    }
                    if(game.getRound()!=0){
                        if (act.length() >= 7 && act.substring(0, 7).equals("convert"))
                            messageBox.setMessage(gameService.convert(gameid, userid, act.substring(8)));
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
                    }
                }
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
                    gameService.updateLasttime(gameid,userid);
                }
            }
            playService.turnEnd(gameid,bidid);
            goingGameId.remove(gameid);
            return messageBox;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("错误定位" + gameid + action);
            goingGameId.remove(gameid);
        }
        return new MessageBox();
    }

    @ApiOperation(value = "蹭得魔力", notes = "蹭得魔力", produces = "application/json")
    @RequestMapping(value = "/power",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "giverace", value = "giverace", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "receiverace", value = "receiverace", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "location", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "structure", value = "structure", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "accept", value = "accept", dataType = "String", paramType = "query")
    })
    public MessageBox LeechPower(@RequestParam("gameid")String gameid,@RequestParam("giverace")String giverace,@RequestParam("receiverace")String receiverace,@RequestParam("location")String location,@RequestParam("structure")String structure,@RequestParam("accept")String accept){
        MessageBox messageBox = new MessageBox();
        while (goingGameId.contains(gameid)) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        goingGameId.put(gameid,gameid);
        try {
            messageBox.setMessage(gameService.leechPower(gameid,giverace,receiverace,location,structure,accept));
            goingGameId.remove(gameid);
        } catch (Exception e){
            goingGameId.remove(gameid);
        }
        return messageBox;
    }

    @ApiOperation(value = "保存记录本", notes = "保存记录本", produces = "application/json")
    @RequestMapping(value = "/save",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "log", value = "log", dataType = "String", paramType = "query")
    })
    public MessageBox saveLog(@RequestParam("gameid")String gameid,@RequestParam("userid")String userid,@RequestParam("log")String log){
        MessageBox messageBox = new MessageBox();
        this.playService.saveLog(gameid,userid,log);
        return messageBox;
    }

    @ApiOperation(value = "获得记录本", notes = "获得记录本", produces = "application/json")
    @RequestMapping(value = "/showlog",method = {RequestMethod.POST},produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "gameid", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userid", value = "userid", dataType = "String", paramType = "query")
    })
    public MessageBox saveLog(@RequestParam("gameid")String gameid,@RequestParam("userid")String userid){
        MessageBox messageBox = new MessageBox();
        messageBox.setMessage(playService.showLog(gameid,userid));
        return messageBox;
    }
}
