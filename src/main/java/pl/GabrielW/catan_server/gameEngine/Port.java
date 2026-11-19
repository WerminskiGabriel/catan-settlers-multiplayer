package pl.GabrielW.catan_server.gameEngine;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Port {
    private final PortType type;
    private final CardType resource;
    private final int ratio;

    public Port( PortType type ) {
        this.type = type;
        if( type == PortType.GENERIC_3 ) {
            this.resource = null;
            this.ratio = 3;
        } else {
            this.ratio = 2;
            switch( type ) {
                case WOOD_2 -> this.resource = CardType.WOOD;
                case BRICK_2_ -> this.resource = CardType.BRICK;
                case SHEEP_2 -> this.resource = CardType.SHEEP;
                case WHEAT_2 -> this.resource = CardType.WHEAT;
                case ORE_2 -> this.resource = CardType.ORE;
                default -> this.resource = null;
            }
        }
    }

    public Port( PortType type , CardType resource , int ratio ) {
        this.type = type;
        this.resource = resource;
        this.ratio = ratio;
    }
}
