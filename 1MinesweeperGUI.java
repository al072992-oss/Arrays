import javax.swing.*;
import java.awt.*;

public class MinesweeperGUI extends JFrame {

    private Minesweeper game;
    private JButton[][] buttons;

    private int rows = 9;
    private int cols = 9;
    private int mines = 10;

    private JPanel boardPanel;

    public MinesweeperGUI() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenu();
        startGame();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Dificultad");

        JMenuItem easy = new JMenuItem("Easy");
        JMenuItem medium = new JMenuItem("Medium");
        JMenuItem hard = new JMenuItem("Hard");

        easy.addActionListener(e -> setDifficulty(9, 9, 10));
        medium.addActionListener(e -> setDifficulty(16, 16, 40));
        hard.addActionListener(e -> setDifficulty(16, 30, 99));

        menu.add(easy);
        menu.add(medium);
        menu.add(hard);
        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    private void setDifficulty(int r, int c, int m) {
        rows = r;
        cols = c;
        mines = m;
        startGame();
    }

    // ✅ MÉTODO CORREGIDO
    private void resetGame() {
        startGame();
    }

    private void startGame() {
        if (boardPanel != null) {
            remove(boardPanel);
        }

        game = new Minesweeper(rows, cols, mines);
        boardPanel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton();
                int row = r;
                int col = c;

                btn.addActionListener(e -> handleClick(row, col));

                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        pack();
    }

    private void handleClick(int row, int col) {
        if (game.isMine(row, col)) {
            buttons[row][col].setText("💣");
            JOptionPane.showMessageDialog(this, "Game Over");
            resetGame();
        } else {
            game.revealCell(row, col);
            updateBoard();
        }
    }

    private void updateBoard() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (game.isRevealed(r, c)) {
                    buttons[r][c].setEnabled(false);
                    int n = game.getNumber(r, c);
                    if (n > 0) {
                        buttons[r][c].setText(String.valueOf(n));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinesweeperGUI::new);
    }
}
