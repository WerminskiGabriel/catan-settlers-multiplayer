package pl.GabrielW.catan_server.gameEngine;

import org.junit.jupiter.api.Test;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTests {

    private Game startGameDefault( ) {
        Game game = new Game( );

        game.addPlayer( "P1" );
        game.addPlayer( "P2" );
        game.addPlayer( "P3" );
        game.startGame( );
        return game;
    }

    @Test
    public void addAndRemovePlayers( ) {
        Game game = new Game( );

        game.addPlayer( "P1" );
        assertEquals( 1 , game.getPlayers( ).size( ) );
        assertEquals( "P1" , game.getPlayers( ).get( 0 ).getNickName( ) );

        game.addPlayer( "P2" );
        assertEquals( 2 , game.getPlayers( ).size( ) );

        game.removePlayer( "P2" );
        assertEquals( 1 , game.getPlayers( ).size( ) );
        assertEquals( "P1" , game.getPlayers( ).get( 0 ).getNickName( ) );
    }

    @Test
    public void startGameWithTooFewPlayersThrows( ) {
        Game game = new Game( );
        game.addPlayer( "P1" );
        assertThrows( IllegalArgumentException.class , game::startGame );
    }

    @Test
    public void startGameWithEnoughPlayers( ) {
        Game game = new Game( );
        game.addPlayer( "P1" );
        game.addPlayer( "P2" );

        game.startGame( );
        assertEquals( TurnPhase.ROLL_PHASE , game.getTurnPhase( ) );
    }

    @Test
    public void startGameSetsRollPhase( ) {
        Game game = startGameDefault( );

        assertEquals( TurnPhase.ROLL_PHASE , game.getTurnPhase( ) );
        assertEquals( 0 , game.getTurnNumber( ) );
    }

    @Test
    public void rollDiceInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.TRADE_AND_BUILD );

        assertThrows( IllegalStateException.class , game::rollDice );
    }

    @Test
    public void rollDiceLeavesRollPhase( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        game.rollDice( );

        assertNotEquals( TurnPhase.ROLL_PHASE , game.getTurnPhase( ) );
        boolean landedInExpectedPhase =
                game.getTurnPhase( ) == TurnPhase.TRADE_AND_BUILD
                        || game.getTurnPhase( ) == TurnPhase.DISCARD_PHASE;
        assertEquals( true , landedInExpectedPhase ,
                "dice_roll -> TRADE_AND_BUILD or DISCARD_PHASE" );
    }

    @Test
    public void buildInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        HashSet< Coordinate > road = new HashSet<>( List.of(
                new Coordinate( 0 , 0 ) , new Coordinate( 1 , 0 ) ) );
        HashSet< Coordinate > building = new HashSet<>( List.of(
                new Coordinate( 0 , 0 ) , new Coordinate( 1 , 0 ) , new Coordinate( 0 , 1 ) ) );

        assertThrows( IllegalStateException.class , ( ) -> game.buildRoad( road ) );
        assertThrows( IllegalStateException.class , ( ) -> game.buildBuilding( building ) );
    }

    @Test
    public void discardInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        assertThrows( IllegalStateException.class , game::discardCards );
    }

    @Test
    public void placeRobberInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        assertThrows( IllegalStateException.class ,
                ( ) -> game.placeRobber( new Coordinate( 0 , 0 ) ) );
    }

    @Test
    public void endTurnInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        assertThrows( IllegalStateException.class , game::endTurn );
    }

    @Test
    public void endTurnRotatesPlayersAndResetsToRollPhase( ) throws Exception {
        Game game = startGameDefault( );
        List< Player > players = game.getPlayers( );

        game.setTurnPhase( TurnPhase.TRADE_AND_BUILD );
        game.startGame( );
        assertSame( players.get( 0 ) , game.getCurrentPlayer( ) );
        assertEquals( 0, game.getTurnNumber( ) );
        assertEquals( TurnPhase.ROLL_PHASE , game.getTurnPhase( ) );

        game.setTurnPhase( TurnPhase.TRADE_AND_BUILD );
        game.endTurn( );
        assertSame( players.get( 1 ) , game.getCurrentPlayer( ) );
        assertEquals( 1 , game.getTurnNumber( ) );

        game.setTurnPhase( TurnPhase.TRADE_AND_BUILD );
        game.endTurn( );
        assertSame( players.get( 2 ) , game.getCurrentPlayer( ) );

        game.setTurnPhase( TurnPhase.TRADE_AND_BUILD );
        game.endTurn( );
        assertSame( players.get( 0 ) , game.getCurrentPlayer( ) ,
                "with 3P the rotation should cycle back to the P1" );
    }

    @Test
    public void placeRobberMovesToStealPhase( ) {
        Game game = startGameDefault( );
        game.setTurnPhase( TurnPhase.ROBBER_PLACEMENT );
        Coordinate robberSpot = new Coordinate( 0 , 0 );

        game.placeRobber( robberSpot );

        assertEquals( robberSpot , game.getRobberCord( ) );
        assertEquals( TurnPhase.STEAL_PHASE , game.getTurnPhase( ) );
    }

    @Test
    public void stealCardMovesCardAndReturnsToTradeAndBuild( ) {
        Game game = startGameDefault( );
        Player thief = game.getPlayers( ).get( 0 );
        Player victim = game.getPlayers( ).get( 1 );
        game.setCurrentPlayer( thief );
        victim.addCard( CardType.WOOD );

        game.setTurnPhase( TurnPhase.STEAL_PHASE );
        game.stealCard( victim );

        assertEquals( 0 , victim.getCardsCount( ) , "victim should have lost a card" );
        assertEquals( 1 , thief.getCardsCount( ) , "current player should have gained the  card" );
        assertEquals( TurnPhase.TRADE_AND_BUILD , game.getTurnPhase( ) );
    }

    @Test
    public void stealCardInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        Player victim = game.getPlayers( ).get( 1 );
        game.setCurrentPlayer( game.getPlayers( ).get( 0 ) );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        assertThrows( IllegalStateException.class , ( ) -> game.stealCard( victim ) );
    }

    @Test
    public void tradeInWrongPhaseThrows( ) {
        Game game = startGameDefault( );
        Player other = game.getPlayers( ).get( 1 );
        game.setCurrentPlayer( game.getPlayers( ).get( 0 ) );
        game.setTurnPhase( TurnPhase.ROLL_PHASE );

        assertThrows( IllegalStateException.class ,
                ( ) -> game.trade( new java.util.HashMap<>( ) , new java.util.HashMap<>( ) , other ) );
    }
}
