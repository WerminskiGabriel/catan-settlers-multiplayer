package pl.GabrielW.catan_server.model;

import lombok.Setter;
import lombok.Getter;
import pl.GabrielW.catan_server.gameEngine.Card;
import pl.GabrielW.catan_server.gameEngine.SpecialCard;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    private String nickName;
    private int points;
    private int longestRoadLength;
    private int armyLength;
    private List< Card > cards;
    private List< SpecialCard > specialCards;
    private int buildingsLeft;
    private int buildingUpgradesLeft;
    private int roadsLeft;


    public Player() { }
    public Player( String nickName ) {
        this.nickName = nickName;
        this.points = 0;
        this.longestRoadLength = 0;
        this.armyLength = 0;
        this.cards = new ArrayList<>();
        this.specialCards = new ArrayList<>();
        this.buildingsLeft = 5;
        this.buildingUpgradesLeft = 3;
        this.roadsLeft = 10;
    }





}
