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
    public void turnEnd(String gameid) {
        Play[] plays = playMapper.getPlayByGameId(gameid);
        for (Play p:plays){
            if(p.getRace().equals("格伦星人")&&p.getAc2().equals("0")) {p.setO(p.getQ()+p.getO());p.setQ(0);}
            if(p.getO()>15) p.setO(15);
            if(p.getK()>15) p.setK(15);
            if(p.getC()>30) p.setC(30);
            playMapper.updatePlayById(p);
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
            if(game.getGamemode().equals("2.1")||game.getGamemode().equals("1.1")) lobby.setAuthority("ok");
            Long time = 0l;
            if(game.getLasttime().equals("")) {time = 99999999999l;}else {
                time = System.currentTimeMillis() - Long.valueOf(game.getLasttime());
            }
                lobby.setLasttime(String.valueOf(time));
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
                lobby.setRound(String.valueOf(game.getRound()));
                lobby.setTurn(String.valueOf(game.getTurn()));
                lobby.setCurrentuserid(gameService.getCurrentUserIdById(gameid));
                if(gameService.getCurrentUserIdById(gameid).equals("all")){
                    String state = "";
                    String[] bid = gameService.getBid(gameid);
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
                    lobby.setCurrentuserid(state);
                }
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
            }
            result.add(lobby);
        }
result.sort(new Comparator<Lobby>(){
@Override
public int compare(Lobby arg0, Lobby arg1) {
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

}
