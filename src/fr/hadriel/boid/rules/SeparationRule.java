package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

import java.util.List;

public class SeparationRule extends BoidRule {

    public SeparationRule(float distance) {
        super(distance);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        List<Boid> visibleBoids = world.getVisibleBoids(subject, distance);
        if (visibleBoids.isEmpty()) return Vec2.ZERO;

        float invDistance = 1 / distance;
        Vec2 steering = Vec2.ZERO;
        for (Boid target : visibleBoids) {
            Vec2 toTarget = target.getPosition().sub(subject.getPosition());
            toTarget = toTarget.scale(invDistance);
            steering = steering.sub(toTarget);
        }

        if (!visibleBoids.isEmpty()) {
            //Apply Reynolds
            steering = steering.normalize().scale(Boid.MAX_SPEED);
            steering = steering.sub(subject.getSpeed());
            steering = steering.limit(Boid.MAX_FORCE * 2);
        }
        return steering;
    }
}
