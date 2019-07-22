package web.gaia.gaiaproject.serviceimpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import web.gaia.gaiaproject.service.*;
import web.gaia.gaiaproject.serviceimpl.*;
@Configuration
public class ServiceConfiguration {
    @Bean
    UserService getUserService() { return new UserServiceImpl(); }
    @Bean
    GameService getGameService() { return new GameServiceImpl(); }
}
