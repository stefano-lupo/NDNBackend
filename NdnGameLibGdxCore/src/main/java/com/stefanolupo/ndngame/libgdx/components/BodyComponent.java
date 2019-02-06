package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class BodyComponent implements Component {

    public Body body;

    public Body getBody() {
        return body;
    }
}
