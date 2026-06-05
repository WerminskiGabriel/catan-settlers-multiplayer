package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;

import java.util.Random;

@Getter
public class RollingDicesFactory {
    // 2 rolling Dices

    private int dice1;
    private int dice2;
    private int sum;
    private final Random rnd;

    public RollingDicesFactory() {
        this.rnd = new Random();
    }

    public void roll() {

        this.dice1 = rnd.nextInt( 1 , 6 );
        this.dice2 = rnd.nextInt( 1 , 6 );
        this.sum = dice1 + dice2;
    }
}
