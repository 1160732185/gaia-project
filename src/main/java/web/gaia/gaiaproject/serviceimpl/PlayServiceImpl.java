package web.gaia.gaiaproject.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import web.gaia.gaiaproject.mapper.PlayMapper;
import web.gaia.gaiaproject.service.PlayService;

public class PlayServiceImpl implements PlayService {
    @Autowired
    PlayMapper playMapper;
    @Override
    public String[] showGames(String userid) {
        return playMapper.showGames(userid);
    }
}
