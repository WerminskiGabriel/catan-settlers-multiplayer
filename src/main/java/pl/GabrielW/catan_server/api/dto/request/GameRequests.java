package pl.GabrielW.catan_server.api.dto.request;

import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.Coordinate;

import java.util.Map;
import java.util.Set;

public final class GameRequests {

    private GameRequests( ) { }

    public record BuildRequest( Set< Coordinate > coordinates ) { }

    public record PlaceRobberRequest( Coordinate coordinate ) { }

    public record StealRequest( String victimNickName ) { }

    public record TradeRequest( Map< CardType, Integer > thisCards , Map< CardType, Integer > otherCards ,
                                String otherPlayerNickName ) { }

    public record BankTradeRequest( CardType give , CardType receive ) { }
}
