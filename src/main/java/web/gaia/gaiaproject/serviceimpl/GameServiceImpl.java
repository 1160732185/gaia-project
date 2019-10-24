package web.gaia.gaiaproject.serviceimpl;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.OtherMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.model.*;
import web.gaia.gaiaproject.service.GameService;

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
    OtherMapper otherMapper;

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
    public void createGame(String gameId, String player1, String player2, String player3, String player4) {
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
            System.out.println("失败！");
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
        while(contain.size()!=4){
            int place = random.nextInt(4)+1;
            if(!contain.contains(place)) {
                contain.add(place);
            }
        }
        //创建对局
        gameMapper.createGame(gameId,terratown,mapseed,otherseed);
        //创建玩家游戏信息
        playMapper.insertPlay(gameId,player1,contain.get(0));
        otherMapper.gainVp(gameId,player1,20,"起始分");
        playMapper.insertPlay(gameId,player2,contain.get(1));
        otherMapper.gainVp(gameId,player2,20,"起始分");
        playMapper.insertPlay(gameId,player3,contain.get(2));
        otherMapper.gainVp(gameId,player3,20,"起始分");
        playMapper.insertPlay(gameId,player4,contain.get(3));
        otherMapper.gainVp(gameId,player4,20,"起始分");
        String[] players = new String[]{player1,player2,player3,player4};
        for (int i = 1; i <= 4 ; i++) {
            gameMapper.updateRecordById(gameId,"Player"+i+": "+players[contain.indexOf(i)]+".");
        }
    }

    @Override
    public Game getGameById(String gameId) {
        Game game = gameMapper.getGameById(gameId);
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
            int row = (int)location.charAt(0)-64;
            int column = Integer.parseInt(location.substring(1));
            mapDetail[row][column]=ga;
        }
    }

    @Override
    public void updateRecordById(String gameid, String record) {
        this.gameMapper.updateRecordById(gameid,record);
    }

    @Override
    public String getGameStateById(String gameid) {
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        String record = game.getGamerecord();
        System.out.println("record:"+record);
        String[] records = record.split("\\.");
        String state = new String();
        System.out.println("recordslength:"+records.length);
        if(records.length<21){
            for (int i = 5; i <= 8 ; i++) {
                //todo 蜂人与异空
                if(records.length==i) state = "轮到玩家："+records[i-4].substring(8)+"选择种族";
            }
            for (int i = 9; i <= 16 ; i++) {
                if(i<=12&&records.length==i) state = "轮到"+plays[i-9].getRace()+"建造初始矿场";
                if(i>=13&&records.length==i) state = "轮到"+plays[16-i].getRace()+"建造初始矿场";
            }
            for (int i = 17; i <= 20 ; i++) {
                if(records.length==i) state = "轮到玩家："+records[21-i].substring(8)+"选择第一回合助推板";
            }
        }else{
            Play p = playMapper.selectPlayByGameIdPosition(gameid,game.getPosition());
            state = "Round:"+game.getRound()+"Turn:"+game.getTurn()+":轮到"+p.getRace()+"执行行动";
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
        return helptile;
    }

    @Override
    public boolean[] getAvaraceById(String gameid) {
        boolean[] races = new boolean[14];
        for (int i = 0; i < 14; i++) {
            races[i]=true;
        }
        Game game = gameMapper.getGameById(gameid);
        int mapseed = Integer.parseInt(game.getMapseed().substring(6,15));
        int[] zhi = new int[]{397,379,347,331,317,233,211,199,197,157,149,109,89,71};
        int a = 0;
        int chu = 0;
        while(a!=4){
        int n = (mapseed/zhi[chu])%14;
        if(races[n]){
            a++;races[n]=false;
            if(n%2==0) races[n+1]=false;
            if(n%2==1) races[n-1]=false;
        }
        chu++;
        }
        String[] records =  game.getGamerecord().split("\\.");
        Play[] play = playMapper.getPlayByGameId(gameid);
        for(Play p:play) {
            if (p.getRace() !=null) {
                if (p.getRace().equals("人类") || p.getRace().equals("亚特兰斯星人")) races[0] = races[1] = true;
                if (p.getRace().equals("圣禽族") || p.getRace().equals("蜂人")) races[2] = races[3] = true;
                if (p.getRace().equals("晶矿星人") || p.getRace().equals("炽炎族")) races[4] = races[5] = true;
                if (p.getRace().equals("翼空族") || p.getRace().equals("格伦星人")) races[6] = races[7] = true;
                if (p.getRace().equals("大使星人") || p.getRace().equals("利爪族")) races[8] = races[9] = true;
                if (p.getRace().equals("章鱼人") || p.getRace().equals("疯狂机器")) races[10] = races[11] = true;
                if (p.getRace().equals("伊塔星人") || p.getRace().equals("超星人")) races[12] = races[13] = true;
            }
        }
        return races;
    }

    @Override
    public String[] getTTByid(String gameid) {
        String[] result = new String[18];
        TechTile[] techtiles =gameMapper.getTTById(gameid);
        TechTile[] techTiles = techtiles.clone();//高级科技板专用
        techTiles[1]=techtiles[7];
        techTiles[2]=techtiles[8];
        techTiles[3]=techtiles[9];
        techTiles[4]=techtiles[10];
        techTiles[5]=techtiles[11];
        techTiles[6]=techtiles[12];
        techTiles[7]=techtiles[13];
        techTiles[8]=techtiles[14];
        techTiles[9]=techtiles[1];
        techTiles[10]=techtiles[2];
        techTiles[11]=techtiles[3];
        techTiles[12]=techtiles[4];
        techTiles[13]=techtiles[5];
        techTiles[14]=techtiles[6];

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
            result[i]=techTiles[Integer.parseInt(attseed.substring(i,i+1),16)-1].getTtno()+": "+
                    techTiles[Integer.parseInt(attseed.substring(i,i+1),16)-1].getTtname();
        }
        for (int i = 6; i < 15; i++) {
            result[i]=techtiles[Integer.parseInt(lttseed.substring(i-6,i-5),16)+14].getTtno()+": "+
                    techtiles[Integer.parseInt(lttseed.substring(i-6,i-5),16)+14].getTtname();
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
        String[] records = game.getGamerecord().split("\\.");
        String[] users = playMapper.getUseridByGameId(gameid);
        if(records.length<=20){
            if(records.length==13||records.length==17) return users[3];
            if(records.length==14||records.length==18) return users[2];
            if(records.length==15||records.length==19) return users[1];
            if(records.length==16||records.length==20) return users[0];
            if(records.length%4==1){
                return users[0];
            }else if(records.length%4==2){
                return users[1];
            }else if(records.length%4==3){
                return users[2];
            }else {
                return users[3];
            }
        }else{
            int order = game.getPosition();
            return playMapper.selectPlayByGameIdPosition(gameid,order).getUserid();
        }
    }

    public void updatePosition(String gameid){
        Game game = gameMapper.getGameById(gameid);
        int position = game.getPosition()+1;
        if(position==5) {
            position=1;
            gameMapper.updateTurnById(gameid,game.getTurn()+1);
    }
        Play[] plays = playMapper.getPlayByGameId(gameid);
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
        Arrays.sort(play, new Comparator<Play>() {
            @Override
            public int compare(Play o1, Play o2) {
                if(o1.getPass()==0&&o2.getPass()!=0) return -1;
                if(o1.getPass()!=0&&o2.getPass()==0) return 1;
                if(o1.getPass()!=0&&o2.getPass()!=0) return o1.getPass()-o2.getPass();
                return o1.getPosition()-o2.getPosition();
            }
        });
        String[][] result =new String[4][20];
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
                result[i][11] = String.valueOf(otherMapper.getvp(gameid,result[i][0]));
                if(play[i].getPass()!=0)result[i][10] = "(passed)";
//                if (play[i].getRace().equals("人类") || play[i].getRace().equals("亚特兰斯星人")) result[i][9] = "#4275e5";
//                if (play[i].getRace().equals("圣禽族") || play[i].getRace().equals("蜂人")) result[i][9] = "#FF0000";
//                if (play[i].getRace().equals("晶矿星人") || play[i].getRace().equals("炽炎族")) result[i][9] = "#FF8C00";
//                if (play[i].getRace().equals("异空族") || play[i].getRace().equals("格伦星人")) result[i][9] = "#ffd700";
//                if (play[i].getRace().equals("大使星人") || play[i].getRace().equals("利爪族")) result[i][9] = "#8b4c39";
//                if (play[i].getRace().equals("章鱼人") || play[i].getRace().equals("疯狂机器")) result[i][9] = "#828282";
//                if (play[i].getRace().equals("伊塔星人") || play[i].getRace().equals("超星人")) result[i][9] = "#FFFFFF";
        }
    return result;
    }

    @Override
    public void chooseRace(String gameid, String userid, String race) {
        gameMapper.updateRecordById(gameid,userid+":choose race:"+race+".");
        playMapper.playerChooseRace(gameid,userid,race);
        System.out.println(race);
        playMapper.setInitResource(raceinitresource[racenummap.get(race)][0],raceinitresource[racenummap.get(race)][1],raceinitresource[racenummap.get(race)][2]
        ,raceinitresource[racenummap.get(race)][3],raceinitresource[racenummap.get(race)][4],raceinitresource[racenummap.get(race)][5],gameid,userid);
        if(race.equals("人类")||race.equals("炽炎族")) playMapper.advanceGaia(gameid,userid);
        if(race.equals("晶矿星人")) playMapper.advanceTerra(gameid,userid);
        if(race.equals("格伦星人")||race.equals("大使星人")) playMapper.advanceShip(gameid,userid);
        if(race.equals("翼空族")) playMapper.advanceQ(gameid,userid);
        if(race.equals("圣禽族")) playMapper.advanceEco(gameid,userid);
        if(race.equals("超星人")) playMapper.advanceSci(gameid,userid);
    }

    @Override
    public String buildMine(String gameid, String userid, String location,String action) {
        Game game = this.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        String[][] mapdetail = new String[21][15];
        int costo = 1;
        int costq = 0;
        int terrascoretile = 0;
        boolean hasgtu = false;
        this.setMapDetail(mapdetail,gameid);
            String racecolor = racecolormap.get(play.getRace());
            int row = (int)location.charAt(0)-64;
            int column = Integer.parseInt(location.substring(1));
     if(play.getGtu1().equals(location)||play.getGtu2().equals(location)||play.getGtu3().equals(location)){
         hasgtu = true;
         if(play.getGtu1().equals(location)) play.setGtu1("0");
         if(play.getGtu2().equals(location)) play.setGtu2("0");
         if(play.getGtu3().equals(location)) play.setGtu3("0");
        }else if(mapdetail[row][column].equals(racecolor) && game.getRound() == 0){}
         else{
         String[][] sd = this.getStructureSituationById(gameid);
         if(sd[row][column]!=null) return "已有其他建筑";
         if (!mapdetail[row][column].equals(racecolor) && game.getRound() == 0) return "请建造在母星上！";
         if (mapdetail[row][column].equals(pu) || mapdetail[row][column].equals(ck) || mapdetail[row][column].equals(""))
             return "建造位置不合法！";
         //改造费用是否足够
         if (!mapdetail[row][column].equals(ga)) {
             int racecolornum = colorroundmap.get(racecolor);
             int locationcolornum = colorroundmap.get(mapdetail[row][column]);
             int diff = racecolornum > locationcolornum ? Math.min(racecolornum - locationcolornum, 7 + locationcolornum - racecolornum) : Math.min(locationcolornum - racecolornum, 7 + racecolornum - locationcolornum);
             terrascoretile = diff;
             if(action.equals("action2")) diff--;
             if(action.equals("action6")) diff-=2;
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
             if (game.getRound() != 0 && (play.getO() < 1 + t * diff || play.getC() < 2)) return "你的资源不够了！";
             costo += 3 * diff;
         }
         else if(!hasgtu){
             costq += 1;
             if(play.getQ() < 1 || play.getC() < 2 || play.getO() < 1) {
                 return "你的资源不够了！";
             }
         }else {
             if(play.getC() < 2 || play.getO() < 1) {
                 return "你的资源不够了！";
             }
         }
         if(!canArrive(gameid,userid,location)) return "距离不够！";
     }
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
                return"矿场已建满！";
            }

            //计分
        String[] rs = this.getRoundScoreById(gameid);
        if(mapdetail[row][column].equals(ga)&&rs[game.getRound()-1].equals("M(G)>>3")) otherMapper.gainVp(gameid,userid,3,"M(G)>>3");
        if(mapdetail[row][column].equals(ga)&&rs[game.getRound()-1].equals("M(G)>>4")) otherMapper.gainVp(gameid,userid,4,"M(G)>>4");
        if(game.getRound()!=0&&rs[game.getRound()-1].equals("M>>2")) otherMapper.gainVp(gameid,userid,2,"M>>2");
       if(terrascoretile!=0&&rs[game.getRound()-1].equals("TERRA>>2")) otherMapper.gainVp(gameid,userid,2*terrascoretile,"TERRA>>2");
            if(game.getRound()!=0){
                play.setO(play.getO()-costo);
                play.setQ(play.getQ()-costq);
                play.setC(play.getC()-2);
                playMapper.updatePlayById(play);
                createPower(gameid,userid,location,"M");
                updatePosition(gameid);
            }
        playMapper.updatePlayById(play);
        updateRecordById(gameid,play.getRace()+":"+action+" build "+location+".");
        return "建造成功";
    }

    private boolean createPower(String gameid, String userid, String location, String structure) {
        Play[] play = playMapper.getPlayByGameId(gameid);
        Play give = playMapper.getPlayByGameIdUserid(gameid,userid);
        boolean hascreate = false;
        for (int i = 0; i < play.length; i++) {
            if(play[i].getRace().equals(give.getRace())) continue;
            int power = 0;
            if(distance(play[i].getAc1(),location)<=2||distance(play[i].getAc2(),location)<=2||distance(play[i].getSh(),location)<=2) {power=3;}else
            if(distance(play[i].getTc1(),location)<=2||distance(play[i].getTc2(),location)<=2||distance(play[i].getTc3(),location)<=2||distance(play[i].getTc4(),location)<=2||distance(play[i].getRl1(),location)<=2||distance(play[i].getRl2(),location)<=2||distance(play[i].getRl3(),location)<=2) {power=2;}else
            if(distance(play[i].getM1(),location)<=2||distance(play[i].getM2(),location)<=2||distance(play[i].getM3(),location)<=2||distance(play[i].getM4(),location)<=2||distance(play[i].getM5(),location)<=2||distance(play[i].getM6(),location)<=2||distance(play[i].getM7(),location)<=2||distance(play[i].getM8(),location)<=2) {power=1;}
            if(power!=0) {otherMapper.insertPower(gameid,give.getRace(),play[i].getRace(),location,structure,power);
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
            if(distance(play[i].getM1(),location)<=2||distance(play[i].getM2(),location)<=2||distance(play[i].getM3(),location)<=2||distance(play[i].getM4(),location)<=2||distance(play[i].getM5(),location)<=2||distance(play[i].getM6(),location)<=2||distance(play[i].getM7(),location)<=2||distance(play[i].getM8(),location)<=2) {power=1;}
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

    @Override
    public String pass(String gameid, String userid, String bon) {
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int bonusno = Integer.parseInt(bon);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (int i = 0; i < plays.length; i++) {
            if(bonusno==plays[i].getBonus()) return "选择回合助推板错误！";
        }
/*        String[][] avahelptile = this.getHelpTileById(gameid);*/
        playMapper.updateBonusById(gameid,userid,bonusno);
        gameMapper.updateRecordById(gameid,play.getRace()+":pass: bon"+bon+".");
        if(game.getRound()==0&&play.getPosition()==1){
               gameMapper.roundEnd(gameid);
               this.income(gameid,true);
               return null;
        }else if(game.getRound()!=0){
            int passedplayers = playMapper.selectPassNo(gameid);
            playMapper.updatePassNo(gameid,userid,playMapper.selectPassNo(gameid)+1);
            //TODO 结算havett表中的bon，显示到前端
            if(passedplayers==3){
                gameMapper.roundEnd(gameid);
                for (Play p:plays){
                    if(!p.getGtu1().equals("0")&&otherMapper.getGaia(gameid,p.getGtu1())==0) otherMapper.insertGaia(gameid,p.getGtu1());
                    if(!p.getGtu2().equals("0")&&otherMapper.getGaia(gameid,p.getGtu2())==0) otherMapper.insertGaia(gameid,p.getGtu2());
                    if(!p.getGtu3().equals("0")&&otherMapper.getGaia(gameid,p.getGtu3())==0) otherMapper.insertGaia(gameid,p.getGtu3());
                }
                this.income(gameid,true);
                playMapper.roundEnd(gameid);
                playMapper.roundEnd2(gameid);
                //todo 各种收入
                //盖亚池出来
                plays = playMapper.getPlayByGameId(gameid);
                for (Play p:plays) {
                    p.setP1(p.getP1()+p.getPg());
                    p.setPg(0);
                    playMapper.updatePlayById(p);
                }
                return null;
            }
        }
        updatePosition(gameid);
        return null;
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
        }
        return p;
    }

    @Override
    public String leechPower(String gameid, String receiverace, String location, String structure, String accept) {
        Power p = otherMapper.getPowerById(gameid,receiverace,location,structure);
        String userid = playMapper.getUseridByRace(gameid,receiverace);
        if(p==null) return "蹭魔错误！";
        if(accept.equals("1")) {
            Play play = playMapper.getPlayByGameIdRace(gameid,receiverace);
            int power = p.getPower();
            int rpower = power;
            int p1 = play.getP1();
            int p2 = play.getP2();
            int p3 = play.getP3();
            while(power!=0&&p1!=0){
                power--;
                p1--;
                p2++;
            }
            while(power!=0&&p2!=0){
                power--;
                p2--;
                p3++;
            }
            int vp = play.getVp();
            vp-=(rpower-power-1);
            otherMapper.gainVp(gameid,userid,1+power-rpower,"蹭魔");
            playMapper.updatePower(gameid,userid,p1,p2,p3,play.getPg());
            gameMapper.updateRecordById(gameid,receiverace+"接受"+"因"+location+"升级为"+structure+"而获得"+(rpower-power)+"点魔力.");
        }
        otherMapper.deletePowerById(gameid,receiverace,location,structure);
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

                if (play.getO() < 2 || play.getC() < 3) return "资源不足！";
                if(!tc3or6(gameid,userid,strs[0])&&play.getC() < 6) return "资源不足！";

                //计分
               String[] rs = this.getRoundScoreById(gameid);
               if(rs[game.getRound()-1].equals("TC>>3")) otherMapper.gainVp(gameid,userid,3,"TC>>3");
               if(rs[game.getRound()-1].equals("TC>>4")) otherMapper.gainVp(gameid,userid,4,"TC>>4");
                Method method = playclass.getMethod("setM" + String.valueOf(mno), String.class);
                method.invoke(play,"0");
                method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
                method.invoke(play,strs[0]);
                if(tc3or6(gameid,userid,strs[0])) {play.setC(play.getC()-3);play.setO(play.getO()-2);}
                if(!tc3or6(gameid,userid,strs[0])) {play.setC(play.getC()-6);play.setO(play.getO()-2);}
                playMapper.updatePlayById(play);
            }else
        if(strs[2].equals("rl")){
            if(!structureSituation[rowint][columnint].equals("tc")||strs.length!=6&&strs.length!=7||!strs[3].equals("advance")) return "操作不合法！";
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
            if(strs.length==6){
                ok = this.takett(gameid,userid,strs[5].substring(1),strs[4]);
            }else if(strs.length==7){
                ok = this.takeatt(gameid,userid,strs[5].substring(1),strs[6].substring(1),strs[4]);
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
            if(!structureSituation[rowint][columnint].equals("tc")||!play.getSh().equals("0")) return "操作不合法！";
            int tcno = 0;
            Class playclass = Play.class;
            for (int i = 1; i <= 4; i++) {
                Method method = playclass.getMethod("getTc" + String.valueOf(i));
                String location = (String) method.invoke(play);
                if (location.equals("0")) {
                    tcno = i;
                    break;
                }
            }
            if (tcno == 0) {
                return "操作不合法！";
            }
            if (play.getO() < 4 || play.getC() < 6) return "资源不足！";
            String[] rs = this.getRoundScoreById(gameid);
            if(rs[game.getRound()-1].equals("SH/AC>>5")) otherMapper.gainVp(gameid,userid,5,"SH/AC>>5");
            play.setO(play.getO()-4);
            play.setC(play.getC()-6);
            Method method = playclass.getMethod("setTc" + String.valueOf(tcno), String.class);
            method.invoke(play,"0");
            play.setSh(strs[0]);
            playMapper.updatePlayById(play);
        }else
        if(strs[2].equals("ac1")||strs[2].equals("ac2")){
            if(strs[2].equals("ac1")&&!play.getAc1().equals("0"))return "操作不合法";
            if(strs[2].equals("ac2")&&!play.getAc2().equals("0"))return "操作不合法";
            if(!structureSituation[rowint][columnint].equals("rl")||strs.length!=6&&strs.length!=7||!strs[3].equals("advance")) return "操作不合法！";
            int rlno = 0;
            Class playclass = Play.class;
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
            boolean ok = false;
            if (play.getO() < 6 || play.getC() < 6) return "资源不足！";
            if(strs.length==6){
                ok = this.takett(gameid,userid,strs[5].substring(1),strs[4]);
            }else if(strs.length==7){
                ok = this.takeatt(gameid,userid,strs[5].substring(1),strs[6].substring(1),strs[4]);
            }
            if(ok){
                play.setO(play.getO()-6);
                play.setC(play.getC()-6);
                Method method = playclass.getMethod("setRl" + String.valueOf(rlno), String.class);
                method.invoke(play,"0");
                if(strs[2].equals("ac1"))play.setAc1(strs[0]);
                if(strs[2].equals("ac2"))play.setAc2(strs[0]);
                playMapper.updatePlayById(play);
                String[] rs = this.getRoundScoreById(gameid);
                if(rs[game.getRound()-1].equals("SH/AC>>5")) otherMapper.gainVp(gameid,userid,5,"SH/AC>>5");
            }else{return "操作不合法！";}
        }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        createPower(gameid,userid,strs[0],strs[2]);
        updatePosition(gameid);
        updateRecordById(gameid,play.getRace()+":upgrade "+substring+".");
        return null;
    }

    @Override
    public int[][] income(String gameid,boolean b) {
        //b为flase仅显示，b为true就进行收入
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        int[][] bc = this.getBuildingcount(gameid);
        int[][] income = new int[4][6];
        int i = 0;
        for (Play p:plays){
            int getpower = 0;
            int getpowerbean = 0;
            HaveTt[] haveTts = otherMapper.getHaveTtByUserid(gameid,p.getUserid());
            for (HaveTt ht : haveTts){
                if(ht.getTtno().equals("ltt1")) income[i][1]+=4;
                if(ht.getTtno().equals("ltt3")) {income[i][1]+=1;income[i][2]+=1;}
                if(ht.getTtno().equals("ltt4")) {income[i][0]+=1;getpower++;}

            }
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
            switch (bc[i][1]){
                case 0: income[i][1]+=0;break;
                case 1: income[i][1]+=3;break;
                case 2: income[i][1]+=7;break;
                case 3: income[i][1]+=11;break;
                case 4: income[i][1]+=16;break;
            }
            if(p.getRace().equals("超星人")){
                switch(bc[i][2]){
                    case 0: income[i][2]+=1;break;
                    case 1: income[i][2]+=1;getpower+=2;break;
                    case 2: income[i][2]+=1;getpower+=4;break;
                    case 3: income[i][2]+=1;getpower+=6;break;
                }
            }else {
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
                    case 5: income[i][0]+=1;getpowerbean++;break;
                    case 6: income[i][0]+=1;getpower+=4;break;
                    case 7: income[i][0]+=1;break;
                    case 8: income[i][2]+=1;break;
                    case 9: getpower+=4;break;
                    case 10:income[i][1]+=4;break;
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
                }else {
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
            if(p.getRace().equals("圣禽族")) income[i][1]+=3;

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
                    if(p1!=0) {p1--;p2++;getpower--;}
                    else if(p2!=0){p2--;p3++;getpower--;}
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
        String ltt = gameMapper.getGameById(gameid).getOtherseed().split(" ")[2];
        if(!techtile.substring(0,3).equals("ltt")||!science.equals("terra")&&!science.equals("gaia")&&!science.equals("ship")&&!science.equals("q")&&!science.equals("eco")&&!science.equals("sci")) return false;
        if(techtile.charAt(3)<=48||techtile.charAt(3)>=58||techtile.length()!=4) {return false;}
        else{
            int avatown = 0;//todo,判断是否有可用城片
            if(ltt.charAt(0)==techtile.charAt(3)&&!science.equals("terra")) return false;
            if(ltt.charAt(1)==techtile.charAt(3)&&!science.equals("ship")) return false;
            if(ltt.charAt(2)==techtile.charAt(3)&&!science.equals("q")) return false;
            if(ltt.charAt(3)==techtile.charAt(3)&&!science.equals("gaia")) return false;
            if(ltt.charAt(4)==techtile.charAt(3)&&!science.equals("eco")) return false;
            if(ltt.charAt(5)==techtile.charAt(3)&&!science.equals("sci")) return false;
            otherMapper.insertHaveTt(gameid,userid,techtile,"可用");
            advance(gameid,userid,science,false);
        }
        return true;
    }

    @Override
    public boolean takeatt(String gameid, String userid, String att, String ltt, String tech) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
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
        if(c==gameatt.charAt(0)&&play.getTerralv()>3||c==gameatt.charAt(1)&&play.getShiplv()>3||
                c==gameatt.charAt(2)&&play.getQlv()>3||c==gameatt.charAt(3)&&play.getGaialv()>3||
                c==gameatt.charAt(4)&&play.getEcolv()>3||c==gameatt.charAt(5)&&play.getScilv()>3){
            avatown[0].setTtstate("已翻面");
            otherMapper.updateHaveTownById(avatown[0]);
            otherMapper.lttfugai(gameid,userid,ltt);
            otherMapper.insertHaveTt(gameid,userid,att,"可用");
            this.advance(gameid,userid,tech,false);
            return true;
        }
        return false;
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
                    }else{
                        result[i][a[i]][0]=t.getTtno();
                    }
                    result[i][a[i]][1]=t.getTtstate();
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
        boolean[] sciencehastop = new boolean[6];
        for(Play p:plays){
            if(p.getTerralv()==5) sciencehastop[0]=true;
            if(p.getShiplv()==5) sciencehastop[1]=true;
            if(p.getQlv()==5) sciencehastop[2]=true;
            if(p.getGaialv()==5) sciencehastop[3]=true;
            if(p.getEcolv()==5) sciencehastop[4]=true;
            if(p.getScilv()==5) sciencehastop[5]=true;
        }
        HaveTown[] avatown = otherMapper.getAvaHTByGameIdUserId(gameid,userid);//Todo,以及城片翻面的操作实现
        if(needk&&play.getK()<4) return "科技不足！";
        if(needk) play.setK(play.getK()-4);
        if(science.equals("terra")&&play.getTerralv()!=5&&!(play.getTerralv()==4&&avatown.length==0)&&!sciencehastop[0]) {
            if(play.getTerralv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            playMapper.advanceTerra(gameid,userid);}
        if(science.equals("ship")&&play.getShiplv()!=5&&!(play.getShiplv()==4&&avatown.length==0)&&!sciencehastop[1]) {
            if(play.getShiplv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            playMapper.advanceShip(gameid,userid);}
        if(science.equals("q")&&play.getQlv()!=5&&!(play.getQlv()==4&&avatown.length==0)&&!sciencehastop[2]) {
            if(play.getQlv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            playMapper.advanceQ(gameid,userid);}
        if(science.equals("gaia")&&play.getGaialv()!=5&&!(play.getGaialv()==4&&avatown.length==0)&&!sciencehastop[3]) {
            if(play.getGaialv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            playMapper.advanceGaia(gameid,userid);}
        if(science.equals("eco")&&play.getEcolv()!=5&&!(play.getEcolv()==4&&avatown.length==0)&&!sciencehastop[4]) {
            if(play.getEcolv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            playMapper.advanceEco(gameid,userid);}
        if(science.equals("sci")&&play.getScilv()!=5&&!(play.getScilv()==4&&avatown.length==0)&&!sciencehastop[5]) {
            if(play.getScilv()==4){
                avatown[0].setTtstate("已翻面");
                otherMapper.updateHaveTownById(avatown[0]);
            }
            playMapper.advanceSci(gameid,userid);}
        playMapper.updatePlayById(play);
        Game game = gameMapper.getGameById(gameid);

        String[] rs = this.getRoundScoreById(gameid);
        if(rs[game.getRound()-1].equals("AT>>2")) otherMapper.gainVp(gameid,userid,2,"AT>>2");

        if(needk) {updatePosition(gameid);updateRecordById(gameid,play.getRace()+":advance "+science+".");}
        return "";
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
        if(game.getTerratown()==1){result[0]--;}
        if(game.getTerratown()==2){result[1]--;}
        if(game.getTerratown()==3){result[2]--;}
        if(game.getTerratown()==4){result[3]--;}
        if(game.getTerratown()==5){result[4]--;}
        if(game.getTerratown()==6){result[5]--;}
        return result;
    }

    @Override
    public String action(String gameid, String userid, String substring) {
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        if(substring.equals("1")&&play.getP3()>=3&&game.getPwa1().equals("1")){
            game.setPwa1("0");
            gameMapper.updateGameById(game);
            play.setP3(play.getP3()-3);
        play.setP1(play.getP1()+5);
        playMapper.updatePlayById(play);
        }else if(substring.charAt(0)=='2'&&play.getP3()>=3&&game.getPwa2().equals("1")){
            this.buildMine(gameid,userid,substring.substring(8),"action2");
            game.setPwa2("0");
            gameMapper.updateGameById(game);
            play = playMapper.getPlayByGameIdUserid(gameid,userid);
            play.setP3(play.getP3()-3);
            play.setP1(play.getP1()+3);
            playMapper.updatePlayById(play);
            return "";
        }else if(substring.equals("3")&&play.getP3()>=4&&game.getPwa3().equals("1")){
            game.setPwa3("0");
            gameMapper.updateGameById(game);
            play.setP3(play.getP3()-4);
            play.setP1(play.getP1()+4);
            play.setO(play.getO()+2);
            playMapper.updatePlayById(play);
        }else if(substring.equals("4")&&play.getP3()>=4&&game.getPwa4().equals("1")){
            game.setPwa4("0");
            gameMapper.updateGameById(game);
            play.setP3(play.getP3()-4);
            play.setP1(play.getP1()+4);
            play.setC(play.getC()+7);
            playMapper.updatePlayById(play);
        }else if(substring.equals("5")&&play.getP3()>=4&&game.getPwa5().equals("1")){
            game.setPwa5("0");
            gameMapper.updateGameById(game);
            play.setP3(play.getP3()-4);
            play.setP1(play.getP1()+4);
            play.setK(play.getK()+2);
            playMapper.updatePlayById(play);
        }else if(substring.charAt(0)=='6'&&play.getP3()>=5&&game.getPwa6().equals("1")){
            this.buildMine(gameid,userid,substring.substring(8),"action6");
            game.setPwa6("0");
            gameMapper.updateGameById(game);
            play = playMapper.getPlayByGameIdUserid(gameid,userid);
            play.setP3(play.getP3()-5);
            play.setP1(play.getP1()+5);
            playMapper.updatePlayById(play);
            return "";
        }else if(substring.equals("7")&&play.getP3()>=7&&game.getPwa7().equals("1")){
            game.setPwa7("0");
            gameMapper.updateGameById(game);
            play.setP3(play.getP3()-7);
            play.setP1(play.getP1()+7);
            play.setK(play.getK()+3);
        }else if(substring.equals("8")&&play.getQ()>=2&&game.getQa1().equals("1")){
            game.setQa1("0");
            gameMapper.updateGameById(game);
            play.setQ(play.getQ()-2);
            otherMapper.gainVp(gameid,userid,3+terratype(gameid,userid),"action8");
            playMapper.updatePlayById(play);
        }else if(substring.charAt(0)=='9'&&substring.substring(2,6).equals("town")&&play.getQ()>=3&&game.getQa2().equals("1")){
            String[] hts = otherMapper.getHTTypeById(gameid,userid);
            int towntype = Integer.parseInt(substring.substring(6));
            boolean ok = false;
            for (String s:hts){
                if (Integer.parseInt(s)==towntype) ok = true;
            }
            if(!ok) return "";
            game.setQa2("0");
            //todo
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
            playMapper.updatePlayById(play);
        }else if(substring.substring(0,2).equals("10")&&play.getQ()>=4&&game.getQa3().equals("1")){
            String[] strs = substring.split(" ");
            boolean ok = false;
            if(strs.length==4){
                ok = this.takett(gameid,userid,strs[3].substring(1),strs[2]);
            }else if(strs.length==5){
                ok = this.takeatt(gameid,userid,strs[3].substring(1),strs[4].substring(1),strs[2]);
            }
            if(!ok) return "cuowu";
            game.setQa3("0");
            gameMapper.updateGameById(game);
            play.setQ(play.getQ()-4);
            playMapper.updatePlayById(play);
            updateRecordById(gameid,play.getRace()+":"+"action"+substring+".");
            return "ok";
        }else{
            return "操作不合法！";
        }
        this.updatePosition(gameid);
        updateRecordById(gameid,play.getRace()+":"+"action"+substring+".");
        return "操作成功！";
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
                if(mapdetail[i][j]!=null&&structurecolor[i][j]!=null&&structurecolor[i][j].equals(racecolor)&&!structure[i][j].equals("gtu")&&!set.contains(mapdetail[i][j]))
                {result++;set.add(mapdetail[i][j]);}
            }
        }
        System.out.println("共有"+result+"种地形");
        return result;
    }

    @Override
    public String gaia(String gameid, String userid, String location) {
        Game game = gameMapper.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int gaiatech = play.getGaialv();
        String[][] mapdetail = new String[21][15];
        String[][] structurecolor = this.getStructureColorById(gameid);
        this.setMapDetail(mapdetail,gameid);
        int row = (int)location.charAt(0)-64;
        int column = Integer.parseInt(location.substring(1));
        if(!mapdetail[row][column].equals(pu)||gaiatech==0||structurecolor[row][column]!=null) return"建造失败！";
        if(!canArrive(gameid,userid,location)) return "距离不够！";
        int needbean = 0;
        switch (gaiatech) {
            case 1: needbean = 6;break;
            case 2: needbean = 6;break;
            case 3: needbean = 4;break;
            case 4: needbean = 3;break;
            case 5: needbean = 3;break;
        }
        int totalbean = play.getP1()+play.getP2()+play.getP3();
        if(totalbean<needbean) return "建造失败！";
        if(gaiatech<3&&!play.getGtu1().equals("0"))return "建造失败！";
        if(gaiatech<4&&!play.getGtu1().equals("0")&&!play.getGtu2().equals("0")) return "建造失败！";
        if(!play.getGtu1().equals("0")&&!play.getGtu2().equals("0")&&!play.getGtu3().equals("0")) return "建造失败！";
        for (int i = 0; i < needbean; i++) {
            if (play.getP1() != 0) {
                play.setP1(play.getP1() - 1);
            } else if (play.getP2() != 0) {
                play.setP2(play.getP2() - 1);
            } else {
                play.setP3(play.getP3() - 1);
            }
        }
        play.setPg(play.getPg()+needbean);
        if(play.getGtu1().equals("0")) {play.setGtu1(location);}
        else if(play.getGtu2().equals("0")){play.setGtu2(location);}
        else {play.setGtu3(location);}
        playMapper.updatePlayById(play);
        updateRecordById(gameid,play.getRace()+":gaia "+location+".");
        updatePosition(gameid);
        return null;
    }

    @Override
    public boolean canArrive(String gameid, String userid, String location) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        ArrayList<String> list = new ArrayList<>();
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
        for (int i = 0; i < list.size(); i++) {
            if (distance(list.get(i), location) <= x) {
                available = true;
            }
        }
        return available;
    }

    @Override
    public String form(String gameid, String userid, String substring) {
        String[] s = substring.split(",");
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        String[][] mapdetail = new String[21][15];this.setMapDetail(mapdetail,gameid);
        String[][] SS = this.getStructureSituationById(gameid);
        String[][] SC = this.getStructureColorById(gameid);
        String racecolor = racecolormap.get(play.getRace());
        String[] locations = s[0].split(" ");
        int totallevel = 0;
        int needsat = 0;
        for (String location:locations){
            int row = (int)location.charAt(0)-64;
            int column = Integer.parseInt(location.substring(1));
            if(!mapdetail[row][column].equals(ck)){
                if(!SC[row][column].equals(racecolor))return "错误！";
                if(SS[row][column].equals("m")) totallevel++;
                if(SS[row][column].equals("tc")) totallevel+=2;
                if(SS[row][column].equals("rl")) totallevel+=2;
                if(SS[row][column].equals("ac")) totallevel+=3;
                if(SS[row][column].equals("sh")) totallevel+=3;
            }else{
                needsat++;
            }
        }
        int power = play.getP1()+play.getP2()+play.getP3();
        if(totallevel>=7&&power>=needsat){
            for (String location:locations){
                int row = (int)location.charAt(0)-64;
                int column = Integer.parseInt(location.substring(1));
                if(mapdetail[row][column].equals(ck)){
                        if (play.getP1() != 0) {
                            play.setP1(play.getP1() - 1);
                        } else if (play.getP2() != 0) {
                            play.setP2(play.getP2() - 1);
                        } else {
                            play.setP3(play.getP3() - 1);
                        }
                    otherMapper.insertSate(gameid,userid,location);}
                else{
                    otherMapper.insertTB(gameid,location);
                }
            }
        }else{return "错误！";}
        int towntype = Integer.parseInt(s[1].substring(5));
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
        playMapper.updatePlayById(play);
        Game game = gameMapper.getGameById(gameid);
        String[] rs = this.getRoundScoreById(gameid);
        if(rs[game.getRound()-1].equals("TOWN>>5")) otherMapper.gainVp(gameid,userid,5,"TOWN>>5");
        updateRecordById(gameid,play.getRace()+":form "+substring+".");
        updatePosition(gameid);
        return null;
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
            i++;
        }
        return result;
    }


    @Override
    public String[][] getStructureSituationById(String gameid) {
        String[][] result = new String[21][15];
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (int i = 0; i < 4; i++) {
            if(!plays[i].getM1().equals("0")) result[(int)plays[i].getM1().charAt(0)-64][Integer.parseInt(plays[i].getM1().substring(1))]="m";
            if(!plays[i].getM2().equals("0")) result[(int)plays[i].getM2().charAt(0)-64][Integer.parseInt(plays[i].getM2().substring(1))]="m";
            if(!plays[i].getM3().equals("0")) result[(int)plays[i].getM3().charAt(0)-64][Integer.parseInt(plays[i].getM3().substring(1))]="m";
            if(!plays[i].getM4().equals("0")) result[(int)plays[i].getM4().charAt(0)-64][Integer.parseInt(plays[i].getM4().substring(1))]="m";
            if(!plays[i].getM5().equals("0")) result[(int)plays[i].getM5().charAt(0)-64][Integer.parseInt(plays[i].getM5().substring(1))]="m";
            if(!plays[i].getM6().equals("0")) result[(int)plays[i].getM6().charAt(0)-64][Integer.parseInt(plays[i].getM6().substring(1))]="m";
            if(!plays[i].getM7().equals("0")) result[(int)plays[i].getM7().charAt(0)-64][Integer.parseInt(plays[i].getM7().substring(1))]="m";
            if(!plays[i].getM8().equals("0")) result[(int)plays[i].getM8().charAt(0)-64][Integer.parseInt(plays[i].getM8().substring(1))]="m";
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
            if(!plays[i].getGtu1().equals("0")) result[(int)plays[i].getGtu1().charAt(0)-64][Integer.parseInt(plays[i].getGtu1().substring(1))]="gtu";
            if(!plays[i].getGtu2().equals("0")) result[(int)plays[i].getGtu2().charAt(0)-64][Integer.parseInt(plays[i].getGtu2().substring(1))]="gtu";
            if(!plays[i].getGtu3().equals("0")) result[(int)plays[i].getGtu3().charAt(0)-64][Integer.parseInt(plays[i].getGtu3().substring(1))]="gtu";
        }
        return result;
    }

    @Override
    public String[][] getStructureColorById(String gameid) {
        String[][] result = new String[21][15];
        Play[] plays = playMapper.getPlayByGameId(gameid);
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
            if(!plays[i].getGtu1().equals("0")) result[(int)plays[i].getGtu1().charAt(0)-64][Integer.parseInt(plays[i].getGtu1().substring(1))]=color;
            if(!plays[i].getGtu2().equals("0")) result[(int)plays[i].getGtu2().charAt(0)-64][Integer.parseInt(plays[i].getGtu2().substring(1))]=color;
            if(!plays[i].getGtu3().equals("0")) result[(int)plays[i].getGtu3().charAt(0)-64][Integer.parseInt(plays[i].getGtu3().substring(1))]=color;
        }
        return result;
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
                m[10][5].equals(m[11][6])|| m[10][6].equals(m[11][6])|| m[10][6].equals(m[11][7])|| m[10][7].equals(m[11][7])|| m[10][7].equals(m[11][8])||
                m[10][7].equals(m[10][8])||m[9][7].equals(m[10][8])||m[9][7].equals(m[9][8])||m[8][8].equals(m[9][8])||m[8][8].equals(m[8][9])
                ||m[11][8].equals(m[10][8])||m[11][9].equals(m[10][8])||m[11][9].equals(m[12][8])||m[12][9].equals(m[12][8])||m[12][9].equals(m[13][8])
                ||m[12][9].equals(m[13][9])||m[12][10].equals(m[13][9])||m[12][10].equals(m[13][10])||m[12][11].equals(m[13][10])||m[12][11].equals(m[13][11])
                ||m[11][5].equals(m[12][5])||m[12][4].equals(m[12][5])||m[12][4].equals(m[13][4])||m[13][3].equals(m[13][4])||m[13][3].equals(m[14][3])
                ||m[13][3].equals(m[14][2])||m[13][2].equals(m[14][2])||m[13][1].equals(m[14][2])||m[13][1].equals(m[14][1])||m[13][4].equals(m[14][3])
                ||m[14][3].equals(m[14][4])||m[15][4].equals(m[14][4])||m[15][4].equals(m[15][5])||m[16][5].equals(m[15][5])||m[16][5].equals(m[16][6])
                ||m[16][5].equals(m[17][5])||m[17][4].equals(m[17][5])||m[17][4].equals(m[18][4])||m[18][4].equals(m[18][3])||m[15][5].equals(m[16][6])
                ||m[15][6].equals(m[16][6])||m[15][6].equals(m[16][7])||m[15][7].equals(m[16][7])||m[15][7].equals(m[16][8])||m[15][8].equals(m[16][8])
                ||m[16][8].equals(m[16][9])||m[17][8].equals(m[16][9])||m[17][8].equals(m[17][9])||m[18][8].equals(m[17][9])||m[13][8].equals(m[13][9])
                ||m[13][8].equals(m[14][8])||m[14][8].equals(m[14][7])||m[14][7].equals(m[15][8])||m[15][7].equals(m[15][8])
        ) return false;
        return true;
    }

}
