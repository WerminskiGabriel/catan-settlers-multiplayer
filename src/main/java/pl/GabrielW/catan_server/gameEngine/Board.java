package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import pl.GabrielW.catan_server.model.Player;

import java.util.*;

@Getter
public class Board {
    private final int radius;
    private final HashMap< Coordinate, List< Road > > roads;
    private final HashMap< Coordinate, List< Building > > buildings;
    private final HashMap< Integer, List< Coordinate > > tokens;
    private final HashMap< HashSet< Coordinate >, Port > ports;
    private final ArrayList< CardType > resourcePool;
    private final ArrayList< Integer > numbersPool;
    private HashMap< Coordinate, Cell > cells;
    private Coordinate desertCord;

    public Board( ) {
        this.radius = 2;

        this.roads = new HashMap<>( );
        this.buildings = new HashMap<>( );
        this.tokens = new HashMap<>( );
        this.ports = new HashMap<>( );

        this.numbersPool = new ArrayList<>(
                List.of( 2 , 3 , 3 , 4 , 4 , 5 , 5 , 6 , 6 , 8 , 8 , 9 , 9 , 10 , 10 , 11 , 11 , 12 ) );
        this.resourcePool = new ArrayList<>(
                List.of( CardType.WOOD , CardType.WOOD , CardType.WOOD , CardType.WOOD , CardType.SHEEP ,
                        CardType.SHEEP , CardType.SHEEP , CardType.SHEEP , CardType.WHEAT , CardType.WHEAT ,
                        CardType.WHEAT , CardType.WHEAT , CardType.BRICK , CardType.BRICK , CardType.BRICK ,
                        CardType.ORE , CardType.ORE , CardType.ORE , CardType.DESERT ) );
        this.cells = generateFullBoard( radius );
        initPorts( );

    }

    public static boolean isNeighbour( Coordinate a , Coordinate b ) {
        int dq = Math.abs( a.q( ) - b.q( ) );
        int dr = Math.abs( a.r( ) - b.r( ) );
        int ds = Math.abs( ( a.q( ) + a.r( ) ) - ( b.q( ) + b.r( ) ) );

        return ( ( dq + dr + ds ) / 2 ) == 1;
    }

    private void initPorts( ) { //TODO algorithmic way of setting ports
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( - 2 , 0 ) , new Coordinate( - 3 , 0 ) , new Coordinate( - 3 , 1 ) ) ) ,
                new HashSet<>( Set.of( new Coordinate( - 2 , 0 ) , new Coordinate( - 2 , - 1 ) ,
                        new Coordinate( - 3 , 0 ) ) ) , new Port( PortType.GENERIC_3 ) );
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( - 1 , - 1 ) , new Coordinate( - 1 , - 2 ) , new Coordinate( - 2 , - 1 ) ) ) ,
                new HashSet<>( Set.of( new Coordinate( - 1 , - 1 ) , new Coordinate( 0 , - 2 ) ,
                        new Coordinate( - 1 , - 2 ) ) ) , new Port( PortType.WHEAT_2 ) );
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( 1 , - 3 ) , new Coordinate( 0 , - 2 ) , new Coordinate( 1 , - 2 ) ) ) ,
                new HashSet<>(
                        Set.of( new Coordinate( 2 , - 3 ) , new Coordinate( 1 , - 3 ) , new Coordinate( 1 , - 2 ) ) ) ,
                new Port( PortType.ORE_2 ) );
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( 2 , - 3 ) , new Coordinate( 2 , - 2 ) , new Coordinate( 3 , - 3 ) ) ) ,
                new HashSet<>(
                        Set.of( new Coordinate( 2 , - 2 ) , new Coordinate( 3 , - 3 ) , new Coordinate( 3 , - 2 ) ) ) ,
                new Port( PortType.GENERIC_3 ) );
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( 2 , - 1 ) , new Coordinate( 3 , - 2 ) , new Coordinate( 3 , - 1 ) ) ) ,
                new HashSet<>(
                        Set.of( new Coordinate( 2 , - 1 ) , new Coordinate( 2 , 0 ) , new Coordinate( 3 , - 1 ) ) ) ,
                new Port( PortType.SHEEP_2 ) );
        addPortPair(
                new HashSet<>( Set.of( new Coordinate( 1 , 1 ) , new Coordinate( 2 , 0 ) , new Coordinate( 2 , 1 ) ) ) ,
                new HashSet<>( Set.of( new Coordinate( 1 , 1 ) , new Coordinate( 1 , 2 ) , new Coordinate( 2 , 1 ) ) ) ,
                new Port( PortType.GENERIC_3 ) );
        addPortPair(
                new HashSet<>( Set.of( new Coordinate( 1 , 2 ) , new Coordinate( 0 , 2 ) , new Coordinate( 0 , 3 ) ) ) ,
                new HashSet<>(
                        Set.of( new Coordinate( 0 , 2 ) , new Coordinate( 0 , 3 ) , new Coordinate( - 1 , 3 ) ) ) ,
                new Port( PortType.GENERIC_3 ) );
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( - 1 , 3 ) , new Coordinate( - 1 , 2 ) , new Coordinate( - 2 , 3 ) ) ) ,
                new HashSet<>(
                        Set.of( new Coordinate( - 2 , 2 ) , new Coordinate( - 2 , 3 ) , new Coordinate( - 1 , 2 ) ) ) ,
                new Port( PortType.BRICK_2_ ) );
        addPortPair( new HashSet<>(
                        Set.of( new Coordinate( - 3 , 2 ) , new Coordinate( - 2 , 1 ) , new Coordinate( - 2 , 2 ) ) ) ,
                new HashSet<>(
                        Set.of( new Coordinate( - 3 , 2 ) , new Coordinate( - 2 , 1 ) , new Coordinate( - 3 , 1 ) ) ) ,
                new Port( PortType.WOOD_2 ) );
    }

    private void addPortPair( HashSet< Coordinate > v1 , HashSet< Coordinate > v2 , Port port ) {
        ports.put( v1 , port );
        ports.put( v2 , port );
    }

    public void addPort( HashSet< Coordinate > vertex , Port port ) {
        ports.put( vertex , port );
    }

    public Port getPort( HashSet< Coordinate > vertex ) {
        return ports.get( vertex );
    }

    private HashMap generateFullBoard( int realRadius ) {

        Coordinate robberCord;

        int radius = realRadius + 1;

        Collections.shuffle( this.resourcePool );
        int resourcesIdx = 0;

        Collections.shuffle( this.numbersPool );
        int numbersIdx = 0;

        cells = new HashMap< Coordinate, Cell >( );

        Coordinate start_curr_cord = new Coordinate( 0 , - radius );
        Coordinate finish_cord = new Coordinate( - radius , radius );
        int max_cols = radius * 2 + 1;

        boolean movement_dir_left_flag = true;

        for( int temp = 0 ; temp < max_cols ; temp++ ) {
            int col = start_curr_cord.q( );
            int row = start_curr_cord.r( );

            Coordinate curr_cord = start_curr_cord;
            int max_i = max_cols - Math.abs( row ) - 1;
            for( int i = 0 ; i <= max_i ; i++ ) {

                int curr_col = curr_cord.q( );
                int curr_row = curr_cord.r( );

                CardType resource = ( Math.abs(
                        curr_row ) == radius || i == 0 || i == max_cols || i == max_i ) ? CardType.OCEAN : this.resourcePool.get(
                        resourcesIdx++ );
                int token = ( resource == CardType.DESERT || resource == CardType.OCEAN ) ? 0 : this.numbersPool.get(
                        numbersIdx++ );

                cells.put( curr_cord , new Cell( resource , token ) );
                if( resource == CardType.DESERT ) {
                    this.desertCord = curr_cord;
                }
                if( token != 0 ) {
                    tokens.computeIfAbsent( token , k -> new ArrayList<>( ) ).add( curr_cord );
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

    private List generateShuffledResourcePool( ) {
        List rPool = this.resourcePool;

        Collections.shuffle( rPool );

        return rPool;
    }

    public boolean placeRoad( HashSet< Coordinate > cords , Player player ) {
        return placeRoad( cords , player , false );
    }

    public boolean placeRoad( HashSet< Coordinate > cords , Player player , boolean isSetup ) {
        if( ! isRoadPlaceValid( cords , player ) ) {
            return false;
        }

        if( ! isSetup ) {
            if( ! canAffordRoad( player ) ) {
                return false;
            }
            player.removeCards( Road.cost( ) );
        }

        Road road = new Road( cords , player );
        for( Coordinate coordinate : cords ) {
            roads.computeIfAbsent( coordinate , k -> new ArrayList<>( ) ).add( road );
        }

        return true;
    }

    public boolean doRoadConnectToRoadNetwork( HashSet< Coordinate > cords , Player player ) {
        for( Coordinate coordinate : cords ) {
            List< Road > roads = coordinateToRoads( coordinate );
            for( Road road : roads ) {
                if( ! road.getPlayer( ).equals( player ) ) {
                    continue;
                }

                Coordinate unsharedOld = cords.stream( ).filter(
                        c -> ! c.equals( coordinate ) ).findFirst( ).orElseThrow( );

                Coordinate unsharedNew = road.getCoordinates( ).stream( ).filter(
                        c -> ! c.equals( coordinate ) ).findFirst( ).orElseThrow( );

                if( isNeighbour( unsharedNew , unsharedOld ) ) {

                    HashSet< Coordinate > sharedVertex = new HashSet<>(
                            Set.of( coordinate , unsharedNew , unsharedOld ) );

                    if( ! isVertexOccupied( sharedVertex ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean doRoadConnectToBuilding( HashSet< Coordinate > cords , Player player ) {

        Iterator< Coordinate > iterator = cords.iterator( );
        if( ! iterator.hasNext( ) ) {
            return false;
        }

        HashSet< Building > buildingsIntersection = new HashSet<>( coordinateToBuildings( iterator.next( ) ) );
        if( iterator.hasNext( ) ) {
            buildingsIntersection.retainAll( coordinateToBuildings( iterator.next( ) ) );
        }

        if( buildingsIntersection.size( ) > 1 ) {
            throw new IllegalArgumentException( "Buildings can not be adjacent" );
        }
        for( Building building : buildingsIntersection ) {
            if( building != null && building.getPlayer( ).equals( player ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean doBuldingConnectToRoad( HashSet< Coordinate > cords , Player player ) {
        if( cords.size( ) != 3 ) {
            throw new IllegalArgumentException( "Buildings have 3 coordinates" );
        }
        for( Coordinate coordinate : cords ) {
            for( Road road : coordinateToRoads( coordinate ) ) {
                if( ! road.getPlayer( ).equals( player ) ) {
                    continue;
                }
                if( cords.containsAll( road.getCoordinates( ) ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isVertexOccupied( HashSet< Coordinate > cords ) {
        for( Coordinate coordinate : cords ) {
            List< Building > buildings = coordinateToBuildings( coordinate );
            for( Building building : buildings ) {
                if( building.getCoordinates( ).equals( cords ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRoadPlaceValid( HashSet< Coordinate > cords , Player player ) {
        return doRoadConnectToBuilding( cords , player ) || doRoadConnectToRoadNetwork( cords , player );
    }

    public boolean placeBuilding( HashSet< Coordinate > cords , Player player , boolean isSetup ) {

        if( ! isBuildingPlaceValid( cords , player ) ) {
            return false;
        }

        if( ! isSetup ) {
            if( ! canAffordBuilding( player ) ) {
                return false;
            }
            if( ! doBuldingConnectToRoad( cords , player ) ) {
                return false;
            }
            player.removeCards( Building.cost( ) );
        }


        Building building = new Building( cords , player );
        for( Coordinate coordinate : cords ) {
            buildings.computeIfAbsent( coordinate , k -> new ArrayList<>( ) ).add( building );
        }

        return true;
    }

    public Building findBuildingAt( HashSet< Coordinate > cords , Player player ) {
        for( Coordinate coordinate : cords ) {
            List< Building > buildingList = coordinateToBuildings( coordinate );
            for( Building building : buildingList ) {
                if( building.getCoordinates( ).equals( cords ) && building.getPlayer( ).equals( player ) ) {
                    return building;
                }
            }
        }
        return null;
    }

    public boolean upgradeBuilding( HashSet< Coordinate > cords , Player player ) {
        Building building = findBuildingAt( cords , player );
        if( building == null ) {
            throw new IllegalArgumentException( "No building found at this location for player" );
        }
        if( building.isCity( ) ) {
            throw new IllegalStateException( "Building is already a city" );
        }
        if( ! player.CanAffordCards( Building.cityCost( ) ) ) {
            return false;
        }
        player.removeCards( Building.cityCost( ) );
        building.upgrade( );
        return true;
    }

    public List< Building > getBuildingsAroundHex( Coordinate hexCoord ) {
        List< Building > result = new ArrayList<>( );
        List< Building > buildingsAtHex = coordinateToBuildings( hexCoord );
        for( Building building : buildingsAtHex ) {
            if( ! result.contains( building ) ) {
                result.add( building );
            }
        }
        return result;
    }

    public boolean canAffordRoad( Player player ) {
        HashMap< CardType, Integer > requiredTypes = Road.cost( );
        return player.CanAffordCards( requiredTypes );
    }

    public boolean canAffordBuilding( Player player ) {
        HashMap< CardType, Integer > requiredTypes = Building.cost( );
        return player.CanAffordCards( requiredTypes );
    }

    public List< HashSet< Coordinate > > roadsFromBuildingCoordinates( HashSet< Coordinate > cords ) {
        List< Coordinate > buildingCords = new ArrayList<>( cords );

        if( buildingCords.size( ) != 3 ) {
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
        /*
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
        }*/


        for( Coordinate coordinate : cords ) {
            List< Building > buildings = coordinateToBuildings( coordinate );
            for( Building building : buildings ) {
                HashSet< Coordinate > interSection = new HashSet<>( building.getCoordinates( ) );
                interSection.retainAll( cords );
                if( interSection.size( ) > 1 ) {
                    return false;
                }
            }
        }
        return true;
    }

    public List< Building > coordinateToBuildings( Coordinate coordinate ) {
        return this.buildings.getOrDefault( coordinate , new ArrayList< Building >( ) );
    }

    public List< Road > coordinateToRoads( Coordinate coordinate ) {
        return this.roads.getOrDefault( coordinate , new ArrayList< Road >( ) );
    }

    public CardType coordinateToCardTypes( Coordinate coordinate ) {
        return this.cells.get( coordinate ).getCellType( );
    }

    public List< Coordinate > tokenToCoordinates( int token ) {
        return this.tokens.getOrDefault( token , new ArrayList< Coordinate >( ) );
    }


}
