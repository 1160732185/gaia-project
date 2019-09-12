package web.gaia.gaiaproject.model;

public class Game {
    private String gameId;
    private String mapseed;
    private String blackstar;
    private String otherseed;
    private Integer terratown;
    private Integer round;
    private Integer pwa1;
    private Integer pwa2;
    private Integer pwa3;
    private Integer pwa4;
    private Integer pwa5;
    private Integer pwa6;
    private Integer pwa7;
    private Integer qa1;
    private Integer qa2;
    private Integer qa3;
    private String gamerecord;

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

    public Integer getPwa1() {
        return pwa1;
    }

    public void setPwa1(Integer pwa1) {
        this.pwa1 = pwa1;
    }

    public Integer getPwa2() {
        return pwa2;
    }

    public void setPwa2(Integer pwa2) {
        this.pwa2 = pwa2;
    }

    public Integer getPwa3() {
        return pwa3;
    }

    public void setPwa3(Integer pwa3) {
        this.pwa3 = pwa3;
    }

    public Integer getPwa4() {
        return pwa4;
    }

    public void setPwa4(Integer pwa4) {
        this.pwa4 = pwa4;
    }

    public Integer getPwa5() {
        return pwa5;
    }

    public void setPwa5(Integer pwa5) {
        this.pwa5 = pwa5;
    }

    public Integer getPwa6() {
        return pwa6;
    }

    public void setPwa6(Integer pwa6) {
        this.pwa6 = pwa6;
    }

    public Integer getPwa7() {
        return pwa7;
    }

    public void setPwa7(Integer pwa7) {
        this.pwa7 = pwa7;
    }

    public Integer getQa1() {
        return qa1;
    }

    public void setQa1(Integer qa1) {
        this.qa1 = qa1;
    }

    public Integer getQa2() {
        return qa2;
    }

    public void setQa2(Integer qa2) {
        this.qa2 = qa2;
    }

    public Integer getQa3() {
        return qa3;
    }

    public void setQa3(Integer qa3) {
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
}
