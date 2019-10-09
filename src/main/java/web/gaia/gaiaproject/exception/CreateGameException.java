package web.gaia.gaiaproject.exception;

public class CreateGameException extends Exception {
    public CreateGameException(){

    }
    public CreateGameException(String message) {
        super(message);// 把参数传递给Throwable的带String参数的构造方法
    }
}
