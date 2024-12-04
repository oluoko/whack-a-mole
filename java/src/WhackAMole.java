
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

    JButton resetButton = new JButton("Reset");
    JButton pauseButton = new JButton("Pause");
    JButton playButton = new JButton("Play");

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;
    int score = 0;

    boolean isPaused = false;

    WhackAMole() {

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top panel for score display
        textLabel.setFont(new Font("Arial", Font.PLAIN, 27));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: 0");
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
                        textLabel.setText("Score: " + score);
                    } else if (tile == currPlantTile) {
                        score -= 3;
                        textLabel.setText("Score: " + score);
                        if (score <= 0) {
                            score = 0;
                            textLabel.setText("--- !!! GAME OVER !!! ---");
                            setMoleTimer.stop();
                            setPlantTimer.stop();
                            for (JButton b : board) {
                                b.setEnabled(false);
                            }
                        }
                    }
                }
            });
        }

        // Timers for mole and plant appearance
        setMoleTimer = new Timer(700, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        setPlantTimer = new Timer(900, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currPlantTile != null) {
                    currPlantTile.setIcon(null);
                    currPlantTile = null;
                }

                int num = random.nextInt(9);
                JButton tile = board[num];

                if (currMoleTile == tile) {
                    return;
                }

                currPlantTile = tile;
                currPlantTile.setIcon(plantIcon);
            }
        });

        // Reset button functionality
        resetButton.addActionListener(e -> resetGame());

        // Pause button functionality
        pauseButton.addActionListener(e -> {
            if (!isPaused) {
                setMoleTimer.stop();
                setPlantTimer.stop();
                isPaused = true;
            }
        });

        // Play button functionality
        playButton.addActionListener(e -> {
            if (isPaused) {
                setMoleTimer.start();
                setPlantTimer.start();
                isPaused = false;
            }
        });

        setMoleTimer.start();
        setPlantTimer.start();
        frame.setVisible(true);
    }

    // Reset game state
    private void resetGame() {
        score = 0;
        textLabel.setText("Score: 0");
        currMoleTile = null;
        currPlantTile = null;

        for (JButton tile : board) {
            tile.setEnabled(true);
            tile.setIcon(null);
        }

        setMoleTimer.restart();
        setPlantTimer.restart();
        isPaused = false;
    }

    // Main method
    public static void main(String[] args) {
        new WhackAMole();
    }
}
