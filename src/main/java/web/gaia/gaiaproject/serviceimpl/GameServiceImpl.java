package web.gaia.gaiaproject.serviceimpl;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.model.Play;
import web.gaia.gaiaproject.model.TechTile;
import web.gaia.gaiaproject.service.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static web.gaia.gaiaproject.controller.MessageBox.*;

public class GameServiceImpl implements GameService {
    @Autowired
    GameMapper gameMapper;
    @Autowired
    PlayMapper playMapper;
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
            while (contain.size() != 10) {
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
        playMapper.insertPlay(gameId,player2,contain.get(1));
        playMapper.insertPlay(gameId,player3,contain.get(2));
        playMapper.insertPlay(gameId,player4,contain.get(3));
        String[] players = new String[]{player1,player2,player3,player4};
        for (int i = 1; i <= 4 ; i++) {
            gameMapper.updateRecordById(gameId,"Player"+i+": "+players[contain.indexOf(i)]+".");
        }
    }

    @Override
    public Game getGameById(String gameId) {
        return gameMapper.getGameById(gameId);
    }

    @Override
    public void setMapDetail(String[][] mapDetail, String mapseed) {
        for (int i = 0; i < 10; i++) {
            int spaceNo = (int)mapseed.charAt(i*2)-48;
            int rorateTime = (int)mapseed.charAt(i*2+1)-48;
            setColor(mapDetail,i,spaceNo,rorateTime);
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
        if(records.length<=21){
            for (int i = 5; i <= 8 ; i++) {
                //todo 蜂人与异空
                if(records.length==i) state = "轮到玩家："+records[i-4].substring(8)+"选择种族";
            }
            for (int i = 9; i <= 16 ; i++) {
                if(i<=12&&records.length==i) state = "轮到"+plays[i-9].getRace()+"建造初始矿场";
                if(i>=13&&records.length==i) state = "轮到"+plays[16-i].getRace()+"建造初始矿场";
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
        System.out.println("seed"+helptileseed);
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
        System.out.println(mapseed);
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
            if (p.getRace() != null) {
                if (p.getRace().equals("人类") || p.getRace().equals("亚特兰斯星人")) races[0] = races[1] = true;
                if (p.getRace().equals("圣禽族") || p.getRace().equals("蜂人")) races[2] = races[3] = true;
                if (p.getRace().equals("晶矿星人") || p.getRace().equals("炽炎族")) races[4] = races[5] = true;
                if (p.getRace().equals("异空族") || p.getRace().equals("格伦星人")) races[6] = races[7] = true;
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
        TechTile[] techTiles =gameMapper.getTTById(gameid);
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
            result[i]=techTiles[Integer.parseInt(lttseed.substring(i-6,i-5),16)+14].getTtno()+": "+
                    techTiles[Integer.parseInt(lttseed.substring(i-6,i-5),16)+14].getTtname();
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
        for (String s : result){
            System.out.println(s);
        }
        return result;
    }

    @Override
    public String getCurrentUserIdById(String gameid) {
        //todo 更改顺序函数
        Game game = gameMapper.getGameById(gameid);
        String[] records = game.getGamerecord().split("\\.");
        String[] users = playMapper.getUseridByGameId(gameid);
        if(records.length==13) return users[3];
        if(records.length==14) return users[2];
        if(records.length==15) return users[1];
        if(records.length==16) return users[0];
        if(records.length%4==1){
            return users[0];
        }else if(records.length%4==2){
            return users[1];
        }else if(records.length%4==3){
            return users[2];
        }else {
            return users[3];
        }
    }

    @Override
    public String[][] getResourceById(String gameid) {
        Play[] play =  playMapper.getPlayByGameId(gameid);
        String[][] result =new String[4][20];
        for (int i = 0; i < 4; i++) {
           System.out.println(play[i]);
                result[i][0] = play[i].getUserid();
                result[i][1] = play[i].getRace();
                result[i][2] = play[i].getO();
                result[i][3] = play[i].getC();
                result[i][4] = play[i].getK();
                result[i][5] = play[i].getQ();
                result[i][6] = play[i].getP1();
                result[i][7] = play[i].getP2();
                result[i][8] = play[i].getP3();
                result[i][9] = racecolormap.get(play[i].getRace());
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
        System .out.println(race);
        playMapper.setInitResource(raceinitresource[racenummap.get(race)][0],raceinitresource[racenummap.get(race)][1],raceinitresource[racenummap.get(race)][2]
        ,raceinitresource[racenummap.get(race)][3],raceinitresource[racenummap.get(race)][4],raceinitresource[racenummap.get(race)][5],gameid,userid);
        if(race.equals("人类")||race.equals("炽炎族")) playMapper.advanceGaia(gameid,userid);
        if(race.equals("晶矿星人")) playMapper.advanceTerra(gameid,userid);
        if(race.equals("格伦星人")||race.equals("大使星人")) playMapper.advanceShip(gameid,userid);
        if(race.equals("翼空族")) playMapper.advanceQ(gameid,userid);
        if(race.equals("圣禽族")) playMapper.advanceEco(gameid,userid);
        if(race.equals("超星人")) playMapper.advanceRes(gameid,userid);
    }

    @Override
    public String buildMine(String gameid, String userid, String location) {
        Game game = this.getGameById(gameid);
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        String[][] mapdetail = new String[21][15];
        this.setMapDetail(mapdetail,game.getMapseed());
        System.out.println(mapdetail[15][3]);
        //建造起始房子
        if(game.getRound()==0){
            String racecolor = racecolormap.get(play.getRace());
            int row = (int)location.charAt(0)-64;
            int column = Integer.parseInt(location.substring(1));
            System.out.println(mapdetail[row][column]+"++++"+racecolor);
            if(!mapdetail[row][column].equals(racecolor)) return"请建造在母星上！";
            if(play.getM1().equals("0")){
                playMapper.updateM1ByGameIdUserid(gameid,userid,location);
            }else if(play.getM2().equals("0")){
                playMapper.updateM2ByGameIdUserid(gameid,userid,location);
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
        }
        //TODO
        updateRecordById(gameid,play.getRace()+"build "+location+".");
        return "建造成功";
    }

    @Override
    public String pass(String gameid, String userid, String bon) {
        Game game = gameMapper.getGameById(gameid);
        int bonusno = Integer.parseInt(bon);
/*        String[][] avahelptile = this.getHelpTileById(gameid);*/
        playMapper.updateBonusById(gameid,userid,bonusno);
        gameMapper.updateRecordById(gameid,userid+":pass: bon"+bon+".");
        int passedplayers = playMapper.selectPassNo(gameid);
        playMapper.updatePassNo(gameid,userid,playMapper.selectPassNo(gameid)+1);
        //TODO 结算havett表中的bon，显示到前端
        if(passedplayers==3){
            gameMapper.roundEnd(gameid);
            playMapper.roundEnd(gameid);
        }
        return null;
    }

    @Override
    public String[][][] getScienceGrade(String gameid) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        String[][][] result = new String[4][6][8];
        for (Play play:plays){
            if (play.getTerralv()==0) result[play.getPosition()-1][0][7]="a";
            if (play.getTerralv()==1) result[play.getPosition()-1][0][6]="a";
            if (play.getTerralv()==2) result[play.getPosition()-1][0][5]="a";
            if (play.getTerralv()==3) result[play.getPosition()-1][0][3]="a";
            if (play.getTerralv()==4) result[play.getPosition()-1][0][2]="a";
            if (play.getTerralv()==5) result[play.getPosition()-1][0][0]="a";
            if (play.getShiplv()==0) result[play.getPosition()-1][1][7]="a";
            if (play.getShiplv()==1) result[play.getPosition()-1][1][6]="a";
            if (play.getShiplv()==2) result[play.getPosition()-1][1][5]="a";
            if (play.getShiplv()==3) result[play.getPosition()-1][1][3]="a";
            if (play.getShiplv()==4) result[play.getPosition()-1][1][2]="a";
            if (play.getShiplv()==5) result[play.getPosition()-1][1][0]="a";
            if (play.getQlv()==0) result[play.getPosition()-1][2][7]="a";
            if (play.getQlv()==1) result[play.getPosition()-1][2][6]="a";
            if (play.getQlv()==2) result[play.getPosition()-1][2][5]="a";
            if (play.getQlv()==3) result[play.getPosition()-1][2][3]="a";
            if (play.getQlv()==4) result[play.getPosition()-1][2][2]="a";
            if (play.getQlv()==5) result[play.getPosition()-1][2][0]="a";
            if (play.getGaialv()==0) result[play.getPosition()-1][3][7]="a";
            if (play.getGaialv()==1) result[play.getPosition()-1][3][6]="a";
            if (play.getGaialv()==2) result[play.getPosition()-1][3][5]="a";
            if (play.getGaialv()==3) result[play.getPosition()-1][3][3]="a";
            if (play.getGaialv()==4) result[play.getPosition()-1][3][2]="a";
            if (play.getGaialv()==5) result[play.getPosition()-1][3][0]="a";
            if (play.getEcolv()==0) result[play.getPosition()-1][4][7]="a";
            if (play.getEcolv()==1) result[play.getPosition()-1][4][6]="a";
            if (play.getEcolv()==2) result[play.getPosition()-1][4][5]="a";
            if (play.getEcolv()==3) result[play.getPosition()-1][4][3]="a";
            if (play.getEcolv()==4) result[play.getPosition()-1][4][2]="a";
            if (play.getEcolv()==5) result[play.getPosition()-1][4][0]="a";
            if (play.getReslv()==0) result[play.getPosition()-1][5][7]="a";
            if (play.getReslv()==1) result[play.getPosition()-1][5][6]="a";
            if (play.getReslv()==2) result[play.getPosition()-1][5][5]="a";
            if (play.getReslv()==3) result[play.getPosition()-1][5][3]="a";
            if (play.getReslv()==4) result[play.getPosition()-1][5][2]="a";
            if (play.getReslv()==5) result[play.getPosition()-1][5][0]="a";
        }
        return result;
    }

    @Override
    public String[][] getStructureSituationById(String gameid) {
        String[][] result = new String[21][15];
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (int i = 0; i < 4; i++) {
            System.out.println(plays[i].getM1());
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
