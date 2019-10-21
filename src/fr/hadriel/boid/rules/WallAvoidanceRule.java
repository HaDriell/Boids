package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.Wall;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

import java.util.List;

public class WallAvoidanceRule extends BoidRule {

    public WallAvoidanceRule(float distance) {
        super(distance);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        List<Wall> nearbyWalls = world.getNearbyWalls(subject.getPosition(), distance);
        if (nearbyWalls.isEmpty()) return Vec2.ZERO;

        Vec2 steering = Vec2.ZERO;
        for (Wall wall : nearbyWalls) {
            Vec2 wallNormal = subject.getPosition().sub(wall.getPosition());
            wallNormal = wallNormal.scale(1f / distance);
            steering = steering.add(wallNormal);
        }

        //Apply Reynolds
        steering = steering.normalize().scale(Boid.MAX_SPEED);
        steering = steering.sub(subject.getSpeed());
        steering = steering.limit(Boid.MAX_FORCE * 2);
        return steering;
    }
}
