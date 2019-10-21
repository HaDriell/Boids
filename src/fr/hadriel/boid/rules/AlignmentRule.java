package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

import java.util.List;

public class AlignmentRule extends BoidRule {

    public AlignmentRule(float distance) {
        super(distance);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        List<Boid> visibleBoids = world.getVisibleBoids(subject, distance);
        if (visibleBoids.isEmpty()) return Vec2.ZERO;

        Vec2 steering = Vec2.ZERO;
        for (Boid target : visibleBoids) {
            steering = steering.add(target.getSpeed());
        }

        if (!visibleBoids.isEmpty()) {
            //Apply Reynolds
//            steering = steering.normalize().scale(Boid.MAX_SPEED);
            steering = steering.sub(subject.getSpeed());
            steering = steering.limit(Boid.MAX_FORCE / 2);
        }
        return steering;
    }
}
