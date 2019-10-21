package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

public class EdgeRule extends BoidRule {

    public EdgeRule(float distance) {
        super(distance);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        Vec2 steering = Vec2.ZERO;

        float left   = subject.getPosition().x;
        float right  = world.width - subject.getPosition().x;
        float top    = subject.getPosition().y;
        float bottom = world.height - subject.getPosition().y;

        if (left <= distance)   steering = steering.add(distance - left, 0);
        if (right <= distance)  steering = steering.sub(distance - right, 0);
        if (top <= distance)    steering = steering.add(0, distance - top);
        if (bottom <= distance) steering = steering.sub(0, distance - bottom);

        if (steering != Vec2.ZERO) {
            //Apply Reynolds
            steering = steering.normalize().scale(Boid.MAX_SPEED);
            steering = steering.sub(subject.getSpeed());
            steering = steering.limit(Boid.MAX_FORCE * 4);
        }

        return steering;
    }
}
