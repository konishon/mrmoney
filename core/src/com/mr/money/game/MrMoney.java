package com.mr.money.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MrMoney extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture[] man;
    private int manState = 0;
    private int pauseManRender = 0;
    private float gravity = 0.2f;
    private float velocity = 0;
    private int manY;


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;

    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        int manY = dropManFromSky();
        drawMan(manY);
        runSlowMan();
        batch.end();
    }

    private int dropManFromSky() {
        velocity = velocity + gravity;
        manY -= velocity;

        if (manY <= 0) {
            manY = 0;
        }

        return manY;

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
