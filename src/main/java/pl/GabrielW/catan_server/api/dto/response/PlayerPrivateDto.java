package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.PlayerColor;
import pl.GabrielW.catan_server.gameEngine.SpecialCardType;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashMap;

public record PlayerPrivateDto( String nickName , int points , int longestRoadLength , int armyLength ,
                                HashMap< CardType, Integer > cards , int cardsCount ,
                                HashMap< SpecialCardType, Integer > specialCards , int specialCardsCount ,
                                int buildingsLeft , int buildingUpgradesLeft , int roadsLeft , PlayerColor color ) {
    public static PlayerPrivateDto from( Player player ) {
        return new PlayerPrivateDto( player.getNickName( ) , player.getPoints( ) , player.getLongestRoadLength( ) ,
                player.getArmyLength( ) , player.getCards( ) , player.getCardsCount( ) , player.getSpecialCards( ) ,
                player.getSpecialCardsCount( ) , player.getBuildingsLeft( ) , player.getBuildingUpgradesLeft( ) ,
                player.getRoadsLeft( ) , player.getColor( ) );
    }
}
