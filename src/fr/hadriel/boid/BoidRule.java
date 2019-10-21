package fr.hadriel.boid;

import fr.hadriel.math.Vec2;

public abstract class BoidRule {

    private float distance;

    protected BoidRule(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Vec2 execute(Boid subject, World world) {
        return execute(subject, world, distance);
    }

    abstract protected Vec2 execute(Boid subject, World world, float distance);
}