package web.gaia.gaiaproject.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.GameMapper;
import web.gaia.gaiaproject.mapper.OtherMapper;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.model.*;
import web.gaia.gaiaproject.service.GameService;
import web.gaia.gaiaproject.service.PlayService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import static web.gaia.gaiaproject.controller.MessageBox.*;
public class PlayServiceImpl implements PlayService {
    @Autowired
    PlayMapper playMapper;
    @Autowired
    OtherMapper otherMapper;
    @Autowired
    GameMapper gameMapper;
    @Autowired
    GameService gameService;
    @Override
    public String[] showGames(String userid) {
        return playMapper.showGames(userid);
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
        String[][] result = new String[42][3];
        for (int i=0;i<14;i++){
            result[i][0]="0";
            result[i+14][0]="300";
        }
        Play[] plays = otherMapper.getAllPlay();
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

    @Override
    public ArrayList<Lobby> showLobby(String userid) {
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
                lobby.setRound("游戏结束("+rank+")");
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
return  (int)(Long.valueOf(arg0.getLasttime())-Long.valueOf(arg1.getLasttime()));
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
        if(gamemode.length()==5&&gamemode.charAt(4)=='2') gm+="e" ;
        if(gamemode.length()==5&&gamemode.charAt(4)=='3') gm+="f" ;
        if(gamemode.length()==5&&gamemode.charAt(4)=='4') gm+="g" ;
        if(gamemode.length()==5&&gamemode.charAt(4)=='5') gm+="h" ;
        if(gamemode.length()==5&&gamemode.charAt(4)=='6') gm+="i" ;
        if(gamemode.charAt(2)=='0') gm+="/第二价格竞拍";
        if(gamemode.charAt(2)=='1') gm+="/随机顺位";
        if(gamemode.charAt(2)=='2') gm+="/末位旋转地图";
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
    public ArrayList<League> showPendingLeague() {
        ArrayList<League> leagues = otherMapper.getPLeagues();
      /*  for (int i=0;i<leagues.size();i++){
            leagues.get(i).setGamemode(getGameModeName(leagues.get(i).getGamemode()));
            if(!leagues.get(i).getPlayer1().equals("")&&!leagues.get(i).getPlayer2().equals("")&&!leagues.get(i).getPlayer3().equals("")&&!leagues.get(i).getPlayer4().equals("")&&!leagues.get(i).getPlayer5().equals("")&&!leagues.get(i).getPlayer6().equals("")&&!leagues.get(i).getPlayer7().equals(""))
            {leagues.remove(i);
            i--;}
        }*/
        return leagues;
    }

    @Override
    public String joinLeague(String gameid, String userid) {
        League league = gameMapper.getPLeaguebyId(gameid);
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
        String[][] result = new String[9][16];
        ArrayList<String> players = new ArrayList<>();
        League league = gameMapper.getPLeaguebyId(leagueid);
        for (int i=1;i<=7;i++){
            String gameid = leagueid+"_G"+String.valueOf(i);
            result[0][i*2] = gameid;
            Game game = gameMapper.getGameById(gameid);
            if(game!=null){
                if(game.getBlackstar()!=null&&game.getBlackstar().equals("游戏结束"))  { result[0][i*2] ="游戏结束";}
               else {
                   result[0][i*2] = "R"+game.getRound()+"T"+game.getTurn();
                }
            }
        }
        result[1][0] = league.getPlayer1(); result[2][0] = league.getPlayer2(); result[3][0] = league.getPlayer3(); result[4][0] = league.getPlayer4(); result[5][0] = league.getPlayer5(); result[6][0] = league.getPlayer6(); result[7][0] = league.getPlayer7();
        players.add(league.getPlayer1());players.add(league.getPlayer2());players.add(league.getPlayer3());players.add(league.getPlayer4());players.add(league.getPlayer5());players.add(league.getPlayer6());players.add(league.getPlayer7());
        for (int i=0;i<=6;i++){
            String userid = players.get(i);
            for (int j=1;j<=7;j++){
                String gameid = leagueid+"_G"+String.valueOf(j);
                Play play = playMapper.getPlayByGameIdUserid(gameid,userid);
                if(play == null) {result[i+1][j*2] = "";}else {
                    int vp = otherMapper.getvp(gameid,userid);
                    result[i+1][j*2] = play.getRace()+"___"+String.valueOf(vp);
                    if(play.getRace().equals("未知种族")) {result[i+1][j*2+1] = "#FFFFFF";}else {
                        result[i+1][j*2+1] = racecolormap.get(play.getRace());
                    }
                }
            }
        }
        return result;
    }

}
