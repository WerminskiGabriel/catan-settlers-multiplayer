package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Cell {
    private CardType cellType;
    private int diceNumber;

    public Cell() { }

    public Cell( CardType celltype , int diceNumber ) {
        this.cellType = celltype;
        this.diceNumber = diceNumber;
    }
}
