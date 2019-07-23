package web.gaia.gaiaproject.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.model.Game;
import web.gaia.gaiaproject.service.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Integer> contain = new ArrayList();
        String mapseed="";
        while(contain.size()!=10){
            int plate = random.nextInt(10);
            if(!contain.contains(plate)) {
                contain.add(plate);
                mapseed += Integer.toString(plate);
                mapseed += Integer.toString(random.nextInt(6));
            }
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

    public static void setColor(String[][] mapDetail,int location,int spaceNo,int rotateTime){
        final String[] spaceNo1 = new String[]{"ck","ye","ck","ck","br","ck","ck","ck","ck","ck","ck","re","ck","bl","ck","or","ck","pu","ck"};
        final String[] spaceNo2 = new String[]{"ck","ck","ck","or","ck","br","re","gr","ck","ck","ck","ck","ck","wh","ck","pu","ck","ye","ck"};
        final String[] spaceNo3 = new String[]{"ck","ck","ck","ck","ga","ck","bl","pu","ck","ck","ck","ye","ck","ck","wh","ck","ck","gr","ck"};
        final String[] spaceNo4 = new String[]{"ck","wh","ck","ck","ck","or","ck","gr","re","ck","ck","ck","ck","ck","br","ck","ck","ck","bl"};
        final String[] spaceNo5 = new String[]{"ck","ck","ck","ck","ga","ck","or","wh","ck","ck","ck","ye","ck","ck","ck","ck","pu","re","ck"};
        final String[] spaceNo6 = new String[]{"ck","ck","ck","ck","br","ck","ck","ck","ck","ck","ga","ck","pu","bl","ck","pu","ck","ck","ye"};
        final String[] spaceNo7 = new String[]{"pu","ck","ck","ck","ck","ga","ck","ck","re","ck","ck","gr","br","ck","ga","ck","ck","ck","ck"};
        final String[] spaceNo8 = new String[]{"ck","ck","ck","ck","ck","or","pu","bl","wh","ck","ck","ck","ck","ck","gr","ck","pu","ck","ck"};
        final String[] spaceNo9 = new String[]{"ck","ck","br","or","ck","gr","ck","ck","ck","ck","ck","ck","pu","ck","ga","ck","wh","ck","ck"};
        final String[] spaceNo0 = new String[]{"ck","ck","bl","ck","ye","ck","re","ck","ck","ck","ck","ck","pu","ck","ga","ck","pu","ck","ck"};
        final int[] rotate0 = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
        final int[] rotate1 = new int[]{2,6,11,1,5,10,15,0,4,9,14,18,3,8,13,17,7,12,16};
        final int[] rotate2 = new int[]{11,15,18,6,10,14,17,2,5,9,13,16,1,4,8,12,0,3,7};
        final int[] rotate3 = new int[]{18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0};
        final int[] rotate4 = new int[]{16,12,7,17,13,8,3,18,14,9,4,0,15,10,5,1,11,6,2};
        final int[] rotate5 = new int[]{7,3,0,12,8,4,1,16,13,9,5,2,17,14,10,6,18,15,11};
        final int[] location1 = new int[]{4,1,4,2,4,3,5,1,5,2,5,3,5,4,6,1,6,2,6,3,6,4,6,5,7,1,7,2,7,3,7,4,8,1,8,2,8,3};
        final int[] location2 = new int[]{9,1,9,2,9,3,10,1,10,2,10,3,10,4,11,1,11,2,11,3,11,4,11,5,12,1,12,2,12,3,12,4,13,1,13,2,13,3};
        final int[] location3 = new int[]{14,1,14,2,14,3,15,1,15,2,15,3,15,4,16,1,16,2,16,3,16,4,16,5,17,1,17,2,17,3,17,4,18,1,18,2,18,3};
        final int[] location4 = new int[]{1,1,1,2,1,3,2,1,2,2,2,3,2,4,3,1,3,2,3,3,3,4,3,5,4,4,4,5,4,6,4,7,5,5,5,6,5,7};
        final int[] location5 = new int[]{6,6,6,7,6,8,7,5,7,6,7,7,7,8,8,4,8,5,8,6,8,7,8,8,9,4,9,5,9,6,9,7,10,5,10,6,10,7};
        final int[] location6 = new int[]{11,6,11,7,11,8,12,5,12,6,12,7,12,8,13,4,13,5,13,6,13,7,13,8,14,4,14,5,14,6,14,7,15,5,15,6,15,7};
        final int[] location7 = new int[]{16,6,16,7,16,8,17,5,17,6,17,7,17,8,18,4,18,5,18,6,18,7,18,8,19,1,19,2,19,3,19,4,20,1,20,2,20,3};
        final int[] location8 = new int[]{3,6,3,7,3,8,4,8,4,9,4,10,4,11,5,8,5,9,5,10,5,11,5,12,6,9,6,10,6,11,6,12,7,9,7,10,7,11};
        final int[] location9 = new int[]{8,9,8,10,8,11,9,8,9,9,9,10,9,11,10,8,10,9,10,10,10,11,10,12,11,9,11,10,11,11,11,12,12,9,12,10,12,11};
        final int[] location10 = new int[]{13,9,13,10,13,11,14,8,14,9,14,10,14,11,15,8,15,9,15,10,15,11,15,12,16,9,16,10,16,11,16,12,17,9,17,10,17,11};
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
}
