package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component {
    public static final int PLAYER = 0;
    public static final int ENEMY = 1;
    public static final int SCENERY = 3;
    public static final int SPRING = 4;
    public static final int OTHER = 5;

    public int type = OTHER;
}
