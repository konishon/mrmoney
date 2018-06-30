package com.mr.money.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MrMoney extends ApplicationAdapter {

    private final int GAME_RUNNING = 1;
    private final int GAME_LOADING = 0;
    private final int GAME_OVER = -1;
    private final int GAME_TIME_OVER = 2;

    private SpriteBatch batch;
    private Texture background;
    private Texture[] man;
    private Texture coin;
    private Texture bomb;


    private ArrayList<Integer> coinInXs = new ArrayList<Integer>();
    private ArrayList<Integer> coinInYs = new ArrayList<Integer>();
    private ArrayList<Integer> bombsInXs = new ArrayList<Integer>();
    private ArrayList<Integer> bombsInYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();

    private Random random;
    private Rectangle manRectangle;
    private BitmapFont font;

    private int manState = 0;
    private int pauseManRender = 0;
    private float velocity = 0;
    private int manY;
    private int coinCount, bombCount;
    private int score = 0;
    private int currentGameState;

    int GAME_RUNTIME = (int) TimeUnit.SECONDS.toMillis(60);

    int manLastY;
    private Texture confusedMan, risingMan, fallingMan;

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
        bomb = new Texture("bomb.png");

        confusedMan = new Texture("dizzy-1.png");
        risingMan = new Texture("rising.png");
        fallingMan = new Texture("falling.png");


        manY = Gdx.graphics.getHeight() / 2;
        random = new Random();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
    }

    private void showMessage(String msg) {
        int x = 0;
        int y = Gdx.graphics.getHeight() / 2;
        font.draw(batch, msg, x, y);
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setupGameState();
        drawMan();
        updateScoreText();

        batch.end();
    }

    private void setupGameState() {
        switch (currentGameState) {
            case GAME_RUNNING:
                dropManFromSky();
                makeBomb();
                makeCoin();
                setupManJump();
                runSlowMan();
                break;
            case GAME_LOADING:
                showMessage("Tap to play");
                if (Gdx.input.justTouched()) {
                    currentGameState = GAME_RUNNING;
                }
                break;

            case GAME_OVER:
                showMessage("Game Over");
                dropManFromSky();
                if (Gdx.input.justTouched()) {
                    currentGameState = GAME_RUNNING;
                    manY = Gdx.graphics.getHeight() / 2;
                    score = 0;
                    velocity = 0;
                    coinInXs.clear();
                    coinInYs.clear();
                    coinRectangles.clear();
                    coinCount = 0;

                    bombsInXs.clear();
                    bombsInYs.clear();
                    bombRectangles.clear();
                    bombCount = 0;
                }
                break;
        }
    }

    private void setupTimer() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Gdx.app.log("COIN", String.valueOf(GAME_RUNTIME));
                if (GAME_RUNTIME == 0) {
                    //currentGameState = GAME_OVER;
                } else {
                    font.draw(batch, String.valueOf(GAME_RUNTIME), 300, 600);
                    --GAME_RUNTIME;

                }
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    }

    private void updateScoreText() {
        font.draw(batch, String.valueOf(score), 100, 200);
    }

    private void setupCollisionDetection() {
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                score++;
                coinRectangles.remove(i);
                coinInXs.remove(i);
                coinInYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
                currentGameState = GAME_OVER;
                break;
            }
        }
    }

    private void makeBomb() {
        if (bombCount < 400) {
            bombCount++;
        } else {
            bombCount = 0;
            float height = random.nextFloat() * Gdx.graphics.getHeight();
            bombsInYs.add((int) height);
            bombsInXs.add(Gdx.graphics.getWidth());
        }

        drawBomb();
    }

    private void setupManJump() {
        if (Gdx.input.justTouched()) {
            velocity = -10;
        }
    }

    private void dropManFromSky() {
        float gravity = 0.2f;
        velocity = velocity + gravity;
        manY -= velocity;

        if (manY <= 0) {
            manY = 0;
        }

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
        coinRectangles.clear();

        for (int i = 0; i < coinInXs.size(); i++) {
            int x = coinInXs.get(i);
            int y = coinInYs.get(i);
            batch.draw(coin, x, y);
            coinInXs.set(i, coinInXs.get(i) - 4);
            coinRectangles.add(new Rectangle(x, y, coin.getWidth(), coin.getHeight()));
        }
    }


    private void drawBomb() {
        bombRectangles.clear();
        for (int i = 0; i < bombsInXs.size(); i++) {

            int x = bombsInXs.get(i);
            int y = bombsInYs.get(i);
            batch.draw(bomb, x, y);
            bombsInXs.set(i, bombsInXs.get(i) - 8);
            bombRectangles.add(new Rectangle(x, y, bomb.getWidth(), bomb.getHeight()));

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

    private void drawMan() {
        Texture currentMan;
        boolean manRising = manY > manLastY;

        if (currentGameState == GAME_OVER) {
            currentMan = confusedMan;
        } else if (manY > manLastY) {
            currentMan = risingMan;
        } else if (manY != 0 && !manRising) {
            currentMan = fallingMan;
        } else {
            currentMan = man[manState];
        }

        int xWithManOffset = Gdx.graphics.getWidth() / 2 - currentMan.getWidth() / 2;
        batch.draw(currentMan, xWithManOffset, manY);
        manRectangle = new Rectangle(xWithManOffset, manY, currentMan.getWidth(), currentMan.getHeight());
        setupCollisionDetection();
        manLastY = manY;

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
