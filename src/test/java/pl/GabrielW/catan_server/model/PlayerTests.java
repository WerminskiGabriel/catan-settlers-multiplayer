package pl.GabrielW.catan_server.model;

import org.junit.jupiter.api.Test;
import pl.GabrielW.catan_server.gameEngine.Building;
import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.PlayerColor;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTests {

    @Test
    public void addCard() {
        Player player = new Player( "Katara" , PlayerColor.BLUE );

        player.addCard( CardType.BRICK );

        assertTrue( player.getCards().size() == 1 , "" );
    }

    @Test
    public void addCards() {
        Player player = new Player( "Katara" , PlayerColor.BLUE );
        HashMap< CardType, Integer > cardTypes = Building.cost();

        int cardsCount = cardTypes.values().stream().mapToInt( Integer::intValue ).sum();

        player.addCards( cardTypes );
        assertTrue( player.getCards().size() == cardsCount , "" );
    }

    public void removeCard() {
        Player player = new Player( "Katara" , PlayerColor.BLUE );

        player.addCard( CardType.BRICK );
        player.addCard( CardType.BRICK );

        assertTrue( player.getCards().size() == 1 , "" );
    }

    @Test
    public void removeCards() {
        Player player = new Player( "Katara" , PlayerColor.BLUE );
        HashMap< CardType, Integer > cardTypes = Building.cost();

        int cardsCount = cardTypes.values().stream().mapToInt( Integer::intValue ).sum();

        player.addCards( cardTypes );
        player.addCards( cardTypes );
        player.removeCards( cardTypes );
        assertTrue( player.getCards().size() == cardsCount , "" );
    }




}
