package web.gaia.gaiaproject.controller;

import java.util.HashMap;

public class MessageBox {
    // 报废记录状态码
    final static public int PLAYER_NOT_EXIST_CODE=100;
    final static public int NEW_GAME_EXIST_CODE=101;
    final static public int NEW_GAME_CREATE_SUCCESS_CODE=102;
    private int status;
    private String message;
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    final static public String[] racename = new String[]
            {"人类","亚特兰斯星人","圣禽族","蜂人","晶矿星人","炽炎族","翼空族","格伦星人","大使星人","利爪族","章鱼人","疯狂机器","伊塔星人","超星人"};
    final static public String ck = "#000000";
    final static public String re = "#FF0000";
    final static public String bl = "#4275e5";
    final static public String ye = "#ffd700";
    final static public String gr = "#828282";
    final static public String br = "#8b4c39";
    final static public String wh = "#E0FFFF";
    final static public String ga = "#7cfc00";
    final static public String pu = "#9400d3";
    final static public String or = "#FF8C00";
    final static public HashMap<String, String> racecolormap = new HashMap<String, String>() {
        {
            put("人类", "#4275e5");
            put("亚特兰斯星人", "#4275e5");
            put("熊猫人", "#4275e5");
            put("殖民者", "#4275e5");
            put("圣禽族", "#FF0000");
            put("蜂人", "#FF0000");
            put("混沌法师", "#FF0000");
            put("晶矿星人", "#FF8C00");
            put("炽炎族", "#FF8C00");
            put("天龙星人", "#FF8C00");
            put("翼空族","#ffd700");
            put("格伦星人", "#ffd700");
            put("蜥族", "#ffd700");
            put("大使星人","#8b4c39");
            put("利爪族", "#8b4c39");
            put("猎户星人", "#8b4c39");
            put("章鱼人", "#828282");
            put("疯狂机器", "#828282");
            put("魔族", "#828282");
            put("伊塔星人", "#E0FFFF");
            put("超星人", "#E0FFFF");
            put("织女星人", "#E0FFFF");
        }
    };
    final static public HashMap<Integer, String> racenumcolormap = new HashMap<Integer, String>() {
        {
            put(0, "#4275e5");
            put(1, "#4275e5");
            put(14, "#4275e5");
            put(21, "#4275e5");
            put(2, "#FF0000");
            put(3, "#FF0000");
            put(15, "#FF0000");
            put(4, "#FF8C00");
            put(5, "#FF8C00");
            put(16, "#FF8C00");
            put(6,"#ffd700");
            put(7, "#ffd700");
            put(17, "#ffd700");
            put(8,"#8b4c39");
            put(9, "#8b4c39");
            put(18, "#8b4c39");
            put(10, "#828282");
            put(11, "#828282");
            put(19, "#828282");
            put(12, "#E0FFFF");
            put(13, "#E0FFFF");
            put(20, "#E0FFFF");
        }
    };
    //种族地形改造轮
    final static public HashMap<String, Integer> colorroundmap = new HashMap<String, Integer>() {
        {
            put("#4275e5",0);
            put("#FF0000",1);
            put("#FF8C00",2);
            put("#ffd700",3);
            put("#8b4c39",4);
            put("#828282",5);
            put("#E0FFFF",6);
        }
    };
    final static public HashMap<String, Integer> racenummap = new HashMap<String, Integer>() {
        {
            put("人类", 0);
            put("亚特兰斯星人", 1);
            put("圣禽族", 2);
            put("蜂人", 3);
            put("晶矿星人", 4);
            put("炽炎族", 5);
            put("翼空族",6);
            put("格伦星人", 7);
            put("大使星人",8);
            put("利爪族", 9);
            put("章鱼人",10);
            put("疯狂机器", 11);
            put("伊塔星人", 12);
            put("超星人", 13);
            put("熊猫人", 14);
            put("混沌法师", 15);
            put("天龙星人", 16);
            put("蜥族", 17);
            put("猎户星人", 18);
            put("魔族", 19);
            put("织女星人", 20);
            put("殖民者", 21);
        }
    };
    //原版种族初始资源
    final static public int[][] raceinitresource = new int[][]{
            {4,15,3,1,4,4},
            {4,13,3,1,4,0},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,3,1,2,4},
            {4,15,3,0,2,2},
            {4,15,3,2,2,4},
            {5,15,3,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,3,4},
            {3,15,2,1,2,4},
            {4,15,1,1,2,4},
            {5,15,3,1,4,4},
            {4,15,2,1,2,4},
    };
    //1.0
    final static public int[][] raceinitresource1 = new int[][]{
            {4,15,3,1,4,3},
            {4,15,3,1,2,2},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,3,2},
            {4,15,3,2,3,4},
            {5,15,4,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,2,4},
            {5,15,2,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,2,4},
    };
    //2.0
    final static public int[][] raceinitresource2 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,2,2},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,2},
            {4,15,3,2,2,4},
            {5,15,4,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,7,0},
            {4,15,2,1,2,4},
            {5,15,2,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,4,2},
            {5,15,4,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
    };
    //2.2
    final static public int[][] raceinitresource22 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,2,2},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4},
            {4,15,3,1,2,2},
            {4,20,3,2,4,4},
            {5,15,4,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,7,0},
            {4,15,2,1,2,4},
            {4,15,1,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,4,2},
            {5,15,4,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
    };
    final static public int[][] raceinitresource23 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,2,2},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4},
            {4,15,3,1,2,2},
            {4,20,3,2,4,4},
            {5,15,4,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,4,2},
            {4,15,2,1,2,4},
            {4,15,1,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,2,4},
            {5,15,4,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
    };
    final static public int[][] raceinitresource24 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,2,2},
            {4,15,3,1,2,4},
            {4,15,3,1,0,6},
            {5,15,3,1,2,4},
            {4,15,3,1,2,2},
            {4,20,3,2,4,4},
            {5,15,4,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,4,2},
            {4,15,2,1,2,4},
            {4,15,1,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,2,4},
            {5,15,4,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
    };
    final static public int[][] raceinitresource25 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,2,2},
            {4,15,3,1,2,4},
            {4,15,3,1,0,6},
            {5,15,3,1,2,4},
            {4,15,3,1,2,2},
            {4,20,3,2,2,4},
            {5,15,4,0,2,4},
            {4,15,3,2,2,4},
            {4,15,3,1,4,2},
            {4,15,2,1,2,4},
            {4,15,1,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,2,4},
            {5,15,4,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4}
    };
    final static public int[][] raceinitresource26 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,4,0},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4},
            {3,15,3,1,2,2},
            {4,20,3,2,2,4},
            {5,15,4,0,2,4},
            {4,17,3,2,2,4},
            {4,15,3,1,3,2},
            {4,15,2,1,2,4},
            {4,15,1,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,0,6},
            {5,15,4,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4}
    };
    final static public int[][] raceinitresource27 = new int[][]{
            {4,15,3,1,4,4},
            {4,15,3,1,4,0},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4},
            {3,15,3,1,2,2},
            {4,20,3,2,2,4},
            {5,15,4,0,2,4},
            {4,17,3,2,2,4},
            {4,15,3,1,3,2},
            {4,15,2,1,2,4},
            {4,15,1,1,2,4},
            {4,15,3,1,3,4},
            {4,15,2,1,0,6},
            {5,15,3,1,2,4},
            {4,15,3,2,2,4},
            {6,15,3,1,2,4},
            {4,15,3,1,2,4},
            {4,15,3,1,2,4},
            {6,15,1,1,2,4},
            {4,15,3,1,2,4},
            {5,15,3,1,2,4}
    };
    final static public String[] spaceNo1 = new String[]{ck,ye,ck,ck,br,ck,ck,ck,ck,ck,ck,re,ck,bl,ck,or,ck,pu,ck};
    final static public  String[] spaceNo2 = new String[]{ck,ck,ck,or,ck,br,re,gr,ck,ck,ck,ck,ck,wh,ck,pu,ck,ye,ck};
    final static public String[] spaceNo3 = new String[]{ck,ck,ck,ck,ga,ck,bl,pu,ck,ck,ck,ye,ck,ck,wh,ck,ck,gr,ck};
    final static public  String[] spaceNo4 = new String[]{ck,wh,ck,ck,ck,or,ck,gr,re,ck,ck,ck,ck,ck,br,ck,ck,ck,bl};
    final static public  String[] spaceNo5 = new String[]{ck,ck,ck,ck,ga,ck,or,wh,ck,ck,ck,ye,ck,ck,ck,ck,pu,re,ck};
    final static public  String[] spaceNo6 = new String[]{ck,ck,ck,ck,br,ck,ck,ck,ck,ck,ga,ck,pu,bl,ck,pu,ck,ck,ye};
    final static public  String[] spaceNo7 = new String[]{pu,ck,ck,ck,ck,ga,ck,ck,re,ck,ck,gr,br,ck,ga,ck,ck,ck,ck};
    final static public  String[] spaceNo8 = new String[]{ck,ck,ck,ck,ck,or,pu,bl,wh,ck,ck,ck,ck,ck,gr,ck,pu,ck,ck};
    final static public  String[] spaceNo9 = new String[]{ck,ck,br,or,ck,gr,ck,ck,ck,ck,ck,ck,pu,ck,ga,ck,wh,ck,ck};
    final static public  String[] spaceNo0 = new String[]{ck,ck,bl,ck,ye,ck,re,ck,ck,ck,ck,ck,pu,ck,ga,ck,pu,ck,ck};
    final static public  int[] rotate0 = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
    final static public  int[] rotate1 = new int[]{2,6,11,1,5,10,15,0,4,9,14,18,3,8,13,17,7,12,16};
    final static public  int[] rotate2 = new int[]{11,15,18,6,10,14,17,2,5,9,13,16,1,4,8,12,0,3,7};
    final static public  int[] rotate3 = new int[]{18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0};
    final static public  int[] rotate4 = new int[]{16,12,7,17,13,8,3,18,14,9,4,0,15,10,5,1,11,6,2};
    final static public  int[] rotate5 = new int[]{7,3,0,12,8,4,1,16,13,9,5,2,17,14,10,6,18,15,11};
    final static public  int[] location1 = new int[]{4,1,4,2,4,3,5,1,5,2,5,3,5,4,6,1,6,2,6,3,6,4,6,5,7,1,7,2,7,3,7,4,8,1,8,2,8,3};
    final static public  int[] location2 = new int[]{9,1,9,2,9,3,10,1,10,2,10,3,10,4,11,1,11,2,11,3,11,4,11,5,12,1,12,2,12,3,12,4,13,1,13,2,13,3};
    final static public  int[] location3 = new int[]{14,1,14,2,14,3,15,1,15,2,15,3,15,4,16,1,16,2,16,3,16,4,16,5,17,1,17,2,17,3,17,4,18,1,18,2,18,3};
    final static public  int[] location4 = new int[]{1,1,1,2,1,3,2,1,2,2,2,3,2,4,3,1,3,2,3,3,3,4,3,5,4,4,4,5,4,6,4,7,5,5,5,6,5,7};
    final static public  int[] location5 = new int[]{6,6,6,7,6,8,7,5,7,6,7,7,7,8,8,4,8,5,8,6,8,7,8,8,9,4,9,5,9,6,9,7,10,5,10,6,10,7};
    final static public  int[] location6 = new int[]{11,6,11,7,11,8,12,5,12,6,12,7,12,8,13,4,13,5,13,6,13,7,13,8,14,4,14,5,14,6,14,7,15,5,15,6,15,7};
    final static public  int[] location7 = new int[]{16,6,16,7,16,8,17,5,17,6,17,7,17,8,18,4,18,5,18,6,18,7,18,8,19,1,19,2,19,3,19,4,20,1,20,2,20,3};
    final static public  int[] location8 = new int[]{3,6,3,7,3,8,4,8,4,9,4,10,4,11,5,8,5,9,5,10,5,11,5,12,6,9,6,10,6,11,6,12,7,9,7,10,7,11};
    final static public  int[] location9 = new int[]{8,9,8,10,8,11,9,8,9,9,9,10,9,11,10,8,10,9,10,10,10,11,10,12,11,9,11,10,11,11,11,12,12,9,12,10,12,11};
    final static public  int[] location10 = new int[]{13,9,13,10,13,11,14,8,14,9,14,10,14,11,15,8,15,9,15,10,15,11,15,12,16,9,16,10,16,11,16,12,17,9,17,10,17,11};

}
