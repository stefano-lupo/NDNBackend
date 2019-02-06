package com.stefanolupo.ndngame.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;


public class MainScreen implements Screen {

    private final NdnGame ndnGame;

    private final OrthographicCamera orthographicCamera;
    private final Box2DDebugRenderer debugRenderer;
    private final KeyboardController controller;
    private final MyModel myModel;
    private final Texture playerTexture;
    private final SpriteBatch spriteBatch;

   public MainScreen(NdnGame ndnGame) {
        this.ndnGame = ndnGame;
        orthographicCamera = new OrthographicCamera(32, 32);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        controller = new KeyboardController();
        myModel = new MyModel(controller, orthographicCamera, ndnGame.myAssetManager);
        ndnGame.myAssetManager.queAddImages();
        ndnGame.myAssetManager.assetManager.finishLoading();
        playerTexture = ndnGame.myAssetManager.assetManager.get("img/player.png");
        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        myModel.logicStep(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(myModel.getWorld(), orthographicCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(playerTexture, myModel.localPlayer.getPosition().x - 1, myModel.localPlayer.getPosition().y - 1, 2, 2);
        spriteBatch.end();
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
