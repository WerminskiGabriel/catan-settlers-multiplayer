package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import lombok.Setter;
import pl.GabrielW.catan_server.model.Player;

import java.util.*;

@Getter
@Setter
public class Game {
    private final Board board;
    private final UUID id;
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
        this.id = UUID.randomUUID( );
        this.players = new ArrayList<>( );
        this.playerFactory = new PlayerFactory( );
        this.turnNumber = 0;
        this.rollingDicesFactory = new RollingDicesFactory( );
        this.turnPhase = TurnPhase.TRADE_AND_BUILD;
        this.limitMT = 7;
    }

    /**
     *
     * @param nickName
     */
    public Player addPlayer( String nickName ) {
        return addPlayer( nickName , null );
    }

    public Player addPlayer( String nickName , String hashToken ) {
        Player player = this.playerFactory.createPlayer( nickName , hashToken );
        players.add( player );
        return player;
    }

    /**
     *
     * @param nickName
     */
    public void removePlayer( String nickName ) {
        this.players.removeIf( player -> player.getNickName( ).equals( nickName ) );
    }

    /**
     *
     * @param nickName
     * @return
     */
    public Player getPlayer( String nickName ) {
        if( nickName == null ) {
            return null;
        }
        return players.stream( ).filter( p -> p.getNickName( ).equals( nickName ) ).findFirst( ).orElse( null );
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


    /**
     *
     * @param thisCards
     * @param otherCards
     * @param otherP
     */
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

    /**
     *
     * @param thisCards
     * @param otherCards
     * @param otherPlayerNickName
     */
    public void trade( HashMap< CardType, Integer > thisCards , HashMap< CardType, Integer > otherCards , String otherPlayerNickName ) {
        Player otherP = getPlayer( otherPlayerNickName );
        if( otherP == null ) {
            throw new IllegalArgumentException( "Player u want to trade with does not exist: " + otherPlayerNickName );
        }
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

    /**
     *
     * @param newB
     * @return
     */
    public boolean buildRoad( HashSet< Coordinate > newB ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD && turnPhase != TurnPhase.SETUP_ROAD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        if( TurnPhase.SETUP_ROAD == turnPhase ) {
            boolean placed = board.placeRoad( newB , currentPlayer , true );
            if( placed ) {
                turnPhase = TurnPhase.SETUP_BUILDING;
            }
            return placed;
        }
        return board.placeRoad( newB , currentPlayer , false );
    }

    /**
     *
     * @param newB
     * @return
     */
    public boolean buildBuilding( HashSet< Coordinate > newB ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD && turnPhase != TurnPhase.SETUP_BUILDING ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        if( TurnPhase.SETUP_BUILDING == turnPhase ) {
            boolean placed = board.placeBuilding( newB , currentPlayer , true );
            if( placed ) {
                turnPhase = TurnPhase.SETUP_ROAD;
                if( turnNumber >= players.size( ) && turnNumber < 2 * players.size( ) ) {
                    for( Coordinate coord : newB ) {
                        CardType resource = board.coordinateToCardTypes( coord );
                        if( resource != null && resource != CardType.DESERT && resource != CardType.OCEAN ) {
                            currentPlayer.addCard( resource );
                        }
                    }
                }
            }
            return placed;
        }
        return board.placeBuilding( newB , currentPlayer , false );
    }

    /**
     *
     */
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

    /**
     *
     * @param newRobber
     */
    public void placeRobber( Coordinate newRobber ) {
        if( turnPhase != TurnPhase.ROBBER_PLACEMENT ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        this.robberCord = newRobber;
        turnPhase = TurnPhase.STEAL_PHASE;
    }

    /**
     *
     * @param stealFromPlayerNickName
     */
    public void stealCard( String stealFromPlayerNickName ) {
        Player stealFromP = getPlayer( stealFromPlayerNickName );
        if( stealFromP == null ) {
            throw new IllegalArgumentException( "Player u want to steal from does not exist: " + stealFromPlayerNickName );
        }
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

    /**
     *
     * @param stealFromP
     */
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


    public void endTurn( ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD && turnPhase != TurnPhase.SETUP_BUILDING ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        turnNumber += 1;

        if( turnNumber < 2 * players.size( ) ) {
            if( turnNumber >= players.size( ) ) {
                currentPlayer = players.get( players.size( ) - 1 - ( turnNumber % players.size( ) ) );
            } else {
                currentPlayer = players.get( turnNumber % players.size( ) );
            }
        } else {
            currentPlayer = players.get( turnNumber % players.size( ) );
            turnPhase = TurnPhase.ROLL_PHASE;
        }
    }

    public void startGame( ) {
        if( this.players.size( ) < 2 ) {
            throw new IllegalArgumentException( "There must be at least 2 players in Game" );
        }
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        turnPhase = TurnPhase.SETUP_BUILDING;
        currentPlayer = players.get( 0 );
    }


}