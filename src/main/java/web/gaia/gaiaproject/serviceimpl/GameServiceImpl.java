package web.gaia.gaiaproject.serviceimpl;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.model.Game;
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
        //高级科技*6
        while(contain.size()!=6){
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
    public void setMapDetail(String[][] mapDetail, String mapseed, String gamerecord) {
        System.out.println(mapseed);
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
        String record = game.getGamerecord();
        System.out.println("record:"+record);
        String[] records = record.split("\\.");
        String state = new String();
        System.out.println("recordslength:"+records.length);
        if(records.length<=21){
            for (int i = 5; i <= 8 ; i++) {
                //todo
                if(records.length==i) state = "轮到玩家："+records[i-4].substring(8)+"选择种族";
            }
        }
        System.out.println("state:"+state);
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
                {"BON1","TERRA","+2C"},
                {"BON2","+3SHIP","+2PW"},
                {"BON3","+1Q","+2C"},
                {"BON4","+1O","+1K"},
                {"BON5","+2PWB","+1O"},
                {"BON6","+1O","M>>1"},
                {"BON7","+1O","TC>>2"},
                {"BON8","+1K","RL>>3"},
                {"BON9","+4PW","SH/AC>>5"},
                {"BON10","+4C","G>>1"}};
        String[][] helptile = new String[10][3];
        int num = 0;
        for (int i = 0; i < 10; i++) {
            if((char)(i+48)==helptileseed.charAt(0)||(char)(i+48)==helptileseed.charAt(1)||(char)(i+48)==helptileseed.charAt(2)) continue;
            helptile[num][0]=helptiles[i][0];
            helptile[num][1]=helptiles[i][1];
            helptile[num][2]=helptiles[i][2];
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
        System.out.println(n);
        if(races[n]){
            a++;races[n]=false;
            if(n%2==0) races[n+1]=false;
            if(n%2==1) races[n-1]=false;
        }
        chu++;
        }
        String[] records =  game.getGamerecord().split("\\.");
        //todo 删除已被选择的种族
        return races;
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
