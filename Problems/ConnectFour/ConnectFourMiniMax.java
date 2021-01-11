//Author: Tushar Jaiswal
//Creation Date: 01/10/2021

/*Runtime Complexity:
  * Initialize board is O(|Rows| * |Columns|)
  * Each move is O(|Columns| ^ (depth of MiniMax Tree / 2))
Space Complexity: O(|Rows| * |Columns|) to store the board*/

import java.util.Arrays;

public class ConnectFourMiniMax {

    private char[][] BOARD;
    private int NUM_ROWS;
    private int NUM_COLUMNS;
    private int[] OPEN_ROW_FOR_COLUMN;

    private static final char PLAYER_A = 'A';
    private static final char PLAYER_B = 'B';
    private static final char EMPTY_POSITION = '_';
    private static final int SUCCESSIVE_CONNECTS_FOR_WIN = 4;
    private static final int MAXIMAL_SCORE = 1000;
    private static final int MINIMAL_SCORE = -1000;

    public static void main(String[] args) {
        ConnectFourMiniMax game = new ConnectFourMiniMax(4, 4);
        game.runConnectFourGame();
    }

    public ConnectFourMiniMax(int rows, int columns) {
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
            Arrays.fill(row, EMPTY_POSITION);
        }
    }

    public void runConnectFourGame() {
        for (int moveCount = 0; moveCount < NUM_ROWS * NUM_COLUMNS; moveCount++) {
            char player = moveCount % 2 == 0 ? PLAYER_A : PLAYER_B;
            Move move = makeMove(player, moveCount);
            System.out.println(String.format("Player %s's turn", player));
            printBoard();
            if(hasPlayerWon(player, move)) {
                System.out.println(String.format("Player %s has won.", player));
                return;
            }
        }
        System.out.println("Game ended in a tie.");
    }

    private Move makeMove(char player, int moveCount) {
        Move move = getBestMove(player, moveCount);

        BOARD[move.row][move.column] = player;
        OPEN_ROW_FOR_COLUMN[move.column]++;

        return move;
    }

    private Move getBestMove(char player, int moveCount) {
        int bestScore = player == PLAYER_A ? MINIMAL_SCORE : MAXIMAL_SCORE;
        Move bestMove = null;

        for (int i = 0; i < NUM_COLUMNS; i++) {
            if (OPEN_ROW_FOR_COLUMN[i] < NUM_ROWS) {
                // Make a move
                Move move = new Move(OPEN_ROW_FOR_COLUMN[i], i);
                BOARD[move.row][move.column] = player;
                OPEN_ROW_FOR_COLUMN[move.column]++;

                // Get score for this move
                int score = miniMax(player, move, 0, moveCount + 1, MINIMAL_SCORE, MAXIMAL_SCORE);

                // Undo move
                BOARD[move.row][move.column] = EMPTY_POSITION;
                OPEN_ROW_FOR_COLUMN[move.column]--;

                if (isScoreBetter(score, bestScore, player)) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    private int miniMax(char player, Move move, int depth, int moveCount, int alpha, int beta) {
        int score = getScore(player, move, depth);

        if (player == PLAYER_A && score > 0) {
            return score; // Player A wins in the end with this move
        } else if (player == PLAYER_B && score < 0) {
            return score; // Player B wins in the end with this move
        } else if (moveCount == NUM_ROWS * NUM_COLUMNS) {
            return 0; // This was the last move resulting in a tie
        }

        // Switch player for the next move through MiniMax
        player = player == PLAYER_A ? PLAYER_B : PLAYER_A;

        int bestScore = player == PLAYER_A ? MINIMAL_SCORE : MAXIMAL_SCORE;

        for (int i = 0; i < NUM_COLUMNS; i++) {
            if (OPEN_ROW_FOR_COLUMN[i] < NUM_ROWS) {
                // Make a move
                move = new Move(OPEN_ROW_FOR_COLUMN[i], i);
                BOARD[move.row][move.column] = player;
                OPEN_ROW_FOR_COLUMN[move.column]++;

                // Get score for this move
                score = miniMax(player, move, depth + 1, moveCount + 1, alpha, beta);

                // Undo move
                BOARD[move.row][move.column] = EMPTY_POSITION;
                OPEN_ROW_FOR_COLUMN[move.column]--;

                if (isScoreBetter(score, bestScore, player)) {
                    bestScore = score;
                    alpha = player == PLAYER_A ? Math.max(alpha, bestScore) : alpha;
                    beta = player == PLAYER_B ? Math.min(beta, bestScore) : beta;
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        return bestScore;
    }

    private int getScore(char player, Move move, int depth) {
        if (checkRowForWin(player, move) || checkColumnForWin(player, move) || checkLeftDiagonalForWin(player, move) || checkRightDiagonalForWin(player, move)) {
            return player == PLAYER_A ? MAXIMAL_SCORE - depth : MINIMAL_SCORE + depth;
        }
        return 0;
    }

    private boolean isScoreBetter(int score, int bestScore, char player) {
        if (player == PLAYER_A) {
            return score > bestScore;
        } else {
            return score < bestScore;
        }
    }

    private boolean hasPlayerWon(char player, Move move) {
        int score = getScore(player, move, 0);
        if (player == PLAYER_A) {
            return score > 0;
        } else {
            return score < 0;
        }
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
