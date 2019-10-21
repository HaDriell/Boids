package fr.hadriel.boid;

import fr.hadriel.math.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    public final int width;
    public final int height;

    public final Random random;
    public final List<Boid> boids;
    public final List<Food> foods;
    public final List<Wall> walls;

    private Vec2 rallyPoint;

    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.random = new Random(seed);
        this.boids = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.walls = new ArrayList<>();
    }

    public World(int width, int height) {
        this(width, height, System.currentTimeMillis());
    }

    public void render(Graphics2D g) {
        if (rallyPoint != null) {
            g.setColor(Color.WHITE);
            g.drawRect(
                    (int) rallyPoint.x - 4,
                    (int) rallyPoint.y - 4,
                    8,
                    8);
        }

        for (Wall wall : walls) {
            wall.render(g);
        }

        for (Boid boid : boids) {
            boid.render(this, g);
        }

        for (Food food : foods) {
            food.render(g);
        }
    }

    public void update(float deltaTime) {
        for (Boid boid : boids) {
            boid.update(this, deltaTime);
        }

        for (Food food : foods) {
            food.update(deltaTime);
        }

        //Clean-up consumed food
        foods.removeIf(Food::isConsumed);
    }

    public Wall spawnWall(float radius) {
        Vec2 position = new Vec2(random.nextFloat(), random.nextFloat());
        position = position.scale(width, height);
        Wall wall = new Wall(position, radius);
        walls.add(wall);
        return wall;
    }

    public Food spawnFood(float x, float y) {
        Food food = new Food(x, y);
        foods.add(food);
        return food;
    }

    public Boid spawnBoid(float fov, List<BoidRule> rules) {
        Vec2 position = new Vec2(random.nextFloat(), random.nextFloat());
        position = position.scale(width, height);
        float dx = (random.nextFloat() - 0.5f) * 2;
        float dy = (random.nextFloat() - 0.5f) * 2;
        Vec2 speed = new Vec2(dx, dy).scale(Boid.MAX_SPEED);
        Boid boid = new Boid(position, speed, fov, rules);
        boids.add(boid);
        return boid;
    }

    public void setRallyPoint(Vec2 rallyPoint) {
        this.rallyPoint = rallyPoint;
    }

    public Vec2 getRallyPoint() {
        return rallyPoint;
    }

    public List<Wall> getNearbyWalls(Vec2 position, float distance) {
        List<Wall> nearbyWalls = new ArrayList<>();

        for (Wall wall : walls) {
            Vec2 toWall = wall.getPosition().sub(position);
            float accurateDistance = toWall.len() - (wall.getRadius() + Boid.BODY_SIZE);
            //Filter far walls
            if (accurateDistance > distance)
                continue;

            nearbyWalls.add(wall);
        }

        return nearbyWalls;
    }

    private static boolean isVisibleFrom(Boid target, Boid subject, float distance) {
        return isVisibleFrom(target.getPosition(), subject.getPosition(), subject.getForwardDirection(), subject.getFov(), distance);
    }

    private static boolean isVisibleFrom(Food target, Boid subject, float distance) {
        return isVisibleFrom(target.getPosition(), subject.getPosition(), subject.getForwardDirection(), subject.getFov(), distance);
    }

    private static boolean isVisibleFrom(Vec2 target, Vec2 position, Vec2 direction, float fieldOfView, float distance) {
        Vec2 toTarget = target.sub(position);

        //Too far to be visible
        if (toTarget.len2() > distance * distance)
            return false;

        toTarget = toTarget.normalize();

        //Too high angle from forward to be visible
        float cosLimit = (float) Math.cos(Math.toRadians(fieldOfView / 2f));
        if (toTarget.dot(direction) < cosLimit)
            return false;

        return true;
    }

    public List<Boid> getVisibleBoids(Boid subject, float distance) {
        List<Boid> list = new ArrayList<>();
        for (Boid target : boids) {
            //Ignore self
            if (target == subject)
                continue;

            //Test visibility
            if (!isVisibleFrom(target, subject, distance))
                continue;

            list.add(target);
        }

        return list;
    }

    public List<Food> getVisibleFoods(Boid subject, float distance) {
        List<Food> list = new ArrayList<>();
        for (Food target : foods) {

            //Test visibility
            if (!isVisibleFrom(target, subject, distance))
                continue;

            list.add(target);
        }

        return list;
    }
}