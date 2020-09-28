package web.gaia.gaiaproject.model;

import java.util.ArrayList;

public class Rec {
    public int dis;
    public int from;
    public int to;
    public ArrayList<String> roadList;

    public Rec(int dis, int from, int to, ArrayList<String> roadList) {
        this.dis = dis;
        this.from = from;
        this.to = to;
        this.roadList = roadList;
    }

    public int getDis() {
        return dis;
    }

    public void setDis(int dis) {
        this.dis = dis;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;

    }

    public ArrayList<String> getRoadList() {
        return roadList;
    }

    public void setRoadList(ArrayList<String> roadList) {
        this.roadList = roadList;
    }
}