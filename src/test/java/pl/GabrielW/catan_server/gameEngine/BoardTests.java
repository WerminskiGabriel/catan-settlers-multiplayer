package pl.GabrielW.catan_server.gameEngine;

import org.junit.jupiter.api.Test;
import pl.GabrielW.catan_server.model.Player;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTests {

    @Test
    void boardRad2Size() {
        Board board = new Board();


        System.out.println( board.getCells() );

        assertEquals( 37 , board.getCells().size() , "size of default board is 37" );
    }

    @Test
    void ResourcesCountByTokens() {
        Board board = new Board();
        int tokensCount = 0;
        for( List< Coordinate > token : board.getTokens().values() ) {
            tokensCount += token.size();
        }
        assertEquals( 18 , tokensCount , "Count of tokens having tiles - non Ocean/desert tiles" );
    }

    @Test
    void ResourcesCountByCells() {
        Board board = new Board();
        int tokensCount = board.getCells().size();
        assertEquals( 18 + 19 , tokensCount , "Count of every cell" );

        int tokensNotDC = 0;
        int DesertCnt = 0;
        int OceanCnt = 0;
        for( Cell cell : board.getCells().values() ) {
            if( cell.getCellType() != CardType.DESERT && cell.getCellType() != CardType.OCEAN ) {
                tokensNotDC++;
            } else {
                if( cell.getCellType() == CardType.DESERT ) {
                    DesertCnt++;
                } else {
                    OceanCnt++;
                }
            }
        }
        assertEquals( 18 , tokensNotDC , "Count of tiles having - non Ocean/desert tiles" );
        assertEquals( 1 , DesertCnt , "Count of desert tiles" );
        assertEquals( 18 , OceanCnt , "Count of  Ocean tiles" );

    }

    @Test
    void everyTokenCount() {
        Board board = new Board();
        HashMap< Integer, Integer > correctTokensCount = new HashMap<>( Map.of(
                2 , 1 ,
                3 , 2 ,
                4 , 2 ,
                5 , 2 ,
                6 , 2 ,
                8 , 2 ,
                9 , 2 ,
                10 , 2 ,
                11 , 2 ,
                12 , 1
        ) );

        int tokensCount = 0;
        board.getTokens().forEach( ( k , v ) -> {
            assertTrue( v.size() == correctTokensCount.get( k ) , "Count of token(" + k + ") should be " + correctTokensCount.get( k ) + "." );
        } );
    }

    @Test
    void placeRoad() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();

        Coordinate middleCell = new Coordinate( 0 , 0 );

        Player playerA = pFactory.CreatePlayer( "Appa" );
        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > roadFromBuilding = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        playerA.addCards( Building.cost() );
        assertTrue( board.canAffordBuilding( playerA ) );

        assertTrue( board.placeBuilding( buildingA , playerA , true ) );

        playerA.addCards( Road.cost() );
        assertTrue( board.canAffordRoad( playerA ) );
        assertTrue( board.isRoadPlaceValid( roadFromBuilding , playerA ) );
        assertTrue( board.placeRoad( roadFromBuilding , playerA ) , "road from settlement should place" );
        assertEquals( 1 , board.coordinateToRoads( middleCell ).size() , "should be just 1 road" );


    }

    @Test
    void resourceDistribution() {
        Board board = new Board();
        HashMap< CardType, Integer > counts = new HashMap<>();
        for( Cell cell : board.getCells().values() ) {
            CardType type = cell.getCellType();
            counts.put( type , counts.getOrDefault( type , 0 ) + 1 );
        }
        assertEquals( 4 , counts.get( CardType.WOOD ) );
        assertEquals( 4 , counts.get( CardType.SHEEP ) );
        assertEquals( 4 , counts.get( CardType.WHEAT ) );
        assertEquals( 3 , counts.get( CardType.BRICK ) );
        assertEquals( 3 , counts.get( CardType.ORE ) );
    }


    @Test
    void desertOceanHaveNoProductionToken() {
        Board board = new Board();
        board.getCells().forEach( ( cord , cell ) -> {
            CardType type = cell.getCellType();
            if( type == CardType.DESERT || type == CardType.OCEAN ) {
                board.getTokens().forEach( ( tokenNum , cordsList ) -> {
                    assertFalse( cordsList.contains( cord ) );
                } );
            }
        } );
    }

    @Test
    void oceanIsOnEdge() {
        Board board = new Board();
        board.getCells().forEach( ( cord , cell ) -> {
            CardType type = cell.getCellType();
            if( type == CardType.OCEAN ) {
                int distance = Math.max( Math.abs( cord.q() ) , Math.max( Math.abs( cord.r() ) , Math.abs( cord.q() + cord.r() ) ) );
                assertTrue( distance >= 2 );
            }
        } );
    }

    @Test
    void placeBuilding() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Coordinate middleCell = new Coordinate( 0 , 0 );
        Player playerA = pFactory.CreatePlayer( "Appa" );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        playerA.addCards( Building.cost() );
        assertTrue( board.canAffordBuilding( playerA ) );
        board.placeBuilding( buildingA , playerA , true );

        assertEquals( true , board.isVertexOccupied( buildingA ) , "this Vertex should be occupied - true" );

        for( Coordinate c : buildingA ) {
            boolean found = false;
            for( Building b : board.coordinateToBuildings( c ) ) {
                if( b.getCoordinates().equals( buildingA ) ) {
                    found = true;
                }
            }
            assertTrue( found );
        }
    }

    @Test
    void emptyHexReturnsEmptyListNotNull() {
        Board board = new Board();
        Coordinate invalidCord = new Coordinate( 99 , 99 );

        assertNotNull( board.coordinateToBuildings( invalidCord ) );
    }


    @Test
    void roadConstructorValidation() {
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > validRoad = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );
        HashSet< Coordinate > invalidRoad1 = new HashSet<>( List.of( middleCell ) );
        HashSet< Coordinate > invalidRoad3 = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.WEST ) ) );

        assertDoesNotThrow( () -> new Road( validRoad , playerA ) );
        assertThrows( IllegalArgumentException.class , () -> new Road( invalidRoad1 , playerA ) );
        assertThrows( IllegalArgumentException.class , () -> new Road( invalidRoad3 , playerA ) );
        assertThrows( NullPointerException.class , () -> new Road( null , playerA ) );
        assertThrows( NullPointerException.class , () -> new Road( validRoad , null ) );
    }

    @Test
    void buildingConstructorValidation() {
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > validBuilding = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > invalidBuilding2 = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        assertDoesNotThrow( () -> new Building( validBuilding , playerA ) );
        assertThrows( IllegalArgumentException.class , () -> new Building( invalidBuilding2 , playerA ) );
        assertThrows( NullPointerException.class , () -> new Building( null , playerA ) );
        assertThrows( NullPointerException.class , () -> new Building( validBuilding , null ) );
    }

    @Test
    void isNeighborGeometry() {
        Coordinate middleCell = new Coordinate( 0 , 0 );
        Coordinate eastNeighbor = middleCell.move( Direction.EAST );
        Coordinate farCell = middleCell.move( Direction.EAST ).move( Direction.EAST );

        assertTrue( Board.isNeighbour( middleCell , eastNeighbor ) );
        assertFalse( Board.isNeighbour( middleCell , farCell ) );
        assertFalse( Board.isNeighbour( middleCell , middleCell ) );
    }

    @Test
    void roadsFromBuildingCoordinatesValidation() {
        Board board = new Board();
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingCords = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        List< HashSet< Coordinate > > roads = board.roadsFromBuildingCoordinates( buildingCords );

        assertEquals( 3 , roads.size() );
        for( HashSet< Coordinate > road : roads ) {
            assertEquals( 2 , road.size() );
        }

        HashSet< Coordinate > invalidCoords = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );
        assertThrows( IllegalArgumentException.class , () -> board.roadsFromBuildingCoordinates( invalidCoords ) );
    }

    @Test
    void isBuildingPlaceValidDistanceRule() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Player playerB = pFactory.CreatePlayer( "Momo" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > vertex1 = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );

        assertTrue( board.isBuildingPlaceValid( vertex1 , playerA ) );
        playerA.addCards( Building.cost() );
        assertTrue( board.canAffordBuilding( playerA ) );
        board.placeBuilding( vertex1 , playerA , true );

        assertFalse( board.isBuildingPlaceValid( vertex1 , playerB ) );

        HashSet< Coordinate > adjacentVertexTooClose = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.NORTH_EAST ) ) );
        assertFalse( board.isBuildingPlaceValid( adjacentVertexTooClose , playerB ) );

        HashSet< Coordinate > safeVertex = new HashSet<>( List.of( middleCell.move( Direction.EAST ) , middleCell.move( Direction.EAST ).move( Direction.EAST ) , middleCell.move( Direction.EAST ).move( Direction.SOUTH_EAST ) ) );
        assertTrue( board.isBuildingPlaceValid( safeVertex , playerB ) );
    }

    @Test
    void doRoadConnectToRoadNetwork() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Player playerB = pFactory.CreatePlayer( "Momo" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > building = new HashSet<>( Set.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > road1 = new HashSet<>( Set.of( middleCell , middleCell.move( Direction.EAST ) ) );
        HashSet< Coordinate > road2Connected = new HashSet<>( Set.of( middleCell , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > road3Disconnected = new HashSet<>( Set.of( middleCell.move( Direction.WEST ) , middleCell.move( Direction.SOUTH_WEST ) ) );

        playerA.addCards( Building.cost() );
        assertTrue( board.canAffordBuilding( playerA ) );
        assertTrue( board.isBuildingPlaceValid( building , playerA ) );
        assertTrue( board.placeBuilding( building , playerA , true ) );

        assertTrue( board.isRoadPlaceValid( road1 , playerA ) );

        playerA.addCards( Road.cost() );
        assertTrue( board.canAffordRoad( playerA ) );
        board.placeRoad( road1 , playerA );

        assertTrue( board.isRoadPlaceValid( road2Connected , playerA ) );
        assertFalse( board.isRoadPlaceValid( road3Disconnected , playerA ) );
        assertFalse( board.isRoadPlaceValid( road1 , playerB ) );
    }

    @Test
    void setupBuildingWithoutRoad() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );

        playerA.addCards( Building.cost() );
        assertTrue( board.placeBuilding( buildingA , playerA , true ) , "setup building should work without road" );
        assertTrue( board.isVertexOccupied( buildingA ) , "vertex should be occupied after setup place" );
    }

    @Test
    void mainBuildingWithoutRoadFails() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );

        playerA.addCards( Building.cost() );
        assertFalse( board.placeBuilding( buildingA , playerA , false ) , "main building without road should fail" );
        assertFalse( board.isVertexOccupied( buildingA ) , "vertex should stay empty" );
    }

    @Test
    void doRoadConnectToBuildingAfterSetup() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > roadFromBuilding = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        playerA.addCards( Building.cost() );
        board.placeBuilding( buildingA , playerA , true );

        assertTrue( board.doRoadConnectToBuilding( roadFromBuilding , playerA ) , "road touching own building should connect" );
    }

    @Test
    void firstRoadFromSettlementValid() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > roadFromBuilding = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        playerA.addCards( Building.cost() );
        board.placeBuilding( buildingA , playerA , true );

        playerA.addCards( Road.cost() );
        assertTrue( board.isRoadPlaceValid( roadFromBuilding , playerA ) , "first road from own settlement should be valid" );
        assertTrue( board.placeRoad( roadFromBuilding , playerA ) , "first road from own settlement should place" );
    }

    @Test
    void placeRoadWithoutResourcesFails() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > roadFromBuilding = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        playerA.addCards( Building.cost() );
        assertTrue( board.placeBuilding( buildingA , playerA , true ) );

        System.out.println( playerA.getCards() );
        assertFalse( board.canAffordRoad( playerA ) );
        assertFalse( board.placeRoad( roadFromBuilding , playerA ) , "road without resources should fail" );
        assertEquals( 0 , board.coordinateToRoads( middleCell ).size() , "no road should be on board" );
    }

    @Test
    void doBuldingConnectToRoadAfterPlaceRoad() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > buildingA = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) , middleCell.move( Direction.SOUTH_EAST ) ) );
        HashSet< Coordinate > roadFromBuilding = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        playerA.addCards( Building.cost() );
        board.placeBuilding( buildingA , playerA , true );

        playerA.addCards( Road.cost() );
        board.placeRoad( roadFromBuilding , playerA );

        assertTrue( board.doBuldingConnectToRoad( buildingA , playerA ) , "building should connect to own road" );
    }

    @Test
    void doBuldingConnectToRoadWrongSizeThrows() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > wrongSize = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        assertThrows( IllegalArgumentException.class , () -> board.doBuldingConnectToRoad( wrongSize , playerA ) );
    }

    @Test
    void roadInMiddleWithoutBuildingInvalid() {
        Board board = new Board();
        PlayerFactory pFactory = new PlayerFactory();
        Player playerA = pFactory.CreatePlayer( "Appa" );
        Coordinate middleCell = new Coordinate( 0 , 0 );

        HashSet< Coordinate > lonelyRoad = new HashSet<>( List.of( middleCell , middleCell.move( Direction.EAST ) ) );

        playerA.addCards( Road.cost() );
        assertFalse( board.isRoadPlaceValid( lonelyRoad , playerA ) , "road without building or network should be invalid" );
    }

    @Test
    void tokenSevenReturnsEmptyList() {
        Board board = new Board();

        assertNotNull( board.tokenToCoordinates( 7 ) );
        assertEquals( 0 , board.tokenToCoordinates( 7 ).size() , "token 7 should have no production tiles" );
    }

}
