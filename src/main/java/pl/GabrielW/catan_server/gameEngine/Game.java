package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import pl.GabrielW.catan_server.model.Player;

import java.util.HashMap;
import java.util.List;

@Getter
public class Game {
    private Board board;
    private HashMap< String, Player > players;
    private PlayerFactory playerFactory;
    private int turnNumber;
    private TurnPhase turnPhase;
    private RollingDicesFactory rollingDicesFactory;

    public Game() {
        this.board = new Board();
        this.players = new HashMap<>();
        this.playerFactory = new PlayerFactory();
        this.turnNumber = 0;
        this.rollingDicesFactory = new RollingDicesFactory();
        this.turnPhase = TurnPhase.FIRST_2_TURNS;
    }

    public void addPlayer( String nickName ) {
        if( players.containsKey( nickName ) ) {
            throw new IllegalArgumentException( "This nickName is already in use" );
        }
        Player player = this.playerFactory.CreatePlayer( nickName );
        players.put( nickName , player );
    }

    public void nextTurn( Player currentPlayer) {

        switch( this.turnPhase ) {
            case TurnPhase.FIRST_2_TURNS -> { }
            case TurnPhase.ROLL_PHASE -> {

                this.rollingDicesFactory.roll();
                int diceSum = rollingDicesFactory.getSum();
                List< Coordinate > coordinates = board.tokenToCoordinates( diceSum );
                for( Coordinate coordinate : coordinates ) {
                    CardType cardType = board.coordinateToCardType( coordinate );
                    List< Building > buildings = board.coordinateToBuildings( coordinate );
                    for( Building building : buildings ) {
                        Player player = building.getPlayer();
                        player.addCard( cardType );
                    }
                }

            }
            case TurnPhase.TRADE_AND_BUILD -> { }
            case TurnPhase.DISCARD_PHASE -> { }
            case TurnPhase.ROBBER_PLACEMENT -> { }
            case TurnPhase.STEAL_PHASE -> { }
            case TurnPhase.GAME_OVER -> { }

        }

    }
}