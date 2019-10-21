package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

public class RallyPointRule extends BoidRule {


    public RallyPointRule() {
        super(0);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        Vec2 rallyPoint = world.getRallyPoint();
        if (rallyPoint == null)
            return Vec2.ZERO;

        Vec2 steering = rallyPoint.sub(subject.getPosition());

        //Consume Rallypoint
        if (steering.len2() < Boid.BODY_SIZE)
            world.setRallyPoint(null);

        //Apply Reynolds
        steering = steering.normalize().scale(Boid.MAX_SPEED);
        steering = steering.sub(subject.getSpeed());
        steering = steering.limit(Boid.MAX_FORCE);
        return steering;
    }
}
