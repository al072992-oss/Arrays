import java.util.Random;

public class Minesweeper {

    private int rows;
    private int cols;
    private int mines;

    private boolean[][] mineField;
    private boolean[][] revealed;
    private int[][] numbers;

    public Minesweeper(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        initializeGame();
    }

    private void initializeGame() {
        mineField = new boolean[rows][cols];
        revealed = new boolean[rows][cols];
        numbers = new int[rows][cols];

        placeMines();
        calculateNumbers();
    }

    private void placeMines() {
        Random random = new Random();
        int placed = 0;

        while (placed < mines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (!mineField[r][c]) {
                mineField[r][c] = true;
                placed++;
            }
        }
    }

    private void calculateNumbers() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!mineField[r][c]) {
                    numbers[r][c] = countAdjacentMines(r, c);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row + dr;
                int nc = col + dc;

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && mineField[nr][nc]) {
                    count++;
                }
            }
        }
        return count;
    }

    // 🔁 RECURSIVIDAD
    public void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;
        if (revealed[row][col]) return;

        revealed[row][col] = true;

        if (numbers[row][col] == 0 && !mineField[row][col]) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    revealCell(row + dr, col + dc);
                }
            }
        }
    }

    public boolean isMine(int row, int col) {
        return mineField[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public int getNumber(int row, int col) {
        return numbers[row][col];
    }
}
