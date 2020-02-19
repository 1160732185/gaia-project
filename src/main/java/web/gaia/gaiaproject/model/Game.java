package web.gaia.gaiaproject.model;

public class Game {
    private String gameId;
    private String mapseed;
    private String blackstar;
    private String otherseed;
    private Integer terratown;
    private Integer round;
    private Integer position;//当前要执行行动的玩家顺位
    private Integer turn;//当前round已经行动到第几轮
    private String pwa1;
    private String pwa2;
    private String pwa3;
    private String pwa4;
    private String pwa5;
    private String pwa6;
    private String pwa7;
    private String qa1;
    private String qa2;
    private String qa3;
    private String gamerecord;
    private int bon1;
    private int bon2;
    private String gamemode;
    private String lasttime;
    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getCurrentplayer() {
        return currentplayer;
    }

    public void setCurrentplayer(Integer currentplayer) {
        this.currentplayer = currentplayer;
    }

    private Integer currentplayer;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getMapseed() {
        return mapseed;
    }

    public void setMapseed(String mapseed) {
        this.mapseed = mapseed;
    }

    public String getBlackstar() {
        return blackstar;
    }

    public void setBlackstar(String blackstar) {
        this.blackstar = blackstar;
    }

    public String getOtherseed() {
        return otherseed;
    }

    public void setOtherseed(String otherseed) {
        this.otherseed = otherseed;
    }

    public Integer getTerratown() {
        return terratown;
    }

    public void setTerratown(Integer terratown) {
        this.terratown = terratown;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public String getPwa1() {
        return pwa1;
    }

    public void setPwa1(String pwa1) {
        this.pwa1 = pwa1;
    }

    public String getPwa2() {
        return pwa2;
    }

    public void setPwa2(String pwa2) {
        this.pwa2 = pwa2;
    }

    public String getPwa3() {
        return pwa3;
    }

    public void setPwa3(String pwa3) {
        this.pwa3 = pwa3;
    }

    public String getPwa4() {
        return pwa4;
    }

    public void setPwa4(String pwa4) {
        this.pwa4 = pwa4;
    }

    public String getPwa5() {
        return pwa5;
    }

    public void setPwa5(String pwa5) {
        this.pwa5 = pwa5;
    }

    public String getPwa6() {
        return pwa6;
    }

    public void setPwa6(String pwa6) {
        this.pwa6 = pwa6;
    }

    public String getPwa7() {
        return pwa7;
    }

    public void setPwa7(String pwa7) {
        this.pwa7 = pwa7;
    }

    public String getQa1() {
        return qa1;
    }

    public void setQa1(String qa1) {
        this.qa1 = qa1;
    }

    public String getQa2() {
        return qa2;
    }

    public void setQa2(String qa2) {
        this.qa2 = qa2;
    }

    public String getQa3() {
        return qa3;
    }

    public void setQa3(String qa3) {
        this.qa3 = qa3;
    }

    public String getGamerecord() {
        return gamerecord;
    }

    public void setGamerecord(String gamerecord) {
        this.gamerecord = gamerecord;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", mapseed='" + mapseed + '\'' +
                ", blackstar='" + blackstar + '\'' +
                ", otherseed='" + otherseed + '\'' +
                ", terratown=" + terratown +
                ", round=" + round +
                ", pwa1=" + pwa1 +
                ", pwa2=" + pwa2 +
                ", pwa3=" + pwa3 +
                ", pwa4=" + pwa4 +
                ", pwa5=" + pwa5 +
                ", pwa6=" + pwa6 +
                ", pwa7=" + pwa7 +
                ", qa1=" + qa1 +
                ", qa2=" + qa2 +
                ", qa3=" + qa3 +
                ", gamerecord='" + gamerecord + '\'' +
                '}';
    }

    public int getBon1() {
        return bon1;
    }

    public void setBon1(int bon1) {
        this.bon1 = bon1;
    }

    public int getBon2() {
        return bon2;
    }

    public void setBon2(int bon2) {
        this.bon2 = bon2;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }
}
