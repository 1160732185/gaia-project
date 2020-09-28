package web.gaia.gaiaproject.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.OtherMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.mapper.UserMapper;
import web.gaia.gaiaproject.model.*;
import web.gaia.gaiaproject.service.GameService;
import web.gaia.gaiaproject.service.PlayService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static web.gaia.gaiaproject.controller.MessageBox.*;

public class PlayServiceImpl implements PlayService {
    static int f = 0;
    @Autowired
    PlayMapper playMapper;
    @Autowired
    OtherMapper otherMapper;
    @Autowired
    GameMapper gameMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    GameService gameService;
    @Override
    public String[] showGames(String userid) {
        ArrayList<String> result = new ArrayList<>();
        String[] games = playMapper.showGames(userid);
        for (String gameid:games){
            Game game = gameMapper.getGameById(gameid);
            if(game.getBlackstar()==null||!game.getBlackstar().equals("游戏结束")) result.add(gameid);
        }
        return result.toArray(new String[0]);
    }

    @Override
    public boolean getPowerPendingLeech(String gameid, String userid) {
        Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
        int a = otherMapper.getPowerPendingLeech(gameid,play.getRace());
        if(a==0) return true;
        return false;
    }

    @Override
    public void turnEnd(String gameid, String bidid) {
        Game game = gameMapper.getGameById(gameid);
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (Play p:plays){
            if(p.getRace().equals("格伦星人")&&p.getAc2().equals("0")) {p.setO(p.getQ()+p.getO());p.setQ(0);}
            if(p.getO()>15) p.setO(15);
            if(p.getK()>15) p.setK(15);
            if(p.getC()>30) p.setC(30);
            playMapper.updatePlayById(p);
        }
        if(!bidid.equals("admin")){
            Power[] ps = otherMapper.getPowerByGameId(gameid);
            for (Power p:ps){
                Play receiveP = playMapper.getPlayByGameIdRace(gameid,p.getReceiverace());
                Power[] pp = otherMapper.getPowerByGameIdUserId(gameid,p.getReceiverace());
                if(receiveP.getPass()!=0&&game.getRound().equals(6)){
                    if(p.getPower()==1){
                        gameService.leechPower(gameid,p.getGiverace(),p.getReceiverace(),p.getLocation(),p.getStructure(),"1");
                    }else {
                        gameService.leechPower(gameid,p.getGiverace(),p.getReceiverace(),p.getLocation(),p.getStructure(),"0");
                    }
                }else
                if(pp.length==1&&!p.getReceiverace().equals("伊塔星人")&&!(p.getReceiverace().equals("利爪族")&&!receiveP.getSh().equals("0"))){
                    if(p.getPower()==1)
                    {
                        gameService.leechPower(gameid,p.getGiverace(),p.getReceiverace(),p.getLocation(),p.getStructure(),"1");
                    }else if(receiveP.getP1()==0&&receiveP.getP2()==1){
                        gameService.leechPower(gameid,p.getGiverace(),p.getReceiverace(),p.getLocation(),p.getStructure(),"1");
                    }else if(receiveP.getP1()==0&&receiveP.getP2()==0){
                        gameService.leechPower(gameid,p.getGiverace(),p.getReceiverace(),p.getLocation(),p.getStructure(),"1");
                    }
                }
            }
        }
    }

    @Override
    public String[][] topScore() {
        System.out.println("清空缓存");
        String[][] result = new String[42][3];
        for (int i=0;i<14;i++){
            result[i][0]="0";
            result[i+14][0]="300";
        }
        ;
        Play[] plays = otherMapper.getAllPlay();
        System.out.println(plays.length);
  /*      for(Play p:plays){
            String gameid = p.getGameid();
            if(gameMapper.getGameById(gameid).getGamemode().equals("1.1")) continue;
            if(p.getRace().equals("未知种族")) continue;
            if(gameMapper.getGameById(p.getGameid()).getBlackstar()==null) continue;
            if(!gameMapper.getGameById(p.getGameid()).getBlackstar().equals("游戏结束")) continue;
            int vp = otherMapper.getvp(p.getGameid(),p.getUserid());
            if(Integer.parseInt(result[racenummap.get(p.getRace())][0])<vp){
                result[racenummap.get(p.getRace())][0] = String.valueOf(vp);
                result[racenummap.get(p.getRace())][1] = p.getUserid();
                result[racenummap.get(p.getRace())][2] = p.getGameid();
            }
            if(Integer.parseInt(result[racenummap.get(p.getRace())+14][0])>vp){
                result[racenummap.get(p.getRace())+14][0] = String.valueOf(vp);
                result[racenummap.get(p.getRace())+14][1] = p.getUserid();
                result[racenummap.get(p.getRace())+14][2] = p.getGameid();
            }
        }*/
        return result;
    }

    //每天一小时清空一次缓存数据
    @Override
    @Scheduled(cron="0 0 * * * ? ")
    @CacheEvict(cacheNames = {"player"},allEntries = true)
    public void executeEvictCache() {
        System.out.println("我清掉我自己");
    }


    @Override
    @Cacheable(cacheNames = {"player"})
    public PlayerDetails getPlayer(String userid) {
        System.out.println("我康康我自己");
       List<User> users = userMapper.getAllUsers();
/*     if(f==0){
          for (User user:users){
              if(user.getUserid().equals("admin")) continue;
              ArrayList<Lobby> fgd = this.showLobby(user.getUserid(),"end");
              int gamesum = 0;
              int scoresum = 0;
              int ranksum = 0;
              for(Lobby l:fgd){
                  if(gameMapper.getGameById(l.getGameid()).getGamemode().charAt(2)!='3'&&gamesum<50){
                      gamesum++;
                      String endinfo = l.getRound();
                      int a=0,b=0,c=0,d=0;
                      for (int i=4;i<endinfo.length();i++){
                          if(endinfo.charAt(i)=='<') a=i;
                          if(endinfo.charAt(i)=='V') b=i;
                          if(endinfo.charAt(i)=='(') c=i;
                          if(endinfo.charAt(i)==')') d=i;
                      }
                      scoresum+=Integer.parseInt(l.getRound().substring(a+1,b));
                      ranksum+=Integer.parseInt(l.getRound().substring(c+1,d));
                  }
              }
              if(gamesum>=5){
                  String avgrank = String.valueOf((float) ranksum/(float) gamesum);
                  if(avgrank.length()>4) avgrank = avgrank.substring(0,4);
                  String avgscore = String.valueOf((float) scoresum/(float) gamesum);
                  if(avgscore.length()>5) avgscore = avgscore.substring(0,5);
                  user.setAvgrank(avgrank);
                  user.setAvgscore(avgscore);
                  userMapper.userUpdate(user);
              }else {
                  user.setAvgscore("0");
                  user.setAvgrank("0");
                  userMapper.userUpdate(user);
              }
          }
          f++;
      }*/

        PlayerDetails playerDetails = new PlayerDetails();
        String[] info = new String[4];
        User user = userMapper.getUser(userid);
        info[0] = user.getAvgscore();
        info[1] = user.getAvgrank();
        if(!info[0].equals("0")){
            int usernum = 0;
            int scorerank = 1;
            int rankrank = 1;
            for (User user1:users){
                if(user1.getAvgrank()!=null&&!user1.getAvgrank().equals("0")){
                    usernum++;
                    if(Float.parseFloat(user1.getAvgrank())<Float.parseFloat(user.getAvgrank())) rankrank++;
                    if(Float.parseFloat(user1.getAvgscore())>Float.parseFloat(user.getAvgscore())) scorerank++;
                }
            }
            info[2] = scorerank+"/"+usernum;
            info[3] = rankrank+"/"+usernum;
        }else {
            info[2] = "局数不足15场";
            info[3] = "局数不足15场";
        }

        playerDetails.setOtherinfo(info);
        ArrayList<Lobby> rgd = this.showLobby(userid,"active");
        ArrayList<Lobby> fgd = this.showLobby(userid,"end");
        ArrayList<ArrayList<String>> ri = new ArrayList<ArrayList<String>>();
        String[] races = new String[]{"人类","亚特兰斯星人", "圣禽族","蜂人","晶矿星人","炽炎族","翼空族",
                "格伦星人","大使星人","利爪族", "章鱼人","疯狂机器","伊塔星人","超星人"};
        for(String race:races){
            ArrayList<String> list = new ArrayList<>();
            //种族、颜色、场数、平均分、最终名次
            list.add(race);list.add(racecolormap.get(race));list.add("0");list.add("0");list.add("");
            int first = 0;int second = 0;int third = 0;int forth = 0;
        for(Lobby l:fgd){
            if(l.getRace().equals(race)&&gameMapper.getGameById(l.getGameid()).getGamemode().charAt(2)!='3'){
                list.set(2,String.valueOf(Integer.parseInt(list.get(2))+1));
                int vp = otherMapper.getvp(l.getGameid(),userid);
                int ini = otherMapper.getiniVpByUserid(l.getGameid(),userid);
                vp+=(10-ini);
                list.set(3,String.valueOf(Integer.parseInt(list.get(3))+vp));
                int leng = l.getRound().length();
                if(l.getRound().charAt(leng-2)=='1') first++;if(l.getRound().charAt(leng-2)=='2') second++;if(l.getRound().charAt(leng-2)=='3') third++;if(l.getRound().charAt(leng-2)=='4') forth++;
            }
        }
        if(first+second+third+forth!=0){
            list.set(3,String.valueOf(Integer.parseInt(list.get(3))/(first+second+third+forth)));
            String rank = "";
            for(int i=0;i<first;i++){
                rank+="1,";
            }
            for(int i=0;i<second;i++){
                rank+="2,";
            }
            for(int i=0;i<third;i++){
                rank+="3,";
            }
            for(int i=0;i<forth;i++){
                rank+="4,";
            }
            list.set(4,rank.substring(0,rank.length()-1));
        }
            ri.add(list);
        }

        ArrayList<ArrayList<String>> oi = new ArrayList<ArrayList<String>>();
        String[] games = playMapper.showGames(userid);
        ArrayList<String> oppo = new ArrayList<>();
        for(String gameid:games){
            Game game = gameMapper.getGameById(gameid);
            if(game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束")&&game.getGamemode().charAt(2)!='3'){
                Play[] plays = playMapper.getPlayByGameId(gameid);
                int myvp = otherMapper.getvp(gameid,userid);
                for (Play p:plays){
                    if(!p.getUserid().equals(userid)){
                        int vp = otherMapper.getvp(gameid,p.getUserid());
                        if(!oppo.contains(p.getUserid())){
                            oppo.add(p.getUserid());
                            ArrayList<String> oppoarray = new ArrayList<>();
                            oppoarray.add(p.getUserid());oppoarray.add("0");oppoarray.add("0");oppoarray.add("0");oppoarray.add("0");
                            oi.add(oppoarray);
                        }
                        int opponum = oppo.indexOf(p.getUserid());
                        if(vp>myvp) {
                            oi.get(opponum).set(1,String.valueOf(Integer.parseInt(oi.get(opponum).get(1))+1));
                            oi.get(opponum).set(3,String.valueOf(Integer.parseInt(oi.get(opponum).get(3))+1));
                        }else
                        if(vp==myvp) {
                            oi.get(opponum).set(1,String.valueOf(Integer.parseInt(oi.get(opponum).get(1))+1));
                            oi.get(opponum).set(4,String.valueOf(Integer.parseInt(oi.get(opponum).get(4))+1));
                        }else
                        if(vp<myvp) {
                            oi.get(opponum).set(1,String.valueOf(Integer.parseInt(oi.get(opponum).get(1))+1));
                            oi.get(opponum).set(2,String.valueOf(Integer.parseInt(oi.get(opponum).get(2))+1));
                        }
                    }
                }
            }
        }
        oi.sort(new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return Integer.parseInt(o2.get(1))-Integer.parseInt(o1.get(1));
            }
        });
        playerDetails.setRungamedetail(rgd);
        playerDetails.setFinishgamedetail(fgd);
        playerDetails.setRaceinfo(ri);
        playerDetails.setOpponentinfo(oi);
        playerDetails.setRungamenum(rgd.size());
        playerDetails.setFinishgamenum(fgd.size());
        return playerDetails;
    }


    @Override
    public ArrayList<Lobby> showLobby(String userid,String end) {
        ArrayList<Lobby> result = new ArrayList<>();
        String[] games = playMapper.showGames(userid);
        if(userid.equals("admin")) games = gameMapper.getAllGames();
        for (String gameid:games){
            Lobby lobby = new Lobby();
            lobby.setGameid(gameid);
            Play play = new Play();
            if(!userid.equals("admin")){
                play = playMapper.getPlayByGameIdUserid(gameid,userid);
                lobby.setRace(play.getRace());
            }
            Game game = gameMapper.getGameById(gameid);
            if(end.equals("active")&&game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束"))continue;
            if(end.equals("end")&&(game.getBlackstar()==null||!game.getBlackstar().equals("游戏结束")))continue;
            if(game.getGamemode().charAt(2)=='3') lobby.setAuthority("ok");
            if(!game.getAdmin().equals("")&&game.getAdmin().equals(userid)) lobby.setAuthority("ok");
            Long time = 0l;
            if(game.getLasttime().equals("")) {time = 99999999999l;}else {
                time = System.currentTimeMillis() - Long.valueOf(game.getLasttime());
            }
            lobby.setLasttime(String.valueOf(time));
            lobby.setBgcolor("");
            if(game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束")&&!userid.equals("admin")){
                int myvp = otherMapper.getvp(gameid,play.getUserid());
                int rank = 1;
                Play[] plays = playMapper.getPlayByGameId(gameid);
                for (Play p:plays){
                    int vp = otherMapper.getvp(gameid,p.getUserid());
                    if(vp>myvp) rank++;
                }
                lobby.setRound("游戏结束<"+myvp+"VP>"+"("+rank+")");
            }else {
                String[] records = game.getGamerecord().split("\\.");
                String panduan = records[records.length-1];
                int kuohao = 0;
                while(kuohao<panduan.length()&&panduan.charAt(kuohao)!='('){
                    kuohao++;
                }
                if(userid.equals("admin")){lobby.setRollback("Y");}else {
                    if(panduan.substring(0,kuohao).contains(play.getRace())) {lobby.setRollback("Y");}else {
                        lobby.setRollback("F");
                    }
                }
                lobby.setRound(String.valueOf(game.getRound()));
                lobby.setTurn(String.valueOf(game.getTurn()));
                lobby.setCurrentuserid(gameService.getCurrentUserIdById(gameid));
                Play[] plays = playMapper.getPlayByGameId(gameid);
                int passednum = 0;
                for(Play p:plays){
                    if(p.getPass()!=0) passednum++;
                }
                if(passednum==3){
                    Power[] powers = otherMapper.getAllPowerById(gameid);
                    if(powers.length!=0){
                        String race = powers[0].getReceiverace();
                        for (Play p:plays){
                            if(p.getRace().equals(race)) lobby.setCurrentuserid(p.getUserid());
                        }
                    }
                }
                if(lobby.getCurrentuserid().equals(userid)) lobby.setBgcolor("#EEEE00");
                if(gameService.getCurrentUserIdById(gameid).equals("all")){
                    String state = "";
                    String[] bid = gameService.getBid(gameid);
                            if(Integer.parseInt(bid[5])==-1){
                                state+=" ";
                                state+=bid[1];
                                if(bid[1].equals(userid)) lobby.setBgcolor("#EEEE00");
                                state+=" ";
                            }
                            if(Integer.parseInt(bid[6])==-1){
                                state+=" ";
                                state+=bid[2];
                                if(bid[2].equals(userid)) lobby.setBgcolor("#EEEE00");
                                state+=" ";
                            }
                            if(Integer.parseInt(bid[7])==-1){
                                state+=" ";
                                state+=bid[3];
                                if(bid[3].equals(userid)) lobby.setBgcolor("#EEEE00");
                                state+=" ";
                            }
                            if(Integer.parseInt(bid[8])==-1){
                                state+=" ";
                                state+=bid[4];
                                if(bid[4].equals(userid)) lobby.setBgcolor("#EEEE00");
                                state+=" ";
                            }
                    lobby.setCurrentuserid(state);
                }
            }
            result.add(lobby);
        }
result.sort(new Comparator<Lobby>(){
@Override
public int compare(Lobby arg0, Lobby arg1) {
    if(arg0.getBgcolor().equals("#EEEE00")&&!arg1.getBgcolor().equals("#EEEE00")) return -1;
    if(arg1.getBgcolor().equals("#EEEE00")&&!arg0.getBgcolor().equals("#EEEE00")) return 1;
    if(Long.valueOf(arg0.getLasttime())>Long.valueOf(arg1.getLasttime())) return 1;
    return -1;
}
});

        for (Lobby l:result){
            String lasttime = "";
            Long time = Long.valueOf(l.getLasttime());
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
            l.setLasttime(lasttime);
        }
        return result;
    }

    @Override
    public Vp[] getiniVP(String gameid) {
        return otherMapper.getiniVps(gameid);
    }

    @Override
    public void saveLog(String gameid, String userid, String log) {
        playMapper.updateLogById(gameid,userid,log);
    }

    @Override
    public String showLog(String gameid, String userid) {
        return playMapper.getLogById(gameid,userid);
    }

    @Override
    public ArrayList<PendingGame> showPending() {
         ArrayList<PendingGame> result = gameMapper.getAPGamebyId();
         for(PendingGame pg:result){
             pg.setGamemode(this.getGameModeName(pg.getGamemode()));
         }
        return result;
    }
@Override
   public String getGameModeName(String gamemode){
        String gm = "";
        if(gamemode.charAt(0)=='0') gm+="原版游戏";
        if(gamemode.equals("1.0")) {gm+="1.0(游戏,种族平衡)a1";return gm;}
        if(gamemode.equals("1.1")) {gm+="1.0(游戏,种族平衡)a2";return gm;}
        if(gamemode.equals("2.0")||gamemode.equals("2.1")||gamemode.equals("2.2")) {gm+="1.0(游戏,种族平衡)b";return gm;}
        if(gamemode.charAt(0)=='2') gm+="1.0(游戏,种族平衡)";
        if(gamemode.charAt(0)=='3') gm+="2.0(1.0+额外7个新种族)";
        if(gamemode.length()==5&&gamemode.charAt(4)=='0') gm+="c" ;
        if(gamemode.length()==5&&gamemode.charAt(4)=='1') gm+="d" ;
        if(gamemode.length()>=5&&gamemode.charAt(4)=='2') gm+="e" ;
        if(gamemode.length()>=5&&gamemode.charAt(4)=='3') gm+="f" ;
        if(gamemode.length()>=5&&gamemode.charAt(4)=='4') gm+="g" ;
        if(gamemode.length()>=5&&gamemode.charAt(4)=='5') gm+="h" ;
        if(gamemode.length()>=5&&gamemode.charAt(4)=='6') gm+="i" ;
        if(gamemode.length()>=5&&gamemode.charAt(4)=='7') gm+="j" ;
        if(gamemode.length()==7&&gamemode.charAt(2)=='0'&&gamemode.charAt(6)=='1') { gm+="/随机种族竞拍";}
        else { if(gamemode.charAt(2)=='0') gm+="/第二价格竞拍"; }
        if(gamemode.charAt(2)=='1') gm+="/随机顺位";
        if(gamemode.charAt(2)=='2'&&gamemode.length()>=7&&gamemode.charAt(6)=='1') {gm+="/固定顺位旋转地图";}else {
            if(gamemode.charAt(2)=='2') gm+="/末位旋转地图";
        }
        if(gamemode.charAt(2)=='3') gm+="/单人游戏";
        return gm;
    }


    @Override
    public String joinGame(String gameid, String userid) {
        PendingGame pendingGame = gameMapper.getPGamebyId(gameid);
        if(pendingGame==null) return "查无此局";
        if(userid.equals(pendingGame.getPlayer1())||userid.equals(pendingGame.getPlayer2())||userid.equals(pendingGame.getPlayer3())||userid.equals(pendingGame.getPlayer4())) return "你已经加入了";
        if(pendingGame.getPlayer2().equals("")) {pendingGame.setPlayer2(userid);}
        else if(pendingGame.getPlayer3().equals("")) {pendingGame.setPlayer3(userid);}
        else if(pendingGame.getPlayer4().equals("")) {pendingGame.setPlayer4(userid);}
        else {
            return "人数已满";
        }
        if(!pendingGame.getPlayer1().equals("")&&!pendingGame.getPlayer2().equals("")&&!pendingGame.getPlayer3().equals("")&&!pendingGame.getPlayer4().equals("")){
           gameService.createGame(gameid,pendingGame.getPlayer1(),pendingGame.getPlayer2(),pendingGame.getPlayer3(),pendingGame.getPlayer4(),pendingGame.getGamemode());
           gameMapper.deletePGame(gameid);
        }else {
            gameMapper.updatePGame(gameid,pendingGame.getPlayer2(),pendingGame.getPlayer3(),pendingGame.getPlayer4());
        }
            return "加入成功";
    }

    @Override
    public ArrayList<ArrayList<League>> showPendingLeague() {
        ArrayList<League> leagues = otherMapper.getPLeagues();
        ArrayList<League> leagues1 = new ArrayList<>();
        ArrayList<League> leagues2 = new ArrayList<>();
        ArrayList<League> leagues3 = new ArrayList<>();
        ArrayList<League> leagues4 = new ArrayList<>();
        ArrayList<ArrayList<League>> result = new ArrayList<>();
        for (League league : leagues) {
            league.setGamemode(getGameModeName(league.getGamemode()));
            if(league.getLeagueid().length()>=8&&league.getLeagueid().substring(2,8).equals("黄金联赛S3")||league.getPlayer7().equals("")){leagues4.add(league);}
            else if(league.getLeagueid().length()>=8&&league.getLeagueid().substring(2,8).equals("黄金联赛S1")){
                leagues1.add(league);
            }else if(league.getLeagueid().length()>=8&&league.getLeagueid().substring(2,8).equals("黄金联赛S2")){
                leagues1.add(league);
            }
            else if(league.getAdmin().equals("")){
                leagues2.add(league);
            }else {
                leagues3.add(league);
            }
        }
        result.add(leagues1);result.add(leagues2);result.add(leagues3);result.add(leagues4);
        return result;
    }

    @Override
    public String joinLeague(String gameid, String userid) {
        League league = gameMapper.getPLeaguebyId(gameid);
        User user = userMapper.getUser(userid);
        if(league.getDes()!=null&&league.getDes().length()>=7){
            int min = Integer.parseInt(league.getDes().substring(0,3));
            int max = Integer.parseInt(league.getDes().substring(4,7));
            System.out.println(Float.parseFloat(user.getAvgscore())>max);
            System.out.println(Float.parseFloat(user.getAvgscore())<min);
            if(Float.parseFloat(user.getAvgscore())>max||Float.parseFloat(user.getAvgscore())<min) return "你暂时没有资格参赛,请等候限制条件放宽！";
        }
        if(league==null) return "查无此局";
        if(userid.equals(league.getPlayer1())||userid.equals(league.getPlayer2())||userid.equals(league.getPlayer3())||userid.equals(league.getPlayer4())||userid.equals(league.getPlayer5())||userid.equals(league.getPlayer6())||userid.equals(league.getPlayer7())) return "你已经加入了";
        if(league.getPlayer1().equals("")) {league.setPlayer1(userid);}
        else if(league.getPlayer2().equals("")) {league.setPlayer2(userid);}
        else if(league.getPlayer3().equals("")) {league.setPlayer3(userid);}
        else if(league.getPlayer4().equals("")) {league.setPlayer4(userid);}
        else if(league.getPlayer5().equals("")) {league.setPlayer5(userid);}
        else if(league.getPlayer6().equals("")) {league.setPlayer6(userid);}
        else if(league.getPlayer7().equals("")) {league.setPlayer7(userid);}
        else {
            return "人数已满";
        }
        gameMapper.updatePLeague(gameid,league.getPlayer1(),league.getPlayer2(),league.getPlayer3(),league.getPlayer4(),league.getPlayer5(),league.getPlayer6(),league.getPlayer7());
        if(!league.getPlayer7().equals("")){
            ArrayList<Integer> contain = new ArrayList<Integer>();
            //回合助推*3
            Random random = new Random();
            String rstring = "";
            while(contain.size()!=7){
                int rn = random.nextInt(7)+1;
                if(!contain.contains(rn)) {
                    contain.add(rn);
                    rstring += String.valueOf(rn);
                }
            }
            gameService.createGame(gameid+"_G"+rstring.charAt(0),league.getPlayer1(),league.getPlayer6(),league.getPlayer3(),league.getPlayer2(),league.getGamemode());
            gameService.createGame(gameid+"_G"+rstring.charAt(1),league.getPlayer2(),league.getPlayer7(),league.getPlayer4(),league.getPlayer3(),league.getGamemode());
            gameService.createGame(gameid+"_G"+rstring.charAt(2),league.getPlayer3(),league.getPlayer1(),league.getPlayer5(),league.getPlayer4(),league.getGamemode());
            gameService.createGame(gameid+"_G"+rstring.charAt(3),league.getPlayer4(),league.getPlayer2(),league.getPlayer6(),league.getPlayer5(),league.getGamemode());
            gameService.createGame(gameid+"_G"+rstring.charAt(4),league.getPlayer5(),league.getPlayer3(),league.getPlayer7(),league.getPlayer6(),league.getGamemode());
            gameService.createGame(gameid+"_G"+rstring.charAt(5),league.getPlayer6(),league.getPlayer4(),league.getPlayer1(),league.getPlayer7(),league.getGamemode());
            gameService.createGame(gameid+"_G"+rstring.charAt(6),league.getPlayer7(),league.getPlayer5(),league.getPlayer2(),league.getPlayer1(),league.getGamemode());
        }
        return "加入成功";
    }

    @Override
    public String[][] getLeaguedetail(String leagueid) {
        String[][] result = new String[8][23];
        ArrayList<String> players = new ArrayList<>();
        League league = gameMapper.getPLeaguebyId(leagueid);
        for (int i=1;i<=7;i++){
            String gameid = leagueid+"_G"+String.valueOf(i);
            Game game = gameMapper.getGameById(gameid);
            if(game!=null){
                if(game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束"))  { result[0][i*2] ="游戏结束";}
               else {
                   result[0][i*2] = "R"+game.getRound()+"T"+game.getTurn();
                }
            }
            if(!result[0][i*2].equals("游戏结束")){
                result[0][i*2] += "("+gameService.getCurrentUserIdById(gameid)+")";
            }

            result[i][16] = "0"; result[i][17] = "0"; result[i][18] = "0"; result[i][19] = "0"; result[i][20] = "0"; result[i][21] = "0";
        }
        result[0][21] = "100";
        result[1][0] = league.getPlayer1(); result[2][0] = league.getPlayer2(); result[3][0] = league.getPlayer3(); result[4][0] = league.getPlayer4(); result[5][0] = league.getPlayer5(); result[6][0] = league.getPlayer6(); result[7][0] = league.getPlayer7();
        players.add(league.getPlayer1());players.add(league.getPlayer2());players.add(league.getPlayer3());players.add(league.getPlayer4());players.add(league.getPlayer5());players.add(league.getPlayer6());players.add(league.getPlayer7());
        for (int i=0;i<=6;i++){
            String userid = players.get(i);
            int finishgames = 0;
            float percent = 0;
            for (int j=1;j<=7;j++){
                String gameid = leagueid+"_G"+String.valueOf(j);
                Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
                if(play == null) {result[i+1][j*2] = "";}else {
                    int vp = otherMapper.getvp(gameid,userid);
                    result[i+1][j*2] = play.getRace()+"___"+String.valueOf(vp);
                    if(play.getRace().equals("未知种族")) {result[i+1][j*2+1] = "#FFFFFF";}else {
                        result[i+1][j*2+1] = racecolormap.get(play.getRace());
                    }
                    Game game = gameMapper.getGameById(gameid);
                    if(game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束")){
                        finishgames++;
                        int win = 0;
                        int draw = -1;
                        Play[] plays = playMapper.getPlayByGameId(gameid);
                        int topscore = 0;
                        for (Play p:plays){
                            int othervp = otherMapper.getvp(gameid,p.getUserid());
                            if(othervp>topscore) topscore = othervp;
                            if(othervp<vp) win++;
                            if(othervp==vp) draw++;
                        }
                        if(win==3) {result[i+1][16] = String.valueOf(Float.parseFloat(result[i+1][16])+1);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+6);}
                        if(win==2&&draw==0) {result[i+1][17] = String.valueOf(Float.parseFloat(result[i+1][17])+1);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+3);}
                        if(win==2&&draw==1) {
                            result[i+1][16] = String.valueOf(Float.parseFloat(result[i+1][16])+0.5);result[i+1][17] = String.valueOf(Float.parseFloat(result[i+1][17])+0.5);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+4.5);
                        }
                        if(win==1&&draw==0) {result[i+1][18] = String.valueOf(Float.parseFloat(result[i+1][18])+1);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+1);}
                        if(win==1&&draw==1) {result[i+1][17] = String.valueOf(Float.parseFloat(result[i+1][17])+0.5);result[i+1][18] = String.valueOf(Float.parseFloat(result[i+1][18])+0.5);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+2);}
                        if(win==1&&draw==2) {result[i+1][16] = String.valueOf(Float.parseFloat(result[i+1][16])+0.33);result[i+1][17] = String.valueOf(Float.parseFloat(result[i+1][17])+0.33);result[i+1][18] = String.valueOf(Float.parseFloat(result[i+1][18])+0.33);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+2);}
                        if(win==0&&draw==0) {result[i+1][19] = String.valueOf(Float.parseFloat(result[i+1][19])+1);}
                        if(win==0&&draw==1) {result[i+1][18] = String.valueOf(Float.parseFloat(result[i+1][18])+0.5);result[i+1][19] = String.valueOf(Float.parseFloat(result[i+1][19])+0.5);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+0.5);}
                        if(win==0&&draw==2) {result[i+1][17] = String.valueOf(Float.parseFloat(result[i+1][17])+0.33);result[i+1][18] = String.valueOf(Float.parseFloat(result[i+1][18])+0.33);result[i+1][19] = String.valueOf(Float.parseFloat(result[i+1][19])+0.33);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+1.33);}
                        if(win==0&&draw==3) {result[i+1][16] = String.valueOf(Float.parseFloat(result[i+1][16])+0.25);result[i+1][17] = String.valueOf(Float.parseFloat(result[i+1][17])+0.25);result[i+1][18] = String.valueOf(Float.parseFloat(result[i+1][18])+0.25);result[i+1][19] = String.valueOf(Float.parseFloat(result[i+1][19])+0.25);result[i+1][21] = String.valueOf(Float.parseFloat(result[i+1][21])+2.5);}
                    percent+=(float) vp/(float) topscore;
                    }
                }
            }
            if(finishgames>=1){
                result[i+1][20]=String.valueOf(percent/(float) finishgames);
            }
            if(result[i+1][20].length()>5) result[i+1][20]=result[i+1][20].substring(0,5);
        }
        ArrayList<String[]> list = new ArrayList<>();
        for (int i=0;i<=7;i++){
            list.add(result[i]);
        }
        for (int i=1;i<=7;i++){
            if(result[i][16].length()>=3&&result[i][16].substring(1,3).equals(".0")) result[i][16] = result[i][16].substring(0,1);
            if(result[i][17].length()>=3&&result[i][17].substring(1,3).equals(".0")) result[i][17] = result[i][17].substring(0,1);
            if(result[i][18].length()>=3&&result[i][18].substring(1,3).equals(".0")) result[i][18] = result[i][18].substring(0,1);
            if(result[i][19].length()>=3&&result[i][19].substring(1,3).equals(".0")) result[i][19] = result[i][19].substring(0,1);
            if(result[i][21].length()>=3&&result[i][21].substring(1,3).equals(".0")) result[i][21] = result[i][21].substring(0,1);
            if(result[i][16].length()>=4&&result[i][16].substring(2,4).equals(".0")) result[i][16] = result[i][16].substring(0,2);
            if(result[i][17].length()>=4&&result[i][17].substring(2,4).equals(".0")) result[i][17] = result[i][17].substring(0,2);
            if(result[i][18].length()>=4&&result[i][18].substring(2,4).equals(".0")) result[i][18] = result[i][18].substring(0,2);
            if(result[i][19].length()>=4&&result[i][19].substring(2,4).equals(".0")) result[i][19] = result[i][19].substring(0,2);
            if(result[i][21].length()>=4&&result[i][21].substring(2,4).equals(".0")) result[i][21] = result[i][21].substring(0,2);
        }
        list.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                if(!o1[21].equals(o2[21])){
                    if(Float.parseFloat(o2[21])>Float.parseFloat(o1[21])){ return 1;}else {
                        return -1;
                    }
                }else {
                    if(Float.parseFloat(o2[20])>Float.parseFloat(o1[20])){ return 1;}else {
                        return -1;
                    }
                }

            }
        });
        return list.toArray(new String[8][22]);
    }

    @Override
    public Log[] getLogs(String gameid) {
        return playMapper.getLogsByGameid(gameid);
    }
    @Override
    public Time[] getTimes(String gameid) {
        return playMapper.getTimesByGameid(gameid);
    }
    @Override
    public ArrayList<Info> getinfo() {
        ArrayList<Info> result = otherMapper.getInfo();
        for(Info info:result){
            info.setColor(racecolormap.get(info.getRace()));
        }
        return result;
    }

}
