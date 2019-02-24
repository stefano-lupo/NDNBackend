package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.protos.Attack;

public class AttackComponent implements Component {
//    private AttackName attackName;
    private Attack attack;

//    public AttackName getAttackName() {
//        return attackName;
//    }

//    public void setAttackName(AttackName attackName) {
//        this.attackName = attackName;
//    }

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }
}
