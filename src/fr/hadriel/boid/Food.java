package fr.hadriel.boid;

import fr.hadriel.math.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Food {
    public static final int RADIUS = 6;

    private Vec2 position;
    private boolean consumed = false;
    private float age;


    public Food(float x, float y) {
        this.position = new Vec2(x, y);
        this.age = 0;
    }

    public void update(float deltaTime) {
        age += deltaTime;

        if (age > 10) {
            consume();
        }
    }

    public void render(Graphics2D g) {
        AffineTransform matrix = g.getTransform();

        float green = (10 - age) / 10f;

        g.setColor(new Color(0, green, 0));
        g.translate(position.x, position.y);
        g.fillOval(-RADIUS, -RADIUS, 2 * RADIUS, 2 * RADIUS);

        g.setTransform(matrix);
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }
}
