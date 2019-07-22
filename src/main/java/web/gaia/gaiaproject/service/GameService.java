package web.gaia.gaiaproject.service;

import web.gaia.gaiaproject.model.Game;

public interface GameService {
    void createGame(String gameId,String player1,String player2,String player3,String player4);
    Game getGameById(String gameId);
}
