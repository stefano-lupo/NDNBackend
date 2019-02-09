//package com.stefanolupo.ndngame.libgdx;
//
//import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.Vector3;
//import com.badlogic.gdx.physics.box2d.*;
//
//public class MyModel {
//
//    public boolean isSwimming = false;
//
//    private final InputController controller;
//    private final OrthographicCamera camera;
//    private final GameAssetManager assetManager;
//
//    private final World world;
//    private Body dynamicBody;
//    private Body staticBody;
//    private Body kinematicBody;
//
//    public final Body localPlayer;
//    private final Body water;
//    private final Body land;
//    private final Sound ping;
//    private final Sound boing;
//
//    public MyModel(InputController controller,
//                   OrthographicCamera camera,
//                   GameAssetManager assetManager) {
//        this.controller = controller;
//        this.camera = camera;
//        this.assetManager = assetManager;
//
//        world = new World(new Vector2(0, -10f), true);
//        world.setContactListener(new MyContactListener());
////        createFloor();
//
//        BodyFactory bodyFactory = BodyFactory.getInstance(world);
//        localPlayer = bodyFactory.makeBoxPolyBody(1, 1, 1, 1, BodyFactory.RUBBER, BodyDef.BodyType.DynamicBody, false);
//        land = bodyFactory.makeBoxPolyBody(-10, -12, 20, 4, BodyFactory.RUBBER, BodyDef.BodyType.StaticBody, false);
//        water = bodyFactory.makeBoxPolyBody(10, -12, 20, 4, BodyFactory.RUBBER, BodyDef.BodyType.StaticBody, false);
//        water.setUserData("IAMTHESEA");
//
//        // Apparently makes it not obstruct the player?
//        // yeah otherwise it just bounces
//        bodyFactory.makeAllFixturesSensors(water);
//
//        assetManager.queueAddSounds();
//        assetManager.assetManager.finishLoading();
//        ping = assetManager.assetManager.get("sounds/ping.wav");
//        boing = assetManager.assetManager.get("sounds/boing.wav");
//    }
//
//    public void logicStep(float delta) {
//        if (controller.isMouse1Down) {
//            if (pointIntersectsBody(localPlayer, controller.mouseLocation)) {
//                System.out.println("player was clicked");
//            }
//        }
//
//        if(controller.left){
//            localPlayer.applyForceToCenter(-10, 0,true);
//        }else if(controller.right){
//            localPlayer.applyForceToCenter(10, 0,true);
//        }else if(controller.up){
//            localPlayer.applyForceToCenter(0, 10,true);
//        }else if(controller.down){
//            localPlayer.applyForceToCenter(0, -10,true);
//        }
//
//        if (isSwimming) {
//            localPlayer.applyForceToCenter(0, 40, true);
//        }
//        world.step(delta, 3, 3);
//    }
//
//    public boolean pointIntersectsBody(Body body, Vector2 mouseLocation) {
//        Vector3 mousePos = new Vector3(mouseLocation, 0);
//
//        // Need to translate screen coords to world coords
//        camera.unproject(mousePos);
//
//        return body.getFixtureList().first().testPoint(mousePos.x, mousePos.y);
//    }
//
//    public World getWorld() {
//        return world;
//    }
//
//    public void playSound(int sound) {
//        switch(sound) {
//            case 1:
//                boing.play();
//                break;
//            default:
//                ping.play();
//        }
//    }
//}
