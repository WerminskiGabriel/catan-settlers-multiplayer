package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.*;

@Getter
public class Board {
    private int radius;
    private HashMap< Coordinate, Cell > board;
    private HashMap< Coordinate, Road > roads;
    private HashMap< Coordinate, Building > buildings;

    private ArrayList< Card > resourcePool = new ArrayList<>( List.of(
            Card.Wood , Card.Wood , Card.Wood , Card.Wood ,
            Card.Sheep , Card.Sheep , Card.Sheep , Card.Sheep ,
            Card.Wheat , Card.Wheat , Card.Wheat , Card.Wheat ,
            Card.Brick , Card.Brick , Card.Brick ,
            Card.Ore , Card.Ore , Card.Ore ,
            Card.Desert
    ) );

    public Board() {
        this.radius = 2;

        this.board = generateFullBoard( radius );
    }

    private HashMap generateFullBoard( int radius ) {
        board = new HashMap< Coordinate, Cell >();

        Coordinate start_curr_cord = new Coordinate( 0 , -radius );
        Coordinate finish_cord = new Coordinate( -radius , radius );
        int max_cols = radius * 2 + 1;

        boolean movement_dir_left_flag = true;

        for( int temp = 0 ; temp < max_cols ; temp++ ) {
            int col = start_curr_cord.q();
            int row = start_curr_cord.r();

            Coordinate curr_cord = start_curr_cord;
            for( int i = 0 ; i < max_cols - Math.abs( row ) ; i++ ) {
                board.put( curr_cord , new Cell( Card.Wood ) );
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

        return board;

    }

    public List generateShuffledResourcePool() {
        List rPool = this.resourcePool;

        Collections.shuffle( rPool );

        return rPool;
    }
}
