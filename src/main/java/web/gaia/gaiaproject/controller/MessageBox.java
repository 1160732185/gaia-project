package web.gaia.gaiaproject.controller;

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
}
