package pl.GabrielW.catan_server.gameEngine;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.random.RandomGenerator;

@Getter
public class RollingDices {
    // 2 rolling Dices

    private int dice1;
    private int dice2;
    private int sum;
    private Random rnd;

    public RollingDices() {
    }

    public void roll() {

        this.dice1 = rnd.nextInt( 0 , 6 );
        this.dice2 = rnd.nextInt( 0 , 6 );
        this.sum = dice1 + dice2;
    }
}
