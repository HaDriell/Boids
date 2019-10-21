package fr.hadriel;

import fr.hadriel.boid.*;
import fr.hadriel.boid.rules.*;
import fr.hadriel.math.Vec2;
import javafx.scene.input.MouseButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Application extends JFrame {

    private final World world;
    private final Canvas canvas;

    private Application(World world) {
        this.world = world;
        this.canvas = new Canvas();

        //Initialize the UI
        canvas.setSize(world.width, world.height);
        canvas.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1)
                    world.spawnFood(e.getX(), e.getY());

                if (e.getButton() == MouseEvent.BUTTON3)
                    world.setRallyPoint(new Vec2(e.getX(), e.getY()));
            }
        });


        add(canvas);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    public void render() {
        BufferStrategy swapChain = canvas.getBufferStrategy();
        if (swapChain == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) swapChain.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        world.render(g);

        g.dispose();
        swapChain.show();
    }

    public void mainloop() {
        Timer timer = new Timer();

        while (!Thread.interrupted()) {
            float dt = timer.elapsed();
            timer.reset();
            update(dt);
            render();

            int frameTimeMS = (int) (timer.elapsed() * 1000);
            if (frameTimeMS < 16) {
                try {
                    Thread.sleep(16 - frameTimeMS);
                } catch (InterruptedException ignore) {}
            }
        }
    }

    public static void main(String... args) {

        //Default rules
        ArrayList<BoidRule> rules = new ArrayList<>();

        //Flocking rules
        rules.add(new AlignmentRule(50));
        rules.add(new CohesionRule(100));
        rules.add(new FoodRule(100));
        rules.add(new RallyPointRule());

        //Collision avoidance rules
        rules.add(new SeparationRule(30));
        rules.add(new EdgeRule(20));
        rules.add(new WallAvoidanceRule(20));

        World world = new World(800, 450);

        //Spawn a bunch of small walls accross the world
        for (int i = 0; i < 40; i++) {
            float radius = (float) (Math.random() * 5) + 10;
            world.spawnWall(radius);
        }

        Boid last = null;
        for(int i = 0; i < 100; i++)
            last = world.spawnBoid(180, rules);
        last.setDebug(true);

        new Application(world).mainloop();
    }
}