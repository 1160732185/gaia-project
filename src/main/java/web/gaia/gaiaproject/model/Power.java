package web.gaia.gaiaproject.model;

import lombok.Data;
import lombok.ToString;

public class Power {
    String gameid;
    String giverace;
    String receiverace;
    String location;
    String structure;
    int power;
    String userid;

    public int getActually() {
        return actually;
    }

    public void setActually(int actually) {
        this.actually = actually;
    }

    int actually;
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    int num;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getGiverace() {
        return giverace;
    }

    public void setGiverace(String giverace) {
        this.giverace = giverace;
    }

    public String getReceiverace() {
        return receiverace;
    }

    public void setReceiverace(String receiverace) {
        this.receiverace = receiverace;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "Power{" +
                "gameid='" + gameid + '\'' +
                ", giverace='" + giverace + '\'' +
                ", receiverace='" + receiverace + '\'' +
                ", location='" + location + '\'' +
                ", structure='" + structure + '\'' +
                ", power=" + power +
                ", userid='" + userid + '\'' +
                ", actually=" + actually +
                ", num=" + num +
                '}';
    }
}
