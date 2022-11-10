package web.gaia.gaiaproject.model;

public class User {
    private  String userid;
    private  String userpassword;
    private  String avgrank;
    private  String avgscore;
    private  String title;
    public User(String userid, String userpassword) {
        this.userid = userid;
        this.userpassword = userpassword;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid='" + userid + '\'' +
                ", userpassword='" + userpassword + '\'' +
                '}';
    }

    public String getAvgrank() {
        return avgrank;
    }

    public void setAvgrank(String avgrank) {
        this.avgrank = avgrank;
    }

    public String getAvgscore() {
        return avgscore;
    }

    public void setAvgscore(String avgscore) {
        this.avgscore = avgscore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
