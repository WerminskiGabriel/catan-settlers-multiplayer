package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import pl.GabrielW.catan_server.model.Player;

import java.util.*;

@Getter
public class Board {
    private final int radius;
    private HashMap< Coordinate, Cell > cells;
    private HashMap< Coordinate, List< Road > > roads;
    private HashMap< Coordinate, List< Building > > buildings;
    private HashMap< Integer, List< Coordinate > > tokens;
    private final ArrayList< CardType > resourcePool;
    private final ArrayList< Integer > numbersPool;


    public Board() {
        this.radius = 2;

        this.roads = new HashMap<>();
        this.buildings = new HashMap<>();
        this.tokens = new HashMap<>();

        this.numbersPool = new ArrayList<>( List.of(
                2 ,
                3 , 3 ,
                4 , 4 ,
                5 , 5 ,
                6 , 6 ,
                8 , 8 ,
                9 , 9 ,
                10 , 10 ,
                11 , 11 ,
                12
        ) );
        this.resourcePool = new ArrayList<>( List.of(
                CardType.WOOD , CardType.WOOD , CardType.WOOD , CardType.WOOD ,
                CardType.SHEEP , CardType.SHEEP , CardType.SHEEP , CardType.SHEEP ,
                CardType.WHEAT , CardType.WHEAT , CardType.WHEAT , CardType.WHEAT ,
                CardType.BRICK , CardType.BRICK , CardType.BRICK ,
                CardType.ORE , CardType.ORE , CardType.ORE ,
                CardType.DESERT
        ) );
        this.cells = generateFullBoard( radius );

    }

    private HashMap generateFullBoard( int realRadius ) {

        int radius = realRadius + 1;

        Collections.shuffle( this.resourcePool );
        int resourcesIdx = 0;

        Collections.shuffle( this.numbersPool );
        int numbersIdx = 0;

        cells = new HashMap< Coordinate, Cell >();

        Coordinate start_curr_cord = new Coordinate( 0 , -radius );
        Coordinate finish_cord = new Coordinate( -radius , radius );
        int max_cols = radius * 2 + 1;

        boolean movement_dir_left_flag = true;

        for( int temp = 0 ; temp < max_cols ; temp++ ) {
            int col = start_curr_cord.q();
            int row = start_curr_cord.r();

            Coordinate curr_cord = start_curr_cord;
            int max_i = max_cols - Math.abs( row ) - 1;
            for( int i = 0 ; i <= max_i ; i++ ) {

                int curr_col = curr_cord.q();
                int curr_row = curr_cord.r();

                CardType resource = ( Math.abs( curr_row ) == radius || i == 0 || i == max_cols || i == max_i ) ? CardType.OCEAN : this.resourcePool.get( resourcesIdx++ );
                int token = ( resource == CardType.DESERT || resource == CardType.OCEAN ) ? 0 : this.numbersPool.get( numbersIdx++ );

                cells.put( curr_cord , new Cell( resource , token ) );
                if( token != 0 ) {
                    tokens.computeIfAbsent( token , k -> new ArrayList<>() ).add( curr_cord );
                }
                curr_cord = curr_cord.move( Direction.EAST );

            }
            if( row == 0 ) {
                movement_dir_left_flag = false;
            }

            if( movement_dir_left_flag ) {
                start_curr_cord = start_curr_cord.move( Direction.SOUTH_WEST );
            } else {
                start_curr_cord = start_curr_cord.move( Direction.SOUTH_EAST );
            }
        }

        return cells;

    }

    private List generateShuffledResourcePool() {
        List rPool = this.resourcePool;

        Collections.shuffle( rPool );

        return rPool;
    }

    public void placeRoad( HashSet< Coordinate > cords , Player player ) {
        if( !isRoadPlaceValid( cords , player ) ) {
            throw new IllegalArgumentException();
        }
        Road road = new Road( cords , player );
        for( Coordinate coordinate : cords ) {
            roads.computeIfAbsent( coordinate , k -> new ArrayList<>() ).add( road );
        }
    }

    public boolean doRoadConnectToRoadNetwork( HashSet< Coordinate > cords , Player player ) {
        for( Coordinate coordinate : cords ) {
            List< Road > roads = coordinateToRoad( coordinate );
            for( Road road : roads ) {
                if( !road.getPlayer().equals( player ) ) {
                    continue;
                }

                Coordinate unsharedNew = cords.stream()
                        .filter( c -> !c.equals( coordinate ) )
                        .findFirst().orElseThrow();

                Coordinate unsharedOld = road.getCoordinates().stream()
                        .filter( c -> !c.equals( coordinate ) )
                        .findFirst()
                        .orElseThrow();

                if( isNeighbour( unsharedNew , unsharedOld ) ) {

                    HashSet< Coordinate > sharedVertex = new HashSet<>( Set.of( coordinate , unsharedNew , unsharedOld ) );

                    if( !isVertexOccupied( sharedVertex ) ) {
                        return true;
                    }
                }

            }
        }
        return true;
    }

    public boolean isVertexOccupied( HashSet< Coordinate > cords ) {
        for( Coordinate coordinate : cords ) {
            List< Building > buildings = coordinateToBuildings( coordinate );
            for( Building building : buildings ) {
                if( building.getCoordinates().equals( cords ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRoadPlaceValid( HashSet< Coordinate > cords , Player player ) {
        if( !doRoadConnectToRoadNetwork( cords , player ) ) {
            return false;
        }
        return true;
    }

    public void placeBuilding( HashSet< Coordinate > cords , Player player ) {
        Building building = new Building( cords , player );
        for( Coordinate coordinate : cords ) {
            buildings.computeIfAbsent( coordinate , k -> new ArrayList<>() ).add( building );
        }

    }

    public boolean CanAffordRoad( Player player ) {
        HashSet< CardType > requiredTypes = new HashSet<>( Set.of(
                CardType.WOOD ,
                CardType.BRICK
        ) );

        HashMap< CardType, Integer > playerCards = player.getCards();

        for( CardType type : requiredTypes ) {
            if( playerCards.getOrDefault( type , 0 ) <= 0 ) {
                return false;
            }
        }

        return true;
    }

    public boolean CanAffordBuilding( Player player ) {
        HashSet< CardType > requiredTypes = new HashSet<>( Set.of(
                CardType.WOOD ,
                CardType.WHEAT ,
                CardType.SHEEP ,
                CardType.BRICK
        ) );

        HashMap< CardType, Integer > playerCards = player.getCards();

        for( CardType type : requiredTypes ) {
            if( playerCards.getOrDefault( type , 0 ) <= 0 ) {
                return false;
            }
        }

        return true;
    }

    public List< HashSet< Coordinate > > roadsFromBuildingCoordinates( HashSet< Coordinate > cords ) {
        List< Coordinate > buildingCords = new ArrayList<>( cords );

        if( buildingCords.size() != 3 ) {
            throw new IllegalArgumentException( "Building must have 3 coordinates" );
        }

        Coordinate a = buildingCords.get( 0 );
        Coordinate b = buildingCords.get( 1 );
        Coordinate c = buildingCords.get( 2 );

        HashSet< Coordinate > road1 = new HashSet<>( Set.of( a , b ) );
        HashSet< Coordinate > road2 = new HashSet<>( Set.of( a , c ) );
        HashSet< Coordinate > road3 = new HashSet<>( Set.of( b , c ) );

        return List.of( road1 , road2 , road3 );
    }

    public boolean isBuildingPlaceValid( HashSet< Coordinate > cords , Player player ) {

        boolean connectToRoadNetwork = false;
        List< HashSet< Coordinate > > allRoads = roadsFromBuildingCoordinates( cords );
        for( HashSet< Coordinate > roadCords : allRoads ) {
            if( doRoadConnectToRoadNetwork( roadCords , player ) ) {
                connectToRoadNetwork = true;
                break;
            }
        }
        if( !connectToRoadNetwork ) {
            return false;
        }


        for( Coordinate coordinate : cords ) {
            List< Building > buildings = coordinateToBuildings( coordinate );
            for( Building building : buildings ) {
                HashSet< Coordinate > interSection = new HashSet<>( building.getCoordinates() );
                interSection.retainAll( cords );
                if( interSection.size() > 1 ) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNeighbour( Coordinate a , Coordinate b ) {
        int dq = Math.abs( a.q() - b.q() );
        int dr = Math.abs( a.r() - b.r() );
        int ds = Math.abs( ( a.q() + a.r() ) - ( b.q() + b.r() ) );

        return ( ( dq + dr + ds ) / 2 ) == 1;
    }

    public List< Building > coordinateToBuildings( Coordinate coordinate ) {
        return this.buildings.getOrDefault( coordinate , new ArrayList< Building >() );
    }

    public List< Road > coordinateToRoad( Coordinate coordinate ) {
        return this.roads.getOrDefault( coordinate , new ArrayList< Road >() );
    }

    public CardType coordinateToCardType( Coordinate coordinate ) {
        return this.cells.get( coordinate ).getCellType();
    }

    public List< Coordinate > tokenToCoordinates( int token ) {
        return this.tokens.getOrDefault( token , new ArrayList< Coordinate >() );
    }


}
