package pl.GabrielW.catan_server.gameEngine;

public record Port( PortType type , CardType resource , int ratio ) {
    public Port( PortType type ) {
        this( type , switch( type ) {
            case WOOD_2 -> CardType.WOOD;
            case BRICK_2_ -> CardType.BRICK;
            case SHEEP_2 -> CardType.SHEEP;
            case WHEAT_2 -> CardType.WHEAT;
            case ORE_2 -> CardType.ORE;
            case GENERIC_3 -> null;
        } , type == PortType.GENERIC_3 ? 3 : 2 );
    }
}