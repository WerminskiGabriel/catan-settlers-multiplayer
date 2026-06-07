package pl.GabrielW.catan_server.gameEngine;

import org.junit.jupiter.api.Test;
import pl.GabrielW.catan_server.model.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTests {

    @Test
    void boardRad2Size() {
        Board board = new Board();


        System.out.println( board.getCells() );

        assertEquals( 37 , board.getCells().size() , "size of default board is 37" );
    }

    @Test
    void placeRoad() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();

        Coordinate middleCell = new Coordinate( 0 , 0 );

        Player playerA = pFactory.CreatePlayer( "Appa" );
        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        board.placeBuilding( buildingA , playerA );
        assertEquals( true , board.isVertexOccupied( buildingA ) , "this Vertex shoould be occupied - true" );


    }

    /*
    @Test
    void RoadConnectToRoadNetwork() {

    }


    isVertexOccupied
            isRoadPlaceValid
    placeBuilding( HashSet< Coordinate > cords , Player player ) {
    public List< HashSet< Coordinate > > roadsFromBuildingCoordinates( HashSet< Coordinate > cords ) {
         public boolean isBuildingPlaceValid( HashSet< Coordinate > cords , Player player ) {
        n isNeighbor

     */


}
