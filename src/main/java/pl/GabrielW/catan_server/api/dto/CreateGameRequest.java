package pl.GabrielW.catan_server.api.dto;

import java.util.List;

public record CreateGameRequest( List< String > playerNickNames ) {
}
