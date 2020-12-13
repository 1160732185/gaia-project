package web.gaia.gaiaproject.serviceimpl;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.OtherMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.mapper.UserMapper;
import web.gaia.gaiaproject.model.*;
import web.gaia.gaiaproject.service.GameService;
import web.gaia.gaiaproject.service.PlayService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static web.gaia.gaiaproject.controller.MessageBox.*;

public class GameServiceImpl implements GameService {
    @Autowired
    GameMapper gameMapper;
    @Autowired
    PlayMapper playMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OtherMapper otherMapper;
    @Autowired
    PlayService playService;
    //判断卫星最短路径用
    static int ARR_LEN = 1000;
    public Map<String, List<String>> linkedMap = new HashMap<>();
    public ForbiddenMap mapForbidden = new ForbiddenMap();

    //Date转化函数
    public static java.sql.Timestamp dtot(java.util.Date d) {
        if (null == d)
            return null;
        return new java.sql.Timestamp(d.getTime());
    }

    @Override
    public void deleteGame(String gameid) {
        otherMapper.deletegaia(gameid);
        otherMapper.deletehavetown(gameid);
        otherMapper.deletehavett(gameid);
        otherMapper.deleteplay(gameid);
        otherMapper.deletepower(gameid);
        otherMapper.deletesatellite(gameid);
        otherMapper.deletetownbuilding(gameid);
        otherMapper.deletevp(gameid);
        otherMapper.deletegame(gameid);
    }

    @Override
    public void createGame(String gameId, String player1, String player2, String player3, String player4, String gamemode) {
        Random random = new Random();
        //随机改造顶城片
        int terratown = random.nextInt(6)+1;
        //随机地图种子
        List<Integer> contain;
        String mapseed="";
        while(true) {
            contain = new ArrayList();
            mapseed="";
            while (contain.size()!=10) {
                int plate = random.nextInt(10);
                if (!contain.contains(plate)) {
                    contain.add(plate);
                    mapseed += Integer.toString(plate);
                    mapseed += Integer.toString(random.nextInt(6));
                }
            }
            String[][] mapDetail = new String[21][16];
            for (int i = 1; i <= 20 ; i++) {
                for (int j = 1; j < 15; j++) {
                   mapDetail[i][j]="";
                }
            }
            for (int i = 0; i < 10; i++) {
                int spaceNo = (int)mapseed.charAt(i*2)-48;
                int rorateTime = (int)mapseed.charAt(i*2+1)-48;
                setColor(mapDetail,i,spaceNo,rorateTime);
            }
            for (int i = 1; i <= 20 ; i++) {
                for (int j = 1; j < 15; j++) {
                    if(mapDetail[i][j].equals("#000000")||mapDetail[i][j].equals("#9400d3")) mapDetail[i][j]+="i"+i+"j:"+j;
                }
            }
            if(mapOk(mapDetail)) break;
        }
        String otherseed="";
        contain = new ArrayList();
        //终局计分*2
        while(contain.size()!=2){
            int endgameescoretile = random.nextInt(6)+1;
            if(!contain.contains(endgameescoretile)) {
                contain.add(endgameescoretile);
                otherseed += Integer.toString(endgameescoretile);
            }
        }
        otherseed+=" ";
        contain = new ArrayList();
        //回合计分*6
        while(contain.size()!=6){
            int roundscoretile = random.nextInt(10);
            if(!contain.contains(roundscoretile)) {
                contain.add(roundscoretile);
                otherseed += Integer.toString(roundscoretile);
            }
        }
        otherseed+=" ";
        contain = new ArrayList();
       //低级科技*6
        while(contain.size()!=6){
            int ltt = random.nextInt(9)+1;
            if(!contain.contains(ltt)) {
                contain.add(ltt);
                otherseed += Integer.toString(ltt);
            }
        }
        otherseed+=" ";
        contain = new ArrayList();
        contain.add(0);
        //高级科技*6
        if(((gamemode.charAt(0)=='2'|gamemode.charAt(0)=='3')&&gamemode.length()>=5&&gamemode.charAt(4)>='8')){
            while(contain.size()!=7){
                int att = random.nextInt(17);
                if(!contain.contains(att)) {
                    contain.add(att);
                    if(att>=10){
                        otherseed += (char)(att+55);
                    }else{
                        otherseed += Integer.toString(att);
                    }
                }
            }
        } else {
            while(contain.size()!=7){
                int att = random.nextInt(16);
                if(!contain.contains(att)) {
                    contain.add(att);
                    if(att>=10){
                        otherseed += (char)(att+55);
                    }else{
                        otherseed += Integer.toString(att);
                    }
                }
            }
        }
        otherseed+=" ";
        contain = new ArrayList();
        //回合助推*3

        while(contain.size()!=3){
            int roundhelp = random.nextInt(10);
            if(!contain.contains(roundhelp)) {
                contain.add(roundhelp);
                otherseed += Integer.toString(roundhelp);
            }
        }
        contain = new ArrayList();
        //起始竞拍顺位
        if(gameId.length()>2&&(gameId.substring(gameId.length()-3,gameId.length()-1).equals("_G")||(gamemode.length()>=7&&gamemode.charAt(2)=='2'&&gamemode.charAt(6)=='1'))){
            contain.add(1);
            contain.add(2);
            contain.add(3);
            contain.add(4);
        }else {
            while(contain.size()!=4){
                int place = random.nextInt(4)+1;
                if(!contain.contains(place)) {
                    contain.add(place);
                }
            }
        }

        Long time = System.currentTimeMillis();
        Date d = new Date();
        //创建对局
        if(gameId.length()>=3&&gameId.substring(gameId.length()-3,gameId.length()-1).equals("_G")){
            gameMapper.createGame(gameId,terratown,mapseed,otherseed,gamemode,String.valueOf(time),new java.sql.Date(System.currentTimeMillis()),"admin",null);
        }else {
            gameMapper.createGame(gameId,terratown,mapseed,otherseed,gamemode,String.valueOf(time),new java.sql.Date(System.currentTimeMillis()),player1,null);
        }
        //创建玩家游戏信息
        playMapper.insertPlay(gameId,player1,contain.get(0));
        otherMapper.gainVp(gameId,player1,10,"起始分");
        playMapper.insertPlay(gameId,player2,contain.get(1));
        otherMapper.gainVp(gameId,player2,10,"起始分");
        playMapper.insertPlay(gameId,player3,contain.get(2));
        otherMapper.gainVp(gameId,player3,10,"起始分");
        playMapper.insertPlay(gameId,player4,contain.get(3));
        otherMapper.gainVp(gameId,player4,10,"起始分");
        String[] players = new String[]{player1,player2,player3,player4};
        for (int i = 1; i <= 4 ; i++) {
            gameMapper.updateRecordById(gameId,"Player"+i+": "+players[contain.indexOf(i)]+".");
        }
    }

    @Override
    public void changeGame(String gameId, String player1, String player2, String player3, String player4, String gamemode, int terratown, String mapseed, String otherseed, Vp[] vps, String admin, String blackstar, String[] logs,String[] times, java.sql.Date createtime) {
        //随机改造顶城片
        //随机地图种子
        List<Integer> contain;
        Long time = System.currentTimeMillis();
        if(blackstar!=null&&blackstar.equals("游戏结束")){
            if(gamemode.charAt(2)=='2') {blackstar = "done";}else {
                blackstar = null;
            }
        }
        gameMapper.createGame(gameId,terratown,mapseed,otherseed,gamemode,String.valueOf(time),createtime,admin,blackstar);
        //创建玩家游戏信息
        playMapper.CinsertPlay(gameId,player1,1,logs[0],times[0]);
        playMapper.CinsertPlay(gameId,player2,2,logs[1],times[1]);
        playMapper.CinsertPlay(gameId,player3,3,logs[2],times[2]);
        playMapper.CinsertPlay(gameId,player4,4,logs[3],times[3]);
        for (Vp v:vps){
            otherMapper.gainVp(gameId,v.getUserid(),v.getGainvp(),v.getReason());
        }
        String[] players = new String[]{player1,player2,player3,player4};
        gameMapper.updateRecordById(gameId,"Player"+1+": "+player1+".");
        gameMapper.updateRecordById(gameId,"Player"+2+": "+player2+".");
        gameMapper.updateRecordById(gameId,"Player"+3+": "+player3+".");
        gameMapper.updateRecordById(gameId,"Player"+4+": "+player4+".");
    }

    @Override
    public Game getGameById(String gameId) {
        Game game = gameMapper.getGameById(gameId);
        return game;
    }

    @Override
    public void setMapDetail(String[][] mapDetail, String gameid) {
        String mapseed = gameMapper.getGameById(gameid).getMapseed();
        for (int i = 0; i < 10; i++) {
            int spaceNo = (int)mapseed.charAt(i*2)-48;
            int rorateTime = (int)mapseed.charAt(i*2+1)-48;
            setColor(mapDetail,i,spaceNo,rorateTime);
        }
        String[] gaia = otherMapper.getAllGaia(gameid);
        for (String location : gaia){
            if(!location.equals("pg")){
                int row = (int)location.charAt(0)-64;
                int column = Integer.parseInt(location.substring(1));
                mapDetail[row][column]=ga;
            }
        }
    }

    @Override
    public void updateRecordById(String gameid, String record) {
        this.gameMapper.updateRecordById(gameid,record);
    }

    @Override
    public void updateRRecordById(String gameid, String tr, String record) {
        String[] records = gameMapper.getRecordById(gameid).split("\\.");
        StringBuffer result = new StringBuffer();
        if(records[records.length-1].contains("R")||records[records.length-1].contains("pass")){
            for (String str:records){
                result.append(str+".");
            }
            result.append(tr+record);
            gameMapper.updateLeechRecordById(gameid,result.toString());
        }else {
            for (int i=0;i<records.length-1;i++){
                result.append(records[i]+".");
            }
            result.append(tr+records[records.length-1]+record);
            gameMapper.updateLeechRecordById(gameid,result.toString());
        }
    }

    @Override
    public void updateConvertRecordById(String gameid, String race, String convert) {
        String record1 = gameMapper.getRecordById(gameid);
        String[] records = gameMapper.getRecordById(gameid).split("\\.");
        StringBuffer record = new StringBuffer();
        if(records[records.length-1].length()<=10||!records[records.length-1].substring(0,11).contains(race)||records[records.length-1].contains("蹭")||records[records.length-1].contains("拒绝")){
            for (String str:records){
                record.append(str);
                if(str.charAt(0)!='<') record.append(".");
            }
            String[] strings = convert.split(" ");

            record.append('<'+convert+'>');
            gameMapper.updateLeechRecordById(gameid,record.toString());
        }else {
            records[records.length-1]+='<'+convert+'>';
            for (String str:records){
                record.append(str+".");
            }
            gameMapper.updateLeechRecordById(gameid,record.toString());
        }
    }

    @Override
    public String getGameStateById(String gameid) {
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        String record = game.getGamerecord();
        String[] records = record.split("\\.");
        String state = new String();
        String[] bid = this.getBid(gameid);
        if(game.getGamemode().charAt(2)=='2'&&game.getBlackstar()==null){
            return "等待"+plays[3].getUserid()+"旋转地图";
        }
        if(game.getGamemode().charAt(2)=='0'&&bid[0].equals("t")){
            if(Integer.parseInt(bid[5])==-1||Integer.parseInt(bid[6])==-1||Integer.parseInt(bid[7])==-1||Integer.parseInt(bid[8])==-1){
                state = "等待";
                if(Integer.parseInt(bid[5])==-1){
                    state+=" ";
                    state+=bid[1];
                    state+=" ";
                }
                if(Integer.parseInt(bid[6])==-1){
                    state+=" ";
                    state+=bid[2];
                    state+=" ";
                }
                if(Integer.parseInt(bid[7])==-1){
                    state+=" ";
                    state+=bid[3];
                    state+=" ";
                }
                if(Integer.parseInt(bid[8])==-1){
                    state+=" ";
                    state+=bid[4];
                    state+=" ";
                }
                state+="暗中竞价";
            }else {
               return "轮到"+this.getCurrentUserIdById(gameid)+"选择顺位";
            }
        }else {
            int yikong = 0;
            int hive = 0;
            int zhimin = 0;
            int buildm = 16;
            for (Play play : plays) {
                if (play.getRace().equals("翼空族")) yikong = play.getPosition();
                if (play.getRace().equals("蜂人")||play.getRace().equals("混沌法师")) hive = play.getPosition();
                if (play.getRace().equals("殖民者")) zhimin = play.getPosition();
            }
            if (yikong != 0) buildm++;
            if (hive != 0) buildm--;
            if (zhimin != 0) buildm--;
            String userid = this.getCurrentUserIdById(gameid);
            String race = "";
            for (Play p : plays) {
                if (p.getUserid().equals(userid)) race = p.getRace();
            }
            if (records.length <= buildm + 4) {
                for (int i = 5; i <= 8; i++) {
                    if (records.length == i) state = "轮到玩家：" + this.getCurrentUserIdById(gameid) + "选择种族";
                }
                for (int i = 9; i <= buildm; i++) {
                    if (race.equals("蜂人")||race.equals("混沌法师")) {
                        state = "轮到" + race + "建造行星要塞";
                    } else if (race.equals(" 殖民者")) {
                        state = "轮到" + race + "降落殖民船";
                    } else{
                        if (records.length == i) state = "轮到" + race + "建造初始矿场";
                    }
                }
                for (int i = buildm + 1; i <= buildm + 4; i++) {
                    if (records.length == i) state = "轮到" + race + "选择第一回合助推板";
                }

            } else {
                if (game.getTurn().equals(0)) {
                    for (Play p : plays) {
                        if (p.getRace().equals("人类") && !p.getRacea1().equals("0"))
                            return "轮到人类转化" + p.getRacea1() + "魔力";
                        if (p.getRace().equals("伊塔星人") && Integer.parseInt(p.getRacea1()) >= 4)
                            return "轮到伊塔星人转化" + p.getRacea1() + "魔力";
                    }
                } else {
                    Play p = playMapper.selectPlayByGameIdPosition(gameid, game.getPosition());
                    state = "Round:" + game.getRound() + "Turn:" + game.getTurn() + ":轮到" + p.getRace() + "执行行动";
                }
            }
        }
            return state;
    }

    @Override
    public String[] getRoundScoreById(String gameid) {
        Game game = gameMapper.getGameById(gameid);
        String otherseed = game.getOtherseed().substring(3,9);
        String[] roundscoretile = new String[]{"M>>2","M(G)>>3","M(G)>>4","TC>>3","TC>>4","SH/AC>>5","SH/AC>>5","TOWN>>5","TERRA>>2","AT>>2"};
        return new String[]{roundscoretile[(int)(otherseed.charAt(0)-48)],
                roundscoretile[(int)(otherseed.charAt(1)-48)],
                roundscoretile[(int)(otherseed.charAt(2)-48)],
                roundscoretile[(int)(otherseed.charAt(3)-48)],
                roundscoretile[(int)(otherseed.charAt(4)-48)],
                roundscoretile[(int)(otherseed.charAt(5)-48)],
                };
    }

    @Override
    public String[][] getHelpTileById(String gameid) {
        Game game = gameMapper.getGameById(gameid);
        String helptileseed = game.getOtherseed().substring(24);
        String[][] helptiles = new String[][]{
                {"BON1","TERRA","+2C",""},
                {"BON2","+3SHIP","+2PW",""},
                {"BON3","+1Q","+2C",""},
                {"BON4","+1O","+1K",""},
                {"BON5","+2PWB","+1O",""},
                {"BON6","+1O","M>>1",""},
                {"BON7","+1O","TC>>2",""},
                {"BON8","+1K","RL>>3",""},
                {"BON9","+4PW","SH/AC>>4",""},
                {"BON10","+4C","G>>1",""}};
        if(game.getBon1()==0) helptiles[0][1]="已使用";
        if(game.getBon2()==0) helptiles[1][1]="已使用";
        String[][] helptile = new String[10][4];
        int num = 0;
        for (int i = 0; i < 10; i++) {
            if((char)(i+48)==helptileseed.charAt(0)||(char)(i+48)==helptileseed.charAt(1)||(char)(i+48)==helptileseed.charAt(2)) continue;
            helptile[num][0]=helptiles[i][0];
            helptile[num][1]=helptiles[i][1];
            helptile[num][2]=helptiles[i][2];
            String race = playMapper.selectRaceByBonus(gameid,i+1);
            if(race!=null){
                helptile[num][3]=racecolormap.get(race);
            }else{
                helptile[num][3]="#FFFFFF";
            }
            num++;
        }
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (Play p:plays){
            if(p.getRace().equals("熊猫人")){
                for (int i=0;i<3;i++){
                    int deletebon = helptileseed.charAt(i) - 48;
                    helptile[7+i][0] = helptiles[deletebon][0];
                    helptile[7+i][1] = helptiles[deletebon][1];
                    helptile[7+i][2] = helptiles[deletebon][2];
                    helptile[7+i][3] = "#ff69b4";
                    if(p.getRacea1().equals(String.valueOf(deletebon+1)))  helptile[7+i][3] = "#4275e5";
                    if(p.getRacea2().equals(String.valueOf(deletebon+1)))  helptile[7+i][3] = "#4275e5";
                }
            }
        }
        return helptile;
    }

    @Override
    public boolean[] getAvaraceById(String gameid) {
        boolean[] races = new boolean[28];
     for (int i = 0; i < 28; i++) {
         //更改随机方式
         races[i]=false;
         //races[i]=true;
        }
    StringBuilder mapseed = new StringBuilder();
        Game game = gameMapper.getGameById(gameid);
        for (int i = 0; i < gameid.length(); i++) {
        mapseed.append((int)gameid.charAt(i));
        if(Long.valueOf(mapseed.toString())>= 372036547587L) mapseed = new StringBuilder(String.valueOf(Long.valueOf(mapseed.toString())% 3703857787L));
        }
       Random random = new Random(Long.valueOf(mapseed.toString()));
        int a = 0;
        if(game.getGamemode().length()==7&&game.getGamemode().charAt(2)=='0'&&game.getGamemode().charAt(6)=='1'){
            for (int i=0;i<28;i++){
                races[i] = true;
            }
            int idlength = game.getGameId().length();
            if(game.getGamemode().charAt(0)=='3'){
                ArrayList<String> color = new ArrayList<>();
                int k = random.nextInt(14);
                if(!color.contains(racenumcolormap.get(k))) {races[k] = false;color.add(racenumcolormap.get(k));}
                while (color.size()==1){
                    k = random.nextInt(14);
                    if(!color.contains(racenumcolormap.get(k))) {races[k] = false;color.add(racenumcolormap.get(k));}
                }
                while (color.size()==2){
                    k = random.nextInt(8)+14;
                    if(!color.contains(racenumcolormap.get(k))) {races[k] = false;color.add(racenumcolormap.get(k));}
                }
                while (color.size()==3){
                    k = random.nextInt(8)+14;
                    if(!color.contains(racenumcolormap.get(k))) {races[k] = false;color.add(racenumcolormap.get(k));}
                }
            }else
            if(game.getGameId().substring(idlength-3,idlength-1).equals("_G")){
                /*String s = game.getGameId().substring(idlength-4,idlength-3);*/
                if(game.getGameId().charAt(idlength-4)>='0'&&game.getGameId().charAt(idlength-4)<='9'){
                    Long seedstr = Long.valueOf(game.getGameId().substring(idlength-4,idlength-3))+199899L;
                    random = new Random(Long.valueOf(seedstr));
                }else {
                    Game game1 = gameMapper.getGameById(game.getGameId().substring(0,idlength-1)+'1');
                    random = new Random(Long.valueOf(game1.getMapseed().substring(5))+199699L);
                }
                while (true){
                    ArrayList<ArrayList<Integer>> list = new ArrayList<>();
                    for (int i=0;i<7;i++){
                        list.add(new ArrayList<Integer>());
                    }
                    int dead = 0;
                    for (int i=0;i<=13;i++){
                        for (int j=0;j<2;j++){
                            while (true){
                                int r = random.nextInt(7);
                                if(dead==1000)break;
                                dead++;
                                if(list.get(r).size()<4){
                                    if(i%2==0&&!list.get(r).contains(i)&&!list.get(r).contains(i+1)){list.get(r).add(i);break;}
                                    if(i%2==1&&!list.get(r).contains(i)&&!list.get(r).contains(i-1)){list.get(r).add(i);break;}
                                }
                            }
                        }
                    }
                    if(list.get(0).size()==4&&list.get(1).size()==4&&list.get(2).size()==4&&list.get(3).size()==4&&list.get(4).size()==4&&list.get(5).size()==4&&list.get(6).size()==4) {
                        races[list.get(game.getGameId().charAt(idlength-1)-49).get(0)] = false;
                        races[list.get(game.getGameId().charAt(idlength-1)-49).get(1)] = false;
                        races[list.get(game.getGameId().charAt(idlength-1)-49).get(2)] = false;
                        races[list.get(game.getGameId().charAt(idlength-1)-49).get(3)] = false;
                        break;
                    }
                }

            }else {
                ArrayList<Integer> randomrace = new ArrayList<>();
                while(randomrace.size()!=4){
                    int i = random.nextInt(7);
                    if(!randomrace.contains(i))randomrace.add(i);
                }
                for (int i=0;i<4;i++){
                        int k = random.nextInt(2);
                        races[2*randomrace.get(i)+k] = false;
                }
            }
        }
        Play[] play = playMapper.getPlayByGameId(gameid);
        for(Play p:play) {
            if (p.getRace() !=null) {
                if (p.getRace().equals("人类") || p.getRace().equals("亚特兰斯星人")|| p.getRace().equals("熊猫人")|| p.getRace().equals("殖民者")) races[0] = races[1] = races[14] = races[21] = true;
                if (p.getRace().equals("圣禽族") || p.getRace().equals("蜂人")|| p.getRace().equals("混沌法师")) races[2] = races[3] = races[15] = true;
                if (p.getRace().equals("晶矿星人") || p.getRace().equals("炽炎族")|| p.getRace().equals("天龙星人")) races[4] = races[5] = races[16] =true;
                if (p.getRace().equals("翼空族") || p.getRace().equals("格伦星人")|| p.getRace().equals("蜥族")) races[6] = races[7] = races[17] = true;
                if (p.getRace().equals("大使星人") || p.getRace().equals("利爪族")|| p.getRace().equals("猎户星人")) races[8] = races[9] = races[18] = true;
                if (p.getRace().equals("章鱼人") || p.getRace().equals("疯狂机器")|| p.getRace().equals("魔族")) races[10] = races[11] = races[19] = true;
                if (p.getRace().equals("伊塔星人") || p.getRace().equals("超星人")|| p.getRace().equals("织女星人")) races[12] = races[13] = races[20] = true;
            }
        }
        return races;
    }

    @Override
    public String[] getTTByid(String gameid) {
        String[] result = new String[18];
        TechTile[] techtiles =gameMapper.getTTById(gameid);
        TechTile[] techTiles = techtiles.clone();//高级科技板专用
        Game game = gameMapper.getGameById(gameid);
        techTiles[1]=techtiles[8];
        techTiles[2]=techtiles[9];
        techTiles[3]=techtiles[10];
        techTiles[4]=techtiles[11];
        techTiles[5]=techtiles[12];
        techTiles[6]=techtiles[13];
        techTiles[7]=techtiles[14];
        techTiles[8]=techtiles[15];
        techTiles[9]=techtiles[1];
        techTiles[10]=techtiles[2];
        techTiles[11]=techtiles[3];
        techTiles[12]=techtiles[4];
        techTiles[13]=techtiles[5];
        techTiles[14]=techtiles[6];
        techTiles[15]=techtiles[7];
        if(game.getGamemode().charAt(0)!='0'){
            techTiles[0].setTtname("+5O");
            techTiles[1].setTtname("+2Q,+5C");
            techTiles[2].setTtname("+5K");
            techTiles[14].setTtname("PASSP32VP");
        }
        HaveTt[] haveTts = otherMapper.getHaveTt(gameid);
        for (HaveTt t : haveTts){
            if(t.getTtno().substring(0,3).equals("att")){
                int x = Integer.parseInt(t.getTtno().substring(3));
                techTiles[x-1].setTtno("xxxx");
                techTiles[x-1].setTtname("xxxx");
            }
        }
        String endscoretile=gameMapper.getGameById(gameid).getOtherseed().substring(0,2);
        String lttseed = gameMapper.getGameById(gameid).getOtherseed().substring(10,16);
        String attseed = gameMapper.getGameById(gameid).getOtherseed().substring(17,23);
        boolean[] ltt = new boolean[10];
        for (int i = 0; i < 10; i++) {
            ltt[i]=false;
        }
        for (int i = 0; i < 6; i++) {
            ltt[Integer.parseInt(lttseed.substring(i,i+1))]=true;
        }
        for (int i = 1; i < 10; i++) {
            if(ltt[i]==false) lttseed += i;
        }
        for (int i = 0; i < 6; i++) {
            int num = (int)(attseed.charAt(i))-48;
            if(num >16) num-=7;
            result[i]=techTiles[num-1].getTtno()+": "+
                    techTiles[num-1].getTtname();
        }
        for (int i = 6; i < 16; i++) {
            result[i]=techtiles[Integer.parseInt(lttseed.substring(i-6,i-5),16)+15].getTtno()+": "+
                    techtiles[Integer.parseInt(lttseed.substring(i-6,i-5),16)+15].getTtname();
        ltt[Integer.parseInt(lttseed.substring(i-6,i-5))]=true;
        }
        int terratown = gameMapper.getGameById(gameid).getTerratown();
        result[17]=gameMapper.getTownNameById(terratown);
        for (int i = 0; i < 2; i++) {
            char end = endscoretile.charAt(i);
            if(end=='1') result[15+i]="终局计分1:总建筑数量最多";
            if(end=='2') result[15+i]="终局计分2:城市内总建筑最多";
            if(end=='3') result[15+i]="终局计分3:建筑地形最多";
            if(end=='4') result[15+i]="终局计分4:盖亚星球最多";
            if(end=='5') result[15+i]="终局计分5:所处星域最多";
            if(end=='6') result[15+i]="终局计分6:连接卫星最多";
        }
        return result;
    }

    @Override
    public String getCurrentUserIdById(String gameid) {
        //todo 更改顺序函数
        Game game = gameMapper.getGameById(gameid);
        String[] bid = this.getBid(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        if(bid[0].equals("t")){
          if(Integer.parseInt(bid[5])==-1||Integer.parseInt(bid[6])==-1||Integer.parseInt(bid[7])==-1||Integer.parseInt(bid[8])==-1) return "all";
          String result = "";
          int bidvp = 0;
          for (Play p:plays){
              if(p.getBidpo()==0&&p.getBidvp()>=bidvp) {result = p.getUserid();bidvp = p.getBidvp();}
          }
          return result;
        }
        if(game.getGamemode().charAt(2)=='2'&&game.getBlackstar()==null){
            return plays[3].getUserid();
        }
        if(game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束"))  return "游戏结束";
        String[] records = game.getGamerecord().split("\\.");
        String[] users = playMapper.getUseridByGameId(gameid);
        int yikong = 0;
        int hive = 0;
        char[] sp = new char[]{' ',' ',' ',' '};
        for (Play play :plays){
            if(play.getPosition()<=4) {
                if(play.getRace().equals("翼空族"))  sp[play.getPosition()-1]='y';
                if(play.getRace().equals("蜂人")||play.getRace().equals("混沌法师")) sp[play.getPosition()-1]='f';
                if(play.getRace().equals("殖民者")) sp[play.getPosition()-1]='z';
            }
        }
        ArrayList<String> result = new ArrayList<>();
        result.add("空"); result.add("空"); result.add("空"); result.add("空");result.add("空");
        result.add(users[0]);result.add(users[1]);result.add(users[2]);result.add(users[3]);
        if(sp[0]=='y'||sp[0]==' ')result.add(users[0]);
        if(sp[1]=='y'||sp[1]==' ')result.add(users[1]);
        if(sp[2]=='y'||sp[2]==' ')result.add(users[2]);
        if(sp[3]=='y'||sp[3]==' ')result.add(users[3]);
        if(sp[3]=='y'||sp[3]==' ')result.add(users[3]);
        if(sp[2]=='y'||sp[2]==' ')result.add(users[2]);
        if(sp[1]=='y'||sp[1]==' ')result.add(users[1]);
        if(sp[0]=='y'||sp[0]==' ')result.add(users[0]);
        if(sp[0]=='y'){result.add(users[0]);}else if(sp[1]=='y'){result.add(users[1]);}else if(sp[2]=='y'){result.add(users[2]);}else if(sp[3]=='y'){result.add(users[3]);}
        if(sp[0]=='z'){result.add(users[0]);}else if(sp[1]=='z'){result.add(users[1]);}else if(sp[2]=='z'){result.add(users[2]);}else if(sp[3]=='z'){result.add(users[3]);}
        if(sp[0]=='f'){result.add(users[0]);}else if(sp[1]=='f'){result.add(users[1]);}else if(sp[2]=='f'){result.add(users[2]);}else if(sp[3]=='f'){result.add(users[3]);}
        result.add(users[3]);result.add(users[2]);result.add(users[1]);result.add(users[0]);
        if(records.length<result.size()){
            return result.get(records.length);
        } else{
            int order = game.getTurn();
            if(order == 0){
                for (Play p : plays) {
                    if(p.getRace().equals("人类")&&!p.getRacea1().equals("0")) return p.getUserid();
                    if(p.getRace().equals("伊塔星人")&&Integer.parseInt(p.getRacea1())>=4) return p.getUserid();
                }
            }
            game = gameMapper.getGameById(gameid);
            return playMapper.selectPlayByGameIdPosition(gameid,game.getPosition()).getUserid();
        }
    }

    public void updatePosition(String gameid){
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        if(game.getTurn()==0){
            boolean ok = true;
            for (Play p:plays){
                if(p.getRace().equals("人类")&&!p.getRacea1().equals("0")||p.getRace().equals("伊塔星人")&&Integer.parseInt(p.getRacea1())>=4)
                    ok = false;
            }
            if(ok) game.setTurn(1);
            gameMapper.updateGameById(game);
            return;
        }
        int position = game.getPosition()+1;
        if(position==5) {
            position=1;
            gameMapper.updateTurnById(gameid,game.getTurn()+1);
    }
        while(playMapper.selectPlayByGameIdPosition(gameid,position).getPass()!=0){
            position++;
            if(position==5) {
                position=1;
                gameMapper.updateTurnById(gameid,game.getTurn()+1);}
        }
        gameMapper.updatePositionById(gameid,position);
    }

    @Override
    public String[][] getResourceById(String gameid) {
        Play[] play =  playMapper.getPlayByGameId(gameid);
        Game game = gameMapper.getGameById(gameid);
        Arrays.sort(play, new Comparator<Play>() {
            @Override
            public int compare(Play o1, Play o2) {
                if(o1.getPass()==0&&o2.getPass()!=0) return -1;
                if(o1.getPass()!=0&&o2.getPass()==0) return 1;
                if(o1.getPass()!=0&&o2.getPass()!=0) return o1.getPass()-o2.getPass();
                return o1.getPosition()-o2.getPosition();
            }
        });
        String[][] result =new String[4][29];
        int[] endaddscore = this.gameEnd(gameid,false);
        for (int i = 0; i < 4; i++) {
                result[i][0] = play[i].getUserid();
                result[i][1] = play[i].getRace();
                result[i][2] = String.valueOf(play[i].getO());
                result[i][3] = String.valueOf(play[i].getC());
                result[i][4] = String.valueOf(play[i].getK());
                result[i][5] = String.valueOf(play[i].getQ());
                result[i][6] = String.valueOf(play[i].getP1());
                result[i][7] = String.valueOf(play[i].getP2());
                result[i][8] = String.valueOf(play[i].getP3());
                result[i][9] = racecolormap.get(play[i].getRace());
                result[i][17] = racecolormap.get(play[i].getRace());
                result[i][11] = String.valueOf(otherMapper.getvp(gameid,result[i][0]));
                result[i][14] = String.valueOf(play[i].getPg());
                result[i][15] =  String.valueOf(Integer.parseInt(result[i][11])+endaddscore[i]+passscore(gameid,play[i].getUserid()));
                if(play[i].getRace().equals("利爪族")) result[i][16] = "智慧石"+play[i].getRacea1()+"区 ";
                if(play[i].getRace().equals("混沌法师")) {
                    result[i][16] = "待使用特权：";
                    if(play[i].getRacea1().equals("")) result[i][16]+="1";
                    if(play[i].getRacea2().equals("")) result[i][16]+="2";
                    if(play[i].getRacea3().equals("")) result[i][16]+="3";
                    if(play[i].getRacea4().equals("")) result[i][16]+="4";
                    if(play[i].getRacea5().equals("")) result[i][16]+="5";
                    if(play[i].getRacea6().equals("")) result[i][16]+="6";
                }
            if(play[i].getRace().equals("殖民者")) result[i][16] = "殖民船坐标："+play[i].getM1();
                if(play[i].getRace().equals("蜂人")) result[i][16] = "主城等级："+this.gethiveno(gameid,play[i].getUserid());
            if(play[i].getPass()!=0){
                result[i][17]+="55";
            }
            result[i][18] = "0";
            result[i][19] = "0";
            result[i][20] = "";
            result[i][21] = "";
            String endscoretile = game.getOtherseed().substring(0,2);
            for (int end = 0; end < 2; end++) {
                if(endscoretile.charAt(end)=='1'){
                    result[i][20+end] = "建筑总数：";
                    if (!play[i].getM1().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM2().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM3().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM4().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM5().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM6().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM7().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getM8().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getBlackstar().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getTc1().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getTc2().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getTc3().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getTc4().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getRl1().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getRl2().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getRl3().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getSh().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getAc1().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    if (!play[i].getAc2().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                }
                if(endscoretile.charAt(end)=='2'){
                    result[i][20+end] = "城内建筑总数：";
                    result[i][18+end] = String.valueOf(otherMapper.zjjf2(gameid,play[i].getUserid()));
                    if(play[i].getRace().equals("蜂人")){
                        if(!play[i].getRacea1().equals("")&&otherMapper.gettownbuilding(gameid,play[i].getUserid(),play[i].getRacea1())!=0) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])-1);
                        if(!play[i].getRacea2().equals("")&&otherMapper.gettownbuilding(gameid,play[i].getUserid(),play[i].getRacea2())!=0) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])-1);
                        if(!play[i].getRacea3().equals("")&&otherMapper.gettownbuilding(gameid,play[i].getUserid(),play[i].getRacea3())!=0) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])-1);
                        if(!play[i].getRacea4().equals("")&&otherMapper.gettownbuilding(gameid,play[i].getUserid(),play[i].getRacea4())!=0) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])-1);
                        if(!play[i].getRacea5().equals("")&&otherMapper.gettownbuilding(gameid,play[i].getUserid(),play[i].getRacea5())!=0) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])-1);
                        if(!play[i].getRacea6().equals("")&&otherMapper.gettownbuilding(gameid,play[i].getUserid(),play[i].getRacea6())!=0) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])-1);
                    }
                }
                if(endscoretile.charAt(end)=='3'){
                    result[i][20+end] = "地形种类：";
                    result[i][18+end] = String.valueOf(terratype(gameid,play[i].getUserid()));
                }
                if(endscoretile.charAt(end)=='4'){
                    result[i][20+end] = "盖亚星总数：";
                    result[i][18+end] = String.valueOf(gaiabuildingcount(play[i]));
                }
                if(endscoretile.charAt(end)=='5'){
                    result[i][20+end] = "星域总数：";
                    result[i][18+end] = String.valueOf(getplanets(play[i],""));
                }
                if(endscoretile.charAt(end)=='6'){
                    result[i][20+end] = "卫星总数：";
                    result[i][18+end] = String.valueOf(otherMapper.zjjf6(gameid,play[i].getUserid()));
                    if(play[i].getRace().equals("蜂人")){
                        if(!play[i].getRacea1().equals("")&&!play[i].getRacea1().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                        if(!play[i].getRacea2().equals("")&&!play[i].getRacea2().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                        if(!play[i].getRacea3().equals("")&&!play[i].getRacea3().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                        if(!play[i].getRacea4().equals("")&&!play[i].getRacea4().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                        if(!play[i].getRacea5().equals("")&&!play[i].getRacea5().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                        if(!play[i].getRacea6().equals("")&&!play[i].getRacea6().equals("0")) result[i][18+end] = String.valueOf(Integer.parseInt(result[i][18+end])+1);
                    }
                }
            }
                switch (play[i].getGaialv()){
                    case 0: result[i][12] = "0";result[i][13] = "0";break;
                    case 1: result[i][12] = "1";result[i][13] = "1";break;
                    case 2: result[i][12] = "1";result[i][13] = "1";break;
                    case 3: result[i][12] = "2";result[i][13] = "2";break;
                    case 4: result[i][12] = "3";result[i][13] = "3";break;
                    case 5: result[i][12] = "3";result[i][13] = "3";break;
                }
                if(!play[i].getGtu1().equals("0")){
                    result[i][12]=String.valueOf(Integer.parseInt(result[i][12])-1);
                }
                if(!play[i].getGtu2().equals("0")){
                result[i][12]=String.valueOf(Integer.parseInt(result[i][12])-1);
               }
                if(!play[i].getGtu3().equals("0")){
                result[i][12]=String.valueOf(Integer.parseInt(result[i][12])-1);
               }
                if(play[i].getPass()!=0)result[i][10] = "(passed)";
                if(play[i].getBonus()==99)result[i][10] = "(体面退出)";
        result[i][22] = "#444";
        if(play[i].getUserid().equals("Panpan")||play[i].getUserid().equals("麻吉麻吉喵")){
            result[i][22] = "#FF1493";
        }else if(play[i].getUserid().equals("1mmm")){
            result[i][22] = "#03A9F4";
        }else if(play[i].getUserid().equals("dolphin111")){
            result[i][22] = "#FF0000";
        }else if(play[i].getUserid().equals("zsxzsx")){
            result[i][22] = "#9400D3";
        }else if(play[i].getUserid().equals("12321")){
            result[i][22] = "#FFFFFF";
        }else if(play[i].getUserid().equals("something")){
            result[i][22] = "#C1CDC1";
        }
            String lasttime = "";
            Long time = Long.valueOf( play[i].getTime());
            if(time>=86400*1000){
                lasttime+=time/(86400*1000);
                lasttime+="天";
            }
            time = time%(86400*1000);
            if(time>=3600*1000){
                lasttime+=time/(3600*1000);
                lasttime+="时";
            }
            time = time%(3600*1000);
            if(time>=60*1000){
                lasttime+=time/(60*1000);
                lasttime+="分钟";
            }
            if(lasttime.equals("")) lasttime = "一分钟内";
            result[i][23] = lasttime;
        }
    return result;
    }

    @Override
    public String chooseRace(String gameid, String userid, String race) {
        boolean[] avarace = this.getAvaraceById(gameid);
        Game game = gameMapper.getGameById(gameid);
        if(avarace[racenummap.get(race)]) return"错误";
        if(game.getGamemode().charAt(2)!='3'&&!userid.equals(this.getCurrentUserIdById(gameid))) return "错误";
        playMapper.playerChooseRace(gameid,userid,race);
        if(((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='8')){
            playMapper.setInitResource(raceinitresource28[racenummap.get(race)][0],raceinitresource28[racenummap.get(race)][1],raceinitresource28[racenummap.get(race)][2]
                    ,raceinitresource28[racenummap.get(race)][3],raceinitresource28[racenummap.get(race)][4],raceinitresource28[racenummap.get(race)][5],gameid,userid);
        }
        else if(((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='6')){
            playMapper.setInitResource(raceinitresource26[racenummap.get(race)][0],raceinitresource26[racenummap.get(race)][1],raceinitresource26[racenummap.get(race)][2]
                    ,raceinitresource26[racenummap.get(race)][3],raceinitresource26[racenummap.get(race)][4],raceinitresource26[racenummap.get(race)][5],gameid,userid);
        }
        else if(((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='5')){
            playMapper.setInitResource(raceinitresource25[racenummap.get(race)][0],raceinitresource25[racenummap.get(race)][1],raceinitresource25[racenummap.get(race)][2]
                    ,raceinitresource25[racenummap.get(race)][3],raceinitresource25[racenummap.get(race)][4],raceinitresource25[racenummap.get(race)][5],gameid,userid);
        }
        else if(((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)=='4')){
            playMapper.setInitResource(raceinitresource24[racenummap.get(race)][0],raceinitresource24[racenummap.get(race)][1],raceinitresource24[racenummap.get(race)][2]
                    ,raceinitresource24[racenummap.get(race)][3],raceinitresource24[racenummap.get(race)][4],raceinitresource24[racenummap.get(race)][5],gameid,userid);
        }
        else if(((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)=='3')){
            playMapper.setInitResource(raceinitresource23[racenummap.get(race)][0],raceinitresource23[racenummap.get(race)][1],raceinitresource23[racenummap.get(race)][2]
                    ,raceinitresource23[racenummap.get(race)][3],raceinitresource23[racenummap.get(race)][4],raceinitresource23[racenummap.get(race)][5],gameid,userid);
        }
        else if(((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)=='2')){
            playMapper.setInitResource(raceinitresource22[racenummap.get(race)][0],raceinitresource22[racenummap.get(race)][1],raceinitresource22[racenummap.get(race)][2]
                    ,raceinitresource22[racenummap.get(race)][3],raceinitresource22[racenummap.get(race)][4],raceinitresource22[racenummap.get(race)][5],gameid,userid);
        }else if(game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3'){
            playMapper.setInitResource(raceinitresource2[racenummap.get(race)][0],raceinitresource2[racenummap.get(race)][1],raceinitresource2[racenummap.get(race)][2]
                    ,raceinitresource2[racenummap.get(race)][3],raceinitresource2[racenummap.get(race)][4],raceinitresource2[racenummap.get(race)][5],gameid,userid);
        } else if(game.getGamemode().charAt(0)=='0'){
            playMapper.setInitResource(raceinitresource[racenummap.get(race)][0],raceinitresource[racenummap.get(race)][1],raceinitresource[racenummap.get(race)][2]
                    ,raceinitresource[racenummap.get(race)][3],raceinitresource[racenummap.get(race)][4],raceinitresource[racenummap.get(race)][5],gameid,userid);
        }else if(game.getGamemode().charAt(0)=='1'){
            playMapper.setInitResource(raceinitresource1[racenummap.get(race)][0],raceinitresource1[racenummap.get(race)][1],raceinitresource1[racenummap.get(race)][2]
                    ,raceinitresource1[racenummap.get(race)][3],raceinitresource1[racenummap.get(race)][4],raceinitresource1[racenummap.get(race)][5],gameid,userid);
        }
        if(race.equals("利爪族")) {Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        play.setRacea1("1");
        playMapper.updatePlayById(play);}
        if(race.equals("圣禽族")&&game.getGameId().equals("zsxzsx414")){
            Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
            play.setQ(play.getQ()+1);
            playMapper.updatePlayById(play);
        }
        if(race.equals("疯狂机器"))otherMapper.insertHaveTt(gameid,userid,"actionm","可用");
        if(race.equals("蜂人"))otherMapper.insertHaveTt(gameid,userid,"actionf","可用");
        if(race.equals("殖民者"))otherMapper.insertHaveTt(gameid,userid,"actionr","可用");
        if(race.equals("人类")||race.equals("炽炎族")||race.equals("猎户星人")) playMapper.advanceGaia(gameid,userid);
        if(race.equals("晶矿星人")||race.equals("天龙星人")) playMapper.advanceTerra(gameid,userid);
        if(race.equals("格伦星人")||race.equals("大使星人")) playMapper.advanceShip(gameid,userid);
        if(race.equals("翼空族")||race.equals("混沌法师")) playMapper.advanceQ(gameid,userid);
        if(race.equals("圣禽族")||race.equals("织女星人")) playMapper.advanceEco(gameid,userid);
        if(race.equals("超星人")||race.equals("魔族")) playMapper.advanceSci(gameid,userid);
        if(race.equals("人类")||race.equals("伊塔星人"))playMapper.updateRacea1(gameid,userid);
        return"成功";
    }

    @Override
    public String buildMine(String gameid, String userid, String location,String action) {
        Game game = this.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid, userid);
        int chaos = 0;
        //判断混沌法师特权发动
        if(play.getRace().equals("混沌法师")){
            if(location.split(" ").length==2&&getplanets(play,location.split(" ")[0])>getplanets(play,"")&&getplanets(play,location.split(" ")[0])<=7&&getplanets(play,"")!=0){
                chaos = Integer.parseInt(location.split(" ")[1]);
                location = location.split(" ")[0];
                if(chaos==1&&!play.getRacea1().equals("")) return "必须选择不相同的特权";
                if(chaos==2&&!play.getRacea2().equals("")) return "必须选择不相同的特权";
                if(chaos==3&&!play.getRacea3().equals("")) return "必须选择不相同的特权";
                if(chaos==4&&!play.getRacea4().equals("")) return "必须选择不相同的特权";
                if(chaos==5&&!play.getRacea5().equals("")) return "必须选择不相同的特权";
                if(chaos==6&&!play.getRacea6().equals("")) return "必须选择不相同的特权";
           }else if(location.split(" ").length==1&&getplanets(play,location)>getplanets(play,"")&&getplanets(play,location)<=7&&getplanets(play,"")!=0) {return "必须选择新特权";}
           else if(location.split(" ").length==2&&(getplanets(play,location.split(" ")[0])==getplanets(play,"")||getplanets(play,location.split(" ")[0])>7||getplanets(play,"")==0))
           {String[] abc = location.split(" ");
           return "不能选择新特权";}
        }
        //用于回退
        Play oldplay = playMapper.getPlayByGameIdUserid(gameid, userid);
        String[][] mapdetail = new String[21][15];
        int jinkbefore = terratype(gameid, userid);
        int costo = 1;
        int costq = 0;
        int costk = 0;
        int terrascoretile = 0;
        boolean hasgtu = false;
        this.setMapDetail(mapdetail, gameid);
        String racecolor = racecolormap.get(play.getRace());
        int row = (int) location.charAt(0) - 64;
        int column = Integer.parseInt(location.substring(1));
        //建造盖亚改造单元所在位置
        if (play.getGtu1().equals(location) || play.getGtu2().equals(location) || play.getGtu3().equals(location)) {
            hasgtu = true;
            int gai = otherMapper.getGaia(gameid,location);
            if(gai==0) return "你想利用BUG造紫星???";
            if (play.getGtu1().equals(location)) play.setGtu1("0");
            if (play.getGtu2().equals(location)) play.setGtu2("0");
            if (play.getGtu3().equals(location)) play.setGtu3("0");
        } else if ( game.getRound() == 0) {//建造初始矿场
            if(!mapdetail[row][column].equals(racecolor))return "请建造在母星上！";
            if(location.equals(play.getM1())||location.equals(play.getM2()))return "失败";
            if(play.getRace().equals("蜂人")||play.getRace().equals("混沌法师")) {
                play.setSh(location);playMapper.updatePlayById(play);
                return "成功";}else {
                if(play.getM1().equals("0")){ play.setM1(location);}
                else if(play.getM2().equals("0")) {play.setM2(location);}
                else if(play.getM3().equals("0")) {play.setM3(location);}
                playMapper.updatePlayById(play);return "成功";
            }
        } else {
            String[][] sd = this.getStructureSituationById(gameid);
            String[][] sc = this.getStructureColorById(gameid);
            boolean[][] ji = this.getJisheng(gameid);
            //寄生
            if (sd[row][column] != null &&!sd[row][column].equals("gtu")&& !sc[row][column].equals(bl) && play.getRace().equals("亚特兰斯星人")&&!ji[row][column]) {
                if (play.getC() < 2 || play.getO() < 1) {
                    return "你的资源不够了！";
                }
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                if (play.getM1().equals("0")) {
                    play.setM1(location);
                } else if (play.getM2().equals("0")) {
                    play.setM2(location);
                } else if (play.getM3().equals("0")) {
                    play.setM3(location);
                } else if (play.getM4().equals("0")) {
                    play.setM4(location);
                } else if (play.getM5().equals("0")) {
                    play.setM5(location);
                } else if (play.getM6().equals("0")) {
                    play.setM6(location);
                } else if (play.getM7().equals("0")) {
                    play.setM7(location);
                } else if (play.getM8().equals("0")) {
                    play.setM8(location);
                } else {
                    return "矿场已建满！";
                }
                //计分
                    if(!canArrive(gameid, userid, location, action,true)) return "距离不够！";
                    play.setQ(playMapper.getQById(gameid,userid));
                    String[] rs = this.getRoundScoreById(gameid);
                    if (rs[game.getRound() - 1].equals("M>>2"))
                        otherMapper.gainVp(gameid, userid, 2, "M>>2");
                    if (otherMapper.getTtByGameidUseridTtno(gameid, userid, "att7") != null)
                        otherMapper.gainVp(gameid, userid, 3, "att7");  if (game.getRound() != 0) {
                        play.setO(play.getO() - 1);
                        play.setC(play.getC() - 2);
                        if(!play.getSh().equals("0")) play.setK(play.getK() + 2);
                        playMapper.updatePlayById(play);
                        createPower(gameid, userid, location, "M");
                        updatePosition(gameid);
                        /*    playMapper.updatePlayById(play);*/
                        //判断是不是直接进城
                        ArrayList<String> townandsate = new ArrayList<>();
                        String[] townbuildings = otherMapper.getTownBuildingByUserid(gameid,userid);
                        String[] sates = otherMapper.getSatelliteByUserid(gameid,userid);
                        Collections.addAll(townandsate, townbuildings);
                        Collections.addAll(townandsate, sates);
                        for (String s:townandsate){
                            if(distance(location,s)==1) {otherMapper.insertTB(gameid,userid,location);break;}
                        }
                    }
                return "成功";
            }
         if(sd[row][column]!=null) return "已有其他建筑";
         if (mapdetail[row][column].equals(pu) || mapdetail[row][column].equals(ck) || mapdetail[row][column].equals(""))
             return "建造位置不合法！";
         //改造费用是否足够
         if (!mapdetail[row][column].equals(ga)) {
             int racecolornum = colorroundmap.get(racecolor);
             int locationcolornum = colorroundmap.get(mapdetail[row][column]);
             int diff = racecolornum > locationcolornum ? Math.min(racecolornum - locationcolornum, 7 + locationcolornum - racecolornum) : Math.min(locationcolornum - racecolornum, 7 + racecolornum - locationcolornum);
             terrascoretile = diff;
             if(action.equals("action2")||action.equals("bon1")||action.equals("bug1")||action.equals("att16")) diff--;
             if(action.equals("action6")||action.equals("bug2")) diff-=2;
             if(action.equals("dragon")){
                 int lcn = colorroundmap.get(mapdetail[row][column]);
                 if(lcn==1||lcn==3){
                    if(play.getP1()+play.getP2()<1) return "豆不够";
                 }
                 if(lcn==0||lcn==4){
                     if(play.getP1()+play.getP2()<2) return "豆不够";
                 }
                 if(lcn==5||lcn==6){
                     if(play.getP1()+play.getP2()<3) return "豆不够";
                 }
                 diff = 0;}
             if (diff<0) diff=0;
             int terralv = play.getTerralv();
             int t = 0;
             switch (terralv) {
                 case 0:
                     t = 3;
                     break;
                 case 1:
                     t = 3;
                     break;
                 case 2:
                     t = 2;
                     break;
                 case 3:
                     t = 1;
                     break;
                 case 4:
                     t = 1;
                     break;
                 case 5:
                     t = 1;
                     break;
             }
             if(play.getRace().equals("魔族")){
                 if (game.getRound() != 0 && (play.getK()<2 * diff||play.getO() < 1 || play.getC() < 2)) return "你的资源不够了！";
                 play.setK(play.getK()-2*diff);
               otherMapper.gainVp(gameid,userid,2*diff,"种族技能");
             }else {
                 if (game.getRound() != 0 && (play.getO() < 1 + t * diff || play.getC() < 2)) return "你的资源不够了！";
                 costo += t * diff;
             }
         }else if(!hasgtu){
             if(play.getRace().equals("格伦星人")) {costo++;}
             else {costq += 1;}
             if(play.getQ() < costq || play.getC() < 2 || play.getO() < costo) {
                 return "你的资源不够了！";
             }
         }else {
             if(play.getC() < 2 || play.getO() < 1) {
                 return "你的资源不够了！";
             }
         }
     }
            play.setO(play.getO()-costo);
            play.setQ(play.getQ()-costq);
            play.setC(play.getC()-2);
            if(play.getO()<0||play.getQ()<0||play.getC()<0) return "资源不足的错误";
            playMapper.updatePlayById(play);
            if(!hasgtu&&!canArrive(gameid,userid,location,action,true)) {playMapper.updatePlayById(oldplay);return "距离不够！";}
            play.setQ(playMapper.getQById(gameid,userid));
            //计分
        if(play.getM1().equals("0")){
            play.setM1(location);
        }else if(play.getM2().equals("0")){
            play.setM2(location);
        }else if(play.getM3().equals("0")){
            play.setM3(location);
        }else if(play.getM4().equals("0")){
            play.setM4(location);
        }else if(play.getM5().equals("0")){
            play.setM5(location);
        }else if(play.getM6().equals("0")){
            play.setM6(location);
        }else if(play.getM7().equals("0")){
            play.setM7(location);
        }else if(play.getM8().equals("0")){
            play.setM8(location);
        }else{
            playMapper.updatePlayById(oldplay); return"矿场已建满！";
        }
        String[] rs = this.getRoundScoreById(gameid);
        if(mapdetail[row][column].equals(ga)&&rs[game.getRound()-1].equals("M(G)>>3")) {otherMapper.gainVp(gameid,userid,3,"M(G)>>3");xizu(play,"MG");}
        if(mapdetail[row][column].equals(ga)&&rs[game.getRound()-1].equals("M(G)>>4")) {otherMapper.gainVp(gameid,userid,4,"M(G)>>4");xizu(play,"MG");}
        if(mapdetail[row][column].equals(ga)&&play.getRace().equals("格伦星人")&&game.getGamemode().charAt(0)=='0') otherMapper.gainVp(gameid,userid,2,"种族技能");
        if(mapdetail[row][column].equals(ga)&&play.getRace().equals("格伦星人")&&game.getGamemode().charAt(0)!='0') otherMapper.gainVp(gameid,userid,3,"种族技能");
        if(game.getRound()!=0&&rs[game.getRound()-1].equals("M>>2")) {otherMapper.gainVp(gameid,userid,2,"M>>2");xizu(play,"M");}
        if(terrascoretile!=0&&rs[game.getRound()-1].equals("TERRA>>2")) {otherMapper.gainVp(gameid,userid,2*terrascoretile,"TERRA>>2");xizu(play,"TERRA"+terrascoretile);}
        if(otherMapper.getTtByGameidUseridTtno(gameid,userid,"att7")!=null) otherMapper.gainVp(gameid,userid,3,"att7");
        if(otherMapper.getTtByGameidUseridTtno(gameid,userid,"ltt5")!=null&&!otherMapper.getTtByGameidUseridTtno(gameid,userid,"ltt5").getTtstate().equals("被覆盖")&&mapdetail[row][column].equals(ga)) otherMapper.gainVp(gameid,userid,3,"ltt5");
                playMapper.updatePlayById(play);
                createPower(gameid,userid,location,"M");
                updatePosition(gameid);
        playMapper.updatePlayById(play);
            play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int jinkafter = terratype(gameid,userid);
        if(play.getRace().equals("晶矿星人")&&!play.getSh().equals("0")&&jinkafter>jinkbefore){
            play.setK(play.getK()+3);
        playMapper.updatePlayById(play);
        }
        if(action.equals("dragon")){
            int lcn = colorroundmap.get(mapdetail[row][column]);
            if(lcn==0||lcn==4){
                for (int i=1;i<=1;i++){
                    if(play.getP1()>0) {play.setP1(play.getP1()-1);continue;}
                    if(play.getP2()>0) play.setP2(play.getP2()-1);
                }
            }
            if(lcn==5||lcn==6){
                for (int i=1;i<=2;i++){
                    if(play.getP1()>0) {play.setP1(play.getP1()-1);continue;}
                    if(play.getP2()>0) play.setP2(play.getP2()-1);
                }
            }
        }
        if(play.getRace().equals("天龙星人")){
            if(mapdetail[row][column].equals(ga)){
                play.setP1(play.getP1()+2);
            }else {
                int lcn = colorroundmap.get(mapdetail[row][column]);
                if(lcn==1||lcn==3){
                    play.setC(play.getC()+1);
                }
                if(lcn==0||lcn==4){
                    incomePower(play,4);
                    otherMapper.gainVp(gameid,userid,1,"种族技能");
                }
                if(lcn==5||lcn==6){
                    play.setQ(play.getQ()+1);
                    otherMapper.gainVp(gameid,userid,2,"种族技能");
                }
            }
            playMapper.updatePlayById(play);
        }
        if(play.getRace().equals("混沌法师")&&chaos!=0){
            int hasused = 1;
            if(play.getRacea1().equals("used")) hasused++; if(play.getRacea2().equals("used")) hasused++; if(play.getRacea3().equals("used")) hasused++; if(play.getRacea4().equals("used")) hasused++; if(play.getRacea5().equals("used")) hasused++; if(play.getRacea6().equals("used")) hasused++;
            switch (chaos){
                case 1: play.setO(play.getO()+hasused);play.setRacea1("used");break;
                case 2: play.setC(play.getC()+3*hasused);play.setRacea2("used");break;
                case 3: play.setK(play.getK()+hasused);play.setRacea3("used");break;
                case 4: play.setP1(play.getP1()+hasused); incomePower(play,hasused);play.setRacea4("used");break;
                case 5: incomePower(play,3*hasused);play.setRacea5("used");break;
                case 6: otherMapper.gainVp(gameid,userid,2*hasused,"种族技能");play.setRacea6("used");break;
            }
            playMapper.updatePlayById(play);
        }
        //判断是不是直接进城
        ArrayList<String> townandsate = new ArrayList<>();
        String[] townbuildings = otherMapper.getTownBuildingByUserid(gameid,userid);
        String[] sates = otherMapper.getSatelliteByUserid(gameid,userid);
        Collections.addAll(townandsate, townbuildings);
        Collections.addAll(townandsate, sates);
        for (String s:townandsate){
            if(distance(location,s)==1) {otherMapper.insertTB(gameid,userid,location);break;}
        }
        if(play.getRace().equals("殖民者")) otherMapper.lttfugaiundo(gameid,userid,"actionr");
        return "成功";
    }

    private void xizu(Play play, String s) {
        if(play.getRace().equals("蜥族")){
            if(s.length()>=5&&s.substring(0,5).equals("TERRA")) {int time = Integer.parseInt(s.substring(5,6));otherMapper.gainVp(play.getGameid(),play.getUserid(),time,"要塞技能"); play.setC(play.getC()+2*time);}
            else {
                play.setC(play.getC()+2);
                otherMapper.gainVp(play.getGameid(),play.getUserid(),1,"种族技能");
            }
            if(!play.getSh().equals("0")){
                if(s.equals("M")) {play.setC(play.getC()+1); otherMapper.gainVp(play.getGameid(),play.getUserid(),1,"要塞技能");}
                if(s.equals("TC")) {play.setO(play.getO()+1); otherMapper.gainVp(play.getGameid(),play.getUserid(),1,"要塞技能");}
                if(s.equals("MG")) {play.setQ(play.getQ()+1); otherMapper.gainVp(play.getGameid(),play.getUserid(),1,"要塞技能");}
                if(s.equals("SH")) {
                    for (int i=1;i<=5;i++){
                        if(play.getP1()!=0) {
                            play.setP1(play.getP1()-1);
                            play.setP2(play.getP2()+1);
                        }else if(play.getP2()!=0){
                            play.setP2(play.getP2()-1);
                            play.setP3(play.getP3()+1);
                        }
                    }
                    otherMapper.gainVp(play.getGameid(),play.getUserid(),2,"要塞技能");}
                if(s.length()>=5&&s.substring(0,5).equals("TERRA")) {int time = Integer.parseInt(s.substring(5,6));otherMapper.gainVp(play.getGameid(),play.getUserid(),time,"要塞技能");}
                if(s.equals("AT")) {play.setK(play.getK()+1); otherMapper.gainVp(play.getGameid(),play.getUserid(),1,"要塞技能");}
                if(s.equals("TOWN")) {otherMapper.gainVp(play.getGameid(),play.getUserid(),4,"要塞技能");}
            }
        }

    }

    private boolean createPower(String gameid, String userid, String location, String structure) {
        Play[] play = playMapper.getPlayByGameId(gameid);
        Play give = playMapper.getPlayByGameIdUserid(gameid,userid);
        String[][] mapdetail = new String[21][15];
        this.setMapDetail(mapdetail,gameid);
        String[][] sc = this.getStructureColorById(gameid);
        boolean hascreate = false;
        for (int i = 0; i < play.length; i++) {
            //todo 不能原地蹭
            if(play[i].getRace().equals(give.getRace())&&!play[i].getRace().equals("织女星人")) continue;
            if(play[1].getBonus()==99) continue;
            int power = 0;
            if(play[i].getRace().equals("殖民者")){
                int level = 3;
                HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,play[i].getUserid(),"ltt8");
                if(tt != null && !tt.getTtstate().equals("被覆盖")) level=4;
                if(distance(play[i].getAc1(),location)<=2) power+=level;
                if(distance(play[i].getAc2(),location)<=2) power+=level;
                if(distance(play[i].getSh(),location)<=2) power+=level;
                if(distance(play[i].getTc1(),location)<=2) power+=2;
                if(distance(play[i].getTc2(),location)<=2) power+=2;
                if(distance(play[i].getTc3(),location)<=2) power+=2;
                if(distance(play[i].getTc4(),location)<=2) power+=2;
                if(distance(play[i].getRl1(),location)<=2) power+=2;
                if(distance(play[i].getRl2(),location)<=2) power+=2;
                if(distance(play[i].getRl3(),location)<=2) power+=2;
                if(distance(play[i].getM1(),location)<=2) power+=1;
                if(distance(play[i].getM2(),location)<=2) power+=1;
                if(distance(play[i].getM3(),location)<=2) power+=1;
                if(distance(play[i].getM4(),location)<=2) power+=1;
                if(distance(play[i].getM5(),location)<=2) power+=1;
                if(distance(play[i].getM6(),location)<=2) power+=1;
                if(distance(play[i].getM7(),location)<=2) power+=1;
                if(distance(play[i].getM8(),location)<=2) power+=1;
                if(distance(play[i].getBlackstar(),location)<=2) power+=1;
            }else {
                if(distance(play[i].getAc1(),location)<=2||distance(play[i].getAc2(),location)<=2||distance(play[i].getSh(),location)<=2)
                {
                    HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,play[i].getUserid(),"ltt8");
                    if(tt != null && !tt.getTtstate().equals("被覆盖")) {power=4;}else {power=3;}
                    if(play[i].getRace().equals("疯狂机器")&&!play[i].getSh().equals("0")){
                        if(distance(play[i].getAc1(),location)<=2&&mapdetail[play[i].getAc1().charAt(0)-64][Integer.parseInt(play[i].getAc1().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getAc2(),location)<=2&&mapdetail[play[i].getAc2().charAt(0)-64][Integer.parseInt(play[i].getAc2().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getSh(),location)<=2&&mapdetail[play[i].getSh().charAt(0)-64][Integer.parseInt(play[i].getSh().substring(1))].equals(gr)) {power++;}
                    }
                }
                else if(distance(play[i].getTc1(),location)<=2||distance(play[i].getTc2(),location)<=2||distance(play[i].getTc3(),location)<=2||distance(play[i].getTc4(),location)<=2||distance(play[i].getRl1(),location)<=2||distance(play[i].getRl2(),location)<=2||distance(play[i].getRl3(),location)<=2) {power=2;
                    if(play[i].getRace().equals("疯狂机器")&&!play[i].getSh().equals("0")){
                        if(distance(play[i].getTc1(),location)<=2&&mapdetail[play[i].getTc1().charAt(0)-64][Integer.parseInt(play[i].getTc1().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getTc2(),location)<=2&&mapdetail[play[i].getTc2().charAt(0)-64][Integer.parseInt(play[i].getTc2().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getTc3(),location)<=2&&mapdetail[play[i].getTc3().charAt(0)-64][Integer.parseInt(play[i].getTc3().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getTc4(),location)<=2&&mapdetail[play[i].getTc4().charAt(0)-64][Integer.parseInt(play[i].getTc4().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getRl1(),location)<=2&&mapdetail[play[i].getRl1().charAt(0)-64][Integer.parseInt(play[i].getRl1().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getRl2(),location)<=2&&mapdetail[play[i].getRl2().charAt(0)-64][Integer.parseInt(play[i].getRl2().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getRl3(),location)<=2&&mapdetail[play[i].getRl3().charAt(0)-64][Integer.parseInt(play[i].getRl3().substring(1))].equals(gr)) {power++;}
                    }
                }else
                if(distance(play[i].getM1(),location)<=2||distance(play[i].getM2(),location)<=2||distance(play[i].getM3(),location)<=2||distance(play[i].getM4(),location)<=2||distance(play[i].getM5(),location)<=2||distance(play[i].getM6(),location)<=2||distance(play[i].getM7(),location)<=2||distance(play[i].getM8(),location)<=2||distance(play[i].getBlackstar(),location)<=2&&distance(play[i].getBlackstar(),location)>0) {power=1;
                    if(play[i].getRace().equals("疯狂机器")&&!play[i].getSh().equals("0")){
                        if(distance(play[i].getM1(),location)<=2&&mapdetail[play[i].getM1().charAt(0)-64][Integer.parseInt(play[i].getM1().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM2(),location)<=2&&mapdetail[play[i].getM2().charAt(0)-64][Integer.parseInt(play[i].getM2().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM3(),location)<=2&&mapdetail[play[i].getM3().charAt(0)-64][Integer.parseInt(play[i].getM3().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM4(),location)<=2&&mapdetail[play[i].getM4().charAt(0)-64][Integer.parseInt(play[i].getM4().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM5(),location)<=2&&mapdetail[play[i].getM5().charAt(0)-64][Integer.parseInt(play[i].getM5().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM6(),location)<=2&&mapdetail[play[i].getM6().charAt(0)-64][Integer.parseInt(play[i].getM6().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM7(),location)<=2&&mapdetail[play[i].getM7().charAt(0)-64][Integer.parseInt(play[i].getM7().substring(1))].equals(gr)) {power++;}
                        else  if(distance(play[i].getM8(),location)<=2&&mapdetail[play[i].getM8().charAt(0)-64][Integer.parseInt(play[i].getM8().substring(1))].equals(gr)) {power++;}
                    }
                }
            }
            if(power!=0) {
                if(otherMapper.getPowerById(gameid,give.getRace(),play[i].getRace(),location,structure)!=null) return false;
                otherMapper.insertPower(gameid,give.getRace(),play[i].getRace(),location,structure,power);
                hascreate = true;}
        }
        return hascreate;
    }

    private boolean tc3or6(String gameid, String userid, String location) {
        Play[] play = playMapper.getPlayByGameId(gameid);
        Play give = playMapper.getPlayByGameIdUserid(gameid,userid);
        boolean hascreate = false;
        for (int i = 0; i < play.length; i++) {
            if(play[i].getRace().equals(give.getRace())) continue;
            int power = 0;
            if(distance(play[i].getAc1(),location)<=2||distance(play[i].getAc2(),location)<=2||distance(play[i].getSh(),location)<=2) {power=3;}else
            if(distance(play[i].getTc1(),location)<=2||distance(play[i].getTc2(),location)<=2||distance(play[i].getTc3(),location)<=2||distance(play[i].getTc4(),location)<=2||distance(play[i].getRl1(),location)<=2||distance(play[i].getRl2(),location)<=2||distance(play[i].getRl3(),location)<=2) {power=2;}else
            if(distance(play[i].getM1(),location)<=2||distance(play[i].getM2(),location)<=2||distance(play[i].getM3(),location)<=2||distance(play[i].getM4(),location)<=2||distance(play[i].getM5(),location)<=2||distance(play[i].getM6(),location)<=2||distance(play[i].getM7(),location)<=2||distance(play[i].getM8(),location)<=2) {power=1;}else
            if(!play[i].getBlackstar().equals("0")&&distance(play[i].getBlackstar(),location)<=2) {power=1;}
            if(power!=0) { hascreate = true;}
        }
        return hascreate;
    }

    private int distance(String c1, String c2) {
        if(c1.equals("0")||c2.equals("0")) return 9999;
        int[] adjust = new int[]{4,4,3,1,0,0,0,1,1,1,0,1,1,2,1,1,1,2,5,6};
        int x1 = c1.charAt(0)-65;
        int x2 = c2.charAt(0)-65;
        int y1 = Integer.parseInt(c1.substring(1));
        int y2 = Integer.parseInt(c2.substring(1));
        y1+=adjust[x1];
        y2+=adjust[x2];
        int result = 0;
        result+=Math.abs(x2-x1);
        if(x1%2==x2%2&&Math.abs(y1-y2)>Math.abs(x2-x1)/2){
            result+=Math.abs(y1-y2)-Math.abs(x2-x1)/2;
        }else if(x1%2!=x2%2){
            String s="";
            int a = 0;
            if(x1>x2) {s+=(char)(c1.charAt(0)-1);a=x1-1;}
            if(x1<x2) {s+=(char)(c1.charAt(0)+1);a=x1+1;}
            if(x1%2==0&&y1<y2) {s+=String.valueOf(y1+1-adjust[a]);}else  if(x1%2==1&&y1>y2) {
                s+=String.valueOf(y1-1-adjust[a]);
            }else{
                s+=String.valueOf(y1-adjust[a]);
            }
            return 1+distance(s,c2);
        }
    return result;
    }
//pass所得分
    int passscore(String gameid, String userid){
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(play.getPass()!=0) return 0;
        HaveTt[] hts = otherMapper.getHaveTtByUserid(gameid,userid);
        int gainvp = 0;
        for (HaveTt ht:hts){
            if(ht.getTtno().equals("att4")){
                if(!play.getRl1().equals("0")) gainvp+=3;
                if(!play.getRl2().equals("0")) gainvp+=3;
                if(!play.getRl3().equals("0")) gainvp+=3;
            }
            if(ht.getTtno().equals("att5")){
                HaveTown[] haveTowns = otherMapper.getHTByGameIdUserId(gameid,userid);
                gainvp+=haveTowns.length*3;
            }
            if(ht.getTtno().equals("att6")){
                gainvp+=this.terratype(gameid,userid);
            }
            if(ht.getTtno().equals("att15")&&(game.getGamemode().charAt(0)!='0')&&game.getGamemode().length()==3){
                gainvp += play.getP3()*2;
            }else if(ht.getTtno().equals("att15")&&game.getGamemode().charAt(0)!='0'&&game.getGamemode().charAt(4)=='1'){
                int min = play.getP1();
                if(play.getP2()<play.getP1()) min = play.getP2();
                if(play.getP3()<min) min = play.getP3();
                gainvp += min*3;
            }else if(ht.getTtno().equals("att15")&&game.getGamemode().charAt(0)!='0'&&game.getGamemode().charAt(4)>='4'){
                gainvp += getplanets(play,"");
            }
        }
        int passbonus = play.getBonus();
        if(play.getRace().equals("熊猫人")){
            if(!play.getRacea1().equals("")){
                switch (Integer.parseInt(play.getRacea1())){
                    case 6: {
                        if(!play.getM1().equals("0")) gainvp++;
                        if(!play.getM2().equals("0")) gainvp++;
                        if(!play.getM3().equals("0")) gainvp++;
                        if(!play.getM4().equals("0")) gainvp++;
                        if(!play.getM5().equals("0")) gainvp++;
                        if(!play.getM6().equals("0")) gainvp++;
                        if(!play.getM7().equals("0")) gainvp++;
                        if(!play.getM8().equals("0")) gainvp++;
                        if(!play.getBlackstar().equals("0")) gainvp++;
                        break;
                    }
                    case 7:{
                        if(!play.getTc1().equals("0")) gainvp+=2;
                        if(!play.getTc2().equals("0")) gainvp+=2;
                        if(!play.getTc3().equals("0")) gainvp+=2;
                        if(!play.getTc4().equals("0")) gainvp+=2;
                        break;
                    }
                    case 8:{
                        if(!play.getRl1().equals("0")) gainvp+=3;
                        if(!play.getRl2().equals("0")) gainvp+=3;
                        if(!play.getRl3().equals("0")) gainvp+=3;
                        break;
                    }
                    case 9:{
                        if(!play.getSh().equals("0")) gainvp+=4;
                        if(!play.getAc1().equals("0")) gainvp+=4;
                        if(!play.getAc2().equals("0")) gainvp+=4;
                        break;
                    }
                    case 10:{
                        gainvp += this.gaiabuildingcount(play);
                        break;
                    }
                }
            }
            if(!play.getRacea2().equals("")){
                switch (Integer.parseInt(play.getRacea2())){
                    case 6: {
                        if(!play.getM1().equals("0")) gainvp++;
                        if(!play.getM2().equals("0")) gainvp++;
                        if(!play.getM3().equals("0")) gainvp++;
                        if(!play.getM4().equals("0")) gainvp++;
                        if(!play.getM5().equals("0")) gainvp++;
                        if(!play.getM6().equals("0")) gainvp++;
                        if(!play.getM7().equals("0")) gainvp++;
                        if(!play.getM8().equals("0")) gainvp++;
                        if(!play.getBlackstar().equals("0")) gainvp++;
                        break;
                    }
                    case 7:{
                        if(!play.getTc1().equals("0")) gainvp+=2;
                        if(!play.getTc2().equals("0")) gainvp+=2;
                        if(!play.getTc3().equals("0")) gainvp+=2;
                        if(!play.getTc4().equals("0")) gainvp+=2;
                        break;
                    }
                    case 8:{
                        if(!play.getRl1().equals("0")) gainvp+=3;
                        if(!play.getRl2().equals("0")) gainvp+=3;
                        if(!play.getRl3().equals("0")) gainvp+=3;
                        break;
                    }
                    case 9:{
                        if(!play.getSh().equals("0")) gainvp+=4;
                        if(!play.getAc1().equals("0")) gainvp+=4;
                        if(!play.getAc2().equals("0")) gainvp+=4;
                        break;
                    }
                    case 10:{
                        gainvp += this.gaiabuildingcount(play);
                        break;
                    }
                }
            }
        }
        switch (passbonus){
            case 6: {
                if(!play.getM1().equals("0")) gainvp++;
                if(!play.getM2().equals("0")) gainvp++;
                if(!play.getM3().equals("0")) gainvp++;
                if(!play.getM4().equals("0")) gainvp++;
                if(!play.getM5().equals("0")) gainvp++;
                if(!play.getM6().equals("0")) gainvp++;
                if(!play.getM7().equals("0")) gainvp++;
                if(!play.getM8().equals("0")) gainvp++;
                if(!play.getBlackstar().equals("0")) gainvp++;
                break;
            }
            case 7:{
                if(!play.getTc1().equals("0")) gainvp+=2;
                if(!play.getTc2().equals("0")) gainvp+=2;
                if(!play.getTc3().equals("0")) gainvp+=2;
                if(!play.getTc4().equals("0")) gainvp+=2;
                break;
            }
            case 8:{
                if(!play.getRl1().equals("0")) gainvp+=3;
                if(!play.getRl2().equals("0")) gainvp+=3;
                if(!play.getRl3().equals("0")) gainvp+=3;
                break;
            }
            case 9:{
                if(!play.getSh().equals("0")) gainvp+=4;
                if(!play.getAc1().equals("0")) gainvp+=4;
                if(!play.getAc2().equals("0")) gainvp+=4;
                break;
            }
            case 10:{
              gainvp += this.gaiabuildingcount(play);
              break;
            }
        }
        return gainvp;
    }

    @Override
    public String drop(String gameid, String userid) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int hasdropped = playMapper.getDroppedNum(gameid);
        playMapper.updateBonusById(gameid,userid,99);
        playMapper.updatePassNo(gameid,userid,4-hasdropped);
        updatePosition(gameid);
        if(hasdropped==3)gameEnd(gameid,true);
        return "成功";
    }

    @Override
    public String pass(String gameid, String userid, String bon) {
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(game.getRound()!=0) {
            int passedplayers = playMapper.selectPassNo(gameid);
            if(passedplayers == 3&&otherMapper.getAllPowerById(gameid).length!=0) return "等待玩家蹭魔";
        }
        int passbonus = play.getBonus();
        int passbonus1 = 0;
        int passbonus2 = 0;
        if(play.getRace().equals("熊猫人")&& !play.getRacea1().equals("")) passbonus1 = Integer.parseInt(play.getRacea1());
        if(play.getRace().equals("熊猫人")&& !play.getRacea2().equals("")) passbonus2 = Integer.parseInt(play.getRacea2());
        int bonusno = 0;
        int passb = 0;
        int passc = 0;
        if(play.getRace().equals("熊猫人")){
            String[] bons= bon.split(" ");
            if(!((bons.length==2&&play.getSh().equals("0"))||bons.length==3&&!play.getSh().equals("0"))) return "错误";
            if(bons.length==2){
                bonusno = Integer.parseInt(bons[0]);
                passb = Integer.parseInt(bons[1]);
                String nobon = game.getOtherseed().substring(24);
                if(passb!=(int)nobon.charAt(0)-47&&passb!=(int)nobon.charAt(1)-47&&passb!=(int)nobon.charAt(2)-47) return "选择回合助推板错误！";
                if(!play.getRacea1().equals("")&&passb==Integer.parseInt(play.getRacea1()))return "选择回合助推板错误！";
                play.setRacea1(String.valueOf(passb));
                playMapper.updatePlayById(play);
            }
            if(bons.length==3){
                bonusno = Integer.parseInt(bons[0]);
                passb = Integer.parseInt(bons[1]);
                String nobon = game.getOtherseed().substring(24);
                passc = Integer.parseInt(bons[2]);
                if(play.getRacea2().equals("")){
                    if(passb==Integer.parseInt(play.getRacea1())||passc==Integer.parseInt(play.getRacea1()))return "选择回合助推板错误！";
                }else {
                    if(passb==Integer.parseInt(play.getRacea1())&&passc==Integer.parseInt(play.getRacea2())) return "选择回合助推板错误！";
                    if(passb==Integer.parseInt(play.getRacea2())&&passc==Integer.parseInt(play.getRacea1())) return "选择回合助推板错误！";
                }
                if(passb!=(int)nobon.charAt(0)-47&&passb!=(int)nobon.charAt(1)-47&&passb!=(int)nobon.charAt(2)-47) return "选择回合助推板错误！";
                if(passc!=(int)nobon.charAt(0)-47&&passc!=(int)nobon.charAt(1)-47&&passc!=(int)nobon.charAt(2)-47) return "选择回合助推板错误！";
                if(passb==passc) return "选择回合助推板错误！";
                play.setRacea1(String.valueOf(passb));
                play.setRacea2(String.valueOf(passc));
                playMapper.updatePlayById(play);
            }
        }else {
            bonusno = Integer.parseInt(bon);
        }
        play = playMapper.getPlayByGameIdUserid(gameid,userid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (int i = 0; i < plays.length; i++) {
            if(bonusno==plays[i].getBonus()) return "选择回合助推板错误！";
        }
        String nobon = game.getOtherseed().substring(24);
        if(bonusno==(int)nobon.charAt(0)-47||bonusno==(int)nobon.charAt(1)-47||bonusno==(int)nobon.charAt(2)-47) return "选择回合助推板错误！";
/*        String[][] avahelptile = this.getHelpTileById(gameid);*/
        HaveTt[] hts = otherMapper.getHaveTtByUserid(gameid,userid);
        for (HaveTt ht:hts){
            if(ht.getTtno().equals("att4")){
                int gainvp = 0;
                if(!play.getRl1().equals("0")) gainvp+=3;
                if(!play.getRl2().equals("0")) gainvp+=3;
                if(!play.getRl3().equals("0")) gainvp+=3;if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"att4");
            }
            if(ht.getTtno().equals("att5")){
               HaveTown[] haveTowns = otherMapper.getHTByGameIdUserId(gameid,userid);
            if(haveTowns.length!=0){
                otherMapper.gainVp(gameid,userid,haveTowns.length*3,"att5");
            }
            }
            if(ht.getTtno().equals("att6")){
               int gainvp = this.terratype(gameid,userid);
               otherMapper.gainVp(gameid,userid,gainvp,"att6");
            }
            if(ht.getTtno().equals("att15")&&(game.getGamemode().charAt(0)!='0')&&game.getGamemode().length()==3){
                int gainvp = play.getP3()*2;
                playMapper.updatePlayById(play);
                otherMapper.gainVp(gameid,userid,gainvp,"att15");
            }else if(ht.getTtno().equals("att15")&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)=='1'){
                int min = play.getP1();
                if(play.getP2()<play.getP1()) min = play.getP2();
                if(play.getP3()<min) min = play.getP3();
                otherMapper.gainVp(gameid,userid,min*3,"att15");
            }else if(ht.getTtno().equals("att15")&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='4'){
                otherMapper.gainVp(gameid,userid,getplanets(play,""),"att15");
            }
        }
        switch (passbonus){
            case 6: {
                int gainvp = 0;
                if(!play.getM1().equals("0")) gainvp++;
                if(!play.getM2().equals("0")) gainvp++;
                if(!play.getM3().equals("0")) gainvp++;
                if(!play.getM4().equals("0")) gainvp++;
                if(!play.getM5().equals("0")) gainvp++;
                if(!play.getM6().equals("0")) gainvp++;
                if(!play.getM7().equals("0")) gainvp++;
                if(!play.getM8().equals("0")) gainvp++;
                if(!play.getBlackstar().equals("0")) gainvp++;
                if(gainvp!=0)
                otherMapper.gainVp(gameid,userid,gainvp,"bon6");break;
            }
            case 7:{
                int gainvp = 0;
                if(!play.getTc1().equals("0")) gainvp+=2;
                if(!play.getTc2().equals("0")) gainvp+=2;
                if(!play.getTc3().equals("0")) gainvp+=2;
                if(!play.getTc4().equals("0")) gainvp+=2;  if(gainvp!=0)
                otherMapper.gainVp(gameid,userid,gainvp,"bon7");break;
            }
            case 8:{
                int gainvp = 0;
                if(!play.getRl1().equals("0")) gainvp+=3;
                if(!play.getRl2().equals("0")) gainvp+=3;
                if(!play.getRl3().equals("0")) gainvp+=3;if(gainvp!=0)
                otherMapper.gainVp(gameid,userid,gainvp,"bon8");break;
            }
            case 9:{
                int gainvp = 0;
                if(!play.getSh().equals("0")) gainvp+=4;
                if(!play.getAc1().equals("0")) gainvp+=4;
                if(!play.getAc2().equals("0")) gainvp+=4;if(gainvp!=0)
                otherMapper.gainVp(gameid,userid,gainvp,"bon9");break;
            }
            case 10:{
                int gainvp = this.gaiabuildingcount(play);
                otherMapper.gainVp(gameid,userid,gainvp,"bon10");break;
            }
        }
        switch (passbonus1){
            case 6: {
                int gainvp = 0;
                if(!play.getM1().equals("0")) gainvp++;
                if(!play.getM2().equals("0")) gainvp++;
                if(!play.getM3().equals("0")) gainvp++;
                if(!play.getM4().equals("0")) gainvp++;
                if(!play.getM5().equals("0")) gainvp++;
                if(!play.getM6().equals("0")) gainvp++;
                if(!play.getM7().equals("0")) gainvp++;
                if(!play.getM8().equals("0")) gainvp++;
                if(!play.getBlackstar().equals("0")) gainvp++;
                if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon6");break;
            }
            case 7:{
                int gainvp = 0;
                if(!play.getTc1().equals("0")) gainvp+=2;
                if(!play.getTc2().equals("0")) gainvp+=2;
                if(!play.getTc3().equals("0")) gainvp+=2;
                if(!play.getTc4().equals("0")) gainvp+=2;  if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon7");break;
            }
            case 8:{
                int gainvp = 0;
                if(!play.getRl1().equals("0")) gainvp+=3;
                if(!play.getRl2().equals("0")) gainvp+=3;
                if(!play.getRl3().equals("0")) gainvp+=3;if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon8");break;
            }
            case 9:{
                int gainvp = 0;
                if(!play.getSh().equals("0")) gainvp+=4;
                if(!play.getAc1().equals("0")) gainvp+=4;
                if(!play.getAc2().equals("0")) gainvp+=4;if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon9");break;
            }
            case 10:{
                int gainvp = this.gaiabuildingcount(play);
                otherMapper.gainVp(gameid,userid,gainvp,"bon10");break;
            }
        }
        switch (passbonus2){
            case 6: {
                int gainvp = 0;
                if(!play.getM1().equals("0")) gainvp++;
                if(!play.getM2().equals("0")) gainvp++;
                if(!play.getM3().equals("0")) gainvp++;
                if(!play.getM4().equals("0")) gainvp++;
                if(!play.getM5().equals("0")) gainvp++;
                if(!play.getM6().equals("0")) gainvp++;
                if(!play.getM7().equals("0")) gainvp++;
                if(!play.getM8().equals("0")) gainvp++;
                if(!play.getBlackstar().equals("0")) gainvp++;
                if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon6");break;
            }
            case 7:{
                int gainvp = 0;
                if(!play.getTc1().equals("0")) gainvp+=2;
                if(!play.getTc2().equals("0")) gainvp+=2;
                if(!play.getTc3().equals("0")) gainvp+=2;
                if(!play.getTc4().equals("0")) gainvp+=2;  if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon7");break;
            }
            case 8:{
                int gainvp = 0;
                if(!play.getRl1().equals("0")) gainvp+=3;
                if(!play.getRl2().equals("0")) gainvp+=3;
                if(!play.getRl3().equals("0")) gainvp+=3;if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon8");break;
            }
            case 9:{
                int gainvp = 0;
                if(!play.getSh().equals("0")) gainvp+=4;
                if(!play.getAc1().equals("0")) gainvp+=4;
                if(!play.getAc2().equals("0")) gainvp+=4;if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"bon9");break;
            }
            case 10:{
                int gainvp = this.gaiabuildingcount(play);
                otherMapper.gainVp(gameid,userid,gainvp,"bon10");break;
            }
        }
        playMapper.updateBonusById(gameid,userid,bonusno);
        /*gameMapper.updateRecordById(gameid,play.getRace()+":pass: bon"+bon+".");*/
        if(game.getRound()==0&&play.getPosition()==1){

            this.income(gameid,true);
            gameMapper.roundEnd(gameid);
               return "成功";
        }else if(game.getRound()!=0){
            int passedplayers = playMapper.selectPassNo(gameid);
            if(playMapper.selectPassNoo(gameid,1)==0){ playMapper.updatePassNo(gameid,userid,1);}
            else if(playMapper.selectPassNoo(gameid,2)==0){ playMapper.updatePassNo(gameid,userid,2);}
            else if(playMapper.selectPassNoo(gameid,3)==0){ playMapper.updatePassNo(gameid,userid,3);}
            else {playMapper.updatePassNo(gameid,userid,4);}
                //TODO 结算havett表中的bon，显示到前端
            if(passedplayers==3){
                if(game.getRound()==6) {gameEnd(gameid,true);return "成功";}

                gameMapper.roundEnd(gameid);
                for (Play p:plays){
                    if(!p.getGtu1().equals("0")&&!p.getGtu1().equals("pg")&&otherMapper.getGaia(gameid,p.getGtu1())==0) otherMapper.insertGaia(gameid,p.getGtu1());
                    if(!p.getGtu2().equals("0")&&!p.getGtu2().equals("pg")&&otherMapper.getGaia(gameid,p.getGtu2())==0) otherMapper.insertGaia(gameid,p.getGtu2());
                    if(!p.getGtu3().equals("0")&&!p.getGtu3().equals("pg")&&otherMapper.getGaia(gameid,p.getGtu3())==0) otherMapper.insertGaia(gameid,p.getGtu3());
                }
                this.income(gameid,true);
                playMapper.roundEnd(gameid);
                playMapper.roundEnd2(gameid);
                otherMapper.roundEnd(gameid);
                //盖亚池出来
                plays = playMapper.getPlayByGameId(gameid);
                boolean wait = false;
                for (Play p:plays) {
                    if(p.getRace().equals("人类")&&!p.getSh().equals("0")&&p.getPg()>=1||p.getRace().equals("伊塔星人")&&!p.getSh().equals("0")&&p.getPg()>=4){p.setRacea1(String.valueOf(p.getPg()));wait = true;}
                    if(!wait){
                        if(p.getRace().equals("人类")) {p.setP2(p.getP2()+p.getPg());}else {
                            p.setP1(p.getP1()+p.getPg());
                        }
                        p.setPg(0);
                        if(p.getRace().equals("炽炎族")){
                            if(p.getGtu1().equals("pg")) p.setGtu1("0");
                            if(p.getGtu2().equals("pg")) p.setGtu2("0");
                            if(p.getGtu3().equals("pg")) p.setGtu3("0");
                        }
                        if(p.getRace().equals("利爪族")){
                            if(p.getRacea1().equals("pg")) p.setRacea1("1");
                        }
                    }
                    playMapper.updatePlayById(p);
                }
                if(wait) {
                    game=gameMapper.getGameById(gameid);
                    game.setTurn(0);
                    gameMapper.updateGameById(game);
                }
                return "成功";
            }
        }
        updatePosition(gameid);
        return "成功";
    }

    private int[] gameEnd(String gameid,boolean isend) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        Game game = gameMapper.getGameById(gameid);
        int result[] = new int[4];
        int[][] score = new int[4][6];
        String endscoretile = gameMapper.getGameById(gameid).getOtherseed().substring(0,2);
        int i= 0;
        for (Play p:plays){
            int Terra = p.getTerralv()>=3?4*(p.getTerralv()-2):0;
            int Ship = p.getShiplv()>=3?4*(p.getShiplv()-2):0;
            int Q = p.getQlv()>=3?4*(p.getQlv()-2):0;
            int Gaia = p.getGaialv()>=3?4*(p.getGaialv()-2):0;
            int Eco = p.getEcolv()>=3?4*(p.getEcolv()-2):0;
            int Sci = p.getScilv()>=3?4*(p.getScilv()-2):0;
            score[i][0]= Terra+Ship+Q+Gaia+Eco+Sci;
            int coin = 0;
            if(p.getRace().equals("超星人")){
                coin+=(p.getP3()*2+p.getP2()/2*2+p.getO()+p.getC()+p.getK()+p.getQ());
            }else {
                coin+=(p.getP3()+p.getP2()/2+p.getO()+p.getC()+p.getK()+p.getQ());
            }
            if(p.getRace().equals("利爪族")&&p.getRacea1().equals("3")) coin+=2;
            if(p.getRace().equals("利爪族")&&p.getRacea1().equals("2")&&p.getP2()>=2) coin+=2;
            coin = coin/3;
            score[i][1] = coin;
            for (int end = 0; end < 2; end++) {
                if(endscoretile.charAt(end)=='1'){
                    if (!p.getM1().equals("0")) score[i][end+2]++;
                    if (!p.getM2().equals("0")) score[i][end+2]++;
                    if (!p.getM3().equals("0")) score[i][end+2]++;
                    if (!p.getM4().equals("0")) score[i][end+2]++;
                    if (!p.getM5().equals("0")) score[i][end+2]++;
                    if (!p.getM6().equals("0")) score[i][end+2]++;
                    if (!p.getM7().equals("0")) score[i][end+2]++;
                    if (!p.getM8().equals("0")) score[i][end+2]++;
                    if (!p.getBlackstar().equals("0")) score[i][end+2]++;
                    if (!p.getTc1().equals("0")) score[i][end+2]++;
                    if (!p.getTc2().equals("0")) score[i][end+2]++;
                    if (!p.getTc3().equals("0")) score[i][end+2]++;
                    if (!p.getTc4().equals("0")) score[i][end+2]++;
                    if (!p.getRl1().equals("0")) score[i][end+2]++;
                    if (!p.getRl2().equals("0")) score[i][end+2]++;
                    if (!p.getRl3().equals("0")) score[i][end+2]++;
                    if (!p.getSh().equals("0")) score[i][end+2]++;
                    if (!p.getAc1().equals("0")) score[i][end+2]++;
                    if (!p.getAc2().equals("0")) score[i][end+2]++;
                }
                if(endscoretile.charAt(end)=='2'){
                    score[i][end+2] = otherMapper.zjjf2(gameid,p.getUserid());
                    if(p.getRace().equals("蜂人")){
                        if(!p.getRacea1().equals("0")) score[i][end+2]--;
                        if(!p.getRacea2().equals("0")) score[i][end+2]--;
                        if(!p.getRacea3().equals("0")) score[i][end+2]--;
                        if(!p.getRacea4().equals("0")) score[i][end+2]--;
                        if(!p.getRacea5().equals("0")) score[i][end+2]--;
                        if(!p.getRacea6().equals("0")) score[i][end+2]--;
                    }
                }
                if(endscoretile.charAt(end)=='3'){
                    score[i][end+2] = terratype(gameid,p.getUserid());
                }
                if(endscoretile.charAt(end)=='4'){
                    score[i][end+2] = gaiabuildingcount(p);
                }
                if(endscoretile.charAt(end)=='5'){
                    score[i][end+2] = getplanets(p,"");
                }
                if(endscoretile.charAt(end)=='6'){
                    score[i][end+2] = otherMapper.zjjf6(gameid,p.getUserid());
                    if(p.getRace().equals("蜂人")){
                        if(!p.getRacea1().equals("0")) score[i][end+2]++;
                        if(!p.getRacea2().equals("0")) score[i][end+2]++;
                        if(!p.getRacea3().equals("0")) score[i][end+2]++;
                        if(!p.getRacea4().equals("0")) score[i][end+2]++;
                        if(!p.getRacea5().equals("0")) score[i][end+2]++;
                        if(!p.getRacea6().equals("0")) score[i][end+2]++;
                    }
                }
            }
            i++;
        }
        for (int k=2;k<=3;k++) {
            for (int j = 0; j <= 3; j++) {
                int high = 0;
                int equal = 0;
                int low = 0;
                if (j == 0) {
                    for (int p=1;p<=3;p++){
                        if (score[j][k]>score[p][k]) {high++;}else if(score[j][k]==score[p][k]){
                            equal++;
                        }else {
                            low++;
                        }
                    }
                }
                if (j == 1) {
                    for (int p=0;p<=3;p++){
                        if(p==1) continue;
                        if (score[j][k]>score[p][k]) {high++;}else if(score[j][k]==score[p][k]){
                            equal++;
                        }else {
                            low++;
                        }
                    }
                }
                if (j == 2) {
                    for (int p=0;p<=3;p++){
                        if(p==2) continue;
                        if (score[j][k]>score[p][k]) {high++;}else if(score[j][k]==score[p][k]){
                            equal++;
                        }else {
                            low++;
                        }
                    }
                }
                if (j == 3) {
                    for (int p=0;p<=2;p++){
                        if (score[j][k]>score[p][k]) {high++;}else if(score[j][k]==score[p][k]){
                            equal++;
                        }else {
                            low++;
                        }
                    }
                }
                score[j][k+2] = 9+3*high-3*low;
            }
        }
        for (int j = 0; j < 4; j++) {
            result[j]+=score[j][0];
            result[j]+=score[j][1];
            result[j]+=score[j][4];
            result[j]+=score[j][5];
            if(isend) {
                otherMapper.gainVp(gameid, plays[j].getUserid(), score[j][0], "终局-科技");
                otherMapper.gainVp(gameid, plays[j].getUserid(), score[j][1], "终局-资源");
                otherMapper.gainVp(gameid, plays[j].getUserid(), score[j][4], "终局-计分1");
                otherMapper.gainVp(gameid, plays[j].getUserid(), score[j][5], "终局-计分2");
            }
        }
        if(isend) {
            for (Play p:plays){
                p.setBidvp(otherMapper.getvp(gameid,p.getUserid()));
                playMapper.updatePlayById(p);
            }
            gameMapper.gameEnd(gameid, "游戏结束");
            String[] playid = new String[4];
            playid[0] = plays[0].getUserid();
            playid[1] = plays[1].getUserid();
            playid[2] = plays[2].getUserid();
            playid[3] = plays[3].getUserid();
            for (String s : playid) {
                User user = userMapper.getUser(s);
                ArrayList<Lobby> fgd = playService.showLobby(user.getUserid(), "end");
                int gamesum = 0;
                int scoresum = 0;
                int ranksum = 0;
                for (Lobby l : fgd) {
                    if (gameMapper.getGameById(l.getGameid()).getGamemode().charAt(2) != '3' && gamesum < 50) {
                        gamesum++;
                        String endinfo = l.getRound();
                        int a = 0, b = 0, c = 0, d = 0;
                        for (int k = 4; k < endinfo.length(); k++) {
                            if (endinfo.charAt(k) == '<') a = k;
                            if (endinfo.charAt(k) == 'V') b = k;
                            if (endinfo.charAt(k) == '(') c = k;
                            if (endinfo.charAt(k) == ')') d = k;
                        }
                        scoresum += Integer.parseInt(l.getRound().substring(a + 1, b));
                        ranksum += Integer.parseInt(l.getRound().substring(c + 1, d));
                    }
                }
                if (gamesum >= 15) {
                    String avgrank = String.valueOf((float) ranksum / (float) gamesum);
                    if (avgrank.length() > 4) avgrank = avgrank.substring(0, 4);
                    String avgscore = String.valueOf((float) scoresum / (float) gamesum);
                    if (avgscore.length() > 5) avgscore = avgscore.substring(0, 5);
                    user.setAvgrank(avgrank);
                    user.setAvgscore(avgscore);
                    userMapper.userUpdate(user);
                } else {
                    user.setAvgscore("0");
                    user.setAvgrank("0");
                    userMapper.userUpdate(user);
                }
            }
//            删除上界
//            double[][] staresult = new double[21][9];
//            int a=0;
//            String[] games = gameMapper.getAllGames();
//            for (String gameidd:games){
//                System.out.println(gameidd);
//                Game gamee = gameMapper.getGameById(gameidd);
//                if(gamee.getBlackstar()!=null&&gamee.getBlackstar().equals("游戏结束")){
//                    Play[] plays1 = playMapper.getPlayByGameId(gameidd);
//                    ArrayList<Integer> scores = new ArrayList<>();
//                    for (Play p:plays1){
//                        scores.add(otherMapper.getvp(gameidd,p.getUserid()));
//                    }
//                    for (Play p:plays1){
//                        int raceno = racenummap.get(p.getRace());
//                        int rank = 1;
//                        int vp = otherMapper.getvp(gameidd,p.getUserid());;
//                        for (int s:scores){
//                            if(vp<s) rank++;
//                        }
//                        vp+=(10-otherMapper.getiniVpByUserid(gameidd,p.getUserid()));
//                        if(vp>100){
//                            if(gamee.getGamemode().charAt(0)=='0'&&gamee.getGamemode().charAt(2)!='3'){
//                                staresult[raceno][0]++;
//                                staresult[raceno][1]+=rank;
//                                staresult[raceno][2]+=vp;
//                            }
//                            if(gamee.getGamemode().charAt(0)!='0'&&gamee.getGamemode().charAt(2)!='3'){
//                                staresult[raceno][3]++;
//                                staresult[raceno][4]+=rank;
//                                staresult[raceno][5]+=vp;
//                            }
//                            if(gamee.getGamemode().charAt(0)!='0'&&gamee.getGamemode().charAt(2)!='3'&&gamee.getGamemode().length()>=7&&gamee.getGamemode().charAt(4)=='6'){
//                                staresult[raceno][6]++;
//                                staresult[raceno][7]+=rank;
//                                staresult[raceno][8]+=vp;
//                            }
//                        }
//                    }
//                }
//            }
//            System.out.println("呵呵呵");
//            for (int g=0;g<=13;g++){
//                System.out.println(staresult[g][1]/staresult[g][0]);
//                otherMapper.insertInfo(racename[g],staresult[g][0],staresult[g][1]/staresult[g][0],staresult[g][2]/staresult[g][0],staresult[g][3],staresult[g][4]/staresult[g][3],staresult[g][5]/staresult[g][3],staresult[g][6],staresult[g][7]/staresult[g][6],staresult[g][8]/staresult[g][6]);
//            }
//删除下界


playService.executeEvictCache();
        }
        return result;
    }

    private int gaiabuildingcount(Play play) {
        String[][] mapdetail = new String[21][15];
        this.setMapDetail(mapdetail,play.getGameid());
        List<String> list = new ArrayList<>();
        int result = 0;
        boolean[][] jisheng = this.getJisheng(play.getGameid());
        list.add(play.getM1());list.add(play.getM2());list.add(play.getM3());list.add(play.getM4());list.add(play.getM5());list.add(play.getM6());
        list.add(play.getM7());list.add(play.getM8());list.add(play.getTc1());list.add(play.getTc2());list.add(play.getTc3());list.add(play.getTc4());
        list.add(play.getRl1());list.add(play.getRl2());list.add(play.getRl3());list.add(play.getSh());list.add(play.getAc1());list.add(play.getAc2());
        for (String str:list) {
            if(!str.equals("0")){
                char row = str.charAt(0);
                String column = str.substring(1);
                int rowint = row-64;
                int columnint = Integer.parseInt(column);
                if(play.getRace().equals("亚特兰斯星人")&&jisheng[rowint][columnint]) continue;
                if(mapdetail[rowint][columnint].equals(ga)) result++;
            }

        }
       return result;
    }

    @Override
    public String[][][] getScienceGrade(String gameid) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        Arrays.sort(plays, new Comparator<Play>() {
            @Override
            public int compare(Play o1, Play o2) {
                if(o1.getPass()==0&&o2.getPass()!=0) return -1;
                if(o1.getPass()!=0&&o2.getPass()==0) return 1;
                if(o1.getPass()!=0&&o2.getPass()!=0) return o1.getPass()-o2.getPass();
                return o1.getPosition()-o2.getPosition();
            }
        });
        String[][][] result = new String[4][6][8];
        for (int i = 0;i< 4 ;i++){
            Play play = plays[i];
            if (play.getTerralv()==0) result[i][0][7]="a";
            if (play.getTerralv()==1) result[i][0][6]="a";
            if (play.getTerralv()==2) result[i][0][5]="a";
            if (play.getTerralv()==3) result[i][0][3]="a";
            if (play.getTerralv()==4) result[i][0][2]="a";
            if (play.getTerralv()==5) result[i][0][0]="a";
            if (play.getShiplv()==0) result[i][1][7]="a";
            if (play.getShiplv()==1) result[i][1][6]="a";
            if (play.getShiplv()==2) result[i][1][5]="a";
            if (play.getShiplv()==3) result[i][1][3]="a";
            if (play.getShiplv()==4) result[i][1][2]="a";
            if (play.getShiplv()==5) result[i][1][0]="a";
            if (play.getQlv()==0) result[i][2][7]="a";
            if (play.getQlv()==1) result[i][2][6]="a";
            if (play.getQlv()==2) result[i][2][5]="a";
            if (play.getQlv()==3) result[i][2][3]="a";
            if (play.getQlv()==4) result[i][2][2]="a";
            if (play.getQlv()==5) result[i][2][0]="a";
            if (play.getGaialv()==0) result[i][3][7]="a";
            if (play.getGaialv()==1) result[i][3][6]="a";
            if (play.getGaialv()==2) result[i][3][5]="a";
            if (play.getGaialv()==3) result[i][3][3]="a";
            if (play.getGaialv()==4) result[i][3][2]="a";
            if (play.getGaialv()==5) result[i][3][0]="a";
            if (play.getEcolv()==0) result[i][4][7]="a";
            if (play.getEcolv()==1) result[i][4][6]="a";
            if (play.getEcolv()==2) result[i][4][5]="a";
            if (play.getEcolv()==3) result[i][4][3]="a";
            if (play.getEcolv()==4) result[i][4][2]="a";
            if (play.getEcolv()==5) result[i][4][0]="a";
            if (play.getScilv()==0) result[i][5][7]="a";
            if (play.getScilv()==1) result[i][5][6]="a";
            if (play.getScilv()==2) result[i][5][5]="a";
            if (play.getScilv()==3) result[i][5][3]="a";
            if (play.getScilv()==4) result[i][5][2]="a";
            if (play.getScilv()==5) result[i][5][0]="a";
        }
        return result;
    }

    @Override
    public Power[] getPowerLeech(String gameid) {
        Power[] p = otherMapper.getAllPowerById(gameid);
        for (Power pp : p){
            pp.setUserid(playMapper.getUseridByRace(gameid,pp.getReceiverace()));
            pp.setActually(-1);
            Play play = playMapper.getPlayByGameIdRace(gameid,pp.getReceiverace());
            if(pp.getReceiverace().equals("利爪族")&&!play.getSh().equals("0")){
                pp.setActually(-2);
            }else {
                int maxleech = play.getP1()*2+play.getP2();
                int power = pp.getPower();
                if(maxleech>power) maxleech = power;
                int totalvp = otherMapper.getvp(gameid,pp.getUserid());
                int inivp = otherMapper.getiniVpByUserid(gameid,pp.getUserid());
                Game game = gameMapper.getGameById(gameid);
                if(totalvp-inivp+11<maxleech){
                    maxleech = totalvp-inivp+11;
                }
                if(maxleech!=pp.getPower()) pp.setActually(maxleech);
            }
        }
        return p;
    }

    public void incomePower(Play play,int num){
        for (int i=1;i<=num;i++){
            if(play.getP1()!=0) {
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("1")) play.setRacea1("2");
                play.setP1(play.getP1()-1);
                play.setP2(play.getP2()+1);
            }else if(play.getP2()!=0){
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("2")) play.setRacea1("3");
                play.setP2(play.getP2()-1);
                play.setP3(play.getP3()+1);
            }
        }
    }
    @Override
    public String leechPower(String gameid,String giverace,String receiverace, String location, String structure, String accept) {
        Power p = otherMapper.getPowerById(gameid,giverace,receiverace,location,structure);
        Power[] ps = otherMapper.getPowerByGameIdUserId(gameid,receiverace);
        if(p == null || ps.length ==0) return "吸魔错误";
        if(p.getNum()!=ps[0].getNum()) return "请按顺序蹭魔";
        String userid = playMapper.getUseridByRace(gameid,receiverace);
        if(p==null) return "蹭魔错误！";
        if(accept.equals("1")||accept.equals("2")) {
            Play play = playMapper.getPlayByGameIdRace(gameid,receiverace);
            int power = p.getPower();
            int totalvp = otherMapper.getvp(gameid,userid);
            int inivp = otherMapper.getiniVpByUserid(gameid,userid);
            Game game = gameMapper.getGameById(gameid);
            //时间版本相关
            if(game.getCreatetime()!=null){
                if(totalvp-inivp+11<power){
                    power = totalvp-inivp+11;
                }
            }
            int rpower = power;
            int p1 = play.getP1();
            int p2 = play.getP2();
            int p3 = play.getP3();
            if(accept.equals("1")&&(play.getRace().equals("利爪族")&&!play.getSh().equals("0"))) p1++;
            while(power!=0&&p1!=0){
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("1")) play.setRacea1("2");
                power--;
                p1--;
                p2++;
            }
            while(power!=0&&p2!=0){
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("2")) play.setRacea1("3");
                power--;
                p2--;
                p3++;
            }
            if(accept.equals("2")&&(play.getRace().equals("利爪族")&&!play.getSh().equals("0"))&&rpower!=power) p1++;
            int vp =(rpower-power-1);
            if(play.getRace().equals("织女星人")&&!play.getSh().equals("0")) {vp = 0;}
            if(vp<0) vp=0;
            if(play.getRace().equals("殖民者")&&!play.getSh().equals("0")){
                if(distance(play.getSh(),location)<=2||distance(play.getAc1(),location)<=2||distance(play.getAc2(),location)<=2){
                    HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,play.getUserid(),"ltt8");
                    if(tt != null && !tt.getTtstate().equals("被覆盖"))
                    {if(vp>3)vp=3;}
                    else {
                        if(vp>2){vp=2;}
                    }
                }else if(distance(play.getTc1(),location)<=2||distance(play.getTc2(),location)<=2||distance(play.getTc3(),location)<=2||distance(play.getTc4(),location)<=2||distance(play.getRl1(),location)<=2||distance(play.getRl2(),location)<=2||distance(play.getRl3(),location)<=2){
                    if(vp>1){vp=1;}
                }else{
                    vp=0;
                }
            }
            if(vp!=0){
                otherMapper.gainVp(gameid,userid,-vp,"蹭魔");
            }
            playMapper.updatePlayById(play);
            playMapper.updatePowerOld(gameid,userid,p1,p2,p3,play.getPg());
            String[] records = gameMapper.getRecordById(gameid).split("\\.");
            StringBuffer record = new StringBuffer();
            for(int i=records.length-1;i>=records.length-20;i--){
                if(structure.equals("M")){
                    if(records[i].contains("build")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"蹭"+(rpower-power)+"魔)";break;}
                    if(records[i].contains("actiont")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"蹭"+(rpower-power)+"魔)";break;}
                }else if(structure.equals("BlackStar")){
                    if(records[i].contains("ship")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"蹭"+(rpower-power)+"魔)";break;}
                }else if(structure.equals("ZTC")){
                    if(records[i].contains("actionz")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"蹭"+(rpower-power)+"魔)";break;}
                }else {
                    if(records[i].contains("upgrade")&&records[i].contains(structure)&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"蹭"+(rpower-power)+"魔)";break;}
                }
            }
            for (String str:records){
                record.append(str+".");
            }
            gameMapper.updateLeechRecordById(gameid,record.toString());
        }else {
            StringBuffer record = new StringBuffer();
            String[] records = gameMapper.getRecordById(gameid).split("\\.");
            for(int i=records.length-1;i>=records.length-20;i--){
                if(structure.equals("M")){
                    if(records[i].contains("build")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"拒绝)";break;}
                }else if(structure.equals("BlackStar")){
                    if(records[i].contains("ship")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"拒绝)";break;}
                }else if(structure.equals("ZTC")){
                    if(records[i].contains("actionz")&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"拒绝)";break;}
                }else {
                    if(records[i].contains("upgrade")&&records[i].contains(structure)&&records[i].contains(location)&&records[i].substring(0,11).contains(giverace)) {records[i]+="("+receiverace+"拒绝)";break;}
                }
            }
            for (String str:records){
                record.append(str+".");
            }
            gameMapper.updateLeechRecordById(gameid,record.toString());
        }
        otherMapper.deletePowerById(gameid,giverace,receiverace,location,structure);
        return null;
    }

    @Override
    public String upgrade(String gameid, String userid, String substring){
        String[] strs = substring.split(" ");
        if(!strs[1].equals("to")||(!strs[2].equals("tc")&&!strs[2].equals("rl")&&!strs[2].equals("sh")&&!strs[2].equals("ac1")&&!strs[2].equals("ac2"))) return "操作不合法！";
        Game game = gameMapper.getGameById(gameid);
        String[][] structureSituation = this.getStructureSituationById(gameid);
        char row = strs[0].charAt(0);
        if(strs[0].length()<2||strs[0].length()>3||row<65||row>84) return "操作不合法！";
        String column = strs[0].substring(1);
        if(!column.equals("1")&&!column.equals("2")&&!column.equals("3")&&!column.equals("4")&&!column.equals("5")&&!column.equals("6")
                &&!column.equals("7")&&!column.equals("8")&&!column.equals("9")&&!column.equals("10")&&!column.equals("11")&&!column.equals("12")) return "操作不合法！";
        int rowint = row-64;
        int columnint = Integer.parseInt(column);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        try {
        if(strs[2].equals("tc")){
            if(structureSituation[rowint][columnint]==null||!structureSituation[rowint][columnint].equals("m")) return "操作不合法！";
            boolean[][] jisheng = getJisheng(gameid);
            if(play.getRace().equals("亚特兰斯星人")&&jisheng[rowint][columnint]) return "操作不合法！";
            int mno = 0;
            int tcno = 0;
            Class playclass = Play.class;
                for (int i = 1; i <= 8; i++) {
                    Method method = playclass.getMethod("getM" + String.valueOf(i));
                    String location = (String) method.invoke(play);
                    if (location.equals(strs[0])) {mno = i;break;}
                }
               for (int i = 1; i <= 4; i++) {
                   Method method = playclass.getMethod("getTc" + String.valueOf(i));
                   String location = (String) method.invoke(play);
                   if (location.equals("0")) {
                       tcno = i;
                       break;
                   }
               }
                if (mno == 0||tcno == 0) {
                    return "操作不合法！";
                }
                if(mno==1&&play.getRace().equals("殖民者"))  return "殖民船无法升级！";
                if (play.getO() < 2 || play.getC() < 3) return "资源不足！";
                if(!tc3or6(gameid,userid,strs[0])&&play.getC() < 6) return "资源不足！";

                //计分
               String[] rs = this.getRoundScoreById(gameid);
               if(rs[game.getRound()-1].equals("TC>>3")) {otherMapper.gainVp(gameid,userid,3,"TC>>3"); xizu(play,"TC");}
               if(rs[game.getRound()-1].equals("TC>>4")) {otherMapper.gainVp(gameid,userid,4,"TC>>4"); xizu(play,"TC");}
            if(otherMapper.getTtByGameidUseridTtno(gameid,userid,"att8")!=null) otherMapper.gainVp(gameid,userid,3,"att8");
                Method method = playclass.getMethod("setM" + String.valueOf(mno), String.class);
                method.invoke(play,"0");
                method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
                method.invoke(play,strs[0]);
                if(tc3or6(gameid,userid,strs[0])) {play.setC(play.getC()-3);play.setO(play.getO()-2);}
                if(!tc3or6(gameid,userid,strs[0])) {play.setC(play.getC()-6);play.setO(play.getO()-2);}
                playMapper.updatePlayById(play);
            }else
        if(strs[2].equals("rl")){
            if(!structureSituation[rowint][columnint].equals("tc")||strs.length!=6&&strs.length!=7||(!strs[3].equals("advance")&&!strs[4].equals("advance")&&!strs[5].equals("advance"))) return "操作不合法！";
            int tcno = 0;
            int rlno = 0;
            Class playclass = Play.class;
            for (int i = 1; i <= 4; i++) {
                Method method = playclass.getMethod("getTc" + String.valueOf(i));
                String location = (String) method.invoke(play);
                if (location.equals(strs[0])) {tcno = i;break;}
            }
            for (int i = 1; i <= 3; i++) {
                Method method = playclass.getMethod("getRl" + String.valueOf(i));
                String location = (String) method.invoke(play);
                if (location.equals("0")) {
                    rlno = i;
                    break;
                }
            }
            if (rlno == 0||tcno == 0) {
                return "操作不合法！";
            }
            boolean ok = false;
            if (play.getO() < 3 || play.getC() < 5) return "资源不足！";
            if(strs.length==6){//两种方式升级RL迎合个人习惯
                if(strs[3].charAt(0)=='+'){
                    ok = this.takett(gameid,userid,strs[3].substring(1),strs[5]);
                }else {
                    ok = this.takett(gameid,userid,strs[5].substring(1),strs[4]);
                }
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }else if(strs.length==7){
                if(strs[3].charAt(0)=='+'){
                    ok = this.takeatt(gameid,userid,strs[3].substring(1),strs[4].substring(1),strs[6],"rl");
                }else {
                    ok = this.takeatt(gameid,userid,strs[5].substring(1),strs[6].substring(1),strs[4],"rl");
                }
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }
            if(ok){
                play.setO(play.getO()-3);
                play.setC(play.getC()-5);
                Method method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
                method.invoke(play,"0");
                method = playclass.getMethod("setRl" + String.valueOf(rlno), String.class);
                method.invoke(play,strs[0]);
                playMapper.updatePlayById(play);
            }else{return "操作不合法！";}
        }else
        if(strs[2].equals("sh")){
            if(play.getRace().equals("疯狂机器")){
                if (!structureSituation[rowint][columnint].equals("rl") || !play.getSh().equals("0")) return "操作不合法！";
                Class playclass = Play.class;
                int rlno = 0;
                for (int i = 1; i <= 3; i++) {
                    Method method = playclass.getMethod("getRl" + String.valueOf(i));
                    String location = (String) method.invoke(play);
                    if (location.equals(strs[0])) {
                        rlno = i;
                        break;
                    }
                }
                if (rlno == 0) {
                    return "操作不合法！";
                }
                if (play.getO() < 4 || play.getC() < 6) return "资源不足！";
                String[] rs = this.getRoundScoreById(gameid);
                if (rs[game.getRound() - 1].equals("SH/AC>>5")) otherMapper.gainVp(gameid, userid, 5, "SH/AC>>5");
                play.setO(play.getO() - 4);
                play.setC(play.getC() - 6);
                play.setSh(strs[0]);
                Method method = playclass.getMethod("setRl" + String.valueOf(rlno), String.class);
                method.invoke(play, "0");
            }else {
                if (!structureSituation[rowint][columnint].equals("tc") || !play.getSh().equals("0")) return "操作不合法！";
                Class playclass = Play.class;
                int tcno = 0;
                for (int i = 1; i <= 4; i++) {
                    Method method = playclass.getMethod("getTc" + String.valueOf(i));
                    String location = (String) method.invoke(play);
                    if (location.equals(strs[0])) {
                        tcno = i;
                        break;
                    }
                }
                if (tcno == 0) {
                    return "操作不合法！";
                }
                if (play.getO() < 4 || play.getC() < 6) return "资源不足！";
                String[] rs = this.getRoundScoreById(gameid);
                if (play.getRace().equals("章鱼人")) otherMapper.insertHaveTt(gameid, userid, "actionz", "可用");
                if (play.getRace().equals("魔族")) play.setK(play.getK()+4);
                if (play.getRace().equals("大使星人")) otherMapper.insertHaveTt(gameid, userid, "actiond", "可用");
                if (play.getRace().equals("天龙星人")) otherMapper.insertHaveTt(gameid, userid, "actiont", "可用");
                if (play.getRace().equals("猎户星人")) otherMapper.insertHaveTt(gameid, userid, "actionl", "可用");
                if (play.getRace().equals("殖民者")) otherMapper.insertHaveTt(gameid, userid, "actionp", "可用");
                if (play.getRace().equals("格伦星人")) {
                    if(game.getGamemode().charAt(0)!='0'){
                        otherMapper.gainVp(gameid,userid,4,"Town");
                        if(rs[game.getRound()-1].equals("TOWN>>5")) otherMapper.gainVp(gameid,userid,5,"TOWN>>5");
                        otherMapper.insertHT(gameid,userid,71,"可用");
                    }else{
                        if(rs[game.getRound()-1].equals("TOWN>>5")) otherMapper.gainVp(gameid,userid,5,"TOWN>>5");
                        otherMapper.insertHT(gameid, userid, 7, "可用");
                    }
                    play.setO(play.getO() + 1);
                    play.setK(play.getK() + 1);
                    play.setC(play.getC() + 2);
                }
                play.setSh(strs[0]);
                if (rs[game.getRound() - 1].equals("SH/AC>>5")) {otherMapper.gainVp(gameid, userid, 5, "SH/AC>>5"); xizu(play,"SH");};
                play.setO(play.getO() - 4);
                play.setC(play.getC() - 6);
                Method method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
                method.invoke(play, "0");
            }
            playMapper.updatePlayById(play);
            if (play.getRace().equals("炽炎族")&&game.getGamemode().charAt(0)!='0') advance(gameid,userid,"ship",false);
        }else
        if(strs[2].equals("ac1")||strs[2].equals("ac2")){
            if(strs[2].equals("ac1")&&!play.getAc1().equals("0"))return "操作不合法";
            if(strs[2].equals("ac2")&&!play.getAc2().equals("0"))return "操作不合法";
            Class playclass = Play.class;
            int rlno = 0; int tcno = 0;
            if(play.getRace().equals("疯狂机器")){
                if(!structureSituation[rowint][columnint].equals("tc")||strs.length!=6&&strs.length!=7||!strs[3].equals("advance")) return "操作不合法！";
                for (int i = 1; i <= 4; i++) {
                    Method method = playclass.getMethod("getTc" + String.valueOf(i));
                    String location = (String) method.invoke(play);
                    if (location.equals(strs[0])) {
                        tcno = i;
                        break;
                    }
                }
                if (tcno == 0) {
                    return "操作不合法！";
                }
            }else {
                if(!structureSituation[rowint][columnint].equals("rl")||strs.length!=6&&strs.length!=7||(!strs[3].equals("advance")&&!strs[4].equals("advance")&&!strs[5].equals("advance"))) return "操作不合法！";
                for (int i = 1; i <= 3; i++) {
                    Method method = playclass.getMethod("getRl" + String.valueOf(i));
                    String location = (String) method.invoke(play);
                    if (location.equals(strs[0])) {
                        rlno = i;
                        break;
                    }
                }
                if (rlno == 0) {
                    return "操作不合法！";
                }
            }
            boolean ok = false;
            if (play.getO() < 6 || play.getC() < 6) return "资源不足！";
            if(strs.length==6){//两种方式升级RL迎合个人习惯
                if(strs[3].charAt(0)=='+'){
                    ok = this.takett(gameid,userid,strs[3].substring(1),strs[5]);
                }else {
                    ok = this.takett(gameid,userid,strs[5].substring(1),strs[4]);
                }
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }else if(strs.length==7){
                if(strs[3].charAt(0)=='+'){
                    ok = this.takeatt(gameid,userid,strs[3].substring(1),strs[4].substring(1),strs[6],"ac");
                }else {
                    ok = this.takeatt(gameid,userid,strs[5].substring(1),strs[6].substring(1),strs[4],"ac");
                }
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }
            if(ok){
                play.setO(play.getO()-6);
                play.setC(play.getC()-6);
                if(play.getRace().equals("疯狂机器")) {
                    Method method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
                    method.invoke(play,"0");
                }else{
                    Method method = playclass.getMethod("setRl" + String.valueOf(rlno), String.class);
                    method.invoke(play,"0");
                }
                if(strs[2].equals("ac1"))play.setAc1(strs[0]);
                if(strs[2].equals("ac2")){play.setAc2(strs[0]);otherMapper.insertHaveTt(gameid,userid,"ac2","可用");}
                String[] rs = this.getRoundScoreById(gameid);
                if(rs[game.getRound()-1].equals("SH/AC>>5")) {otherMapper.gainVp(gameid,userid,5,"SH/AC>>5");xizu(play,"SH");}
                playMapper.updatePlayById(play);
            }else{return "操作不合法！";}
        }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        createPower(gameid,userid,strs[0],strs[2]);
        updatePosition(gameid);
        if(play.getRace().equals("殖民者")) otherMapper.lttfugaiundo(gameid,userid,"actionr");
        return "成功";
    }

    @Override
    public int[][] income(String gameid,boolean b) {
        //b为flase仅显示，b为true就进行收入
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        int[][] bc = this.getBuildingcount(gameid);
        int[][] income = new int[4][6];
        int i = 0;
        String mode = game.getGamemode();
        for (Play p:plays){
            int getpower = 0;
            int getpowerbean = 0;
            HaveTt[] haveTts = otherMapper.getHaveTtByUserid(gameid,p.getUserid());
            switch (bc[i][0]){
                case 0: income[i][0]+=1;break;
                case 1: income[i][0]+=2;break;
                case 2: income[i][0]+=3;break;
                case 3: income[i][0]+=3;break;
                case 4: income[i][0]+=4;break;
                case 5: income[i][0]+=5;break;
                case 6: income[i][0]+=6;break;
                case 7: income[i][0]+=7;break;
                case 8: income[i][0]+=8;break;
            }
            if(p.getRace().equals("熊猫人")) {income[i][0]--;}
            if(p.getRace().equals("亚特兰斯星人")&&((bc[i][0]>=3&&game.getGamemode().charAt(0)!='0'&&!(mode.length()==7&&mode.charAt(4)>='5')))){
                income[i][0]++;
            }
            if(p.getRace().equals("魔族")){
                income[i][2] = income[i][0] - 1;
                income[i][0] = 1;
            }
            for (HaveTt ht : haveTts){
                if(ht.getTtno().equals("ltt1")&&ht.getTtstate().equals("可用")) income[i][1]+=4;
                if(ht.getTtno().equals("ltt3")&&ht.getTtstate().equals("可用")) {income[i][1]+=1;income[i][2]+=1;}
                if(ht.getTtno().equals("ltt4")&&ht.getTtstate().equals("可用")) {income[i][0]+=1;getpower++;}

            }
            if((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='2'&&p.getRace().equals("疯狂机器")) income[i][2]++;
            if(p.getRace().equals("疯狂机器")){
                switch (bc[i][1]){
                    case 0: income[i][2]+=0;break;
                    case 1: income[i][2]+=1;break;
                    case 2: income[i][2]+=2;break;
                    case 3: income[i][2]+=3;break;
                    case 4: income[i][2]+=4;break;
                }
            }else{
                switch (bc[i][1]){
                    case 0: income[i][1]+=0;break;
                    case 1: income[i][1]+=3;break;
                    case 2: income[i][1]+=7;break;
                    case 3: income[i][1]+=11;break;
                    case 4: income[i][1]+=16;break;
                }
            }

            if(p.getRace().equals("超星人")){
                switch(bc[i][2]){
                    case 0: income[i][2]+=1;break;
                    case 1: income[i][2]+=1;getpower+=2;break;
                    case 2: income[i][2]+=1;getpower+=4;break;
                    case 3: income[i][2]+=1;getpower+=6;break;
                }
            }else if( p.getRace().equals("疯狂机器")) {
                switch(bc[i][2]){
                    case 0: income[i][1]+=0;break;
                    case 1: income[i][1]+=3;break;
                    case 2: income[i][1]+=7;break;
                    case 3: income[i][1]+=12;break;
                }
            }else if( p.getRace().equals("魔族")) {
                income[i][2]+=1;
                switch(bc[i][2]){
                    case 0: income[i][0]+=0;break;
                    case 1: income[i][0]+=2;break;
                    case 2: income[i][0]+=4;break;
                    case 3: income[i][0]+=6;break;
                }
            }else{
                switch(bc[i][2]){
                        case 0: income[i][2]+=1;break;
                        case 1: income[i][2]+=2;break;
                        case 2: income[i][2]+=3;break;
                        case 3: income[i][2]+=4;break;
                                }
                }
            if(p.getPass()!=0||game.getRound()==0){
                switch (p.getBonus()){
                    case 1: income[i][1]+=2;break;
                    case 2: getpower+=2;break;
                    case 3: income[i][1]+=2;income[i][3]+=1;break;
                    case 4: income[i][0]+=1;income[i][2]+=1;break;
                    case 5: income[i][0]+=1;getpowerbean+=2;break;
                    case 6: income[i][0]+=1;break;
                    case 7: income[i][0]+=1;break;
                    case 8: income[i][2]+=1;break;
                    case 9: getpower+=4;break;
                    case 10:income[i][1]+=4;break;
                }
                if(p.getRace().equals("熊猫人")&&!p.getRacea1().equals("")){
                    switch (Integer.parseInt(p.getRacea1())){
                        case 1: income[i][1]+=2;break;
                        case 2: getpower+=2;break;
                        case 3: income[i][1]+=2;income[i][3]+=1;break;
                        case 4: income[i][0]+=1;income[i][2]+=1;break;
                        case 5: income[i][0]+=1;getpowerbean+=2;break;
                        case 6: income[i][0]+=1;break;
                        case 7: income[i][0]+=1;break;
                        case 8: income[i][2]+=1;break;
                        case 9: getpower+=4;break;
                        case 10:income[i][1]+=4;break;
                    }
                }
                if(p.getRace().equals("熊猫人")&&!p.getRacea2().equals("")){
                    switch (Integer.parseInt(p.getRacea2())){
                        case 1: income[i][1]+=2;break;
                        case 2: getpower+=2;break;
                        case 3: income[i][1]+=2;income[i][3]+=1;break;
                        case 4: income[i][0]+=1;income[i][2]+=1;break;
                        case 5: income[i][0]+=1;getpowerbean+=2;break;
                        case 6: income[i][0]+=1;break;
                        case 7: income[i][0]+=1;break;
                        case 8: income[i][2]+=1;break;
                        case 9: getpower+=4;break;
                        case 10:income[i][1]+=4;break;
                    }
                }
            }
            if(!p.getSh().equals("0")){
                if(p.getRace().equals("亚特兰斯星人")){
                    getpower+=4;
                }else if(p.getRace().equals("大使星人")||p.getRace().equals("疯狂机器")){
                    getpowerbean+=2;
                    getpower+=4;
                }else if(p.getRace().equals("翼空族")){
                    income[i][3]++;
                    getpower+=4;
                }else if(p.getRace().equals("格伦星人")){
                    income[i][0]++;
                    getpower+=4;
                }else if(p.getRace().equals("圣禽族")&&game.getGamemode().charAt(0)!='0'){
                    if(game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='6'||game.getGameId().equals("丧1吊打局")){
                        getpower+=4;
                        getpowerbean++;
                    }else if(game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='5'){
                        income[i][1]+=1;
                        getpower+=4;
                        getpowerbean++;
                    }else if(game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='4'){
                        income[i][1]+=2;
                        getpower+=4;
                        getpowerbean++;
                    }else{
                        income[i][1]+=5;
                        getpowerbean++;
                    }
                }else if(p.getRace().equals("魔族")){
                    income[i][2]+=1;
                    getpower+=4;
                    getpowerbean++;
                }
                else if(p.getRace().equals("蜂人")&&(game.getGamemode().charAt(0)!='0')){
                    getpower+=2;
                    getpowerbean++;
                }else if(p.getRace().equals("熊猫人")){
                }else if(p.getRace().equals("混沌法师")){
                    if(p.getRacea1().equals("")) income[i][0]++;
                    if(p.getRacea2().equals("")) income[i][1]+=3;
                    if(p.getRacea3().equals("")) income[i][2]++;
                    if(p.getRacea4().equals("")) getpowerbean++;getpower+=1;
                    if(p.getRacea5().equals("")) getpower+=3;
                }
                else {
                    getpowerbean++;
                    getpower+=4;
                }
            }
            if(!p.getAc1().equals("0")){
                if(p.getRace().equals("伊塔星人")){
                    income[i][2]+=3;
                }else{
                    income[i][2]+=2;
                }
            }

            if(p.getRace().equals("伊塔星人")) getpowerbean++;
            if(p.getRace().equals("章鱼人")) income[i][2]++;
            if(p.getRace().equals("大使星人")) income[i][0]++;
            if(p.getRace().equals("晶矿星人")&&(game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')) income[i][0]++;
            if(p.getRace().equals("圣禽族")) {
                if(game.getGamemode().length()>=5&&game.getGamemode().charAt(4)>='6'){income[i][1]+=4;}else {
                    income[i][1]+=3;
                }
            }
            if(p.getRace().equals("猎户星人")) {
                income[i][1]+=3;
            }
            if(p.getRace().equals("蜂人")) income[i][3]++;

            switch (p.getEcolv()){
                case 0: break;
                case 1: income[i][1]+=2;getpower++;break;
                case 2: income[i][0]+=1;income[i][1]+=2;getpower+=2;break;
                case 3: income[i][0]+=1;income[i][1]+=3;getpower+=3;break;
                case 4: income[i][0]+=2;income[i][1]+=4;getpower+=4;break;
                case 5: break;
            }
            switch (p.getScilv()){
                case 0: break;
                case 1: income[i][2]+=1;break;
                case 2: income[i][2]+=2;break;
                case 3: income[i][2]+=3;break;
                case 4: income[i][2]+=4;break;
                case 5: break;
            }
            income[i][4] = getpower;
            income[i][5] = getpowerbean;
            if(b){
                int p1 = p.getP1();
                int p2 = p.getP2();
                int p3 = p.getP3();
                while(getpower!=0){
                    if(p1!=0) {if(p.getRace().equals("利爪族")&&p.getRacea1().equals("1")) p.setRacea1("2"); p1--;p2++;getpower--;}
                    else if(p2!=0){if(p.getRace().equals("利爪族")&&p.getRacea1().equals("2")) p.setRacea1("3"); p2--;p3++;getpower--;}
                    else if(getpowerbean!=0) {getpowerbean--;p1++;}
                    else break;
                }
                p1+=getpowerbean;
                p.setP1(p1);
                p.setP2(p2);
                p.setP3(p3);
                p.setO(p.getO()+income[i][0]);
                p.setC(p.getC()+income[i][1]);
                p.setK(p.getK()+income[i][2]);
                p.setQ(p.getQ()+income[i][3]);
                playMapper.updatePlayById(p);
            }
            i++;
        }
        return income;
    }

    @Override
    public int[][] getBuildingcount(String gameid) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        int[][] result = new int[4][6];
        int i = 0;
        for (Play p:plays){
            if (!p.getM1().equals("0")) result[i][0]++;
            if (!p.getM2().equals("0")) result[i][0]++;
            if (!p.getM3().equals("0")) result[i][0]++;
            if (!p.getM4().equals("0")) result[i][0]++;
            if (!p.getM5().equals("0")) result[i][0]++;
            if (!p.getM6().equals("0")) result[i][0]++;
            if (!p.getM7().equals("0")) result[i][0]++;
            if (!p.getM8().equals("0")) result[i][0]++;
            if (!p.getTc1().equals("0")) result[i][1]++;
            if (!p.getTc2().equals("0")) result[i][1]++;
            if (!p.getTc3().equals("0")) result[i][1]++;
            if (!p.getTc4().equals("0")) result[i][1]++;
            if (!p.getRl1().equals("0")) result[i][2]++;
            if (!p.getRl2().equals("0")) result[i][2]++;
            if (!p.getRl3().equals("0")) result[i][2]++;
            if (!p.getSh().equals("0")) result[i][3]++;
            if (!p.getAc1().equals("0")) result[i][4]++;
            if (!p.getAc2().equals("0")) result[i][5]++;
        i++;
        }
        return result;
    }

    @Override
    public boolean takett(String gameid, String userid, String techtile, String science) {
        Play play  = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(techtile.equals("ltt0")) return true;
        String ltt = gameMapper.getGameById(gameid).getOtherseed().split(" ")[2];
        if(!techtile.substring(0,3).equals("ltt")||!science.equals("terra")&&!science.equals("gaia")&&!science.equals("q")&&!science.equals("eco")&&!science.equals("sci")&&!science.substring(0,4).equals("ship")) return false;
        if(techtile.charAt(3)<=48||techtile.charAt(3)>=58||techtile.length()!=4) {return false;}
        else{
            if(ltt.charAt(0)==techtile.charAt(3)&&!science.equals("terra")) return false;
            if(ltt.charAt(1)==techtile.charAt(3)&&!science.substring(0,4).equals("ship")) return false;
            if(ltt.charAt(2)==techtile.charAt(3)&&!science.equals("q")) return false;
            if(ltt.charAt(3)==techtile.charAt(3)&&!science.equals("gaia")) return false;
            if(ltt.charAt(4)==techtile.charAt(3)&&!science.equals("eco")) return false;
            if(ltt.charAt(5)==techtile.charAt(3)&&!science.equals("sci")) return false;
            otherMapper.insertHaveTt(gameid,userid,techtile,"可用");
            if(techtile.equals("ltt7")) otherMapper.gainVp(gameid,userid,7,"ltt7");
            if(techtile.equals("ltt9")) {
                play.setO(play.getO()+1);
                play.setQ(play.getQ()+1);
                playMapper.updatePlayById(play);
            }
            if(techtile.equals("ltt6")) {
                play.setK(play.getK()+this.terratype(gameid,userid));
                playMapper.updatePlayById(play);
            }
            advance(gameid,userid,science,false);
        }
        return true;
    }

    @Override
    public boolean takeatt(String gameid, String userid, String att, String ltt, String tech,String way) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        Game game = gameMapper.getGameById(gameid);
        HaveTown[] avatown = otherMapper.getAvaHTByGameIdUserId(gameid,userid);
        if (avatown.length==0) return false;
        String gameatt = gameMapper.getGameById(gameid).getOtherseed().split(" ")[3];
        int ok = otherMapper.getminusltt(gameid,userid,ltt);
        if(ok==0||!att.substring(0,3).equals("att")) return false;
        char c = '0';
        int attno = Integer.parseInt(att.substring(3));
        if(attno == 1) {c='1';}
        else if(attno == 2) {c='2';}
        else if(attno == 3) {c='3';}
        else if(attno == 4) {c='4';}
        else if(attno == 5) {c='5';}
        else if(attno == 6) {c='6';}
        else if(attno == 7) {c='7';}
        else if(attno == 8) {c='8';}
        else if(attno == 9) {c='9';}
        else if(attno == 10) {c='A';}
        else if(attno == 11) {c='B';}
        else if(attno == 12) {c='C';}
        else if(attno == 13) {c='D';}
        else if(attno == 14) {c='E';}
        else if(attno == 15) {c='F';}
        else if(attno == 16) {c='G';}
        if(c==gameatt.charAt(0)&&play.getTerralv()>3||c==gameatt.charAt(1)&&play.getShiplv()>3||
                c==gameatt.charAt(2)&&play.getQlv()>3||c==gameatt.charAt(3)&&play.getGaialv()>3||
                c==gameatt.charAt(4)&&play.getEcolv()>3||c==gameatt.charAt(5)&&play.getScilv()>3){
            avatown[0].setTtstate("已翻面");
            otherMapper.updateHaveTownById(avatown[0]);
            otherMapper.lttfugai(gameid,userid,ltt);
            otherMapper.insertHaveTt(gameid,userid,att,"可用");
            if(this.advance(gameid,userid,tech,false).equals("错误")){avatown[0].setTtstate("可用");
                otherMapper.updateHaveTownById(avatown[0]);
                otherMapper.lttfugaiundo(gameid,userid,ltt);
                otherMapper.deleteHaveTt(gameid,userid,att);
                return false;}
            if(attno==11){
                int gainvp = 0;
                if(!play.getM1().equals("0")) gainvp++;
                if(!play.getM2().equals("0")) gainvp++;
                if(!play.getM3().equals("0")) gainvp++;
                if(!play.getM4().equals("0")) gainvp++;
                if(!play.getM5().equals("0")) gainvp++;
                if(!play.getM6().equals("0")) gainvp++;
                if(!play.getM7().equals("0")) gainvp++;
                if(!play.getM8().equals("0")) gainvp++;
                if(!play.getBlackstar().equals("0")&&!tech.equals("ship")) gainvp++;
                if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,2*gainvp,"att11");
            }
            if(attno==12){
                int gainvp = 0;
                if(!play.getTc1().equals("0")) gainvp+=1;
                if(!play.getTc2().equals("0")) gainvp+=1;
                if(!play.getTc3().equals("0")) gainvp+=1;
                if(!play.getTc4().equals("0")) gainvp+=1;  if(gainvp!=0)
                    if(way.equals("rl")){
                        gainvp--;
                    }else if(play.getRace().equals("疯狂机器")&&way.equals("ac")){
                        gainvp--;
                    }
                    otherMapper.gainVp(gameid,userid,4*gainvp,"att12");
            }
            if(attno==14){
                int gainvp = 2*this.gaiabuildingcount(play);
                if(gainvp!=0)
                    otherMapper.gainVp(gameid,userid,gainvp,"att14");
            }
            if(attno==13){
                HaveTown[] haveTowns = otherMapper.getHTByGameIdUserId(gameid,userid);
                if(haveTowns.length!=0){
                    otherMapper.gainVp(gameid,userid,haveTowns.length*5,"att13");
                }
            }
            if(attno==10){
                int planets = this.getplanets(play,"");
                otherMapper.gainVp(gameid,userid,planets*2,"att10");
            }
            if(attno==15&&game.getGamemode().charAt(0)=='0'){
                int planets = this.getplanets(play,"");
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                play.setO(play.getO()+planets);
                playMapper.updatePlayById(play);
            }
            if(attno==15&&game.getGamemode().charAt(0)!='0'&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)=='2'){
                otherMapper.gainVp(gameid,userid,play.getP3()*3,"att15");
            }
            if(attno==15&&game.getGamemode().charAt(0)!='0'&&game.getGamemode().length()>=5&&game.getGamemode().charAt(4)=='3'){
                otherMapper.gainVp(gameid,userid,play.getP3()*3,"att15");
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                play.setP1(play.getP1()+play.getP3());
                play.setP3(0);
                if(tech.equals("terra")&&play.getTerralv()==3||tech.equals("ship")&&play.getShiplv()==3||tech.equals("q")&&play.getQlv()==3||tech.equals("gaia")&&play.getGaialv()==3||tech.equals("eco")&&play.getEcolv()==3||tech.equals("sci")&&play.getScilv()==3)
                incomePower(play,3);
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("3"))play.setRacea1("1");
                playMapper.updatePlayById(play);
            }
            return true;
        }
        return false;
    }

    private int getplanets(Play play,String location) {
        List<String> list = new ArrayList<>();
        boolean[] planets = new boolean[11];
        int result = 0;
        if(!location.equals("")) list.add(location);
        list.add(play.getM1());list.add(play.getM2());list.add(play.getM3());list.add(play.getM4());list.add(play.getM5());list.add(play.getM6());
        list.add(play.getM7());list.add(play.getM8());list.add(play.getTc1());list.add(play.getTc2());list.add(play.getTc3());list.add(play.getTc4());
        list.add(play.getRl1());list.add(play.getRl2());list.add(play.getRl3());list.add(play.getSh());list.add(play.getAc1());list.add(play.getAc2());
        list.add(play.getBlackstar());
        for (String s:list){
            if(!s.equals("0")){
                int row = (int)s.charAt(0)-64;
                int column = Integer.parseInt(s.substring(1));
                for (int i=0;i<=36;i+=2){
                    if(row==location1[i]&&column==location1[i+1]) planets[1]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location2[i]&&column==location2[i+1]) planets[2]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location3[i]&&column==location3[i+1]) planets[3]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location4[i]&&column==location4[i+1]) planets[4]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location5[i]&&column==location5[i+1]) planets[5]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location6[i]&&column==location6[i+1]) planets[6]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location7[i]&&column==location7[i+1]) planets[7]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location8[i]&&column==location8[i+1]) planets[8]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location9[i]&&column==location9[i+1]) planets[9]=true;
                }
                for (int i=0;i<=36;i+=2){
                    if(row==location10[i]&&column==location10[i+1]) planets[10]=true;
                }
            }
        }
        for (int i = 0; i < 11; i++) {
            if(planets[i]) result++;
        }
        return result;
    }

    @Override
    public String[][][] getPlayerAction(String gameid) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        int[] a = new int[]{0,0,0,0};
        HaveTt[] havett = otherMapper.getHaveTt(gameid);
        HaveTt[] havetown = otherMapper.getAllHT(gameid);
        String[][][] result = new String[4][20][2];
        for (HaveTt t : havett){
            for (int i=0;i<=3;i++){
                if(t.getUserid().equals(plays[i].getUserid())){
                    if(t.getTtstate().equals("被覆盖")){
                        result[i][a[i]][0]=t.getTtno()+"minus";
                    }else if(t.getTtstate().equals("已使用")){
                        result[i][a[i]][0]=t.getTtno()+"used";
                    }
                     else{
                        result[i][a[i]][0]=t.getTtno();
                    }
                     if(result[i][a[i]][0].equals("ac2")){
                         result[i][a[i]][1]="1Q";
                         if(plays[i].getRace().equals("炽炎族")) result[i][a[i]][1]="4C";
                     }else{result[i][a[i]][1]=t.getTtstate();}
                    a[i]++;
                }
            }
        }
        for (HaveTt t : havetown){
            for (int i=0;i<=3;i++){
                if(t.getUserid().equals(plays[i].getUserid())){
                    result[i][a[i]][0]=t.getTtno();
                    if(t.getTtstate().equals("可用"))
                    {result[i][a[i]][1]="lime";}else{
                        result[i][a[i]][1]="grey";
                    }
                    a[i]++;
                }
            }
        }
        return result;
    }

    @Override
    public String advance(String gameid, String userid, String science,boolean needk) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        Game game = gameMapper.getGameById(gameid);
        boolean[] sciencehastop = new boolean[6];
        for(Play p:plays){
            if(p.getTerralv()==5) sciencehastop[0]=true;
            if(p.getShiplv()==5) sciencehastop[1]=true;
            if(p.getQlv()==5) sciencehastop[2]=true;
            if(p.getGaialv()==5) sciencehastop[3]=true;
            if(p.getEcolv()==5) sciencehastop[4]=true;
            if(p.getScilv()==5) sciencehastop[5]=true;
        }
        HaveTown[] avatown = otherMapper.getAvaHTByGameIdUserId(gameid,userid);
        if(needk&&play.getK()<4) return "科技不足！";
        if(needk) play.setK(play.getK()-4);
        if(science.equals("terra")&&play.getTerralv()!=5&&!(play.getTerralv()==4&&(sciencehastop[0]||avatown.length==0))) {
            if(play.getRace().equals("魔族")) return "错误";
            if(play.getTerralv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            if(play.getTerralv()==0||play.getTerralv()==3) play.setO(play.getO()+2);
            if(play.getTerralv()==2) takePower(play,3);
            if(play.getTerralv()==4) {
                playMapper.updatePlayById(play);
                form(gameid,userid,"terratop");
                gameMapper.updateGameById(game);
            play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }
            playMapper.advanceTerra(gameid,userid);}
       else if(science.equals("q")&&play.getQlv()!=5&&!(play.getQlv()==4&&(sciencehastop[2]||avatown.length==0))) {
            if(play.getQlv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            if(play.getQlv()==0||play.getQlv()==1) play.setQ(play.getQ()+1);
            if(play.getQlv()==2||play.getQlv()==3) play.setQ(play.getQ()+2);
            if(play.getQlv()==4) play.setQ(play.getQ()+4);
            if(play.getQlv()==4&&game.getGamemode().charAt(0)!='0') otherMapper.gainVp(gameid,userid,play.getQ(),"q-top");
            if(play.getQlv()==2) takePower(play,3);
            playMapper.advanceQ(gameid,userid);}
       else if(science.equals("gaia")&&play.getGaialv()!=5&&!(play.getGaialv()==4&&(sciencehastop[3]||avatown.length==0))) {
           if(play.getGaialv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            if(play.getGaialv()==1) play.setP1(play.getP1()+3);
            if(play.getGaialv()==2) takePower(play,3);
            if(play.getGaialv()==4) otherMapper.gainVp(gameid,userid,4+gaiabuildingcount(play),"gaia-top");
            playMapper.advanceGaia(gameid,userid);}
       else if(science.equals("eco")&&play.getEcolv()!=5&&!(play.getEcolv()==4&&(sciencehastop[4]||avatown.length==0))) {
            if(play.getEcolv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            if(play.getEcolv()==4) {play.setC(play.getC()+6);play.setO(play.getO()+3);takePower(play,6);}
            if(play.getEcolv()==2) takePower(play,3);
            playMapper.advanceEco(gameid,userid);}
        else if(science.equals("sci")&&play.getScilv()!=5&&!(play.getScilv()==4&&(sciencehastop[5]||avatown.length==0))) {
            if(play.getScilv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            if(play.getScilv()==4) {play.setK(play.getK()+9);}
            if(play.getScilv()==2) takePower(play,3);
            playMapper.advanceSci(gameid,userid);}
         else if(science.length()>=4&&science.substring(0,4).equals("ship")&&!(science.length()>=5&&science.charAt(4)==' ')&&play.getShiplv()!=5&&!(play.getShiplv()==4&&(sciencehastop[1]||avatown.length==0))) {
            if(play.getRace().equals("炽炎族")&&play.getSh().equals("0")) return "错误";
            if(play.getShiplv()==0||play.getShiplv()==2) play.setQ(play.getQ()+1);
            if(play.getShiplv()==2) takePower(play,3);
            if(play.getShiplv()==4) {
                String location = science.substring(4);
                String[][] mapdetail = new String[21][15];
                this.setMapDetail(mapdetail,gameid);
                if(location.equals("")) return "错误";
                int row = (int)location.charAt(0)-64;
                int column = Integer.parseInt(location.substring(1));
                if(!mapdetail[row][column].equals(ck)||otherMapper.hassatellite(gameid,location)!=0) return "错误";
                playMapper.updatePlayById(play);
                if(!canArrive(gameid,userid,location,"Blackstar",true)) return "错误";
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                play.setBlackstar(location);
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
                if(play.getRace().equals("晶矿星人")&&!play.getSh().equals("0")){
                    play.setK(play.getK()+3);
                }
                createPower(gameid,userid,location,"BlackStar");
                String[] rs = this.getRoundScoreById(gameid);
                game = gameMapper.getGameById(gameid);
                if (rs[game.getRound() - 1].equals("M>>2")) otherMapper.gainVp(gameid, userid, 2, "M>>2");
                if (otherMapper.getTtByGameidUseridTtno(gameid, userid, "att7") != null)
                    otherMapper.gainVp(gameid, userid, 3, "att7");
                ArrayList<String> townandsate = new ArrayList<>();
                String[] townbuildings = otherMapper.getTownBuildingByUserid(gameid,userid);
                String[] sates = otherMapper.getSatelliteByUserid(gameid,userid);
                Collections.addAll(townandsate, townbuildings);
                Collections.addAll(townandsate, sates);
                for (String s:townandsate){
                    if(distance(location,s)==1) {otherMapper.insertTB(gameid,userid,location);break;}
                }
            }
            playMapper.advanceShip(gameid,userid);}
         else if(science.equals("terra")&&(play.getTerralv()==5||play.getTerralv()==4&&(sciencehastop[0]||avatown.length==0))){

        }else if(science.equals("ship")&&(play.getShiplv()==5||play.getShiplv()==4&&(sciencehastop[1]||avatown.length==0))){

        }else if(science.equals("q")&&(play.getQlv()==5||play.getQlv()==4&&(sciencehastop[2]||avatown.length==0))){

        }else if(science.equals("gaia")&&(play.getGaialv()==5||play.getGaialv()==4&&(sciencehastop[3]||avatown.length==0))){

        }else if(science.equals("eco")&&(play.getEcolv()==5||play.getEcolv()==4&&(sciencehastop[4]||avatown.length==0))){

        }else if(science.equals("sci")&&(play.getScilv()==5||play.getScilv()==4&&(sciencehastop[5]||avatown.length==0))){

        }else {
             return "错误";
        }
         game = gameMapper.getGameById(gameid);
        String[] rs = this.getRoundScoreById(gameid);
        if(rs[game.getRound()-1].equals("AT>>2")) {otherMapper.gainVp(gameid,userid,2,"AT>>2");xizu(play,"AT");}
        playMapper.updatePlayById(play);
        if(otherMapper.getTtByGameidUseridTtno(gameid,userid,"att9")!=null) otherMapper.gainVp(gameid,userid,2,"att9");
        if(needk) {updatePosition(gameid);}
        return "成功";
    }

    private void takePower(Play play, int power) {
        int p1 = play.getP1();
        int p2 = play.getP2();
        int p3 = play.getP3();
        while(power!=0&&p1!=0){
            if(play.getRace().equals("利爪族")&&play.getRacea1().equals("1")) play.setRacea1("2");
            power--;
            p1--;
            p2++;
        }
        while(power!=0&&p2!=0){
            if(play.getRace().equals("利爪族")&&play.getRacea1().equals("2")) play.setRacea1("3");
            power--;
            p2--;
            p3++;
        }
        play.setP1(p1);
        play.setP2(p2);
        play.setP3(p3);
    }

    @Override
    public int[] getTownremain(String gameid) {
        int[] result = new int[]{3,3,3,3,3,3};
        String[] havetown = otherMapper.getHT(gameid);
        for (String ht:havetown){
            if (ht.equals("1")){
                result[0]--;
            }else  if (ht.equals("2")){
                result[1]--;
            }else  if (ht.equals("3")){
                result[2]--;
            }else  if (ht.equals("4")){
                result[3]--;
            }else  if (ht.equals("5")){
                result[4]--;
            }else  if (ht.equals("6")){
                result[5]--;
            }
        }
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        boolean terratown = true;
        for (Play p:plays){
            if(p.getTerralv()==5) terratown = false;
        }
        if(terratown){
            if(game.getTerratown()==1){result[0]--;}
            if(game.getTerratown()==2){result[1]--;}
            if(game.getTerratown()==3){result[2]--;}
            if(game.getTerratown()==4){result[3]--;}
            if(game.getTerratown()==5){result[4]--;}
            if(game.getTerratown()==6){result[5]--;}
        }
        return result;
    }

    @Override
    public String action(String gameid, String userid, String substring) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(substring.equals("1")&&(game.getPwa1().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            if(!this.usePower(play,3)) return "错误";
            game.setPwa1("0");
            gameMapper.updateGameById(game);
            play.setP1(play.getP1()+2);
        }else if(substring.charAt(0)=='2'&&(game.getPwa2().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            Play oldplay = playMapper.getPlayByGameIdUserid(gameid,userid);
            play = playMapper.getPlayByGameIdUserid(gameid,userid);
            if(!this.usePower(play,3)) return "错误";
            playMapper.updatePower(gameid,userid,play.getP1(),play.getP2(),play.getP3(),play.getPg(),play.getRacea1());
            if(this.buildMine(gameid,userid,substring.substring(8),"action2").equals("成功")){
                game = gameMapper.getGameById(gameid);
                game.setPwa2("0");
                gameMapper.updateGameById(game);
                return "成功";
            }else {
                playMapper.updatePower(gameid,userid,oldplay.getP1(),oldplay.getP2(),oldplay.getP3(),oldplay.getPg(),oldplay.getRacea1());
                return "失败！";
            }
        }else if(substring.equals("3")&&(game.getPwa3().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            game.setPwa3("0");
            if(!this.usePower(play,4)) return "错误";
            gameMapper.updateGameById(game);
            play.setO(play.getO()+2);
        }else if(substring.equals("4")&&(game.getPwa4().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            if(!this.usePower(play,4)) return "错误";
            game.setPwa4("0");
            gameMapper.updateGameById(game);
            play.setC(play.getC()+7);
        }else if(substring.equals("5")&&(game.getPwa5().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            if(!this.usePower(play,4)) return "错误";
            game.setPwa5("0");
            gameMapper.updateGameById(game);
            play.setK(play.getK()+2);
        }else if(substring.charAt(0)=='6'&&(game.getPwa6().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            Play oldplay = playMapper.getPlayByGameIdUserid(gameid,userid);
            play = playMapper.getPlayByGameIdUserid(gameid,userid);
            if(!this.usePower(play,5)) return "错误";
            playMapper.updatePower(gameid,userid,play.getP1(),play.getP2(),play.getP3(),play.getPg(),play.getRacea1());
            if(this.buildMine(gameid,userid,substring.substring(8),"action6").equals("成功")){
                game = gameMapper.getGameById(gameid);
                game.setPwa6("0");
                gameMapper.updateGameById(game);
                return "成功";
            }else {
                playMapper.updatePower(gameid,userid,oldplay.getP1(),oldplay.getP2(),oldplay.getP3(),oldplay.getPg(),oldplay.getRacea1());
                return "失败！";
            }
        }else if(substring.equals("7")&&(game.getPwa7().equals("1")||play.getRace().equals("织女星人")&&!play.getSh().equals("0"))){
            if(!this.usePower(play,7)) return "错误";
            game.setPwa7("0");
            gameMapper.updateGameById(game);
            play.setK(play.getK()+3);
        }else if(substring.equals("8")&&play.getQ()>=2&&game.getQa1().equals("1")){
            game.setQa1("0");
            gameMapper.updateGameById(game);
            play.setQ(play.getQ()-2);
            otherMapper.gainVp(gameid,userid,3+terratype(gameid,userid),"action8");
        }else if(substring.charAt(0)=='9'&&substring.substring(2,6).equals("town")&&play.getQ()>=3&&game.getQa2().equals("1")){
            String[] hts = otherMapper.getHTTypeById(gameid,userid);
            int towntype = Integer.parseInt(substring.substring(6));
            boolean ok = false;
            for (String s:hts){
                if (Integer.parseInt(s)==towntype) ok = true;
            }
            if(!ok) return "错误！";
            game.setQa2("0");
            if(towntype==1){
                play.setO(play.getO()+2);
                otherMapper.gainVp(gameid,userid,7,"action9");
            }else if(towntype==2){
                play.setC(play.getC()+6);
                otherMapper.gainVp(gameid,userid,7,"action9");
            }else if(towntype==3){
                play.setQ(play.getQ()+1);
                otherMapper.gainVp(gameid,userid,8,"action9");
            }else if(towntype==4){
                play.setK(play.getK()+2);
                otherMapper.gainVp(gameid,userid,6,"action9");
            }else if(towntype==5){
                play.setP1(play.getP1()+2);
                otherMapper.gainVp(gameid,userid,8,"action9");
            }else if(towntype==6){
                otherMapper.gainVp(gameid,userid,12,"action9");
            }
            gameMapper.updateGameById(game);
            play.setQ(play.getQ()-3);
        }else if(substring.length()>=2&&substring.substring(0,2).equals("10")&&play.getQ()>=4&&game.getQa3().equals("1")){
            String[] strs = substring.split(" ");
            boolean ok = false;
            if(strs.length==4){
                if(strs[1].charAt(0)=='+'){
                    ok = this.takett(gameid,userid,strs[1].substring(1),strs[3]);
                }else{
                    ok = this.takett(gameid,userid,strs[3].substring(1),strs[2]);
                };
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }else if(strs.length==5){
                if(strs[1].charAt(0)=='+'){
                    ok = this.takeatt(gameid,userid,strs[1].substring(1),strs[2].substring(1),strs[4],"action10");
                }else{
                    ok = this.takeatt(gameid,userid,strs[3].substring(1),strs[4].substring(1),strs[2],"action10");
                };
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }
            if(!ok) return "cuowu";
            game.setQa3("0");
            gameMapper.updateGameById(game);
            play.setQ(play.getQ()-4);
        }else if(substring.length()>=3&&substring.substring(0,3).equals("ac2")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"ac2");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")) {
                if(play.getRace().equals("炽炎族")){
                    play.setC(play.getC()+4);
                }else {
                    play.setQ(play.getQ()+1);
                }
                otherMapper.ttuse(gameid,userid,"ac2");
            }
        } else if(substring.length()>=4&&substring.substring(0,4).equals("bon1")&&substring.substring(5,10).equals("build")){
            if(game.getBon1()==0) return "已被使用！";
            if(play.getRace().equals("熊猫人")){
                if(play.getBonus()!=1&&!play.getRacea1().equals("1")&&!play.getRacea2().equals("1")) return "你并未持有一铲版！";
            }else if(play.getBonus()!=1)return "你并未持有一铲版！";
            if(this.buildMine(gameid,userid,substring.substring(11),"bon1").equals("成功")){
                game = gameMapper.getGameById(gameid);
                game.setBon1(0);
                if(play.getRace().equals("超星人")){play.setP3(play.getP3()/2);play.setP1((play.getP1()+1)/2);}
                gameMapper.updateGameById(game);
                return "成功";
            }
            return "失败！";
        }  else if(substring.length()>=5&&substring.substring(0,5).equals("att16")&&substring.substring(6,11).equals("build")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"att16");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")){
                if(this.buildMine(gameid,userid,substring.substring(12),"att16").equals("成功")){
                    game = gameMapper.getGameById(gameid);
                    otherMapper.ttuse(gameid,userid,"att16");
                    return "成功";
                }
            }
            return "失败！";
        } else if(substring.length()>=4&&substring.substring(0,4).equals("bon2")){
            if(game.getBon2()==0) return "已被使用！";
            if(play.getRace().equals("熊猫人")){
                if(play.getBonus()!=2&&!play.getRacea1().equals("2")&&!play.getRacea2().equals("2")) return "你并未持有三航版！";
            }else if(play.getBonus()!=2)return "你并未持有三航版！";
            String BMGA = "";
            Play play1 = playMapper.getPlayByGameIdUserid(gameid,userid);
            if(substring.substring(5,10).equals("build")) {BMGA=this.buildMine(gameid,userid,substring.substring(11),"bon2");}
            else if(substring.substring(5,9).equals("gaia")) {BMGA=this.gaia(gameid,userid,substring.substring(10),"bon2");}
            else {return "操作不合法！";}
            game = gameMapper.getGameById(gameid);
            if(BMGA.equals("成功")){
                game.setBon2(0);
            gameMapper.updateGameById(game);
            return "成功";}
            return "失败";
        }else if(substring.length()>=4&&substring.substring(0,4).equals("ltt2")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"ltt2");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")){
                otherMapper.ttuse(gameid,userid,"ltt2");
                for (int i=1;i<=4;i++){
                    if(play.getP1()!=0) {
                        if(play.getRace().equals("利爪族")&&play.getRacea1().equals("1")) play.setRacea1("2");
                        play.setP1(play.getP1()-1);
                        play.setP2(play.getP2()+1);
                    }else if(play.getP2()!=0){
                        if(play.getRace().equals("利爪族")&&play.getRacea1().equals("2")) play.setRacea1("3");
                        play.setP2(play.getP2()-1);
                        play.setP3(play.getP3()+1);
                    }
                }
            }else return " 四转版不可用！";
        }else if(substring.length()>=4&&substring.substring(0,4).equals("att1")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"att1");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")){
                otherMapper.ttuse(gameid,userid,"att1");
                if(game.getGamemode().charAt(0)=='0') play.setO(play.getO()+3);
                if(game.getGamemode().charAt(0)!='0') play.setO(play.getO()+4);
            }else return "高级科技不可用！";
        }else if(substring.length()>=4&&substring.substring(0,4).equals("att2")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"att2");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")){
                otherMapper.ttuse(gameid,userid,"att2");
                if(game.getGamemode().charAt(0)=='0'){play.setC(play.getC()+5);play.setQ(play.getQ()+1);}
                if(game.getGamemode().charAt(0)!='0'){play.setC(play.getC()+7);play.setQ(play.getQ()+1);}
            }else return "高级科技不可用！";
        }else if(substring.length()>=4&&substring.substring(0,4).equals("att3")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"att3");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")){
                otherMapper.ttuse(gameid,userid,"att3");
                if(game.getGamemode().charAt(0)=='0') play.setK(play.getK()+3);
                if(game.getGamemode().charAt(0)!='0') play.setK(play.getK()+4);
            }else return "高级科技不可用！";
        }else if(substring.substring(0,1).equals("d")){
             HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"actiond");
             if(play.getRace().equals("大使星人")&&tt!=null&&tt.getTtstate().equals("可用")){
                 String location1 = substring.split(" ")[1];
                 String location2 = substring.split(" ")[2];
                 int row1 = (int)location1.charAt(0)-64;
                 int column1 = Integer.parseInt(location1.substring(1));
                 int row2 = (int)location2.charAt(0)-64;
                 int column2 = Integer.parseInt(location2.substring(1));
                 String[][] structurecolor = this.getStructureColorById(gameid);
                 String[][] structuresituation = this.getStructureSituationById(gameid);
                 if(!structurecolor[row1][column1].equals(br)||!structurecolor[row2][column2].equals(br)||play.getBlackstar().equals(location1)||play.getBlackstar().equals(location2)) return null;
                 if(structuresituation[row1][column1].equals("m")&&structuresituation[row2][column2].equals("sh")||structuresituation[row1][column1].equals("sh")&&structuresituation[row2][column2].equals("m")){
                     String shlocation = play.getSh();
                     String mlocation = location1.equals(shlocation) ?location2:location1;
                     if(play.getM1().equals(mlocation)) {play.setSh(play.getM1());play.setM1(shlocation);}
                     if(play.getM2().equals(mlocation)) {play.setSh(play.getM2());play.setM2(shlocation);}
                     if(play.getM3().equals(mlocation)) {play.setSh(play.getM3());play.setM3(shlocation);}
                     if(play.getM4().equals(mlocation)) {play.setSh(play.getM4());play.setM4(shlocation);}
                     if(play.getM5().equals(mlocation)) {play.setSh(play.getM5());play.setM5(shlocation);}
                     if(play.getM6().equals(mlocation)) {play.setSh(play.getM6());play.setM6(shlocation);}
                     if(play.getM7().equals(mlocation)) {play.setSh(play.getM7());play.setM7(shlocation);}
                     if(play.getM8().equals(mlocation)) {play.setSh(play.getM8());play.setM8(shlocation);}
                     otherMapper.ttuse(gameid,userid,"actiond");
                 }else return "操作不合法！";
            }else return "操作不合法！";
        }else if(substring.substring(0,1).equals("t")) {
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid, userid, "actiont");
            if (play.getRace().equals("天龙星人") && tt != null && tt.getTtstate().equals("可用")) {
                String location1 = substring.split(" ")[1];
                if(!this.buildMine(gameid,userid,location1,"dragon").equals("成功")) return "失败";
                otherMapper.ttuse(gameid,userid,"actiont");return "成功";
            }
        }else if(substring.substring(0,1).equals("r")) {
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid, userid, "actionr");
            if (play.getRace().equals("殖民者") && tt != null && tt.getTtstate().equals("可用")) {
                String location1 = substring.split(" ")[1];
                if(otherMapper.gettownbuilding(gameid,userid,location1)==1) return "失败";
                int row = (int) location1.charAt(0) - 64;
                int column = Integer.parseInt(location1.substring(1));
                String[][] structuresituation = this.getStructureSituationById(gameid);
                String[][] mapdetail = new String[21][15];
                this.setMapDetail(mapdetail,gameid);
                int shiplv = play.getShiplv();
                int x = 0;
                switch (shiplv) {
                    case 0:
                        x = 1;
                        break;
                    case 1:
                        x = 1;
                        break;
                    case 2:
                        x = 2;
                        break;
                    case 3:
                        x = 2;
                        break;
                    case 4:
                        x = 3;
                        break;
                    case 5:
                        x = 4;
                        break;
                }
                if(distance(play.getM1(),location1)>x) return "失败";
                if(!mapdetail[row][column].equals(ck)||structuresituation[row][column]!=null)  return "失败";
                play.setM1(location1);
                playMapper.updatePlayById(play);
                otherMapper.ttuse(gameid,userid,"actionr");
            }
        }else if(substring.substring(0,1).equals("p")) {
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid, userid, "actionp");
            if (play.getRace().equals("殖民者") && tt != null && tt.getTtstate().equals("可用")) {
                int bonusno = Integer.parseInt(substring.split(" ")[2].substring(3));
                Play[] plays = playMapper.getPlayByGameId(gameid);
                for (int i = 0; i < plays.length; i++) {
                    if(bonusno==plays[i].getBonus()) return "选择回合助推板错误！";
                }
                String nobon = game.getOtherseed().substring(24);
                if(bonusno==(int)nobon.charAt(0)-47||bonusno==(int)nobon.charAt(1)-47||bonusno==(int)nobon.charAt(2)-47) return "选择回合助推板错误！";
                int passbonus = play.getBonus();
                switch (passbonus){
                    case 6: {
                        int gainvp = 0;
                        if(!play.getM1().equals("0")) gainvp++;
                        if(!play.getM2().equals("0")) gainvp++;
                        if(!play.getM3().equals("0")) gainvp++;
                        if(!play.getM4().equals("0")) gainvp++;
                        if(!play.getM5().equals("0")) gainvp++;
                        if(!play.getM6().equals("0")) gainvp++;
                        if(!play.getM7().equals("0")) gainvp++;
                        if(!play.getM8().equals("0")) gainvp++;
                        if(!play.getBlackstar().equals("0")) gainvp++;
                        if(gainvp!=0)
                            otherMapper.gainVp(gameid,userid,gainvp,"bon6");break;
                    }
                    case 7:{
                        int gainvp = 0;
                        if(!play.getTc1().equals("0")) gainvp+=2;
                        if(!play.getTc2().equals("0")) gainvp+=2;
                        if(!play.getTc3().equals("0")) gainvp+=2;
                        if(!play.getTc4().equals("0")) gainvp+=2;  if(gainvp!=0)
                            otherMapper.gainVp(gameid,userid,gainvp,"bon7");break;
                    }
                    case 8:{
                        int gainvp = 0;
                        if(!play.getRl1().equals("0")) gainvp+=3;
                        if(!play.getRl2().equals("0")) gainvp+=3;
                        if(!play.getRl3().equals("0")) gainvp+=3;if(gainvp!=0)
                            otherMapper.gainVp(gameid,userid,gainvp,"bon8");break;
                    }
                    case 9:{
                        int gainvp = 0;
                        if(!play.getSh().equals("0")) gainvp+=4;
                        if(!play.getAc1().equals("0")) gainvp+=4;
                        if(!play.getAc2().equals("0")) gainvp+=4;if(gainvp!=0)
                            otherMapper.gainVp(gameid,userid,gainvp,"bon9");break;
                    }
                    case 10:{
                        int gainvp = this.gaiabuildingcount(play);
                        otherMapper.gainVp(gameid,userid,gainvp,"bon10");break;
                    }
                }
                play.setBonus(bonusno);
                playMapper.updateBonusById(gameid,userid,bonusno);
                otherMapper.ttuse(gameid,userid,"actionr");
            }
        }
        else if(substring.substring(0,1).equals("z")){
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"actionz");
            if(play.getRace().equals("章鱼人")&&tt!=null&&tt.getTtstate().equals("可用")){
                String location = substring.split(" ")[1];
                String science = substring.split(" ")[3];
                int row = (int)location.charAt(0)-64;
                int column1 = Integer.parseInt(location.substring(1));
                int tcno = 0;
                int rlno = 0;
                Class playclass = Play.class;
                for (int i = 1; i <= 4; i++) {
                    Method method = playclass.getMethod("getTc" + String.valueOf(i));
                    String locationtc = (String) method.invoke(play);
                    if (locationtc.equals("0")) {
                        tcno = i;
                        break;
                    }
                }
                for (int i = 1; i <= 3; i++) {
                    Method method = playclass.getMethod("getRl" + String.valueOf(i));
                    String locationrl = (String) method.invoke(play);
                    if (locationrl.equals(location)) {
                        rlno = i;
                        break;
                    }
                }
                if (rlno == 0||tcno == 0) {
                    return "操作不合法！";
                }
                Method method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
                method.invoke(play,location);
                method = playclass.getMethod("setRl" + String.valueOf(rlno), String.class);
                method.invoke(play,"0");
                String[] rs = this.getRoundScoreById(gameid);
                if(rs[game.getRound()-1].equals("TC>>3")) otherMapper.gainVp(gameid,userid,3,"TC>>3");
                if(rs[game.getRound()-1].equals("TC>>4")) otherMapper.gainVp(gameid,userid,4,"TC>>4");
                if(otherMapper.getTtByGameidUseridTtno(gameid,userid,"att8")!=null) otherMapper.gainVp(gameid,userid,3,"att8");
                createPower(gameid,userid,location,"ZTC");
                playMapper.updatePlayById(play);
                advance(gameid,userid,science,false);
                otherMapper.ttuse(gameid,userid,"actionz");
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }
        }else if(substring.substring(0,1).equals("m")) {
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid, userid, "actionm");
            if (play.getRace().equals("疯狂机器") && tt != null && tt.getTtstate().equals("可用")) {
                String science = substring.substring(2);
                if(substring.substring(2,9).equals("advance")) science = substring.substring(10);
                List<Integer> list = new ArrayList<>();
                list.add(play.getTerralv());
                list.add(play.getShiplv());
                list.add(play.getQlv());
                list.add(play.getGaialv());
                list.add(play.getEcolv());
                list.add(play.getScilv());
                Collections.sort(list);
                if(science.equals("terra")&&play.getTerralv()==list.get(0)) {advance(gameid,userid,"terra",false);}else
                if(science.equals("ship")&&play.getShiplv()==list.get(0)) {advance(gameid,userid,"ship",false);}else
                if(science.equals("gaia")&&play.getGaialv()==list.get(0)) {advance(gameid,userid,"gaia",false);}else
                if(science.equals("q")&&play.getQlv()==list.get(0)) {advance(gameid,userid,"q",false);}else
                if(science.equals("eco")&&play.getEcolv()==list.get(0)) {advance(gameid,userid,"eco",false);}else
                if(science.equals("sci")&&play.getScilv()==list.get(0)) {advance(gameid,userid,"sci",false);}else {
                    return "错误";
                }
                otherMapper.ttuse(gameid,userid,"actionm");
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
            }
        }else if(substring.substring(0,1).equals("f")){
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"actionf");
            if(play.getRace().equals("蜂人")&&tt!=null&&tt.getTtstate().equals("可用")){
                String location = substring.split(" ")[1];
                int row = (int)location.charAt(0)-64;
                int column = Integer.parseInt(location.substring(1));
                String[][] mapdetail = new String[21][15];
                this.setMapDetail(mapdetail,gameid);
                String[][] structurecolor = this.getStructureColorById(gameid);
                String[][] structuresituation = this.getStructureSituationById(gameid);
                if(!mapdetail[row][column].equals(ck)) return "请建造在太空中";
                if(structuresituation[row][column]!=null&&structuresituation[row][column].equals("m")) return "不能建造在建筑上";
                if(!canArrive(gameid,userid,location,"",true)) return "航线距离不足";
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                List<String> hivelist = new ArrayList<>();
                if(!play.getRacea1().equals("")) hivelist.add(play.getRacea1());if(!play.getRacea2().equals("")) hivelist.add(play.getRacea2());if(!play.getRacea3().equals("")) hivelist.add(play.getRacea3());
                if(!play.getRacea4().equals("")) hivelist.add(play.getRacea4());if(!play.getRacea5().equals("")) hivelist.add(play.getRacea5());if(!play.getRacea6().equals("")) hivelist.add(play.getRacea6());
                if(hivelist.contains(location))return "不合法！";
                Class playclass = Play.class;
                for (int i = 1; i <= 6; i++) {
                    Method method = playclass.getMethod("getRacea" + String.valueOf(i));
                    String hiveloca = (String) method.invoke(play);
                    if(hiveloca.equals("")){
                        method = playclass.getMethod("setRacea" + String.valueOf(i),String.class);
                        method.invoke(play,location);
                        break;
                    }
                }
                otherMapper.ttuse(gameid,userid,"actionf");
            }else return "操作不合法！";
        }else if(substring.substring(0,1).equals("l")){
            HaveTt haveTt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"actionl");
            if(haveTt!=null&&haveTt.getTtstate().equals("可用")) {
                play.setP2(play.getP2()+play.getP1()+play.getP3());
                play.setP1(0);
                play.setP3(0);
                otherMapper.ttuse(gameid,userid,"actionl");
            }
        }
        else {
            return "操作不合法！";
        }
        playMapper.updatePlayById(play);
        this.updatePosition(gameid);
        /*updateRecordById(gameid,play.getRace()+":"+"action"+substring+".");*/
        return "成功";
    }

    private boolean usePower(Play play, int i) {
        int hasconvertpower = 0;
        while(hasconvertpower<i){
            if(i>=3&&play.getRace().equals("利爪族")&&play.getRacea1().equals("3")) {
                play.setRacea1("1");hasconvertpower+=3; play.setP3(play.getP3()-1);play.setP1(play.getP1()+1);
            }else{
             if(play.getP3()==0) return false;
             play.setP3(play.getP3()-1);
             play.setP1(play.getP1()+1);
             if(play.getRace().equals("超星人")&&!play.getSh().equals("0")) {hasconvertpower+=2;}else hasconvertpower++;
            }
        }
        return true;
    }

    private int terratype(String gameid, String userid) {
        String[][] mapdetail = new String[21][15];
        this.setMapDetail(mapdetail,gameid);
        String[][] structurecolor = this.getStructureColorById(gameid);
        String[][] structure = this.getStructureSituationById(gameid);
        String racecolor = racecolormap.get(playMapper.getPlayByGameIdUserid(gameid,userid).getRace());
        int result = 0;
        Set<String> set = new HashSet();
        for (int i = 1; i < 21; i++) {
            for (int j = 1; j < 15; j++) {
                if(mapdetail[i][j]!=null&&structurecolor[i][j]!=null&&structurecolor[i][j].equals(racecolor)&&!structure[i][j].equals("gtu")&&!structure[i][j].equals("hive")&&!set.contains(mapdetail[i][j]))
                {result++;set.add(mapdetail[i][j]);}
            }
        }
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(play.getRace().equals("殖民者")&&play.getBlackstar().equals("0")) result--;
        return result;
    }

    @Override
    public String gaia(String gameid, String userid, String location,String action) {
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int gaiatech = play.getGaialv();
        String[][] mapdetail = new String[21][15];
        String[][] structurecolor = this.getStructureColorById(gameid);
        this.setMapDetail(mapdetail,gameid);
        int row = (int)location.charAt(0)-64;
        int column = Integer.parseInt(location.substring(1));
        if(!mapdetail[row][column].equals(pu)||gaiatech==0||structurecolor[row][column]!=null) return"建造失败！";
        play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int needbean = 0;
        switch (gaiatech) {
            case 1: needbean = 6;break;
            case 2: needbean = 6;break;
            case 3: needbean = 4;break;
            case 4: needbean = 3;break;
            case 5: needbean = 3;break;
        }
        int totalbean = play.getP1()+play.getP2()+play.getP3();
        if(play.getRace().equals("猎户星人")) totalbean = play.getP2()+play.getP3();
        if(totalbean<needbean) return "建造失败！";
        if(gaiatech<3&&!play.getGtu1().equals("0"))return "建造失败！";
        if(gaiatech<4&&!play.getGtu1().equals("0")&&!play.getGtu2().equals("0")) return "建造失败！";
        if(!play.getGtu1().equals("0")&&!play.getGtu2().equals("0")&&!play.getGtu3().equals("0")) return "建造失败！";
        if(!canArrive(gameid,userid,location,action,true)) return "距离不够！";
        play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(play.getRace().equals("利爪族")&&Integer.parseInt(play.getRacea1())==1&&needbean!=totalbean){
            play.setP1(play.getP1()-1);
        }
        for (int i = 0; i < needbean; i++) {
            if (play.getP1() != 0 && !play.getRace().equals("猎户星人")) {
                play.setP1(play.getP1() - 1);
            } else if (play.getP2() != 0) {
                play.setP2(play.getP2() - 1);
            } else {
                play.setP3(play.getP3() - 1);
            }
        }
        if(play.getRace().equals("利爪族")&&Integer.parseInt(play.getRacea1())==1&&needbean!=totalbean){
            play.setP1(play.getP1()+1);
        }
        if(play.getRace().equals("利爪族")&&Integer.parseInt(play.getRacea1())==1&&play.getP1()==0) play.setRacea1("pg");
        if(play.getRace().equals("利爪族")&&Integer.parseInt(play.getRacea1())==2&&play.getP2()==0) play.setRacea1("pg");
        if(play.getRace().equals("猎户星人")){
            play.setP1(play.getP1()+needbean);
        }else {
            play.setPg(play.getPg()+needbean);
        }
        if(play.getGtu1().equals("0")) {play.setGtu1(location);}
        else if(play.getGtu2().equals("0")){play.setGtu2(location);}
        else {play.setGtu3(location);}
        playMapper.updatePlayById(play);
        if(play.getRace().equals("猎户星人")) otherMapper.insertGaia(gameid,location);
        updatePosition(gameid);
        return "成功";
    }

    @Override
    public boolean canArrive(String gameid, String userid, String location,String action,boolean mq) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        ArrayList<String> list = new ArrayList<>();
        if (!play.getRace().equals("殖民者")) {
            if (!play.getM1().equals("0")) list.add(play.getM1());
            if (!play.getM2().equals("0")) list.add(play.getM2());
            if (!play.getM3().equals("0")) list.add(play.getM3());
            if (!play.getM4().equals("0")) list.add(play.getM4());
            if (!play.getM5().equals("0")) list.add(play.getM5());
            if (!play.getM6().equals("0")) list.add(play.getM6());
            if (!play.getM7().equals("0")) list.add(play.getM7());
            if (!play.getM8().equals("0")) list.add(play.getM8());
            if (!play.getTc1().equals("0")) list.add(play.getTc1());
            if (!play.getTc2().equals("0")) list.add(play.getTc2());
            if (!play.getTc3().equals("0")) list.add(play.getTc3());
            if (!play.getTc4().equals("0")) list.add(play.getTc4());
            if (!play.getRl1().equals("0")) list.add(play.getRl1());
            if (!play.getRl2().equals("0")) list.add(play.getRl2());
            if (!play.getRl3().equals("0")) list.add(play.getRl3());
            if (!play.getSh().equals("0")) list.add(play.getSh());
            if (!play.getAc1().equals("0")) list.add(play.getAc1());
            if (!play.getAc2().equals("0")) list.add(play.getAc2());
            if (!play.getBlackstar().equals("0")) list.add(play.getBlackstar());
        } else {
            if (!play.getM1().equals("0")) list.add(play.getM1());
        }
        if(play.getRace().equals("蜂人")) {
            if(!play.getRacea1().equals("")) list.add(play.getRacea1());
            if(!play.getRacea2().equals("")) list.add(play.getRacea2());
            if(!play.getRacea3().equals("")) list.add(play.getRacea3());
            if(!play.getRacea4().equals("")) list.add(play.getRacea4());
            if(!play.getRacea5().equals("")) list.add(play.getRacea5());
            if(!play.getRacea6().equals("")) list.add(play.getRacea6());
        }
        boolean available = false;//是否航线可达
        int shiplv = play.getShiplv();
        int x = 0;
        switch (shiplv) {
            case 0:
                x = 1;
                break;
            case 1:
                x = 1;
                break;
            case 2:
                x = 2;
                break;
            case 3:
                x = 2;
                break;
            case 4:
                x = 3;
                break;
            case 5:
                x = 4;
                break;
        }
        if(action.equals("Blackstar")) x = 4;
        int mindis = 100;
        if(action.equals("bon2")) x+=3;
        if(action.equals("att16")) x+=3;
        for(int i = 0; i < list.size(); i++) {
            if(distance(list.get(i), location)<mindis) mindis = distance(list.get(i), location);
            if (distance(list.get(i), location) <= x) {
                available = true;
            }
        }
        if(!available){
            int q = play.getQ();
            int needq = (mindis - x+1)/2;
            if(q>=needq) {
                if(mq){
                    play.setQ(q-needq);
                    playMapper.updatePlayById(play);
                }
            }else return false;
        }
        return true;
    }

    @Override
    public String form(String gameid, String userid, String substring) {
        int towntype = 0;
        int yikongteshu = 7;
        Play play = playMapper.getPlayByGameIdUserid(gameid, userid);
        Game game = gameMapper.getGameById(gameid);
        if(play.getRace().equals("翼空族")&&!play.getSh().equals("0")) {
            yikongteshu=6;
            if((game.getGamemode().charAt(0)=='2'||game.getGamemode().charAt(0)=='3')&&!(game.getGamemode().length()==5&&game.getGamemode().charAt(4)=='2'))      yikongteshu=5;
        }

        if(substring.equals("terratop")){
            towntype = game.getTerratown();
        }else {
            String[] s = substring.split(",");
            towntype = Integer.parseInt(s[1].substring(5));
            if(towntype<1||towntype>6) return  "错误";
            if(this.getTownremain(gameid)[towntype-1]<=0) return "城片不足";
            String[][] mapdetail = new String[21][15];
            this.setMapDetail(mapdetail, gameid);
            String[][] SS = this.getStructureSituationById(gameid);
            String[][] SC = this.getStructureColorById(gameid);
            ArrayList<String> buildings = new ArrayList<>();//存放起城建筑位置
            boolean[][] ji = this.getJisheng(gameid);
            String racecolor = racecolormap.get(play.getRace());
            String[] locations = s[0].split(" ");
            ArrayList<String> ls = new ArrayList<>();
            for(String lo:locations) {
                if(ls.contains(lo)) return "错误";
                ls.add(lo);
            }
            if(!play.getRace().equals("蜂人")&&!allConnected(ls,play)) return "位置未连接或连接到其他城市";
            int totallevel = 0;
            int needsat = 0;
            int shaclevel = 3;
            HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"ltt8");
            if (tt != null&&!tt.getTtstate().equals("被覆盖")) shaclevel = 4;
            for (String location : locations) {
                if(!location.equals("")){
                    int row = (int) location.charAt(0) - 64;
                    int column = Integer.parseInt(location.substring(1));
                    if (ji[row][column]||!mapdetail[row][column].equals(ck)||location.equals(play.getBlackstar())||play.getRace().equals("蜂人")&&SS[row][column]!=null&&SS[row][column].equals("hive")||play.getRace().equals("殖民者")&&location.equals(play.getM1())) {
                        if(play.getRace().equals("亚特兰斯星人")&&ji[row][column]&&otherMapper.gettownbuilding(gameid, userid, location) == 0) {totallevel++;buildings.add(location);}else {
                            if (!SC[row][column].equals(racecolor)||SS[row][column].equals("gtu")) return "错误！";
                            if (!play.getRace().equals("蜂人") && otherMapper.gettownbuilding(gameid, userid, location) != 0)
                                return "错误！";
                            if (SS[row][column].equals("m")) totallevel++;
                            if (SS[row][column].equals("tc")) totallevel += 2;
                            if (SS[row][column].equals("rl")) totallevel += 2;
                            if (SS[row][column].equals("ac")) totallevel += shaclevel;
                            if (SS[row][column].equals("sh")) totallevel += shaclevel;
                            if (SS[row][column].equals("hive")) totallevel++;
                            if (play.getRace().equals("疯狂机器") && !play.getSh().equals("0") && mapdetail[row][column].equals(gr)) totallevel++;
                            buildings.add(location);
                        }
                    } else {
                        if(SS[row][column]!=null&&SS[row][column].equals("m")) return "错误";
                        needsat++;
                    }
                }
            }
/*            this.Minsate(buildings,gameid,userid);*/
     /*     if(needsat>this.Minsate(buildings,gameid,userid)&&!play.getRace().equals("蜂人")) return "卫星不是最优解";*/
            if(play.getRace().equals("蜂人")){
                if(play.getQ()<needsat) return "卫星不足！";
                play.setQ(play.getQ()-needsat);
                int hivetown = 0;
                if(!play.getM1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM1())!=0) hivetown++;
                if(!play.getM2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM2())!=0) hivetown++;
                if(!play.getM3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM3())!=0) hivetown++;
                if(!play.getM4().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM4())!=0) hivetown++;
                if(!play.getM5().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM5())!=0) hivetown++;
                if(!play.getM6().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM6())!=0) hivetown++;
                if(!play.getM7().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM7())!=0) hivetown++;
                if(!play.getM8().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM8())!=0) hivetown++;
                if(!play.getRacea1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea1())!=0) hivetown++;
                if(!play.getRacea2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea2())!=0) hivetown++;
                if(!play.getRacea3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea3())!=0) hivetown++;
                if(!play.getRacea4().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea4())!=0) hivetown++;
                if(!play.getRacea5().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea5())!=0) hivetown++;
                if(!play.getRacea6().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea6())!=0) hivetown++;
                if(!play.getTc1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc1())!=0) hivetown+=2;
                if(!play.getTc2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc2())!=0) hivetown+=2;
                if(!play.getTc3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc3())!=0) hivetown+=2;
                if(!play.getTc4().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc4())!=0) hivetown+=2;
                if(!play.getRl1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRl1())!=0) hivetown+=2;
                if(!play.getRl2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRl2())!=0) hivetown+=2;
                if(!play.getRl3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRl3())!=0) hivetown+=2;
                if(!play.getSh().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getSh())!=0) hivetown+=shaclevel;
                if(!play.getAc1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getAc1())!=0) hivetown+=shaclevel;
                if(!play.getAc2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getAc2())!=0) hivetown+=shaclevel;
                if(!play.getBlackstar().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getBlackstar())!=0) hivetown++;
                int townnum = otherMapper.gettownnum(gameid,userid);
                if(play.getTerralv()==5) townnum--;
                if(hivetown+totallevel<townnum*7+7) return "等级不够";
                for (String location : locations) {
                    if(!location.equals("")){
                        int row = (int) location.charAt(0) - 64;
                        int column = Integer.parseInt(location.substring(1));
                        if (mapdetail[row][column].equals(ck)&&!location.equals(play.getBlackstar())&&!(SS[row][column]!=null&&SS[row][column].equals("hive"))) {
                            otherMapper.insertSate(gameid, userid, location);
                        } else {
                            otherMapper.insertTB(gameid,userid,location);
                        }
                    }
                }
            }else{
                int power = play.getP1() + play.getP2() + play.getP3();
                if (totallevel >= yikongteshu && power >= needsat) {
                    if (play.getRace().equals("利爪族") && power >= needsat + 1 && play.getRacea1().equals("1")) {
                        play.setP1(play.getP1() - 1);
                    }
                    for (String location : locations) {
                        int row = (int) location.charAt(0) - 64;
                        int column = Integer.parseInt(location.substring(1));
                        if (mapdetail[row][column].equals(ck)&&!location.equals(play.getBlackstar())&&!ji[row][column]&&!location.equals(play.getM1())) {
                            if (play.getP1() != 0) {
                                play.setP1(play.getP1() - 1);
                            } else if (play.getP2() != 0) {
                                play.setP2(play.getP2() - 1);
                            } else {
                                play.setP3(play.getP3() - 1);
                            }
                            otherMapper.insertSate(gameid, userid, location);
                        } else {
                            otherMapper.insertTB(gameid, userid, location);
                        }
                    }
                    if(play.getRace().equals("利爪族")&&power>=needsat+1&&play.getRacea1().equals("1")){
                        play.setP1(play.getP1() + 1);
                    }
                } else {
                    return "错误！";
                }
            }
        }
        if(towntype==1){
            play.setO(play.getO()+2);
            otherMapper.gainVp(gameid,userid,7,"Town");
        }else if(towntype==2){
            play.setC(play.getC()+6);
            otherMapper.gainVp(gameid,userid,7,"Town");
        }else if(towntype==3){
            play.setQ(play.getQ()+1);
            otherMapper.gainVp(gameid,userid,8,"Town");
        }else if(towntype==4){
            play.setK(play.getK()+2);
            otherMapper.gainVp(gameid,userid,6,"Town");
        }else if(towntype==5){
            play.setP1(play.getP1()+2);
            otherMapper.gainVp(gameid,userid,8,"Town");
        }else if(towntype==6){
            otherMapper.gainVp(gameid,userid,12,"Town");
            otherMapper.insertHT(gameid,userid,towntype,"已翻面");
        }
        if(towntype!=6) otherMapper.insertHT(gameid,userid,towntype,"可用");
        String[] rs = this.getRoundScoreById(gameid);
        if(rs[game.getRound()-1].equals("TOWN>>5")) {otherMapper.gainVp(gameid,userid,5,"TOWN>>5");xizu(play,"TOWN");}
        playMapper.updatePlayById(play);
        //卫星与城市附近一格的建筑进城
        ArrayList<String> allbuildings = this.getBuildings(gameid,userid);
        ArrayList<String> townandsate = new ArrayList<>();
        String[] townbuildings = otherMapper.getTownBuildingByUserid(gameid,userid);
        String[] sates = otherMapper.getSatelliteByUserid(gameid,userid);
        Collections.addAll(townandsate, townbuildings);
        Collections.addAll(townandsate, sates);
        for (String location:allbuildings){
            int t = otherMapper.gettownbuilding(gameid,userid,location);
            if(t==0){
                for (String s:townandsate){
                    if(distance(location,s)==1) {otherMapper.insertTB(gameid,userid,location);break;}
                }
            }
        }
        if(!substring.equals("terratop")){
//            updateRecordById(gameid,play.getRace()+":form "+substring+".");
            updatePosition(gameid);
        }
        return "成功";
    }

    //判断卫星是否最少
    private int Minsate(ArrayList<String> locations,String gameid,String userid) {
        int[] adjust = new int[]{4,4,3,1,0,0,0,1,1,1,0,1,1,2,1,1,1,2,5,6};
        int[] exsit = {3,4,8,11,12,12,11,11,11,12,12,11,11,11,12,12,11,8,4,3}; //用四边形地图模拟
        String[][] mapdetail = new String[21][15];
        this.setMapDetail(mapdetail,gameid);
        mapForbidden = new ForbiddenMap();
        for (char i = 'A'; i <= 'T'; i++) {
            String currentPointString = String.valueOf(i) + String.valueOf(1);
            List<String> currentPointList = getList(currentPointString);//当前点的相邻坐标集合
            for (int j = 1; j <= exsit[i - 'A']; j++) {
                if(!mapdetail[i-'A'+1][j].equals(ck))  mapForbidden.setForbidden(currentPointString);
                String nextPointString = String.valueOf(i) + String.valueOf(j + 1);
                List<String> nextPointList = getList(nextPointString);
                if (j < exsit[i - 'A']) {
                    currentPointList.add(nextPointString);
                    nextPointList.add(currentPointString);
                } //将上下相邻坐标插入list
                if (i < 'T') {
                    for (int k = 1; k <= exsit[i - 'A' + 1]; k++) {
                        String pointString = String.valueOf((char)(i + 1)) + String.valueOf(k);
                      /*  System.out.println("now is "+ currentPointString + "next is "+ pointString);*/
                        if (distance(currentPointString, pointString) == 1) {
                            List<String> pointList = getList(pointString);
                            pointList.add(currentPointString);
                            currentPointList.add(pointString);
                        }
                    }//将右侧相邻坐标插入list
                }
                currentPointList = nextPointList;
                currentPointString = nextPointString;
            }
        }
        ArrayList<String> allbuildings = this.getBuildings(gameid,userid);//当前玩家所有建筑物坐标
        String[] allsates = otherMapper.getSatelliteByUserid(gameid,userid);//当前玩家所有卫星坐标
        for (String building:allbuildings){
            mapForbidden.setForbidden(building);//建筑不可连接卫星
            if(!locations.contains(building)){//不是该次起城建筑周围一圈不可连接卫星
                List<String> forbids = getList(building);
                for (String forbid:forbids){
                    mapForbidden.setForbidden(forbid);
                }
            }
        }
        for (String building:allsates){//已有卫星的位置与其周围一圈不可连接卫星
            mapForbidden.setForbidden(building);
            List<String> forbids = getList(building);
            for (String forbid:forbids){
                mapForbidden.setForbidden(forbid);
            }
        }
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (Play p:plays){//黑星位置不可连接卫星
            if(!p.getBlackstar().equals("0")) mapForbidden.setForbidden(p.getBlackstar());
        }
//        System.out.print(gameid+userid+getPointSetMinDistance(locations));
        return getPointSetMinDistance(locations);
    }


     List<String> getList(String src) {
        if (linkedMap.containsKey(src)) {
            return linkedMap.get(src);
        } else {
            List<String> newtList = new LinkedList<String>();
            linkedMap.put(src, newtList);
            return newtList;
        }
    }


    ArrayList<Rec> getPointMinDistance(String src, String tar) {//宽度优先搜索
        String[] actionArray = new String[ARR_LEN];//存储到达过的位置
        ForbiddenMap tmpForbidden = new ForbiddenMap();
        int[] dis = new int[ARR_LEN];//与actionarray相关，存储到达位置的行进距离
        ArrayList<String> roadList = new ArrayList<String>();//存储到达位置的所有路径
        ArrayList<Rec> result = new ArrayList<Rec>();//存储到达位置的所有路径
        int tail = 1;
        int head = 0;
        int mindis = -1;
        ArrayList<ArrayList<String>> roadLists = new ArrayList<ArrayList<String>>();
        roadLists.add(roadList);
        actionArray[tail] = src;
        dis[tail] = 0;
        while (head < tail) {//上来就死路了，那么tail不会增加，就此结束
            head++;//当前位置往后走一步，1是起点，2是1能到达的第一个位置，3有可能也是1能到达的位置，当前已行走距离存储在dis数组中
            String currentPointString = actionArray[head];
            int nowDis = dis[head];//当前已走的距离
            if(mindis!=-1&&nowDis>mindis) return result;
            List<String> currentPointList = getList(currentPointString);
            for (String nextPoint : currentPointList) {
                if (nextPoint.equals(tar)) {
                    if(mindis==-1){
                        result.add( new Rec(nowDis, 0, 0, roadLists.get(head - 1)));
                        mindis = nowDis;
                    }else if(nowDis==mindis){
                        result.add( new Rec(nowDis, 0, 0, roadLists.get(head - 1)));
                    }
                }
                if (!tmpForbidden.isForbidden(nextPoint) && !mapForbidden.isForbidden(nextPoint)) { // 地图不允许，或者节点已遍历，直接返回
                    tmpForbidden.setForbidden(nextPoint);//去除之后再走到此处的可能
                    tail++;
                    actionArray[tail] = nextPoint;//tail表示当前位置，head表示当前位置上一步位置
                    dis[tail] = nowDis + 1;//当前位置距离=上一步距离+1
                    ArrayList<String> road = new ArrayList<String>(roadLists.get(head - 1));//将当前上一步位置的道路取出加入此次新添加的位置放入road列表
                    road.add(nextPoint);
                    roadLists.add(road);
                }
            }
        }
        return null;
    }



     int getPointSetMinDistance(ArrayList<String> tarList) {
        int listLen = tarList.size();
        LinkedList<Rec> recs = new LinkedList<Rec>();
         LinkedList<ArrayList<Rec>> allrec = new  LinkedList<ArrayList<Rec>>();
        for (int i = 0; i < listLen; i++) {//获得每两点直接的最短路径
            for (int j = i+1; j < listLen; j++) {
//				//todo: 这里应该要一次剪枝
//				if (disMap.containsKey(new SrcTar(tarSet.get(i), tarSet.get(j)))) {
//					continue;
//				}
                ArrayList<Rec> rec = getPointMinDistance(tarList.get(i), tarList.get(j));
                allrec.add(rec);
                if (rec.get(0) != null) {
                    rec.get(0).setFrom(i);
                    rec.get(0).setTo(j);
                    recs.add(rec.get(0));
                }
            }
        }
        Collections.sort(recs, new Comparator<Rec>() {
            @Override
            public int compare(Rec o1, Rec o2) {
                return o1.dis - o2.dis;
            }
        });
        //爆搜求距离over
        //并查集实现最小生成树begin
        int father[] = new int[listLen];

        for (int i = 0; i < father.length; i++) {
            father[i] = i;
        }
        Set<String> res = new HashSet<String>();
        int num = 0;
        for (Rec rec : recs) {
            //并查集
            int dx = rec.from;
            int dy = rec.to;
            while (father[dx] != dx) {
                dx = father[dx];
            }
            while (father[dy] != dy) {
                dy = father[dy];
            }//各自找自己的老大
            if (dx != dy) {
/*                int oldfather = father[dx];
                for(int i=0;i<father.length;i++){
                    if(father[i]==oldfather) father[i]=dy;
                }*/
                father[dx] = dy;//数字较小的更改老大
                father[rec.from] = dy;//也许可以删除？
                res.addAll(rec.roadList);//加入卫星路径
                num++;
            }
            if (num == listLen - 1) {//都有共同老大，退出
                break;
            }
        }
        if (num < listLen - 1) {
            return 99999;
        } else {
            return res.size();
        }
    }



    public ArrayList<String> getBuildings(String gameid,String userid){
        ArrayList<String> result = new ArrayList<String>();
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(!play.getM1().equals("0"))result.add(play.getM1());
        if(!play.getM2().equals("0"))result.add(play.getM2());
        if(!play.getM3().equals("0"))result.add(play.getM3());
        if(!play.getM4().equals("0"))result.add(play.getM4());
        if(!play.getM5().equals("0"))result.add(play.getM5());
        if(!play.getM6().equals("0"))result.add(play.getM6());
        if(!play.getM7().equals("0"))result.add(play.getM7());
        if(!play.getM8().equals("0"))result.add(play.getM8());
        if(!play.getTc1().equals("0"))result.add(play.getTc1());
        if(!play.getTc2().equals("0"))result.add(play.getTc2());
        if(!play.getTc3().equals("0"))result.add(play.getTc3());
        if(!play.getTc4().equals("0"))result.add(play.getTc4());
        if(!play.getRl1().equals("0"))result.add(play.getRl1());
        if(!play.getRl2().equals("0"))result.add(play.getRl2());
        if(!play.getRl3().equals("0"))result.add(play.getRl3());
        if(!play.getAc1().equals("0"))result.add(play.getAc1());
        if(!play.getAc2().equals("0"))result.add(play.getAc2());
        if(!play.getSh().equals("0"))result.add(play.getSh());
        if(!play.getBlackstar().equals("0"))result.add(play.getBlackstar());
        return result;
    }

    @Override
    public ArrayList<String>[][] getSatellite(String gameid) {
        ArrayList[][] result = new ArrayList[21][15];
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 15; j++) {
                result[i][j] = new ArrayList<String>();
            }
        }
        Satellite[] satellites = otherMapper.getSatellite(gameid);
        for (Satellite sat : satellites) {
            String location = sat.getLocation();
            int row = (int)location.charAt(0)-64;
            int column = Integer.parseInt(location.substring(1));
            String userid = sat.getUserid();
            String color = racecolormap.get(playMapper.getPlayByGameIdUserid(gameid,userid).getRace());
            result[row][column].add(color);
        }
        return result;
    }


    @Override
    public ArrayList<String>[] getVpDetail(String gameid) {
        ArrayList[] result = new ArrayList[4];
        Play[] plays = playMapper.getPlayByGameId(gameid);
        int i = 0;
        HashMap<String,Integer> hashMap= new HashMap<String, Integer>();
        hashMap.put("起始分",0);
        hashMap.put("要塞",1);
        hashMap.put("要塞技能",2);
        hashMap.put("种族技能",3);
        hashMap.put("蹭魔",4);
        hashMap.put("M>>2",5);
        hashMap.put("M(G)>>3",6);
        hashMap.put("M(G)>>4",7);
        hashMap.put("TC>>3",8);
        hashMap.put("TC>>4",9);
        hashMap.put("SH/AC>>5",10);
        hashMap.put("TERRA>>2",11);
        hashMap.put("AT>>2",12);
        hashMap.put("TOWN>>5",13);
        hashMap.put("ltt5",14);
        hashMap.put("ltt7",15);
        hashMap.put("att4",16);
        hashMap.put("att5",17);
        hashMap.put("att6",18);
        hashMap.put("att7",19);
        hashMap.put("att8",20);
        hashMap.put("att9",21);
        hashMap.put("att10",22);
        hashMap.put("att11",23);
        hashMap.put("att12",24);
        hashMap.put("att13",25);
        hashMap.put("att14",26);
        hashMap.put("att15",27);
        hashMap.put("bon6",28);
        hashMap.put("bon7",29);
        hashMap.put("bon8",30);
        hashMap.put("bon9",31);
        hashMap.put("bon10",32);
        hashMap.put("q-top",33);
        hashMap.put("gaia-top",34);
        hashMap.put("Town",35);
        hashMap.put("action8",36);
        hashMap.put("action9",37);
        hashMap.put("终局-计分1",38);
        hashMap.put("终局-计分2",39);
        hashMap.put("终局-科技",40);
        hashMap.put("终局-资源",41);
        for(Play p: plays){
            ArrayList<String> list = new ArrayList<>();
            result[i] = list;
            Map<String,Integer> map = new HashMap<>();
            Vp[] vps = otherMapper.getVpByGameidUserid(gameid,p.getUserid());
            for (Vp v:vps){
                if(!map.containsKey(v.getReason())) {map.put(v.getReason(),v.getGainvp());}
                else{
                    int vp = map.get(v.getReason());
                    map.put(v.getReason(),vp+v.getGainvp());
                }
            }
            for (String s : map.keySet()){
                list.add(s+":"+String.valueOf(map.get(s)));
            }
            list.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    for(int i=0;i<o1.length();i++){
                        if(o1.charAt(i)==':') o1=o1.substring(0,i);
                    }
                    for(int i=0;i<o2.length();i++){
                        if(o2.charAt(i)==':') o2=o2.substring(0,i);
                    }
                    return hashMap.get(o1)-hashMap.get(o2);
                }
            });
            i++;
        }
        return result;
    }

    @Override
    public String convert(String gameid, String userid, String substring) {
        String[] resource = substring.split(" ");
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(play.getRace().equals("殖民者")&&resource[0].equals("fly")){
            String[] records = gameMapper.getGameById(gameid).getGamerecord().split("\\.");
     //todo
        }
        if(resource[0].charAt(resource[0].length()-1)=='o'&&resource[2].charAt(resource[2].length()-1)=='c'){
            int times = Integer.parseInt(resource[0].substring(0,resource[0].length()-1));
            if(play.getO()>=times) {
                play.setO(play.getO()-times);
                play.setC(play.getC()+times);
            }
            playMapper.updatePlayById(play);
            return "成功";
        }
        if(resource[0].charAt(resource[0].length()-1)=='k'&&resource[2].charAt(resource[2].length()-1)=='c'){
            int times = Integer.parseInt(resource[0].substring(0,resource[0].length()-1));
            if(play.getK()>=times) {
                play.setK(play.getK()-times);
                play.setC(play.getC()+times);
            }
            playMapper.updatePlayById(play);
            return "成功";
        }
        if(resource[0].charAt(resource[0].length()-1)=='q'&&resource[2].charAt(resource[2].length()-1)=='o'){
            int times = Integer.parseInt(resource[0].substring(0,resource[0].length()-1));
            if(play.getQ()>=times) {
                play.setQ(play.getQ()-times);
                play.setO(play.getO()+times);
            }
            playMapper.updatePlayById(play);
            return "成功";
        }
        if(resource[0].charAt(resource[0].length()-1)=='o'&&resource[2].substring(resource[2].length()-3).equals("pwb")){
            int times = Integer.parseInt(resource[0].substring(0,resource[0].length()-1));
            if(play.getO()>=times) {
                play.setO(play.getO()-times);
                play.setP1(play.getP1()+times);
            }
            playMapper.updatePlayById(play);
            return "成功";
        }
        if(play.getRace().equals("殖民者")) {
            if (substring.equals("2pw to 3c") && play.getP3() >= 2) {
                play.setP3(play.getP3() - 2);
                play.setP1(play.getP1() + 2);
                play.setC(play.getC() + 3);
                playMapper.updatePlayById(play);
                return "成功";
            }
        }
        if(play.getRace().equals("超星人")){
            if(substring.equals("2pw to 1o1c")&&play.getP3()>=2){
                play.setP3(play.getP3()-2);
                play.setP1(play.getP1()+2);
                play.setO(play.getO()+1);
                play.setC(play.getC()+1);
                playMapper.updatePlayById(play);
                return "成功";
            }
            if(substring.equals("1pwg to 1k")&&play.getP3()>=1){
                play.setP3(play.getP3()-1);
                play.setPg(play.getPg()+1);
                play.setK(play.getK()+1);
                playMapper.updatePlayById(play);
                return "成功";
            }
        }
        if(play.getRace().equals("伊塔星人")&&gameMapper.getGameById(gameid).getTurn()==1){
            if(substring.equals("convert p1p3 to 2p2")&&play.getP3()>=1&&play.getP1()>=1){
                play.setP3(play.getP3()-1);
                play.setP1(play.getP1()-1);
                play.setP2(play.getP2()+2);
                playMapper.updatePlayById(play);
                return "成功";
            }
        }
        if(substring.substring(1,3).equals("pw")){
            for(int i = 7;i<substring.length()-1;i+=2){
                char r = substring.charAt(i+1);
                if(r=='c') {if(!usePower(play,Integer.parseInt(substring.substring(i,i+1)))) return "错误";play.setC(play.getC()+Integer.parseInt(substring.substring(i,i+1))); playMapper.updatePlayById(play);
                    return "成功";}
                if(r=='o') {if(!usePower(play,3*Integer.parseInt(substring.substring(i,i+1)))) return "错误";play.setO(play.getO()+Integer.parseInt(substring.substring(i,i+1))); playMapper.updatePlayById(play);
                    return "成功";}
                if(r=='k') {if(!usePower(play,4*Integer.parseInt(substring.substring(i,i+1)))) return "错误";play.setK(play.getK()+Integer.parseInt(substring.substring(i,i+1))); playMapper.updatePlayById(play);
                    return "成功";}
                if(r=='q') {if(!usePower(play,4*Integer.parseInt(substring.substring(i,i+1)))) return "错误";play.setQ(play.getQ()+Integer.parseInt(substring.substring(i,i+1))); playMapper.updatePlayById(play);
                    return "成功";}
            }
        }
        if(substring.length()==5&&substring.substring(0,4).equals("burn")&&substring.charAt(4)>48&&substring.charAt(4)<58&&play.getP2()>=Integer.parseInt(substring.substring(4))*2){
            int times = Integer.parseInt(substring.substring(4));
            play.setP2(play.getP2()-times*2);
            if(play.getRace().equals("利爪族")&&play.getRacea1().equals("2"))play.setRacea1("3");
            play.setP3(play.getP3()+times);
            if(play.getRace().equals("伊塔星人"))
                play.setPg(play.getPg()+times);
            playMapper.updatePlayById(play);
            return "成功";
        }
        if(play.getRace().equals("圣禽族")&&!play.getSh().equals("0")){
            if(substring.equals("4c to 1q")&&play.getC()>=4){
                play.setC(play.getC()-4);
                play.setQ(play.getQ()+1);
                playMapper.updatePlayById(play);
                return "成功";
            }
            if(substring.equals("4c to 1k")&&play.getC()>=4){
                play.setC(play.getC()-4);
                play.setK(play.getK()+1);
                playMapper.updatePlayById(play);
                return "成功";
            }
            if(substring.equals("3c to 1o")&&play.getC()>=3){
                play.setC(play.getC()-3);
                play.setO(play.getO()+1);
                playMapper.updatePlayById(play);
                return "成功";
            }
        }
        if(play.getRace().equals("炽炎族")){
            int gtunum = 1;
            if(play.getGaialv()==3)  gtunum = 2;
            if(play.getGaialv()==4||play.getGaialv()==5)  gtunum = 3;
            if(substring.equals("1gtu to 1q")){
                if(gtunum>=1&&play.getGtu1().equals("0")) {
                    play.setQ(play.getQ() + 1);
                    play.setGtu1("pg");
                    playMapper.updatePlayById(play);
                    return "成功";
                }
                if(gtunum>=2&&play.getGtu2().equals("0")) {
                    play.setQ(play.getQ() + 1);
                    play.setGtu2("pg");
                    playMapper.updatePlayById(play);
                    return "成功";
                }
                if(gtunum>=3&&play.getGtu3().equals("0")) {
                    play.setQ(play.getQ() + 1);
                    play.setGtu3("pg");
                    playMapper.updatePlayById(play);
                    return "成功";
                }
            }
        }
        return "失败";
    }

    @Override
    public Play getPlayByGameidUserid(String gameid, String userid) {
        return playMapper.getPlayByGameIdUserid(gameid,userid);
    }

    @Override
    public String roundbeginaction(String gameid, String userid, String act) {
        //todo(人类+伊塔)
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(game.getTurn()!=0||act==null)return "错误";
        if(play.getRace().equals("人类")&&act.length()==3&&act.charAt(0)=='+'&&act.charAt(2)=='o'||act.charAt(2)=='k'||act.charAt(2)=='q'||act.charAt(2)=='c'){
            int power = Integer.parseInt(play.getRacea1());
            int times = Integer.parseInt(act.substring(1,2));
            if(act.charAt(2)=='o'&&times*3<=power){
                play.setRacea1(String.valueOf(power-times*3));
                play.setO(play.getO()+times);
                playMapper.updatePlayById(play);
            }else if(act.charAt(2)=='c'&&times*1<=power){
                play.setRacea1(String.valueOf(power-times*1));
                play.setC(play.getC()+times);
                playMapper.updatePlayById(play);
            }else if(act.charAt(2)=='k'&&times*4<=power){
                play.setRacea1(String.valueOf(power-times*4));
                play.setK(play.getK()+times);
                playMapper.updatePlayById(play);
            }else if(act.charAt(2)=='q'&&times*4<=power){
                play.setRacea1(String.valueOf(power-times*4));
                play.setQ(play.getQ()+times);
                playMapper.updatePlayById(play);
            }
        }else if(play.getRace().equals("伊塔星人")&&Integer.parseInt(play.getRacea1())>=4){
            if(act.equals("+pass")) {play.setRacea1("0");playMapper.updatePlayById(play);}
            else
            if(act.substring(0,5).equals("+pass")&&Integer.parseInt(act.substring(5))<=Integer.parseInt(play.getRacea1()))
            {play.setPg(play.getPg()-Integer.parseInt(act.substring(5)));play.setRacea1(String.valueOf(Integer.parseInt(play.getRacea1())-Integer.parseInt(act.substring(5))));play.setP1(play.getP1()+Integer.parseInt(act.substring(5)));playMapper.updatePlayById(play);}
            else {
                String[] acts = act.split(" ");
                if(acts.length==3) {if(!takett(gameid,userid,acts[0].substring(1),acts[2]))return "错误";}else
                if(acts.length==4) {if(!takeatt(gameid,userid,acts[0].substring(1),acts[1].substring(1),acts[3],"ltar")) return "错误";}else return "错误";
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                play.setRacea1(String.valueOf(Integer.parseInt(play.getRacea1())-4));
                play.setPg(play.getPg()-4);
                playMapper.updatePlayById(play);
            }

        }else {
            return "错误";
        }
        boolean ok = true;
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (Play p:plays){
            if(p.getRace().equals("人类")&&!p.getRacea1().equals("0")) ok = false;
            if(p.getRace().equals("伊塔星人")&&Integer.parseInt(p.getRacea1())>=4) ok = false;
        }
        if(ok){
            for (Play p:plays){
                if(p.getRace().equals("人类")) {p.setP2(p.getP2()+p.getPg());}else {
                    p.setP1(p.getP1()+p.getPg());
                }
                p.setPg(0);
                if(p.getRace().equals("炽炎族")){
                    if(p.getGtu1().equals("pg")) p.setGtu1("0");
                    if(p.getGtu2().equals("pg")) p.setGtu2("0");
                    if(p.getGtu3().equals("pg")) p.setGtu3("0");
                }
                playMapper.updatePlayById(p);
            }
            game.setTurn(1);
            gameMapper.updateGameById(game);
        }
        return "成功";
    }


    @Override
    public String[][] getStructureSituationById(String gameid) {
        String[][] result = new String[21][15];
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (int i=1;i<=3;i++){
            if(plays[i].getRace().equals("亚特兰斯星人")){
                Play p = plays[0];
                plays[0] = plays[i];
                plays[i] = p;
            }
        }
        for (int i = 0; i < 4; i++) {
            if(!plays[i].getM1().equals("0")) result[(int)plays[i].getM1().charAt(0)-64][Integer.parseInt(plays[i].getM1().substring(1))]="m";
            if(!plays[i].getM2().equals("0")) result[(int)plays[i].getM2().charAt(0)-64][Integer.parseInt(plays[i].getM2().substring(1))]="m";
            if(!plays[i].getM3().equals("0")) result[(int)plays[i].getM3().charAt(0)-64][Integer.parseInt(plays[i].getM3().substring(1))]="m";
            if(!plays[i].getM4().equals("0")) result[(int)plays[i].getM4().charAt(0)-64][Integer.parseInt(plays[i].getM4().substring(1))]="m";
            if(!plays[i].getM5().equals("0")) result[(int)plays[i].getM5().charAt(0)-64][Integer.parseInt(plays[i].getM5().substring(1))]="m";
            if(!plays[i].getM6().equals("0")) result[(int)plays[i].getM6().charAt(0)-64][Integer.parseInt(plays[i].getM6().substring(1))]="m";
            if(!plays[i].getM7().equals("0")) result[(int)plays[i].getM7().charAt(0)-64][Integer.parseInt(plays[i].getM7().substring(1))]="m";
            if(!plays[i].getM8().equals("0")) result[(int)plays[i].getM8().charAt(0)-64][Integer.parseInt(plays[i].getM8().substring(1))]="m";
            if(!plays[i].getBlackstar().equals("0")) result[(int)plays[i].getBlackstar().charAt(0)-64][Integer.parseInt(plays[i].getBlackstar().substring(1))]="m";
            if(!plays[i].getTc1().equals("0")) result[(int)plays[i].getTc1().charAt(0)-64][Integer.parseInt(plays[i].getTc1().substring(1))]="tc";
            if(!plays[i].getTc2().equals("0")) result[(int)plays[i].getTc2().charAt(0)-64][Integer.parseInt(plays[i].getTc2().substring(1))]="tc";
            if(!plays[i].getTc3().equals("0")) result[(int)plays[i].getTc3().charAt(0)-64][Integer.parseInt(plays[i].getTc3().substring(1))]="tc";
            if(!plays[i].getTc4().equals("0")) result[(int)plays[i].getTc4().charAt(0)-64][Integer.parseInt(plays[i].getTc4().substring(1))]="tc";
            if(!plays[i].getRl1().equals("0")) result[(int)plays[i].getRl1().charAt(0)-64][Integer.parseInt(plays[i].getRl1().substring(1))]="rl";
            if(!plays[i].getRl2().equals("0")) result[(int)plays[i].getRl2().charAt(0)-64][Integer.parseInt(plays[i].getRl2().substring(1))]="rl";
            if(!plays[i].getRl3().equals("0")) result[(int)plays[i].getRl3().charAt(0)-64][Integer.parseInt(plays[i].getRl3().substring(1))]="rl";
            if(!plays[i].getSh().equals("0")) result[(int)plays[i].getSh().charAt(0)-64][Integer.parseInt(plays[i].getSh().substring(1))]="sh";
            if(!plays[i].getAc1().equals("0")) result[(int)plays[i].getAc1().charAt(0)-64][Integer.parseInt(plays[i].getAc1().substring(1))]="ac";
            if(!plays[i].getAc2().equals("0")) result[(int)plays[i].getAc2().charAt(0)-64][Integer.parseInt(plays[i].getAc2().substring(1))]="ac";
            if(!plays[i].getGtu1().equals("0")&&!plays[i].getGtu1().equals("pg")) result[(int)plays[i].getGtu1().charAt(0)-64][Integer.parseInt(plays[i].getGtu1().substring(1))]="gtu";
            if(!plays[i].getGtu2().equals("0")&&!plays[i].getGtu2().equals("pg")) result[(int)plays[i].getGtu2().charAt(0)-64][Integer.parseInt(plays[i].getGtu2().substring(1))]="gtu";
            if(!plays[i].getGtu3().equals("0")&&!plays[i].getGtu3().equals("pg")) result[(int)plays[i].getGtu3().charAt(0)-64][Integer.parseInt(plays[i].getGtu3().substring(1))]="gtu";
            if(!plays[i].getBlackstar().equals("0")) result[(int)plays[i].getBlackstar().charAt(0)-64][Integer.parseInt(plays[i].getBlackstar().substring(1))]="m";
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea1().equals("")) result[(int)plays[i].getRacea1().charAt(0)-64][Integer.parseInt(plays[i].getRacea1().substring(1))]="hive";
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea2().equals("")) result[(int)plays[i].getRacea2().charAt(0)-64][Integer.parseInt(plays[i].getRacea2().substring(1))]="hive";
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea3().equals("")) result[(int)plays[i].getRacea3().charAt(0)-64][Integer.parseInt(plays[i].getRacea3().substring(1))]="hive";
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea4().equals("")) result[(int)plays[i].getRacea4().charAt(0)-64][Integer.parseInt(plays[i].getRacea4().substring(1))]="hive";
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea5().equals("")) result[(int)plays[i].getRacea5().charAt(0)-64][Integer.parseInt(plays[i].getRacea5().substring(1))]="hive";
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea6().equals("")) result[(int)plays[i].getRacea6().charAt(0)-64][Integer.parseInt(plays[i].getRacea6().substring(1))]="hive";
        }
        return result;
    }

    @Override
    public String[][] getStructureColorById(String gameid) {
        String[][] result = new String[21][15];
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (int i=1;i<=3;i++){
            if(plays[i].getRace().equals("亚特兰斯星人")){
                Play p = plays[0];
                plays[0] = plays[i];
                plays[i] = p;
            }
        }
        for (int i = 0; i < 4; i++) {
            String color = racecolormap.get(plays[i].getRace());
            if(!plays[i].getM1().equals("0")) result[(int)plays[i].getM1().charAt(0)-64][Integer.parseInt(plays[i].getM1().substring(1))]=color;
            if(!plays[i].getM2().equals("0")) result[(int)plays[i].getM2().charAt(0)-64][Integer.parseInt(plays[i].getM2().substring(1))]=color;
            if(!plays[i].getM3().equals("0")) result[(int)plays[i].getM3().charAt(0)-64][Integer.parseInt(plays[i].getM3().substring(1))]=color;
            if(!plays[i].getM4().equals("0")) result[(int)plays[i].getM4().charAt(0)-64][Integer.parseInt(plays[i].getM4().substring(1))]=color;
            if(!plays[i].getM5().equals("0")) result[(int)plays[i].getM5().charAt(0)-64][Integer.parseInt(plays[i].getM5().substring(1))]=color;
            if(!plays[i].getM6().equals("0")) result[(int)plays[i].getM6().charAt(0)-64][Integer.parseInt(plays[i].getM6().substring(1))]=color;
            if(!plays[i].getM7().equals("0")) result[(int)plays[i].getM7().charAt(0)-64][Integer.parseInt(plays[i].getM7().substring(1))]=color;
            if(!plays[i].getM8().equals("0")) result[(int)plays[i].getM8().charAt(0)-64][Integer.parseInt(plays[i].getM8().substring(1))]=color;
            if(!plays[i].getBlackstar().equals("0")) result[(int)plays[i].getBlackstar().charAt(0)-64][Integer.parseInt(plays[i].getBlackstar().substring(1))]=color;
            if(!plays[i].getTc1().equals("0")) result[(int)plays[i].getTc1().charAt(0)-64][Integer.parseInt(plays[i].getTc1().substring(1))]=color;
            if(!plays[i].getTc2().equals("0")) result[(int)plays[i].getTc2().charAt(0)-64][Integer.parseInt(plays[i].getTc2().substring(1))]=color;
            if(!plays[i].getTc3().equals("0")) result[(int)plays[i].getTc3().charAt(0)-64][Integer.parseInt(plays[i].getTc3().substring(1))]=color;
            if(!plays[i].getTc4().equals("0")) result[(int)plays[i].getTc4().charAt(0)-64][Integer.parseInt(plays[i].getTc4().substring(1))]=color;
            if(!plays[i].getRl1().equals("0")) result[(int)plays[i].getRl1().charAt(0)-64][Integer.parseInt(plays[i].getRl1().substring(1))]=color;
            if(!plays[i].getRl2().equals("0")) result[(int)plays[i].getRl2().charAt(0)-64][Integer.parseInt(plays[i].getRl2().substring(1))]=color;
            if(!plays[i].getRl3().equals("0")) result[(int)plays[i].getRl3().charAt(0)-64][Integer.parseInt(plays[i].getRl3().substring(1))]=color;
            if(!plays[i].getSh().equals("0")) result[(int)plays[i].getSh().charAt(0)-64][Integer.parseInt(plays[i].getSh().substring(1))]=color;
            if(!plays[i].getAc1().equals("0")) result[(int)plays[i].getAc1().charAt(0)-64][Integer.parseInt(plays[i].getAc1().substring(1))]=color;
            if(!plays[i].getAc2().equals("0")) result[(int)plays[i].getAc2().charAt(0)-64][Integer.parseInt(plays[i].getAc2().substring(1))]=color;
            if(!plays[i].getGtu1().equals("0")&&!plays[i].getGtu1().equals("pg")) result[(int)plays[i].getGtu1().charAt(0)-64][Integer.parseInt(plays[i].getGtu1().substring(1))]=color;
            if(!plays[i].getGtu2().equals("0")&&!plays[i].getGtu2().equals("pg")) result[(int)plays[i].getGtu2().charAt(0)-64][Integer.parseInt(plays[i].getGtu2().substring(1))]=color;
            if(!plays[i].getGtu3().equals("0")&&!plays[i].getGtu3().equals("pg")) result[(int)plays[i].getGtu3().charAt(0)-64][Integer.parseInt(plays[i].getGtu3().substring(1))]=color;
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea1().equals("")) result[(int)plays[i].getRacea1().charAt(0)-64][Integer.parseInt(plays[i].getRacea1().substring(1))]=color;
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea2().equals("")) result[(int)plays[i].getRacea2().charAt(0)-64][Integer.parseInt(plays[i].getRacea2().substring(1))]=color;
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea3().equals("")) result[(int)plays[i].getRacea3().charAt(0)-64][Integer.parseInt(plays[i].getRacea3().substring(1))]=color;
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea4().equals("")) result[(int)plays[i].getRacea4().charAt(0)-64][Integer.parseInt(plays[i].getRacea4().substring(1))]=color;
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea5().equals("")) result[(int)plays[i].getRacea5().charAt(0)-64][Integer.parseInt(plays[i].getRacea5().substring(1))]=color;
            if(plays[i].getRace().equals("蜂人")&&!plays[i].getRacea6().equals("")) result[(int)plays[i].getRacea6().charAt(0)-64][Integer.parseInt(plays[i].getRacea6().substring(1))]=color;
        }
        return result;
    }

    public boolean[][] getJisheng(String gameid){
        boolean[][] result = new boolean[21][15];
        String[][] sc = this.getStructureColorById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for(Play p:plays){
            if(p.getRace().equals("亚特兰斯星人")){
                String location = "";
                if(!p.getM1().equals("0")){
                    location = p.getM1();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM2().equals("0")){
                    location = p.getM2();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM3().equals("0")){
                    location = p.getM3();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM4().equals("0")){
                    location = p.getM4();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM5().equals("0")){
                    location = p.getM5();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM6().equals("0")){
                    location = p.getM6();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM7().equals("0")){
                    location = p.getM7();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
                if(!p.getM8().equals("0")){
                    location = p.getM8();
                    if(!sc[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))].equals(bl))
                        result[(int)location.charAt(0)-64][Integer.parseInt(location.substring(1))] = true;
                }
            }
        }
        return result;
    }

    @Override
    public boolean[][][] getTownBuilding(String gameid) {
        boolean[][][] result = new boolean[21][15][2];
        boolean[][] jisheng = this.getJisheng(gameid);
        TownBuilding[] buildings = otherMapper.getAllTownBuilding(gameid);
        for(TownBuilding tb:buildings){
            int row = (int)tb.getLocation().charAt(0)-64;
            int column = Integer.parseInt(tb.getLocation().substring(1));
            Play play = playMapper.getPlayByGameIdUserid(gameid,tb.getUserid());
            if(jisheng[row][column]&&play.getRace().equals("亚特兰斯星人")) {result[row][column][1]=true;}else {
                result[row][column][0] = true;
            }
        }
        return result;
    }

    @Override
    public int gethiveno(String gameid,String userid) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int shaclevel = 3;
        HaveTt tt = otherMapper.getTtByGameidUseridTtno(gameid,userid,"ltt8");
        if (tt != null && !tt.getTtstate().equals("被覆盖")) shaclevel = 4;
        int hivetown = 0;
        if(!play.getM1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM1())!=0) hivetown++;
        if(!play.getM2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM2())!=0) hivetown++;
        if(!play.getM3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM3())!=0) hivetown++;
        if(!play.getM4().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM4())!=0) hivetown++;
        if(!play.getM5().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM5())!=0) hivetown++;
        if(!play.getM6().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM6())!=0) hivetown++;
        if(!play.getM7().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM7())!=0) hivetown++;
        if(!play.getM8().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getM8())!=0) hivetown++;
        if(!play.getBlackstar().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getBlackstar())!=0) hivetown++;
        if(!play.getRacea1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea1())!=0) hivetown++;
        if(!play.getRacea2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea2())!=0) hivetown++;
        if(!play.getRacea3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea3())!=0) hivetown++;
        if(!play.getRacea4().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea4())!=0) hivetown++;
        if(!play.getRacea5().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea5())!=0) hivetown++;
        if(!play.getRacea6().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRacea6())!=0) hivetown++;
        if(!play.getTc1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc1())!=0) hivetown+=2;
        if(!play.getTc2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc2())!=0) hivetown+=2;
        if(!play.getTc3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc3())!=0) hivetown+=2;
        if(!play.getTc4().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getTc4())!=0) hivetown+=2;
        if(!play.getRl1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRl1())!=0) hivetown+=2;
        if(!play.getRl2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRl2())!=0) hivetown+=2;
        if(!play.getRl3().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getRl3())!=0) hivetown+=2;
        if(!play.getSh().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getSh())!=0) hivetown+=shaclevel;
        if(!play.getAc1().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getAc1())!=0) hivetown+=shaclevel;
        if(!play.getAc2().equals("0")&&otherMapper.gettownbuilding(gameid,userid,play.getAc2())!=0) hivetown+=shaclevel;
        return hivetown;
    }

    @Override
    //更改记录时的蹭魔
    public String LeechPower(String gameid, String giverace, String receiverace,int num) {
        Power[] p = otherMapper.getPowerByIdCR(gameid,giverace,receiverace);
        if(p.length>=2&&!p[0].getStructure().equals("BlackStar")&&!p[1].getStructure().equals("BlackStar")) return "错误";
        String userid = playMapper.getUseridByRace(gameid,receiverace);
        Play play = playMapper.getPlayByGameIdRace(gameid,receiverace);
        if(num == 0/*&&!(play.getRace().equals("利爪族")&&!play.getSh().equals("0"))*/){
            otherMapper.deletePowerByIdCR(gameid,receiverace);return "成功";
        }
            int power = num;
            int rpower = power;
            int p1 = play.getP1();
            int p2 = play.getP2();
            int p3 = play.getP3();
            boolean tak = true;
            if(num>p1*2+p2&&play.getRace().equals("利爪族")&&!play.getSh().equals("0")) {p1++;tak = false;}
            while(power!=0&&p1!=0){
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("1")) play.setRacea1("2");
                power--;
                p1--;
                p2++;
            }
            while(power!=0&&p2!=0){
                if(play.getRace().equals("利爪族")&&play.getRacea1().equals("2")) play.setRacea1("3");
                power--;
                p2--;
                p3++;
            }
            int vp =(rpower-power-1);
            if(vp<0) vp=0;
            if(play.getRace().equals("利爪族")&&!play.getSh().equals("0")&&tak) p1++;
            if(vp!=0){
                otherMapper.gainVp(gameid,userid,-vp,"蹭魔");
            }
            playMapper.updatePlayById(play);
            playMapper.updatePowerOld(gameid,userid,p1,p2,p3,play.getPg());
            otherMapper.deletePowerByIdCR(gameid,receiverace);
            return "成功";
    }

    @Override
    //用于修改记录时的删除蹭魔
    public void deletePower(String gameid) {
        otherMapper.deleteAllPower(gameid);
    }

    @Override
    public void updateRecordByIdCR(String gameid, String record) {
        this.gameMapper.updateRecordByIdCR(gameid,record);
    }

    @Override
    public void updateLasttime(String gameid, String userid) {
        String time = String.valueOf(System.currentTimeMillis());
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if (play != null) {
            long waittime = Long.valueOf(play.getTime()) + System.currentTimeMillis() - Long.valueOf(game.getLasttime());
            if (waittime<5000) waittime = 0;
            play.setTime(String.valueOf(waittime));
            playMapper.updatePlayById(play);
        }
        gameMapper.updateLasttime(gameid,time);
    }

    @Override
    public String getuseridByGameidRace(String gameid, String race) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (Play p:plays){
            if(p.getRace().equals(race)) return p.getUserid();
        }
        return null;
    }

    @Override
    public ArrayList<String> getFasts(String gameid, String userid) {
        Game game = gameMapper.getGameById(gameid);
        if(game.getGamemode().charAt(2)=='3') userid = this.getCurrentUserIdById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        ArrayList<String> result = new ArrayList<>();
        for(Play p:plays){
            if(userid.equals(p.getUserid())){
                 if(p.getRace().equals("超星人")&&!p.getSh().equals("0")){
                     if(p.getP3()>=1) result.add("convert 1pw to 2c");
                     if(p.getP3()>=2) result.add("convert 2pw to 1o1c");
                     if(p.getP3()>=2) result.add("convert 2pw to 1k");
                     if(p.getP3()>=2) result.add("convert 2pw to 1q");
                     if(p.getP3()>=3) result.add("convert 3pw to 2o");
                 }else if(p.getRace().equals("利爪族")&&p.getRacea1().equals("3")){
                     if(p.getP3()>=1) {
                         result.add("convert 1pw to 1c");
                         result.add("convert 3pw to 3c");
                         result.add("convert 3pw to 1o");
                     }
                     if(p.getP3()>=2) {
                         result.add("convert 4pw to 1k");
                         result.add("convert 4pw to 1q");
                     }
                 }else {
                     if(p.getP3()>=2&&p.getRace().equals("殖民者"))result.add("convert 2pw to 3c");
                     if(p.getP3()>=1) result.add("convert 1pw to 1c");
                     if(p.getP3()>=3) result.add("convert 3pw to 1o");
                     if(p.getP3()>=4) result.add("convert 4pw to 1k");
                     if(p.getP3()>=4) result.add("convert 4pw to 1q");
                 }
                 if(p.getRace().equals("伊塔星人")&&p.getP3()>=1&&p.getP1()>=1&&game.getTurn()==1) result.add("convert p1p3 to 2p2");
                 if(p.getRace().equals("超星人")&&p.getP3()>=1) result.add("convert 1pwg to 1k");
                 if(p.getP2()>=2) result.add("convert burn1");
                int firegtu = 0;
                switch (p.getGaialv()){
                    case 0: firegtu=0;break;
                    case 1: firegtu=1;break;
                    case 2: firegtu=1;break;
                    case 3: firegtu=2;break;
                    case 4: firegtu=3;break;
                    case 5: firegtu=3;break;
                }
                if(!p.getGtu1().equals("0")){
                    firegtu--;
                }
                if(!p.getGtu2().equals("0")){
                    firegtu--;
                }
                if(!p.getGtu3().equals("0")){
                    firegtu--;
                }
                if(p.getRace().equals("炽炎族")&&firegtu>=1) result.add("convert 1gtu to 1q");
                if(p.getQ()>=1) result.add("convert 1q to 1o");
                if(p.getO()>=1) {result.add("convert 1o to 1c");result.add("convert 1o to 1pwb");}
                if(p.getK()>=1) result.add("convert 1k to 1c");
                if(p.getRace().equals("圣禽族")&&!p.getSh().equals("0")){
                    if(p.getC()>=3) result.add("convert 3c to 1o");
                    if(p.getC()>=4) result.add("convert 4c to 1k");
                    if(p.getC()>=4) result.add("convert 4c to 1q");
                }
            }
        }
        return result;
    }

    @Override
    public Power[] getAllpower(String gameid) {
        return otherMapper.getAllPowerById(gameid);
    }

    @Override
    public String[] getBid(String gameid) {
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        String[] result = new String[13];
        result[0] = "f";
        int hasbidnum = 0;
        if(plays.length!=4){

        }
        for (int i=0;i<=3;i++){
            Play p = plays[i];
            if(p.getBidpo()!=0) hasbidnum++;
            result[1+i] = p.getUserid();
            result[5+i] = String.valueOf(p.getBidvp());
            result[9+i] = String.valueOf(p.getBidpo());
        }
        if(hasbidnum<3) result[0] = "t";
        if(game.getGamemode().charAt(2)!='0'&&game.getGamemode().charAt(2)!='4')result[0] = "f";
        return result;
    }

    @Override
    public void bid(String gameid, String bidid, String action) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,bidid);
        if(this.getCurrentUserIdById(gameid).equals("all")){
            //没叫分的，叫分
            if(play.getBidvp()==-1) {play.setBidvp(Integer.parseInt(action));}
            playMapper.updatePlayById(play);
            Play[] plays = playMapper.getPlayByGameId(gameid);
            ArrayList<Integer> list = new ArrayList<Integer>();
            String biduserid = "";
            int bidvp = 0;
            for (Play p:plays){
                if(p.getBidvp()==-1) return;
                if(p.getBidpo()!=0)  continue;
                list.add(p.getBidvp());
                if(p.getBidvp()>= bidvp) {bidvp = p.getBidvp();biduserid = p.getUserid();}
            }
            Collections.sort(list);
            Play p = playMapper.getPlayByGameIdUserid(gameid,biduserid);
            p.setBidvp(list.get(list.size()-2)+1);
            playMapper.updatePlayById(p);
        } //选顺位
        else if(this.getCurrentUserIdById(gameid).equals(bidid)){
            if(Integer.parseInt(action)!=1&&Integer.parseInt(action)!=2&&Integer.parseInt(action)!=3&&Integer.parseInt(action)!=4) return;
            Play[] plays = playMapper.getPlayByGameId(gameid);
            for(Play p:plays){
                if(Integer.parseInt(action)==p.getBidpo()) return;
            }
            play.setBidpo(Integer.parseInt(action));
            playMapper.updatePlayById(play);
            plays = playMapper.getPlayByGameId(gameid);
            int hasbid = 0;
            for (Play p:plays){
                if(p.getBidpo()==0) p.setBidvp(-1);
                if(p.getBidpo()!=0) hasbid++;
                playMapper.updatePlayById(p);
            }
            if(hasbid ==3){
              otherMapper.deleteiniscore(gameid);
              HashSet<Integer> position = new HashSet<>();
              position.add(1);position.add(2);position.add(3);position.add(4);
              for (Play p:plays){
                  if(p.getBidpo()!=0) {
                      otherMapper.gainVp(gameid,p.getUserid(),10-p.getBidvp(),"起始分");
                      p.setPosition(p.getBidpo());
                      playMapper.updatePlayById(p);
                      position.remove(p.getBidpo());
                  }
              }
                for (Play p:plays){
                    if(p.getBidpo()==0){
                        otherMapper.gainVp(gameid,p.getUserid(),10,"起始分");
                        int po = 0;
                        for(int i=1;i<=4;i++){
                            if(position.contains(i)) po=i;
                        }
                        p.setPosition(po);
                        playMapper.updatePlayById(p);
                    }
                }
                plays = playMapper.getPlayByGameId(gameid);
                String record = "Game Start.";
                Arrays.sort(plays, new Comparator<Play>() {
                    @Override
                    public int compare(Play o1, Play o2) {
                        if(o1.getPass()==0&&o2.getPass()!=0) return -1;
                        if(o1.getPass()!=0&&o2.getPass()==0) return 1;
                        if(o1.getPass()!=0&&o2.getPass()!=0) return o1.getPass()-o2.getPass();
                        return o1.getPosition()-o2.getPosition();
                    }
                });
                record+="Player1: "+plays[0].getUserid()+".";
                record+="Player2: "+plays[1].getUserid()+".";
                record+="Player3: "+plays[2].getUserid()+".";
                record+="Player4: "+plays[3].getUserid()+".";
                gameMapper.updateRecordByIdCR(gameid,record);
            }
        }
    }

    @Override
    public void rotate(String gameid, String action) {
        Game game = gameMapper.getGameById(gameid);
        if(!action.equals("ok")){
            String mapseed = game.getMapseed();
            for (int i=0;i<=9;i++){
                if(mapseed.charAt(i*2)==action.charAt(0)){
                    if(i==9){
                        if(mapseed.charAt(i*2+1)=='5') {mapseed = mapseed.substring(0,i*2+1) + '0';}
                        else {
                             mapseed = mapseed.substring(0,i*2+1) + (mapseed.charAt(i*2+1)-47) ;
                        }
                    }else {
                        if(mapseed.charAt(i*2+1)=='5') {mapseed = mapseed.substring(0,i*2+1) + '0' + mapseed.substring(i*2+2);}
                        else {
                            mapseed = mapseed.substring(0,i*2+1) + (mapseed.charAt(i*2+1)-47) + mapseed.substring(i*2+2);
                        }
                    }

                }
            }
            gameMapper.updateMapseed(gameid,mapseed);
        }else {
            String mapseed = game.getMapseed();
            String[][] mapDetail = new String[21][16];
            for (int i = 1; i <= 20 ; i++) {
                for (int j = 1; j < 15; j++) {
                    mapDetail[i][j]="";
                }
            }
            for (int i = 0; i < 10; i++) {
                int spaceNo = (int)mapseed.charAt(i*2)-48;
                int rorateTime = (int)mapseed.charAt(i*2+1)-48;
                setColor(mapDetail,i,spaceNo,rorateTime);
            }
            for (int i = 1; i <= 20 ; i++) {
                for (int j = 1; j < 15; j++) {
                    if(mapDetail[i][j].equals("#000000")||mapDetail[i][j].equals("#9400d3")) mapDetail[i][j]+="i"+i+"j:"+j;
                }
            }
//            System.out.println(mapOk(mapDetail));
            if(mapOk(mapDetail)) {game.setBlackstar("done");gameMapper.updaterotate(gameid);}
        }
    }

    @Override
    public PendingGame getPGameById(String gameId) {
        return gameMapper.getPGamebyId(gameId);
    }

    @Override
    public void createPGame(String gameId, String player1, String player2, String player3, String player4, String gamemode, String des) {
        gameMapper.addPGame(gameId,player1,player2,player3,player4,gamemode,des);
    }

    @Override
    public void deletePGGame(String gameid) {
        gameMapper.deletePGame(gameid);
    }

    @Override
    //0:无任何1：bon1/action2 2：action6 3：bon2
    public ArrayList<ArrayList<String>> getBugs(String gameid, String userid) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        Game game = gameMapper.getGameById(gameid);
        if(game.getGamemode().charAt(2)=='3') userid = this.getCurrentUserIdById(gameid);
        if(!userid.equals(plays[0].getUserid())&&!userid.equals(plays[1].getUserid())&&!userid.equals(plays[2].getUserid())&&!userid.equals(plays[3].getUserid())) return null;
        String[][] mapdetail = new String[21][15];
        String[][] structure = new String[21][15];
        structure = this.getStructureSituationById(gameid);
        this.setMapDetail(mapdetail,gameid);
        Play p = playMapper.getPlayByGameIdUserid(gameid,userid);
        ArrayList<ArrayList<String>> result = new  ArrayList<ArrayList<String>>();
        for (int i=0;i<=3;i++){
            result.add(new ArrayList<String>());
        }
        for (int i = 1; i <= 20; i++) {
            for (int j = 1; j <= 12; j++) {
                String location = (char)(i+64)+String.valueOf(j);
                //所有行动
                if(mapdetail[i][j]!=null&&!mapdetail[i][j].equals(ck)){
                    //build行动
                    if(game.getRound().equals(0)){
                        if(mapdetail[i][j].equals(racenummap.get(p.getRace())))result.get(0).add("build "+location);
                    }else {
                        if(!p.getRace().equals("亚特兰斯星人")){
                            if(buildMine(gameid,userid,location,"bug").equals("成功")) result.get(0).add("build "+location);
                        }else {

                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getRecordByGameid(String gameid) {
        return gameMapper.getRecordById(gameid);
    }

    @Override
    public void updateTime(String gameid, String lastMove, String currentUser) {
        String time = String.valueOf(System.currentTimeMillis());
        Play play = playMapper.getPlayByGameIdUserid(gameid,currentUser);
        if (play != null) {
            long waittime = Long.valueOf(play.getTime()) + System.currentTimeMillis() - Long.valueOf(lastMove);
            play.setTime(String.valueOf(waittime));
            playMapper.updatePlayById(play);
        }
        gameMapper.updateLasttime(gameid,time);
    }

    public static void setColor(String[][] mapDetail,int location,int spaceNo,int rotateTime){
        String[][] spaceNos = new String[10][];
        spaceNos[0]=spaceNo0;spaceNos[1]=spaceNo1;spaceNos[2]=spaceNo2;spaceNos[3]=spaceNo3;spaceNos[4]=spaceNo4;
        spaceNos[5]=spaceNo5;spaceNos[6]=spaceNo6;spaceNos[7]=spaceNo7;spaceNos[8]=spaceNo8;spaceNos[9]=spaceNo9;
        int[][] locations = new int[10][];
        locations[0]=location1;locations[1]=location2;locations[2]=location3;locations[3]=location4;locations[4]=location5;
        locations[5]=location6;locations[6]=location7;locations[7]=location8;locations[8]=location9;locations[9]=location10;
        int[][] rotates = new int[6][];
        rotates[0]=rotate0;rotates[1]=rotate1;rotates[2]=rotate2;rotates[3]=rotate3;rotates[4]=rotate4;rotates[5]=rotate5;
        for(int i=0;i<19;i++){
        mapDetail[locations[location][i*2]][locations[location][i*2+1]]=spaceNos[spaceNo][rotates[rotateTime][i]];
    }
    }
    public static boolean mapOk(String[][] m){
        if(m[3][1].equals(m[4][3])||
                m[4][4].equals(m[5][4])|| m[4][4].equals(m[4][3])|| m[5][4].equals(m[5][5])|| m[5][5].equals(m[6][5])|| m[5][5].equals(m[6][6])||
                m[5][6].equals(m[6][6])|| m[5][6].equals(m[6][7])|| m[5][7].equals(m[6][7])|| m[5][7].equals(m[6][8])|| m[5][7].equals(m[5][8])||
                m[4][7].equals(m[5][8])|| m[4][7].equals(m[4][8])|| m[3][5].equals(m[4][8])|| m[3][5].equals(m[3][6])|| m[6][5].equals(m[6][6])||
                m[6][5].equals(m[7][5])|| m[7][4].equals(m[7][5])|| m[7][4].equals(m[8][4])|| m[8][3].equals(m[8][4])|| m[8][3].equals(m[9][3])||
                m[8][3].equals(m[9][2])|| m[8][2].equals(m[9][2])|| m[8][2].equals(m[9][1])|| m[8][1].equals(m[9][1])|| m[5][8].equals(m[6][8])||
                m[6][9].equals(m[6][8])|| m[6][9].equals(m[7][8])|| m[7][9].equals(m[7][8])|| m[7][9].equals(m[8][8])|| m[7][9].equals(m[8][9])||
                m[7][10].equals(m[8][9])|| m[7][10].equals(m[8][10])|| m[7][11].equals(m[8][10])|| m[7][11].equals(m[8][11])||
                m[8][4].equals(m[9][3])|| m[9][4].equals(m[9][3])|| m[9][4].equals(m[10][4])|| m[10][4].equals(m[10][5])|| m[10][5].equals(m[11][5])||
                m[10][5].equals(m[11][6])|| m[11][5].equals(m[11][6])|| m[10][6].equals(m[11][6])|| m[10][6].equals(m[11][7])|| m[10][7].equals(m[11][7])|| m[10][7].equals(m[11][8])||
                m[10][7].equals(m[10][8])||m[9][7].equals(m[10][8])||m[9][7].equals(m[9][8])||m[8][8].equals(m[9][8])||m[8][8].equals(m[8][9])
                ||m[11][8].equals(m[10][8])||m[11][8].equals(m[11][9])||m[11][9].equals(m[10][8])||m[11][9].equals(m[12][8])||m[12][9].equals(m[12][8])||m[12][9].equals(m[13][8])
                ||m[12][9].equals(m[13][9])||m[12][10].equals(m[13][9])||m[12][10].equals(m[13][10])||m[12][11].equals(m[13][10])||m[12][11].equals(m[13][11])
                ||m[11][5].equals(m[12][5])||m[12][4].equals(m[12][5])||m[12][4].equals(m[13][4])||m[13][3].equals(m[13][4])||m[13][3].equals(m[14][3])
                ||m[13][3].equals(m[14][2])|| m[13][2].equals(m[14][2])|| m[13][1].equals(m[14][1])|| m[13][4].equals(m[14][3])
                ||m[14][3].equals(m[14][4])||m[15][4].equals(m[14][4])||m[15][4].equals(m[15][5])||m[16][5].equals(m[15][5])||m[16][5].equals(m[16][6])
                ||m[16][5].equals(m[17][5])||m[17][4].equals(m[17][5])||m[17][4].equals(m[18][4])||m[18][4].equals(m[18][3])||m[15][5].equals(m[16][6])
                ||m[15][6].equals(m[16][6])||m[15][6].equals(m[16][7])||m[15][7].equals(m[16][7])||m[15][7].equals(m[16][8])||m[15][8].equals(m[16][8])
                ||m[16][8].equals(m[16][9])||m[17][8].equals(m[16][9])||m[17][8].equals(m[17][9])||m[18][8].equals(m[17][9])||m[13][8].equals(m[13][9])
                ||m[13][8].equals(m[14][8])||m[14][8].equals(m[14][7])||m[14][7].equals(m[15][8])||m[15][7].equals(m[15][8])||m[13][2].equals(m[14][1])
        ) return false;
        return true;
    }

    //判断起城位置是否都相连
    public boolean allConnected(ArrayList<String> locations,Play play){
        boolean[] connect = new boolean[locations.size()];
        ArrayList<String> stack = new ArrayList<>();
        stack.add(locations.get(0));
        connect[0] = true;
        while(!stack.isEmpty()){
            String location = stack.remove(0);
            for(int i=0;i<locations.size();i++){
                if(distance(location,locations.get(i))==1&&!connect[i]) {
                    connect[i] = true;
                    stack.add(locations.get(i));
            }
        }
    }
    for(boolean b:connect){
        if(!b) return false;
    }
    //判断是不是与其他已有城市位置相连
        ArrayList<String> townlocation = new ArrayList<>();
        String[]  townbuildings = otherMapper.getTownBuildingByUserid(play.getGameid(),play.getUserid());
        String[]  sates = otherMapper.getSatelliteByUserid(play.getGameid(),play.getUserid());
        for(int i=0;i<locations.size();i++){
            for(String s:townbuildings) {
                if(distance(s,locations.get(i))<=1) return false;
            }
            for(String s:sates) {
                if(distance(s,locations.get(i))<=1) return false;
            }
        }
    return true;
}
}



