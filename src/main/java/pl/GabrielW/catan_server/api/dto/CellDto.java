package pl.GabrielW.catan_server.api.dto;

import pl.GabrielW.catan_server.gameEngine.CardType;
import pl.GabrielW.catan_server.gameEngine.Cell;
import pl.GabrielW.catan_server.gameEngine.Coordinate;

public record CellDto(
        Coordinate coordinate ,
        CardType resourceType ,
        int diceNumber
) {
    public static CellDto from( Coordinate coord , Cell cell ) {
        return new CellDto( coord , cell.getCellType( ) , cell.getDiceNumber( ) );
    }
}