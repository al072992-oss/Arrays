import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperGUI extends JFrame {

    private Minesweeper game;
    private JButton[][] cellButtons;
    private JLabel minesLabel;
    private JLabel timerLabel;
    private JButton resetButton;
    private Timer timer;

    private int rows = 9;
    private int cols = 9;
    private int mines = 10;
    private int flagsPlaced;

    public MinesweeperGUI() {
        // Set Windows Look and Feel
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.out.println("Windows Look and Feel not found, using default.");
        }

        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New");
        JMenu difficultyMenu = new JMenu("Difficulty");
        JMenuItem easyItem = new JMenuItem("Easy");
        JMenuItem mediumItem = new JMenuItem("Medium");
        JMenuItem hardItem = new JMenuItem("Hard");
        JMenuItem customItem = new JMenuItem("Custom...");

        newGameItem.addActionListener(e -> resetGame());
        easyItem.addActionListener(e -> setDifficulty(9, 9, 10));
        mediumItem.addActionListener(e -> setDifficulty(16, 16, 40));
        hardItem.addActionListener(e -> setDifficulty(16, 30, 99));
        customItem.addActionListener(e -> showCustomDialog());

        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        difficultyMenu.add(easyItem);
        difficultyMenu.add(mediumItem);
        difficultyMenu.add(hardItem);
        difficultyMenu.add(customItem);
        gameMenu.add(difficultyMenu);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLoweredBevelBorder()));

        minesLabel = new JLabel(String.format("%03d", mines));
        minesLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        minesLabel.setForeground(Color.RED);
        minesLabel.setOpaque(true);
        minesLabel.setBackground(Color.BLACK);

        timerLabel = new JLabel("000");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        timerLabel.setForeground(Color.RED);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.BLACK);
        timer = new Timer(1000, e -> updateTimer());

        resetButton = new JButton("🙂");
        resetButton.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetGame());

        topPanel.add(minesLabel, BorderLayout.WEST);
        topPanel.add(resetButton, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Game Panel
        startGame();

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void startGame() {

        if (getContentPane().getComponentCount() > 1) {

            remove(((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER));

        }

        JPanel gamePanel = new JPanel(new GridLayout(rows, cols));

        gamePanel.setBorder(BorderFactory.createLoweredBevelBorder());

        add(gamePanel, BorderLayout.CENTER);

        game = new Minesweeper(rows, cols, mines);

        game.generateMines();

        game.scanAndAssignNumbers();

        cellButtons = new JButton[rows][cols];

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                cellButtons[i][j] = new JButton();

                cellButtons[i][j].setFocusable(false);

                cellButtons[i][j].setPreferredSize(new Dimension(25, 25));

                final int r = i;

                final int c = j;

                cellButtons[i][j].addMouseListener(new MouseAdapter() {

                    public void mousePressed(MouseEvent e) {

                        if (e.getButton() == MouseEvent.BUTTON1) { // Left click

                            revealCell(r, c);

                        } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click

                            flagCell(r, c);

                        }

                    }

                });

                gamePanel.add(cellButtons[i][j]);

            }

        }

        flagsPlaced = 0;

        minesLabel.setText(String.format("%03d", mines - flagsPlaced));

        timer.stop();

        timerLabel.setText("000");

        resetButton.setText("🙂");

        pack();

    }

    private void revealCell(int r, int c) {

        if (!timer.isRunning()) {

            timer.start();

        }

        if (r < 0 || r >= rows || c < 0 || c >= cols || !cellButtons[r][c].isEnabled()) {

            return;

        }

        JButton cell = cellButtons[r][c];

        cell.setEnabled(false);

        int value = game.getMinesweeperMatrix()[r][c];

        if (value == -1) {

            gameOver(false);

            cell.setBackground(Color.RED);

            cell.setText("M");

        } else {

            cell.setText(value > 0 ? String.valueOf(value) : "");

            cell.setFont(new Font("Monospaced", Font.BOLD, 14));

            cell.setForeground(getColorForNumber(value));

            if (value == 0) {

                for (int i = -1; i <= 1; i++) {

                    for (int j = -1; j <= 1; j++) {

                        if (i == 0 && j == 0)
                            continue;

                        revealCell(r + i, c + j);

                    }

                }

            }

        }

        checkWinCondition();

    }

    private void flagCell(int r, int c) {

        if (!cellButtons[r][c].isEnabled()) {

            return;

        }

        if (cellButtons[r][c].getText().equals("F")) {

            cellButtons[r][c].setText("");

            flagsPlaced--;

        } else {

            cellButtons[r][c].setText("F");

            flagsPlaced++;

        }

        minesLabel.setText(String.format("%03d", mines - flagsPlaced));

    }

    private Color getColorForNumber(int number) {

        switch (number) {

            case 1:
                return Color.BLUE;

            case 2:
                return new Color(0, 128, 0); // Green

            case 3:
                return Color.RED;

            case 4:
                return new Color(0, 0, 128); // Dark Blue

            case 5:
                return new Color(128, 0, 0); // Dark Red

            case 6:
                return new Color(0, 128, 128); // Teal

            case 7:
                return Color.BLACK;

            case 8:
                return Color.GRAY;

            default:
                return Color.BLACK;

        }

    }

    private void gameOver(boolean win) {

        timer.stop();

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                cellButtons[i][j].setEnabled(false);

                if (game.getMinesweeperMatrix()[i][j] == -1) {

                    cellButtons[i][j].setText("M");

                }

            }

        }

        if (win) {

            resetButton.setText("😎");

            JOptionPane.showMessageDialog(this, "You win!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);

        } else {

            resetButton.setText("😵");

            JOptionPane.showMessageDialog(this, "Game Over!", "Boom!", JOptionPane.ERROR_MESSAGE);

        }

    }

    private void checkWinCondition() {
        int revealedCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!cellButtons[i][j].isEnabled()) {
                    revealedCount++;
                }
            }
        }
        if (revealedCount == (rows * cols) - game.getMines()) {
            gameOver(true);
        }
    }

    private void resetGame() {
        setDifficulty(this.rows, this.cols, this.mines);
    }

    private void setDifficulty(int r, int c, int m) {
        this.rows = r;
        this.cols = c;
        this.mines = m;
        startGame();
    }

    private void showCustomDialog() {
        JTextField rowsField = new JTextField(Integer.toString(rows));
        JTextField colsField = new JTextField(Integer.toString(cols));
        JTextField minesField = new JTextField(Integer.toString(mines));

        Object[] message = {
                "Rows:", rowsField,
                "Columns:", colsField,
                "Mines:", minesField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Custom Difficulty", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int r = Integer.parseInt(rowsField.getText());
                int c = Integer.parseInt(colsField.getText());
                int m = Integer.parseInt(minesField.getText());
                setDifficulty(r, c, m);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers only.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTimer() {
        int currentTime = Integer.parseInt(timerLabel.getText());
        currentTime++;
        timerLabel.setText(String.format("%03d", currentTime));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinesweeperGUI::new);
    }
}
