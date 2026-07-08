package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import lombok.Setter;
import pl.GabrielW.catan_server.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
public class Game {
    private final Board board;
    private final ArrayList< Player > players;
    private Player currentPlayer;
    private final PlayerFactory playerFactory;
    private int turnNumber;
    private TurnPhase turnPhase;
    private final RollingDicesFactory rollingDicesFactory;
    private final int limitMT;
    private Coordinate robberCord;


    public Game( ) {
        this.board = new Board( );
        this.players = new ArrayList<>( );
        this.playerFactory = new PlayerFactory( );
        this.turnNumber = 0;
        this.rollingDicesFactory = new RollingDicesFactory( );
        this.turnPhase = TurnPhase.TRADE_AND_BUILD;
        this.limitMT = 7;
    }

    public void addPlayer( String nickName ) {
        Player player = this.playerFactory.createPlayer( nickName );
        players.add( player );
    }

    public void removePlayer( String nickName ) {
        this.players.removeIf( player -> player.getNickName( ).equals( nickName ) );
    }


    public void rollDice( ) {
        if( turnPhase != TurnPhase.ROLL_PHASE ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        this.rollingDicesFactory.roll( );
        int diceSum = rollingDicesFactory.getSum( );

        if( diceSum == 7 ) {
            turnPhase = TurnPhase.DISCARD_PHASE;
        } else {

            List< Coordinate > coordinates = board.tokenToCoordinates( diceSum );
            for( Coordinate coordinate : coordinates ) {
                CardType cardType = board.coordinateToCardTypes( coordinate );
                List< Building > buildings = board.coordinateToBuildings( coordinate );
                for( Building building : buildings ) {
                    Player player = building.getPlayer( );
                    player.addCard( cardType );
                }
            }
            turnPhase = TurnPhase.TRADE_AND_BUILD;
        }

    }

    public void trade( HashMap< CardType, Integer > thisCards , HashMap< CardType, Integer > otherCards , Player otherP ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        if( currentPlayer.canAfford( thisCards ) && otherP.canAfford( otherCards ) ) {
            currentPlayer.removeCards( thisCards );
            currentPlayer.addCards( otherCards );
            otherP.removeCards( otherCards );
            otherP.addCards( thisCards );
        }

    }

    public void buildRoad( HashSet< Coordinate > newB ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        board.placeRoad( newB , currentPlayer );
    }

    public void buildBuilding( HashSet< Coordinate > newB ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        boolean isSetup = turnNumber % players.size( ) < 2;
        board.placeBuilding( newB , currentPlayer , isSetup );
    }

    public void discardCards( ) {
        if( turnPhase != TurnPhase.DISCARD_PHASE ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        //TODO change random to choosing
        for( Player player : players ) {
            if( player.hasCardsAboveLimit( limitMT ) ) {
                player.removeRandomCardsToMatchLimit( limitMT );
            }
        }
        turnPhase = TurnPhase.ROBBER_PLACEMENT;
    }

    public void placeRobber( Coordinate newRobber ) {
        if( turnPhase != TurnPhase.ROBBER_PLACEMENT ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        this.robberCord = newRobber;
        turnPhase = TurnPhase.STEAL_PHASE;
    }

    public void stealCard( Player stealFromP ) {
        if( turnPhase != TurnPhase.STEAL_PHASE ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        if( stealFromP.hasCardsAboveLimit( 0 ) ) {

            CardType stolenCard = stealFromP.getRandomCard( );
            stealFromP.removeCard( stolenCard );
            currentPlayer.addCard( stolenCard );
        } else {
            //throw
        }
        turnPhase = TurnPhase.TRADE_AND_BUILD;
    }

    public void endTurn( ) throws Exception {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        currentPlayer = players.get( turnNumber % players.size( ) );
        turnNumber += 1;

        turnPhase = TurnPhase.ROLL_PHASE;
    }

    public void startGame( ) {
        if( this.players.size( ) < 2 ) {
            throw new IllegalArgumentException( "There must be at least 2 players in Game" );
        }
        turnPhase = TurnPhase.ROLL_PHASE;
    }


}