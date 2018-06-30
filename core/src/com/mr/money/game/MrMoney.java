package com.mr.money.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Random;

public class MrMoney extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture[] man;
    private Texture coin;

    private ArrayList<Integer> coinInXs = new ArrayList<Integer>();
    private ArrayList<Integer> coinInYs = new ArrayList<Integer>();
    private Random random;

    private int manState = 0;
    private int pauseManRender = 0;
    private float velocity = 0;
    private int manY;
    private int coinCount;


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        coin = new Texture("coin.png");

        manY = Gdx.graphics.getHeight() / 2;
        random = new Random();
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        makeCoin();

        int manY = dropManFromSky();
        drawMan(manY);
        runSlowMan();

        setupManJump();

        batch.end();
    }

    private void setupManJump() {
        if (Gdx.input.justTouched()) {
            velocity = -10;
        }
    }

    private int dropManFromSky() {
        float gravity = 0.2f;
        velocity = velocity + gravity;
        manY -= velocity;

        if (manY <= 0) {
            manY = 0;
        }

        return manY;

    }

    private void makeCoin() {
        if (coinCount < 100) {
            coinCount++;
        } else {
            coinCount = 0;
            float height = random.nextFloat() * Gdx.graphics.getHeight();
            coinInYs.add((int) height);
            coinInXs.add(Gdx.graphics.getWidth());
        }

        drawCoin();
    }

    private void drawCoin() {
        for (int i = 0; i < coinInXs.size(); i++) {
            batch.draw(coin, coinInXs.get(i), coinInYs.get(i));
            coinInXs.set(i, coinInXs.get(i) - 4);
        }
    }

    private void makeManRun() {
        if (manState < 3) {
            manState++;
        } else {
            manState = 0;
        }
    }


    private void runSlowMan() {
        if (pauseManRender < 8) {
            pauseManRender++;
        } else {
            pauseManRender = 0;
            makeManRun();
        }
    }

    private void drawMan(int y) {
        Texture currentMan = man[manState];
        int xWithManOffset = Gdx.graphics.getWidth() / 2 - currentMan.getWidth() / 2;
        batch.draw(currentMan, xWithManOffset, y);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
