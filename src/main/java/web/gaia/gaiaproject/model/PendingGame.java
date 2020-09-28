package web.gaia.gaiaproject.model;

public class PendingGame {
    private String gameid;
    private String player1;
    private String player2;
    private String player3;
    private String player4;
    private String des;
    private String gamemode;

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer3() {
        return player3;
    }

    public void setPlayer3(String player3) {
        this.player3 = player3;
    }

    public String getPlayer4() {
        return player4;
    }

    public void setPlayer4(String player4) {
        this.player4 = player4;
    }


    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
