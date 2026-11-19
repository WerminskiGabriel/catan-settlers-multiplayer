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
    private final PlayerFactory playerFactory;
    private final RollingDicesFactory rollingDicesFactory;
    private final int limitMT; // cardLimit before discarding
    private Player currentPlayer;
    private int turnNumber;
    private TurnPhase turnPhase;
    private Coordinate robberCord;
    private int winningPointsCondition;


    public Game( ) {
        this.board = new Board( );
        this.id = UUID.randomUUID( );
        this.players = new ArrayList<>( );
        this.playerFactory = new PlayerFactory( );
        this.turnNumber = 0;
        this.rollingDicesFactory = new RollingDicesFactory( );
        this.turnPhase = TurnPhase.TRADE_AND_BUILD;
        this.limitMT = 7;
        this.robberCord = board.getDesertCord( );
        this.winningPointsCondition = 10;

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

                if( coordinate.equals( this.robberCord ) ) {
                    continue; // tiles with robber can not produce cards
                }

                CardType cardType = board.coordinateToCardTypes( coordinate );
                List< Building > buildings = board.coordinateToBuildings( coordinate );
                for( Building building : buildings ) {
                    Player player = building.getPlayer( );
                    player.addCard( cardType );
                    if( building.isCity( ) ) {
                        player.addCard( cardType );
                    }
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
    public void trade( HashMap< CardType, Integer > thisCards , HashMap< CardType, Integer > otherCards ,
                       Player otherP ) {
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
    public void trade( HashMap< CardType, Integer > thisCards , HashMap< CardType, Integer > otherCards ,
                       String otherPlayerNickName ) {
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
                currentPlayer.setRoadsLeft( currentPlayer.getRoadsLeft( ) - 1 );
                turnPhase = TurnPhase.SETUP_BUILDING;
            }
            return placed;
        }
        boolean placed = board.placeRoad( newB , currentPlayer , false );
        if( placed ) {
            currentPlayer.setRoadsLeft( currentPlayer.getRoadsLeft( ) - 1 );
        }
        return placed;
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
                currentPlayer.setPoints( currentPlayer.getPoints( ) + Building.points( ) );
                currentPlayer.setBuildingsLeft( currentPlayer.getBuildingsLeft( ) - 1 );
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
        boolean placed = board.placeBuilding( newB , currentPlayer , false );
        if( placed ) {
            currentPlayer.setPoints( currentPlayer.getPoints( ) + Building.points( ) );
            currentPlayer.setBuildingsLeft( currentPlayer.getBuildingsLeft( ) - 1 );
            checkWinCondition( );
        }
        return placed;
    }


    public boolean upgradeBuilding( HashSet< Coordinate > cords ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        if( currentPlayer.getBuildingUpgradesLeft( ) <= 0 ) {
            throw new IllegalStateException( "No city upgrades left" );
        }
        boolean upgraded = board.upgradeBuilding( cords , currentPlayer );
        if( upgraded ) {
            currentPlayer.setPoints( currentPlayer.getPoints( ) + 1 );
            currentPlayer.setBuildingUpgradesLeft( currentPlayer.getBuildingUpgradesLeft( ) - 1 );
            currentPlayer.setBuildingsLeft( currentPlayer.getBuildingsLeft( ) + 1 );
            checkWinCondition( );
        }
        return upgraded;
    }


    /**
     *
     */
    public void discardCardsAllPlayersRandom( ) {
        if( turnPhase != TurnPhase.DISCARD_PHASE ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }

        //TODO remove later
        for( Player player : players ) {
            if( player.hasCardsAboveLimit( limitMT ) ) {
                player.removeRandomCardsToMatchLimit( limitMT );
            }
        }
        turnPhase = TurnPhase.ROBBER_PLACEMENT;
    }

    /**
     *
     */
    public void discardCardsOnePlayer( String playerNickName , HashMap< CardType, Integer > cardsToDiscard ) {
        if( turnPhase != TurnPhase.DISCARD_PHASE ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        Player player = getPlayer( playerNickName );

        if( player == null ) {
            throw new IllegalArgumentException( "Player " + playerNickName + " does not exist" );
        }

        if( ! player.hasCardsAboveLimit( limitMT ) ) {
            throw new IllegalArgumentException( "Player does not have more than " + limitMT + " cards" );
        }

        int expectedDiscardCount = player.getCardsCount( ) / 2;
        int submittedDiscardCount = cardsToDiscard.values( ).stream( ).mapToInt( Integer::intValue ).sum( );

        if( submittedDiscardCount != expectedDiscardCount ) {
            throw new IllegalArgumentException( "Player must discard exactly " + expectedDiscardCount + " cards" );
        }

        player.removeCards( cardsToDiscard );

        boolean anyPlayerStillNeedsToDiscard = players.stream( ).anyMatch( p -> p.hasCardsAboveLimit( limitMT ) );

        if( ! anyPlayerStillNeedsToDiscard ) {
            turnPhase = TurnPhase.ROBBER_PLACEMENT;
        }
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
            throw new IllegalArgumentException(
                    "Player u want to steal from does not exist: " + stealFromPlayerNickName );
        }
        stealCard( stealFromP );
    }

    /**
     *
     * @param stealFromP
     */
    public void stealCard( Player stealFromP ) {
        if( turnPhase != TurnPhase.STEAL_PHASE ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        if( stealFromP.equals( currentPlayer ) ) {
            throw new IllegalArgumentException( "Cannot steal from yourself" );
        }

        List< Building > buildingsOnRobber = board.getBuildingsAroundHex( robberCord );
        if( ! buildingsOnRobber.isEmpty( ) ) {
            boolean victimHasBuilding = buildingsOnRobber.stream( ).anyMatch(
                    b -> b.getPlayer( ).equals( stealFromP ) );
            if( ! victimHasBuilding ) {
                throw new IllegalArgumentException( "Victim has no building adjacent to robber" );
            }
        }

        if( stealFromP.hasCardsAboveLimit( 0 ) ) {
            CardType stolenCard = stealFromP.getRandomCard( );
            stealFromP.removeCard( stolenCard );
            currentPlayer.addCard( stolenCard );
        } else {
            throw new IllegalArgumentException( "Victim has no cards to steal" );
        }
        turnPhase = TurnPhase.TRADE_AND_BUILD;
    }

    public void bankTrade( CardType give , CardType receive ) {
        if( turnPhase != TurnPhase.TRADE_AND_BUILD ) {
            throw new IllegalStateException( "Wrong turnPhase" );
        }
        if( give == null || receive == null || give == receive ) {
            throw new IllegalArgumentException( "Invalid trade resource types" );
        }
        if( give == CardType.DESERT || give == CardType.OCEAN || receive == CardType.DESERT || receive == CardType.OCEAN ) {
            throw new IllegalArgumentException( "Cannot trade non-resource cards" );
        }
        int tradeRatio = getTradeRatio( give );
        HashMap< CardType, Integer > cost = new HashMap<>( Map.of( give , tradeRatio ) );
        if( ! currentPlayer.canAfford( cost ) ) {
            throw new IllegalArgumentException( "Not enough cards to trade with bank" );
        }
        currentPlayer.removeCards( cost );
        currentPlayer.addCard( receive );
    }

    public int getTradeRatio( Player player , CardType cardType ) {
        int bestRatio = 4;
        if( board.getPorts( ) == null || board.getPorts( ).isEmpty( ) ) {
            return bestRatio;
        }
        for( Map.Entry< HashSet< Coordinate >, Port > entry : board.getPorts( ).entrySet( ) ) {
            HashSet< Coordinate > vertex = entry.getKey( );
            Port port = entry.getValue( );
            Building building = board.findBuildingAt( vertex , player );
            if( building != null ) {
                if( port.resource( ) != null && port.resource( ).equals( cardType ) ) {
                    return 2;
                }
                if( port.resource( ) == null ) {
                    bestRatio = Math.min( bestRatio , 3 );
                }
            }
        }
        return bestRatio;
    }

    public int getTradeRatio( CardType cardType ) {
        return getTradeRatio( currentPlayer , cardType );
    }

    private void checkWinCondition( ) {
        if( currentPlayer.getPoints( ) >= winningPointsCondition ) {
            turnPhase = TurnPhase.GAME_OVER;
        }
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