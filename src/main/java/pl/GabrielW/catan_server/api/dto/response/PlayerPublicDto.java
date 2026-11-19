package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.PlayerColor;
import pl.GabrielW.catan_server.model.Player;

public record PlayerPublicDto(
        String nickName ,
        int points ,
        int longestRoadLength ,
        int armyLength ,
        int cardsCount ,
        int specialCardsCount ,
        int buildingsLeft ,
        int buildingUpgradesLeft ,
        int roadsLeft ,
        PlayerColor color

) {
    public static PlayerPublicDto from( Player player ) {
        return new PlayerPublicDto(
                player.getNickName( ) ,
                player.getPoints( ) ,
                player.getLongestRoadLength( ) ,
                player.getArmyLength( ) ,
                player.getCardsCount( ) ,
                player.getSpecialCardsCount( ) ,
                player.getBuildingsLeft(),
                player.getBuildingUpgradesLeft(),
                player.getRoadsLeft(),
                player.getColor( )
        );
    }
}
