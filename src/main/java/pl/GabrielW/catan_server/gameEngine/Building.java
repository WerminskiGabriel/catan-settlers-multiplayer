package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import lombok.Setter;
import pl.GabrielW.catan_server.model.Player;

@Getter
@Setter
public class Building {
    private Player player;

    public Building(){}

    public Building(Player player){
        this.player = player;
    }

}
