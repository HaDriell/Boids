package fr.hadriel.boid;

import fr.hadriel.math.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Wall {
    private Vec2 position;
    private float radius;

    public Wall(Vec2 position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public void render(Graphics2D g) {
        AffineTransform matrix = g.getTransform();

        g.translate(position.x, position.y);
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(
                (int) -radius,
                (int) -radius,
                (int) (2*radius),
                (int) (2*radius));

        g.setTransform(matrix);
    }

    public Vec2 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }
}