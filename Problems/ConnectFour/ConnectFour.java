//Author: Tushar Jaiswal
//Creation Date: 01/09/2021

/*Runtime Complexity:
  * Initialize board is O(|Rows| * |Columns|)
  * Each move is O(|Columns|)
Space Complexity: O(|Rows| * |Columns|) to store the board*/

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ConnectFour {

    private char[][] BOARD;
    private int NUM_ROWS;
    private int NUM_COLUMNS;
    private int[] OPEN_ROW_FOR_COLUMN;

    private static final char PLAYER_A = 'A';
    private static final char PLAYER_B = 'B';
    private static final int SUCCESSIVE_CONNECTS_FOR_WIN = 4;

    public static void main(String[] args) {
        ConnectFour game = new ConnectFour(6, 8);
        game.runConnectFourGame();
    }

    public ConnectFour(int rows, int columns) {
        if (columns < SUCCESSIVE_CONNECTS_FOR_WIN) {
            throw new IllegalArgumentException("Board must have more than 4 columns for Connect Four game.");
        }

        NUM_ROWS = rows;
        NUM_COLUMNS = columns;
        BOARD = new char[NUM_ROWS][NUM_COLUMNS];
        OPEN_ROW_FOR_COLUMN = new int[columns];
        Arrays.fill(OPEN_ROW_FOR_COLUMN, 0);
        initializeBoard();
    }

    private void initializeBoard() {
        for (char[] row : BOARD) {
            Arrays.fill(row, '-');
        }
    }

    public void runConnectFourGame() {
        for (int moveCount = 0; moveCount < NUM_ROWS * NUM_COLUMNS; moveCount++) {
            char player = moveCount % 2 == 0 ? PLAYER_A : PLAYER_B;
            Move move = makeMove(player);
            System.out.println(String.format("Player %s's turn", player));
            printBoard();
            if(hasCurrentPlayerWon(player, move)) {
                System.out.println(String.format("Player %s has won.", player));
                return;
            }
        }
        System.out.println("Game ended in a tie.");
    }

    private Move makeMove(char player) {
        int randomColumn;
        do {
            randomColumn = ThreadLocalRandom.current().nextInt(0, NUM_COLUMNS);
        } while (OPEN_ROW_FOR_COLUMN[randomColumn] >= NUM_ROWS);

        Move move = new Move(OPEN_ROW_FOR_COLUMN[randomColumn], randomColumn);

        BOARD[move.row][move.column] = player;
        OPEN_ROW_FOR_COLUMN[randomColumn]++;

        return move;
    }

    private boolean hasCurrentPlayerWon(char player, Move move) {
        return checkRowForWin(player, move) || checkColumnForWin(player, move) || checkLeftDiagonalForWin(player, move) || checkRightDiagonalForWin(player, move);
    }

    private boolean checkRowForWin(char player, Move move) {
        int startColumn = Math.max(0, move.column - (SUCCESSIVE_CONNECTS_FOR_WIN - 1));
        int endColumn = Math.min(move.column, NUM_COLUMNS - SUCCESSIVE_CONNECTS_FOR_WIN);

        for (int i = startColumn; i <= endColumn; i++) {
            int j = 0;
            for (; j < SUCCESSIVE_CONNECTS_FOR_WIN; j++) {
                if (BOARD[move.row][i + j] != player) {
                    // Move to next potential position to start matching from
                    i = i + j;
                    break;
                }
            }
            if (j == 4) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumnForWin(char player, Move move) {
        int startRow = Math.max(0, move.row - (SUCCESSIVE_CONNECTS_FOR_WIN - 1));
        int endRow = Math.min(move.row, NUM_ROWS - SUCCESSIVE_CONNECTS_FOR_WIN);

        for (int i = startRow; i <= endRow; i++) {
            int j = 0;
            for (; j < SUCCESSIVE_CONNECTS_FOR_WIN; j++) {
                if (BOARD[i + j][move.column] != player) {
                    // Move to next potential position to start matching from
                    i = i + j;
                    break;
                }
            }
            if (j == 4) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLeftDiagonalForWin(char player, Move move) {
        int placesFromStart = move.row < (SUCCESSIVE_CONNECTS_FOR_WIN - 1) || move.column < (SUCCESSIVE_CONNECTS_FOR_WIN - 1) ?
                Math.min(move.row, move.column) : SUCCESSIVE_CONNECTS_FOR_WIN - 1;
        int startColumn = move.column - placesFromStart;
        int startRow = move.row - placesFromStart;

        for (int row = startRow, col = startColumn; row + SUCCESSIVE_CONNECTS_FOR_WIN - 1 < NUM_ROWS &&
                col + SUCCESSIVE_CONNECTS_FOR_WIN  - 1 < NUM_COLUMNS; row++, col++) {
            int j = 0;
            for (; j < SUCCESSIVE_CONNECTS_FOR_WIN; j++) {
                if (BOARD[row + j][col + j] != player) {
                    // Move to next potential position to start matching from
                    row = row + j;
                    col = col + j;
                    break;
                }
            }
            if (j == 4) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRightDiagonalForWin(char player, Move move) {
        int placesFromStart = move.row + (SUCCESSIVE_CONNECTS_FOR_WIN - 1) >= NUM_ROWS || move.column < (SUCCESSIVE_CONNECTS_FOR_WIN - 1) ?
                Math.min(NUM_ROWS - 1 - move.row, move.column) : SUCCESSIVE_CONNECTS_FOR_WIN - 1;
        int startColumn = move.column - placesFromStart;
        int startRow = move.row + placesFromStart;

        for (int row = startRow, col = startColumn; row - (SUCCESSIVE_CONNECTS_FOR_WIN - 1) >= 0 &&
                col + SUCCESSIVE_CONNECTS_FOR_WIN  - 1 < NUM_COLUMNS; row--, col++) {
            int j = 0;
            for (; j < SUCCESSIVE_CONNECTS_FOR_WIN; j++) {
                if (BOARD[row - j][col + j] != player) {
                    // Move to next potential position to start matching from
                    row = row - j;
                    col = col + j;
                    break;
                }
            }
            if (j == 4) {
                return true;
            }
        }
        return false;
    }

    private void printBoard() {
        for (int row = NUM_ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                System.out.print(BOARD[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
