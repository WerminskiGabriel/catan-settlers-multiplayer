package pl.GabrielW.catan_server.api.dto;

import java.util.UUID;

public record PlayerJoinGameResponse(
        UUID gameId ,
        UUID playerId ,
        String rawToken
) {
}
