package com.stefanolupo.ndngame.libgdx.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.stefanolupo.ndngame.libgdx.BodyFactory;
import com.stefanolupo.ndngame.libgdx.MyContactListener;
import com.stefanolupo.ndngame.libgdx.components.*;
import com.stefanolupo.ndngame.libgdx.systems.RenderingSystem;
import com.stefanolupo.ndngame.libgdx.util.Util;

public class LevelFactory {
    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;
    private SimplexNoise sim;
    private SimplexNoise simRough;
    public int currentLevel = 0;
    private TextureRegion floorTex;
    private TextureRegion enemyTex;
    private TextureRegion platformTex;
    private TextureRegion waterTextureRegion;

    public LevelFactory(PooledEngine en, TextureAtlas textureAtlas){
        engine = en;
        world = new World(new Vector2(0,-10f), true);
        world.setContactListener(new MyContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        // create a new SimplexNoise (size,roughness,seed)
        sim = new SimplexNoise(512, 0.85f, 1);
        simRough = new SimplexNoise(512, 0.95f, 1);

        floorTex = Util.makeTextureRegion(40*RenderingSystem.PIXELS_PER_METER, 0.5f*RenderingSystem.PIXELS_PER_METER, "111111FF");
        enemyTex = Util.makeTextureRegion(1*RenderingSystem.PIXELS_PER_METER,1* RenderingSystem.PIXELS_PER_METER, "331111FF");
        platformTex = Util.makeTextureRegion(2*RenderingSystem.PIXELS_PER_METER, 0.1f*RenderingSystem.PIXELS_PER_METER, "221122FF");
        waterTextureRegion = Util.makeTextureRegion(2*RenderingSystem.PIXELS_PER_METER, 0.1f*RenderingSystem.PIXELS_PER_METER, "221122FF");
    }


    /** Creates a pair of platforms per level up to yLevel
     * @param ylevel
     */
    public void generateLevel(int ylevel){
        while(ylevel > currentLevel){
            // get noise      sim.getNoise(xpos,ypos,zpos) 3D noise
            float noise1 = (float)sim.getNoise(1, currentLevel, 0);
            float noise2 = (float)sim.getNoise(1, currentLevel, 100);
            float noise3 = (float)sim.getNoise(1, currentLevel, 200);
            float noise4 = (float)sim.getNoise(1, currentLevel, 300);
            float noise5 = (float)sim.getNoise(1, currentLevel ,1400);	// should spring exist on p1?
            float noise6 = (float)sim.getNoise(1, currentLevel ,2500);	// should spring exists on p2?
            float noise7 = (float)sim.getNoise(1, currentLevel, 2700);	// should enemy exist?
            float noise8 = (float)sim.getNoise(1, currentLevel, 3000);	// platform 1 or 2?
            if(noise1 > 0.2f){
                createPlatform(noise2 * 25 +2 ,currentLevel * 2);
                if(noise5 > 0.5f){
                    // add bouncy platform
                    createBouncyPlatform(noise2 * 25 +2,currentLevel * 2);
                }
                if (noise7 > 0.5f) {
                    createEnemy(noise2 * 25 + 2, currentLevel * 2 + 1);
                }
            }
            if(noise3 > 0.2f){
                createPlatform(noise4 * 25 +2, currentLevel * 2);
                if(noise6 > 0.4f){
                    // add bouncy platform
                    createBouncyPlatform(noise4 * 25 +2,currentLevel * 2);
                }
                if(noise8 > 0.5f){
                    // add an enemy
                    createEnemy(noise4 * 25 +2,currentLevel * 2 + 1);
                }
            }
            currentLevel++;
        }
    }

    public void createPlatform(float x, float y){
        Entity entity = engine.createEntity();
        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(x, y, 1.5f, 0.2f, BodyFactory.STONE, BodyType.StaticBody);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = floorTex;
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        b2dbody.body.setUserData(entity);
        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);
        engine.addEntity(entity);

    }

    public Entity createBouncyPlatform(float x, float y){
        Entity entity = engine.createEntity();
        // create body component
        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(x, y, .5f, 0.5f, BodyFactory.STONE, BodyType.StaticBody);
        //make it a sensor so not to impede movement
        bodyFactory.makeAllFixturesSensors(b2dbody.body);

        // give it a texture..todo get another texture and anim for springy action
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = floorTex;

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SPRING;

        b2dbody.body.setUserData(entity);
        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);
        engine.addEntity(entity);

        return entity;
    }

    public void createFloor(){
        Entity entity = engine.createEntity();
        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(0, 0, 100, 0.2f, BodyFactory.STONE, BodyType.StaticBody);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = floorTex;
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);

        engine.addEntity(entity);
    }

    public Entity createWaterFloor() {
        Entity entity = engine.createEntity();
        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        WaterFloorComponent waterFloor = engine.createComponent(WaterFloorComponent.class);

        type.type = TypeComponent.ENEMY;
        texture.region = waterTextureRegion;
        b2dbody.body = bodyFactory.makeBoxPolyBody(20,-15,40,10, BodyFactory.STONE, BodyType.KinematicBody,true);
        position.getPosition().set(20,-15,0);
        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(type);
        entity.add(waterFloor);

        b2dbody.body.setUserData(entity);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createEnemy(float x, float y) {
        Entity entity = engine.createEntity();
        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        EnemyComponent enemy = engine.createComponent(EnemyComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);

        b2dbody.body = bodyFactory.makeCirclePolyBody(x,y,1, BodyFactory.STONE, BodyType.KinematicBody,true);
        position.getPosition().set(x,y,0);
        texture.region = enemyTex;
        enemy.xPosCenter = x;
        type.type = TypeComponent.ENEMY;
        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(enemy);
        entity.add(type);

        engine.addEntity(entity);

        return entity;
    }

//    public void createPlayer(TextureRegion tex, OrthographicCamera cam){
//
//        Entity entity = engine.createEntity();
//        BodyComponent b2dbody = engine.createComponent(BodyComponent.class);
//        TransformComponent position = engine.createComponent(TransformComponent.class);
//        TextureComponent texture = engine.createComponent(TextureComponent.class);
//        PlayerComponent player = engine.createComponent(PlayerComponent.class);
//        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
//        TypeComponent type = engine.createComponent(TypeComponent.class);
//        StateComponent stateCom = engine.createComponent(StateComponent.class);
//
//
//        player.cam = cam;
//        b2dbody.body = bodyFactory.makeCirclePolyBody(10,1,1, BodyFactory.STONE, BodyType.DynamicBody,true);
//        // set object position (x,y,z) z used to define draw order 0 first drawn
//        position.position.set(10,1,0);
//        texture.region = tex;
//        type.type = TypeComponent.PLAYER;
//        stateCom.set(StateComponent.STATE_NORMAL);
//        b2dbody.body.setUserData(entity);
//
//        entity.add(b2dbody);
//        entity.add(position);
//        entity.add(texture);
//        entity.add(player);
//        entity.add(colComp);
//        entity.add(type);
//        entity.add(stateCom);
//
//        engine.addEntity(entity);
//
//    }
}