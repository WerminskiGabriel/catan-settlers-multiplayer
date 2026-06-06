package pl.GabrielW.catan_server.gameEngine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTests {

    @Test
    void boardRad2Size() {
        Board board = new Board();


        System.out.println( board.getCells() );

        assertEquals(19 + 18, board.getCells().size() , "size of default board is 19");
    }

}
