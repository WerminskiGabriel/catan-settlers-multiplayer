package pl.GabrielW.catan_server.api.dto;

import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.Coordinate;
import pl.GabrielW.catan_server.gameEngine.TurnPhase;

import java.util.HashMap;
import java.util.HashSet;

public record GameActionRequest(
        TurnPhase phase ,
        HashMap< CardType, Integer > TradeCardsOut,
        HashMap< CardType, Integer > TradeCardsIn,
        String otherPlayerNickName ,
        HashSet< Coordinate > coordinates ,
        Coordinate coordinate,
        HashMap< CardType, Integer > cardsToDiscard
) {
}
