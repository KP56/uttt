package me.kp56.tictactoe.game.boards;

import me.kp56.tictactoe.game.GameState;
import me.kp56.tictactoe.game.boards.fieldstate.FieldState;
import me.kp56.tictactoe.game.boards.fieldstate.FieldStateProvider;
import me.kp56.tictactoe.game.boards.fieldstate.SimpleFieldStateProvider;

public class Board implements FieldStateProvider {
    private int moves;
    private int requiredMoves;
    private FieldStateProvider[][] board;

    public Board() {
        this(false);
    }

    public Board(boolean isLargerBoard) {
        if (isLargerBoard) {
            board = new Board[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = new Board();
                }
            }
            this.requiredMoves = 81;
        } else {
            board = new SimpleFieldStateProvider[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = new SimpleFieldStateProvider(FieldState.NEUTRAL);
                }
            }
            this.requiredMoves = 9;
        }
    }

    private FieldState isAnyLineEqual() {
        outerLoop:
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                if (board[i][j].provide() != board[i][j-1].provide()) {
                    continue outerLoop;
                }
            }

            if (board[i][0].provide() == FieldState.CIRCLE) {
                return FieldState.CIRCLE;
            } else if (board[i][0].provide() == FieldState.CROSS) {
                return FieldState.CROSS;
            }
        }

        outerLoop:
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                if (board[j][i].provide() != board[j-1][i].provide()) {
                    continue outerLoop;
                }
            }

            if (board[0][i].provide() == FieldState.CIRCLE) {
                return FieldState.CIRCLE;
            } else if (board[0][i].provide() == FieldState.CROSS) {
                return FieldState.CROSS;
            }
        }

        return FieldState.NEUTRAL;
    }

    private FieldState isAnyDiagonalEqual() {
        if (board[0][0].provide() == board[1][1].provide() && board[1][1].provide() == board[2][2].provide()) {
            if (board[0][0].provide() == FieldState.CIRCLE) {
                return FieldState.CIRCLE;
            } else if (board[0][0].provide() == FieldState.CROSS) {
                return FieldState.CROSS;
            }
        }

        if (board[0][2].provide() == board[1][1].provide() && board[1][1].provide() == board[2][0].provide()) {
            if (board[0][2].provide() == FieldState.CIRCLE) {
                return FieldState.CIRCLE;
            } else if (board[0][2].provide() == FieldState.CROSS) {
                return FieldState.CROSS;
            }
        }

        return FieldState.NEUTRAL;
    }

    public GameState getState() {
        if (isAnyDiagonalEqual() != FieldState.NEUTRAL || isAnyLineEqual() != FieldState.NEUTRAL || moves == requiredMoves) {
            return GameState.FINISHED;
        }

        return GameState.RUNNING;
    }

    public void move(int x1, int y1, int x2, int y2, FieldState newState) {
        moves++;

        if (board instanceof Board[][]) {
            Board smallerBoard = (Board) board[x1][y1];
            smallerBoard.move(x2, y2, -1, -1, newState);
        } else {
            SimpleFieldStateProvider provider = (SimpleFieldStateProvider) board[x1][y1];
            provider.modify(newState);
        }
    }

    public GameState getSubBoardState(int x, int y) {
        if (board instanceof SimpleFieldStateProvider[][]) {
            throw new RuntimeException("Wtf are you doing??");
        }

        return ((Board) board[x][y]).getState();
    }

    public FieldState getSubBoardResult(int x, int y) {
        if (board instanceof SimpleFieldStateProvider[][]) {
            throw new RuntimeException("Wtf are you doing??");
        }

        return ((Board) board[x][y]).getResult();
    }

    public FieldState getFieldState(int x1, int y1, int x2, int y2) {
        if (board instanceof Board[][]) {
            Board smallerBoard = (Board) board[x1][y1];
            return smallerBoard.getFieldState(x2, y2, -1, -1);
        } else {
            SimpleFieldStateProvider provider = (SimpleFieldStateProvider) board[x1][y1];
            return provider.provide();
        }
    }

    public FieldState getResult() {
        if (getState() == GameState.RUNNING) {
            return FieldState.NEUTRAL;
        }

        FieldState diagonals = isAnyDiagonalEqual();
        if (diagonals != FieldState.NEUTRAL) {
            return diagonals;
        }

        return isAnyLineEqual();
    }

    @Override
    public FieldState provide() {
        return getResult();
    }
}
