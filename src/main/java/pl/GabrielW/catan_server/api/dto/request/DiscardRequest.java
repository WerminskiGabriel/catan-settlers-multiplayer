package pl.GabrielW.catan_server.api.dto.request;

import pl.GabrielW.catan_server.gameEngine.CardType;

import java.util.HashMap;

public record DiscardRequest(
        String playerNickName,
        HashMap< CardType, Integer > cards
) {
}
