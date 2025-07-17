import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class SnakeGame extends JFrame {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int CELL_SIZE = 20;
    private final int GRID_WIDTH = WIDTH / CELL_SIZE;
    private final int GRID_HEIGHT = HEIGHT / CELL_SIZE;
    private ArrayList<Point> snakePositions;
    private Point foodPosition;
    private int snakeLength = 1;
    private Point direction;
    private final Point UP = new Point(0, -1);
    private final Point DOWN = new Point(0, 1);
    private final Point LEFT = new Point(-1, 0);
    private final Point RIGHT = new Point(1, 0);
    private int score = 0;
    private int highestScore = 0; // Variable to store the highest score
    private Random random = new Random();
    private JPanel panel;
    private Font scoreFont = new Font("SansSerif", Font.BOLD, 25); // Bold and larger font for score

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSnake(g);
                drawFood(g);
                drawScore(g);
            }
        };
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(panel);

        loadHighestScore(); // Load highest score from file

        initGame();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeys(e);
            }
        });
        Timer timer = new Timer(100, e -> {
            move();
            checkFoodCollision();
            panel.repaint();
        });
        timer.start();
    }

    private void initGame() {
        snakePositions = new ArrayList<>();
        snakePositions.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = randomDirection();
        randomizeFood();
    }

    private void drawSnake(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point p : snakePositions) {
            g.fillRect(p.x, p.y, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(p.x, p.y, CELL_SIZE, CELL_SIZE);
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(foodPosition.x, foodPosition.y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(foodPosition.x, foodPosition.y, CELL_SIZE, CELL_SIZE);
    }

    private void drawScore(Graphics g) {
        g.setFont(scoreFont);
        g.setColor(Color.BLACK);
        g.drawString("SCORE: " + score, 10, 30);
        g.drawString("HIGHEST: " + highestScore, 10, 60); // Display highest score
    }

    private void handleKeys(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                if (!direction.equals(DOWN)) direction = UP;
                break;
            case KeyEvent.VK_DOWN:
                if (!direction.equals(UP)) direction = DOWN;
                break;
            case KeyEvent.VK_LEFT:
                if (!direction.equals(RIGHT)) direction = LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                if (!direction.equals(LEFT)) direction = RIGHT;
                break;
        }
    }

    private void move() {
        Point head = new Point(snakePositions.get(0).x + direction.x * CELL_SIZE,
                               snakePositions.get(0).y + direction.y * CELL_SIZE);

        if (head.x < 0) head.x = WIDTH - CELL_SIZE;
        else if (head.x >= WIDTH) head.x = 0;
        if (head.y < 0) head.y = HEIGHT - CELL_SIZE;
        else if (head.y >= HEIGHT) head.y = 0;

        if (snakePositions.contains(head)) {
            initGame();  // Reset game on collision
            score = 0;
        } else {
            snakePositions.add(0, head);
            if (snakePositions.size() > snakeLength) {
                snakePositions.remove(snakePositions.size() - 1);
            }
        }
    }

    private void checkFoodCollision() {
        if (snakePositions.get(0).equals(foodPosition)) {
            snakeLength++;
            score++;
            if (score > highestScore) {
                highestScore = score; // Update the highest score if the current score is higher
                saveHighestScore(); // Save the new highest score to file
            }
            randomizeFood();
        }
    }

    private void randomizeFood() {
        foodPosition = new Point(random.nextInt(GRID_WIDTH) * CELL_SIZE,
                                 random.nextInt(GRID_HEIGHT) * CELL_SIZE);
    }

    private Point randomDirection() {
        Point[] directions = {UP, DOWN, LEFT, RIGHT};
        return directions[random.nextInt(directions.length)];
    }

    private void loadHighestScore() {
        try {
            File file = new File("highest_score.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                highestScore = Integer.parseInt(reader.readLine());
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHighestScore() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("highest_score.txt"));
            writer.write(Integer.toString(highestScore));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new SnakeGame();
            frame.pack();
            frame.setVisible(true);
        });
    }
}
