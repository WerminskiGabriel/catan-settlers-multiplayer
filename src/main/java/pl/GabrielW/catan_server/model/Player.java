package pl.GabrielW.catan_server.model;

import lombok.Setter;
import lombok.Getter;
import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.PlayerColor;
import pl.GabrielW.catan_server.gameEngine.SpecialCardType;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Player {
    private String nickName;
    private int points;
    private int longestRoadLength;
    private int armyLength;
    private HashMap< CardType, Integer > cards;
    private HashMap< SpecialCardType, Integer > specialCards;
    private int buildingsLeft;
    private int buildingUpgradesLeft;
    private int roadsLeft;
    private PlayerColor color;

    public Player() { }

    public Player( String nickName , PlayerColor color ) {
        this.nickName = nickName;
        this.color = color;
        this.points = 0;
        this.longestRoadLength = 0;
        this.armyLength = 0;
        this.cards = new HashMap< CardType, Integer >();
        this.specialCards = new HashMap<>();
        this.buildingsLeft = 5;
        this.buildingUpgradesLeft = 3;
        this.roadsLeft = 10;
    }

    public void addCard( CardType cardType ) {
        this.cards.compute( cardType , ( k , v ) -> ( v == null ) ? 1 : v + 1 );
    }

    public void addCards( HashMap< CardType, Integer > cardTypes ) {
        cardTypes.forEach( ( type , cnt ) -> {
            for( int i = 0 ; i < cnt ; i++ ) {
                this.addCard( type );
            }
        } );
    }

    public void removeCard( CardType cardType ) {
        this.cards.compute( cardType , ( k , v ) -> {
            if( v == null || v <= 0 ) {
                throw new IllegalArgumentException( "Not enough cards" );
            } else { return v - 1; }
        } );
    }

    public void removeCards( HashMap< CardType, Integer > cardTypes ) {
        cardTypes.forEach( ( type , cnt ) -> {
            for( int i = 0 ; i < cnt ; i++ ) {
                this.removeCard( type );
            }
        } );
    }

    public boolean CanAffordCards( HashMap< CardType, Integer > requiredTypes ) {
        HashMap< CardType, Integer > playerCards = this.getCards();

        for( Map.Entry< CardType, Integer > entry : requiredTypes.entrySet() ) {
            CardType type = entry.getKey();
            int cnt = entry.getValue();
            if( playerCards.getOrDefault( type , 0 ) < cnt ) {
                return false;
            }
        }
        return true;
    }
}

