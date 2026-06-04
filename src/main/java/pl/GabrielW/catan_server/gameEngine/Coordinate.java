package pl.GabrielW.catan_server.gameEngine;

public record Coordinate( int q , int r ) {

    // We use Axial coordiantes more about this system in https://www.redblobgames.com/grids/hexagons/
    public Coordinate move( Direction direction ) {
        return switch( direction ) {
            case NORTH_WEST -> new Coordinate( q , r - 1 );
            case NORTH_EAST -> new Coordinate( q + 1 , r - 1 );
            case EAST -> new Coordinate( q + 1 , r );
            case SOUTH_EAST -> new Coordinate( q , r + 1 );
            case SOUTH_WEST -> new Coordinate( q - 1 , r + 1 );
            case WEST -> new Coordinate( q - 1 , r );
        };
    }


}
