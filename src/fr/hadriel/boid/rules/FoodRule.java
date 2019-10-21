package fr.hadriel.boid.rules;

import fr.hadriel.boid.Boid;
import fr.hadriel.boid.BoidRule;
import fr.hadriel.boid.Food;
import fr.hadriel.boid.World;
import fr.hadriel.math.Vec2;

import java.util.List;

public class FoodRule extends BoidRule {

    public FoodRule(float distance) {
        super(distance);
    }

    protected Vec2 execute(Boid subject, World world, float distance) {
        List<Food> visibleFoods = world.getVisibleFoods(subject, distance);
        if (visibleFoods.isEmpty()) return Vec2.ZERO;

        Vec2 bpos = subject.getPosition();

        Vec2 steering = visibleFoods.get(0).getPosition();
        for (Food target : visibleFoods) {
            Vec2 tpos = target.getPosition();
            if (bpos.distance2(tpos) < bpos.distance2(steering)) {
                steering = tpos;
            }

            //Consume if near
            if (target.getPosition().distance2(subject.getPosition()) < Food.RADIUS * 3) {
                target.consume();
            }
        }

        steering = steering.sub(subject.getPosition());

        if (!visibleFoods.isEmpty()) {
            //Apply Reynolds
//            steering = steering.normalize().scale(Boid.MAX_SPEED);
            steering = steering.sub(subject.getSpeed());
            steering = steering.limit(Boid.MAX_FORCE);
        }
        return steering;
    }
}
