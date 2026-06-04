package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.GabrielW.catan_server.model.Player;

@Getter
@Setter
@ToString
public class Cell {
    private Card cellType;
    private int diceNumber;

    public Cell() { }

    public Cell( Card celltype ) {
        this.cellType = celltype;
    }
}
