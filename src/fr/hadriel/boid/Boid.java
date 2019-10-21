package fr.hadriel.boid;

import fr.hadriel.math.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Boid {

    public static final float BODY_SIZE = 3;
    public static final float MAX_SPEED = 100;
    public static final float MAX_FORCE = 100;

    private static final int[] ARROW_X  = new int[] {-5, 5, -5, -3};
    private static final int[] ARROW_Y  = new int[] { 5, 0, -5,  0};
    private static final int ARROW_SIZE = 4;

    private boolean debug;
    private Color color = Color.RED;
    private Vec2 position;
    private Vec2 speed;
    private float fov;

    private List<BoidRule> rules;

    public Boid(Vec2 position, Vec2 speed, float fov, List<BoidRule> defaultRules) {
        this.position = position;
        this.speed = speed;
        this.fov = fov;
        this.rules = new ArrayList<>(defaultRules);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void render(World world, Graphics2D g) {
        //Save current transform
        AffineTransform matrix = g.getTransform();

        float rotation = (float) Math.toRadians(speed.angle(Vec2.X));

        //Transform to local
        g.translate(position.x, position.y);
        g.rotate(-rotation);

        //Draw Boid
        g.setColor(Color.RED);
        g.fillPolygon(ARROW_X, ARROW_Y, ARROW_SIZE);

        if (debug) {

            Vec2 left = Vec2.X.rotate(-fov / 2);
            Vec2 right = Vec2.X.rotate(+fov / 2);

            //Draw rules distances
            g.setColor(new Color(1, 1, 1, 0.04f));
            for (BoidRule rule : rules) {
                Vec2 lv = left.scale(rule.getDistance());
                Vec2 rv = right.scale(rule.getDistance());
                g.drawLine(0, 0, (int) lv.x, (int) lv.y);
                g.drawLine(0, 0, (int) rv.x, (int) rv.y);
                g.drawArc(
                        (int) -rule.getDistance(),
                        (int) -rule.getDistance(),
                        (int) (2 * rule.getDistance()),
                        (int) (2 * rule.getDistance()),
                        (int) -fov / 2,
                        (int) fov);
            }

            //Draw Rules Accelerations (without rotation)
            g.rotate(rotation);
            float offset = 0;
            for (BoidRule rule : rules) {
                offset += g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
                //Random color picker
                int hash = rule.getClass().getName().hashCode();
                Color color = new Color(hash, false);
                g.setColor(color);

                Vec2 steering = rule.execute(this, world);
                g.drawLine(0, 0, (int) steering.x, (int) steering.y);

                g.translate(-position.x, -position.y);
                g.drawString(rule.getClass().getSimpleName(), 0, offset);
                g.translate(position.x, position.y);
            }
            g.rotate(-rotation);

            //Draw Speed vector
            Vec2 forward = Vec2.X.scale(speed.len());
            g.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
            g.drawLine(0, 0, (int) forward.x, (int) forward.y);
        }

        //Reload current transform
        g.setTransform(matrix);
    }

    public void update(World world, float stepTime) {
        // Evaluate rules

        //Add all the steering forces
        Vec2 acceleration = Vec2.ZERO;
        for (BoidRule rule : rules) {
            Vec2 steering = rule.execute(this, world);
            acceleration = acceleration.add(steering);
        }


        //Limit speed
        speed = speed.add(acceleration.scale(stepTime));
        speed = speed.limit(MAX_SPEED);
        // Move
        position = position.add(speed.scale(stepTime));

        //Solve collisions with walls

        List<Wall> collidingWalls = world.getNearbyWalls(position, BODY_SIZE);
        if (!collidingWalls.isEmpty()) {
            Vec2 displacement = Vec2.ZERO;
            for (Wall wall : collidingWalls) {
                Vec2 wallNormal = position.sub(wall.getPosition());

                float overlap = Math.abs(wallNormal.len() - (wall.getRadius() + BODY_SIZE));

                wallNormal = wallNormal.normalize().scale(overlap);
                displacement = displacement.add(wallNormal);
            }
            displacement = displacement.scale( 1f / (float) collidingWalls.size());
            position = position.add(displacement);
        }

        //Clamp to world borders
        float dx = position.x;
        if (dx < 0) dx = world.width;
        if (dx > world.width) dx = 0;
        float dy = position.y;
        if (dy < 0) dy = world.height;
        if (dy > world.height) dy = 0;
        position = new Vec2(dx, dy);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vec2 getSpeed() {
        return speed;
    }

    public Vec2 getForwardDirection() {
        return speed.normalize();
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public void setSpeed(Vec2 speed) {
        this.speed = speed;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }
}