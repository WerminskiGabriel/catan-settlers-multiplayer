package pl.GabrielW.catan_server.api.dto.response;

import pl.GabrielW.catan_server.gameEngine.Board;

import java.util.List;

public record BoardDto(
        int radius ,
        List< CellDto > cells ,
        List< RoadDto > roads ,
        List< BuildingDto > buildings
) {
    public static BoardDto from( Board board ) {
        return new BoardDto(
                board.getRadius( ) ,
                board.getCells( ).entrySet( ).stream( ).map( entry -> CellDto.from( entry.getKey( ) , entry.getValue( ) ) ).toList( ) ,
                board.getRoads( ).values( ).stream( ).flatMap( List::stream ).distinct( ).map( r -> RoadDto.from( r ) ).toList( ) ,
                board.getBuildings( ).values( ).stream( ).flatMap( List::stream ).distinct( ).map( b -> BuildingDto.from( b ) ).toList( )
        );
    }
}
