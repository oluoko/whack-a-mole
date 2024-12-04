
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class WhackAMole {

    int boardWidth = 670;
    int boardHeight = 750;

    JFrame frame = new JFrame("Brian's: Whack-A-Mole");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel controlPanel = new JPanel();

    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;

    JButton currMoleTile;
    JButton currPlantTile;
    JButton currPlantTile2;

    JButton resetButton = new JButton("Reset");
    JButton pauseButton = new JButton("Pause");
    JButton playButton = new JButton("Play");

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;
    Timer inactivityTimer;

    int score = 0;
    int highScore = 0;
    boolean isPaused = false;

    WhackAMole() {

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top panel for score display
        textLabel.setFont(new Font("Arial", Font.PLAIN, 27));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: 0 | High Score: 0");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        // Center panel for the game board
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.black);
        frame.add(boardPanel, BorderLayout.CENTER);

        // Bottom panel for control buttons
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(resetButton);
        controlPanel.add(pauseButton);
        controlPanel.add(playButton);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Load images
        Image plantImg = new ImageIcon(getClass().getResource("./images/piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(167, 167, Image.SCALE_SMOOTH));

        Image moleImg = new ImageIcon(getClass().getResource("./images/monty.png")).getImage();
        moleIcon = new ImageIcon(moleImg.getScaledInstance(167, 167, Image.SCALE_SMOOTH));

        // Initialize game tiles
        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton tile = (JButton) e.getSource();
                    if (tile == currMoleTile) {
                        score += 5;
                        updateScore();
                        resetInactivityTimer();
                    } else if (tile == currPlantTile) {
                        score -= 3;
                        updateScore();
                        resetInactivityTimer();
                        if (score <= 0) {
                            endGame("Game Over! Score reached zero.");
                        }
                    }
                }
            });
        }

        // Timers for mole and plant appearance
        setMoleTimer = new Timer(700, e -> {
            if (currMoleTile != null) {
                currMoleTile.setIcon(null);
                currMoleTile = null;
            }

            int num = random.nextInt(9);
            JButton tile = board[num];

            if (currPlantTile == tile) {
                return;
            }

            currMoleTile = tile;
            currMoleTile.setIcon(moleIcon);
        });

        setPlantTimer = new Timer(900, e -> {
            // Clear the current plants
            if (currPlantTile != null) {
                currPlantTile.setIcon(null);
                currPlantTile = null;
            }
            if (currPlantTile2 != null) {
                currPlantTile2.setIcon(null);
                currPlantTile2 = null;
            }

            // Select unique tiles for the two plants
            int num1 = random.nextInt(9);
            int num2;

            do {
                num2 = random.nextInt(9);
            } while (num2 == num1); // Ensure the two tiles are different

            JButton tile1 = board[num1];
            JButton tile2 = board[num2];

            // Prevent overlapping with the mole
            if (tile1 == currMoleTile) {
                tile1 = null;
            }
            if (tile2 == currMoleTile || tile2 == tile1) {
                tile2 = null;
            }

            // Assign the plant icons to the tiles
            if (tile1 != null) {
                currPlantTile = tile1;
                currPlantTile.setIcon(plantIcon);
            }
            if (tile2 != null) {
                currPlantTile2 = tile2;
                currPlantTile2.setIcon(plantIcon);
            }
        });

        // Inactivity timer to detect timeout
        inactivityTimer = new Timer(15000, e -> endGame("Game Over! Timed out due to inactivity."));
        inactivityTimer.setRepeats(false);

        // Reset button functionality
        resetButton.addActionListener(e -> resetGame());

        // Pause button functionality
        pauseButton.addActionListener(e -> {
            if (!isPaused) {
                pauseGame();
            }
        });

        // Play button functionality
        playButton.addActionListener(e -> {
            if (isPaused) {
                resumeGame();
            }
        });

        setMoleTimer.start();
        setPlantTimer.start();
        resetInactivityTimer();
        frame.setVisible(true);
    }

    // Update score and check for new high score
    private void updateScore() {
        textLabel.setText("Score: " + score + " | High Score: " + highScore);
        if (score > highScore) {
            highScore = score;
            textLabel.setText("Score: " + score + " | High Score: " + highScore);
        }
    }

    // Reset inactivity timer
    private void resetInactivityTimer() {
        inactivityTimer.restart();
    }

    // End the game
    private void endGame(String message) {
        textLabel.setText(message + " | High Score: " + highScore);
        setMoleTimer.stop();
        setPlantTimer.stop();
        inactivityTimer.stop();
        for (JButton tile : board) {
            tile.setEnabled(false);
        }
    }

    // Reset game state
    private void resetGame() {
        score = 0;
        currMoleTile = null;
        currPlantTile = null;

        for (JButton tile : board) {
            tile.setEnabled(true);
            tile.setIcon(null);
        }

        textLabel.setText("Score: 0 | High Score: " + highScore);
        setMoleTimer.restart();
        setPlantTimer.restart();
        resetInactivityTimer();
        isPaused = false;
    }

    // Pause the game
    private void pauseGame() {
        setMoleTimer.stop();
        setPlantTimer.stop();
        inactivityTimer.stop();
        isPaused = true;
    }

    // Resume the game
    private void resumeGame() {
        setMoleTimer.start();
        setPlantTimer.start();
        resetInactivityTimer();
        isPaused = false;
    }

    // Main method
    public static void main(String[] args) {
        new WhackAMole();
    }
}
