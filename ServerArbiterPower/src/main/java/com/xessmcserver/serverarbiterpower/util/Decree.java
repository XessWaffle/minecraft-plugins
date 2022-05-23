package com.xessmcserver.serverarbiterpower.util;

import org.bukkit.entity.Player;

import java.io.Serializable;

public abstract class Decree implements Serializable {
    protected Player enforced;
    protected String name;
    protected double probability;
    protected boolean singleTimeEvent;

    public Decree(Player enforced, String name, double probability, boolean singleTimeEvent){
        this.enforced = enforced;
        this.name = name;
        this.probability = probability;
        this.singleTimeEvent = singleTimeEvent;
    }

    public abstract void enforce();

    public void setEnforced(Player enforced){
        this.enforced = enforced;
    }

    public Player getEnforced(){
        return this.enforced;
    }

    public String getName(){
        return this.name;
    }

    public void setProbability(double probability){
        this.probability = probability;
        if(probability == 0.0f){
            singleTimeEvent = true;
        } else {
            singleTimeEvent = false;
        }

    }

    public double getProbability() {
        return probability;
    }

    public boolean isSingleTimeEvent() {
        return singleTimeEvent;
    }
}
