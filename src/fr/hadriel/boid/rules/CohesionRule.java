package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

import java.util.List;

public class CohesionRule extends BoidRule {

    public CohesionRule(float distance) {
        super(distance);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        List<Boid> visibleBoids = world.getVisibleBoids(subject, distance);
        if (visibleBoids.isEmpty()) return Vec2.ZERO;

        Vec2 steering = Vec2.ZERO;
        for (Boid target : visibleBoids) {
            steering = steering.add(target.getPosition());
        }
        steering = steering.scale( 1f / (float) visibleBoids.size());
        steering = steering.sub(subject.getPosition());


        if (!visibleBoids.isEmpty()) {
            //Apply Reynolds
            steering = steering.sub(subject.getSpeed());
            steering = steering.limit(Boid.MAX_FORCE / 2);
        }
        return steering;
    }
}
