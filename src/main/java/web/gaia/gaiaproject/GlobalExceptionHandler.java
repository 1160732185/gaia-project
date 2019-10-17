package web.gaia.gaiaproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import web.gaia.gaiaproject.controller.MessageBox;
import web.gaia.gaiaproject.exception.CreateGameException;

@EnableWebMvc
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @ExceptionHandler({Exception.class})   //此处为自定义业务异常类
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //返回一个指定的http response状态码
    public MessageBox creategameerror (Exception e) {
        MessageBox messageBox = new MessageBox();
        messageBox.setMessage(e.getMessage());
        return messageBox;
    }
}
