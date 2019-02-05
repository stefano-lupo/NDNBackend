package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class MainScreen implements Screen {

    private LibGdxGame libGdxGame;
    private OrthographicCamera orthographicCamera;
    private Box2DDebugRenderer debugRenderer;
    private Model model;

    public MainScreen(LibGdxGame libGdxGame) {
        this.libGdxGame = libGdxGame;
        orthographicCamera = new OrthographicCamera(32, 32);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        model = new Model();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        model.logicStep(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(model.getWorld(), orthographicCamera.combined);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }
}
