import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;

        public Block(Image image, int x, int y, int width, int height) {
            this.image  = image;
            this.x      = x;
            this.y      = y;
            this.width  = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char dir) {
            this.direction = dir;
            updateVelocity();
        }

        void updateVelocity() {
            switch (direction) {
                case 'U': velocityX = 0;             velocityY = -tileSize / 4; break;
                case 'D': velocityX = 0;             velocityY =  tileSize / 4; break;
                case 'L': velocityX = -tileSize / 4; velocityY = 0;             break;
                case 'R': velocityX =  tileSize / 4; velocityY = 0;             break;
            }
        }

        void reset() {
            x = startX;
            y = startY;
        }
    }

    private final int rowCount    = 21;
    private final int columnCount = 19;
    private final int tileSize    = 32;
    private final int boardWidth  = columnCount * tileSize;
    private final int boardHeight = rowCount    * tileSize;

    private Image wallImage;
    private Image blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;


    private final String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls  = new HashSet<>();
    HashSet<Block> foods  = new HashSet<>();
    HashSet<Block> ghosts = new HashSet<>();
    Block pacman;

    final char[] ghostDirections = {'U', 'D', 'L', 'R'};
    final Random random = new Random();

    Timer gameLoop;

    int velocityX = 0, velocityY = 0;

    int score    = 0;
    int lives    = 3;
    boolean gameOver = false;

    
    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage        = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage   = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage   = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage    = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage    = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage  = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage  = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();

        for (Block ghost : ghosts) {
            ghost.updateDirection(ghostDirections[random.nextInt(4)]);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }


    public void loadMap() {
        walls  = new HashSet<>();
        foods  = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tile = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tile) {
                    case 'X': walls.add(new Block(wallImage,        x, y, tileSize, tileSize)); break;
                    case 'b': ghosts.add(new Block(blueGhostImage,   x, y, tileSize, tileSize)); break;
                    case 'o': ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize)); break;
                    case 'p': ghosts.add(new Block(pinkGhostImage,   x, y, tileSize, tileSize)); break;
                    case 'r': ghosts.add(new Block(redGhostImage,    x, y, tileSize, tileSize)); break;
                    case 'P': pacman = new Block(pacmanRightImage,   x, y, tileSize, tileSize); break;
                    case ' ': foods.add(new Block(null, x + 14, y + 14, 4, 4));                 break;
                }
            }
        }
    }

    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
    
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

    
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

    
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Courier New", Font.BOLD, 16));
        if (gameOver) {
            String msg = foods.isEmpty()
                ? "YOU WIN!  Score: " + score
                : "GAME OVER  Score: " + score;
            g.drawString(msg, tileSize / 2, tileSize - 8);
            g.setFont(new Font("Courier New", Font.PLAIN, 13));
            g.setColor(Color.WHITE);
            g.drawString("Press any key to restart", tileSize / 2, tileSize + 8);
        } else {
            g.drawString("Lives: " + lives + "   Score: " + score, tileSize / 2, tileSize - 8);
        }
    }


    public void move() {
    
        pacman.x += velocityX;
        pacman.y += velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= velocityX;
                pacman.y -= velocityY;
                break;
            }
        }

    
        if (pacman.x + pacman.width < 0) pacman.x = boardWidth;
        if (pacman.x > boardWidth)        pacman.x = -pacman.width;
        if (pacman.y + pacman.height < 0) pacman.y = boardHeight;
        if (pacman.y > boardHeight)       pacman.y = -pacman.height;

        
        Iterator<Block> foodIt = foods.iterator();
        while (foodIt.hasNext()) {
            Block food = foodIt.next();
            if (collision(pacman, food)) {
                foodIt.remove();
                score += 10;
            }
        }

    
        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            boolean hitWall = false;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    hitWall = true;
                    break;
                }
            }

            if (hitWall || random.nextInt(20) == 0) {
                ghost.updateDirection(ghostDirections[random.nextInt(4)]);
            }

        
            if (ghost.x + ghost.width < 0) ghost.x = boardWidth;
            if (ghost.x > boardWidth)       ghost.x = -ghost.width;
            if (ghost.y + ghost.height < 0) ghost.y = boardHeight;
            if (ghost.y > boardHeight)      ghost.y = -ghost.height;

            if (collision(pacman, ghost)) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                    gameLoop.stop();
                } else {
                    resetPositions();
                }
                return;
            }
        }

    
        if (foods.isEmpty()) {
            score += 500;
            gameOver = true;
            gameLoop.stop();
        }
    }

    
    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width  &&
            a.x + a.width  > b.x &&
            a.y < b.y + b.height &&
            a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.image = pacmanRightImage;
        velocityX = 0;
        velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(ghostDirections[random.nextInt(4)]);
        }
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) move();
        repaint();
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            score    = 0;
            lives    = 3;
            gameOver = false;
            loadMap();
            for (Block ghost : ghosts) {
                ghost.updateDirection(ghostDirections[random.nextInt(4)]);
            }
            velocityX = 0;
            velocityY = 0;
            gameLoop.start();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                velocityX = 0; velocityY = -tileSize / 4;
                pacman.image = pacmanUpImage;    break;
            case KeyEvent.VK_DOWN:
                velocityX = 0; velocityY = tileSize / 4;
                pacman.image = pacmanDownImage;  break;
            case KeyEvent.VK_LEFT:
                velocityX = -tileSize / 4; velocityY = 0;
                pacman.image = pacmanLeftImage;  break;
            case KeyEvent.VK_RIGHT:
                velocityX = tileSize / 4; velocityY = 0;
                pacman.image = pacmanRightImage; break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
}