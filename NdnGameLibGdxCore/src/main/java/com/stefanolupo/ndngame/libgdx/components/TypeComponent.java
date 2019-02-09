package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.libgdx.components.enums.Type;

public class TypeComponent implements Component {

    private Type type = Type.OTHER;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
