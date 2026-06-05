package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import pl.GabrielW.catan_server.model.Player;

import java.util.*;

@Getter
public class Board {
    private int radius;
    private HashMap< Coordinate, Cell > cells;
    private HashMap< Coordinate, List< Road > > roads;
    private HashMap< Coordinate, List< Building > > buildings;
    private HashMap< Integer, List< Coordinate > > tokens;

    private ArrayList< CardType > resourcePool = new ArrayList<>( List.of(
            CardType.Wood , CardType.Wood , CardType.Wood , CardType.Wood ,
            CardType.Sheep , CardType.Sheep , CardType.Sheep , CardType.Sheep ,
            CardType.Wheat , CardType.Wheat , CardType.Wheat , CardType.Wheat ,
            CardType.Brick , CardType.Brick , CardType.Brick ,
            CardType.Ore , CardType.Ore , CardType.Ore ,
            CardType.Desert
    ) );
    private final ArrayList< Integer > numbersPool = new ArrayList<>( List.of(
            2 ,
            3 , 3 ,
            4 , 4 ,
            5 , 5 ,
            6 , 6 ,
            8 , 8 ,
            9 , 9 ,
            10 , 10 ,
            11 , 11 ,
            12
    ) );


    public Board() {
        this.radius = 2;
        this.cells = generateFullBoard( radius );
        this.roads =new HashMap<>();
        this.buildings =new HashMap<>();
        this.tokens =new HashMap<>();
    }

    private HashMap generateFullBoard( int radius ) {

        Collections.shuffle( this.resourcePool );
        int resourcesIdx = 0;

        Collections.shuffle( this.numbersPool );
        int numbersIdx = 0;

        cells = new HashMap< Coordinate, Cell >();

        Coordinate start_curr_cord = new Coordinate( 0 , -radius );
        Coordinate finish_cord = new Coordinate( -radius , radius );
        int max_cols = radius * 2 + 1;

        boolean movement_dir_left_flag = true;

        for( int temp = 0 ; temp < max_cols ; temp++ ) {
            int col = start_curr_cord.q();
            int row = start_curr_cord.r();

            Coordinate curr_cord = start_curr_cord;
            for( int i = 0 ; i < max_cols - Math.abs( row ) ; i++ ) {

                CardType resource = this.resourcePool.get( resourcesIdx++ );
                int token = resource == CardType.Desert ? 0 : this.numbersPool.get( numbersIdx++ );

                cells.put( curr_cord , new Cell( resource , token ) );
                tokens.get( token ).add( curr_cord );
                curr_cord = curr_cord.move( Direction.EAST );

            }
            if( row == 0 ) {
                movement_dir_left_flag = false;
            }

            if( movement_dir_left_flag ) {
                start_curr_cord = start_curr_cord.move( Direction.SOUTH_WEST );
            } else {
                start_curr_cord = start_curr_cord.move( Direction.SOUTH_EAST );
            }
        }

        return cells;

    }

    private List generateShuffledResourcePool() {
        List rPool = this.resourcePool;

        Collections.shuffle( rPool );

        return rPool;
    }

    public List< Coordinate > tokenToCoordinates( int token ) {
        return this.tokens.get( token );
    }

    public void placeRoad( HashSet< Coordinate > cords , Player player ) {
        if( cords.size() == 0 || cords.size() > 2 ) {
            throw new IllegalArgumentException( "Road has 1-2 Coordinates" );
        }
        Road road = new Road( cords , player );
        for( Coordinate coordinate : cords ) {
            roads.get( coordinate ).add( road );
        }

    }

    public void placeBuilding( HashSet< Coordinate > cords , Player player ) {
        if( cords.size() == 0 || cords.size() > 3 ) {
            throw new IllegalArgumentException( "Building has 1-3 Coordinates" );
        }
        Building building = new Building( cords , player );
        for( Coordinate coordinate : cords ) {
            buildings.get( coordinate ).add( building );
        }

    }

    public List< Building > coordinateToBuildings( Coordinate coordinate ) {
        return this.buildings.get( coordinate );
    }

    public CardType coordinateToCardType( Coordinate coordinate){
        return this.cells.get( coordinate ).getCellType();
    }


}
