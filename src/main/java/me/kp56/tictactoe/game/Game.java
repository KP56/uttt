package me.kp56.tictactoe.game;

import me.kp56.tictactoe.game.boards.Board;
import me.kp56.tictactoe.game.boards.fieldstate.FieldState;
import me.kp56.tictactoe.game.exceptions.IllegalMoveException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Game {
    private Board board = new Board(true);
    public FieldState whoseTurn = FieldState.CROSS;
    private int currentSubBoardX = -1;
    private int currentSubBoardY = -1;
    public final boolean online;
    public final FieldState localPlayer;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private Socket socket;

    public Game() {
        this.online = false;
        this.localPlayer = null;
    }

    public Game(Socket socket) throws IOException {
        this.online = true;
        this.bis = new BufferedInputStream(socket.getInputStream());
        this.bos = new BufferedOutputStream(socket.getOutputStream());
        this.socket = socket;

        System.out.println("Connected to the server.");

        this.localPlayer = FieldState.values()[bis.read()];
    }

    public void gameLoop() {
        Scanner inputScanner = new Scanner(System.in);
        while (getState() == GameState.RUNNING) {
            printBoard();

            if (online && whoseTurn != localPlayer) {
                System.out.println("Waiting for your opponent to make a move...");

                // Here wait for the server to send a move packet
                // Then make a move for the opponent
                try {
                    int opponentX = bis.read();
                    int opponentY = bis.read();

                    if (opponentX == -1 || opponentY == -1) {
                        System.out.println("Connection terminated.");
                        break;
                    }

                    attemptMove(opponentX, opponentY, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // And print the board
                printBoard();
            }

            if (online && localPlayer != whoseTurn) continue;

            if (currentSubBoardX == -1) {
                System.out.println("You can make a move anywhere on the board.");
            } else {
                System.out.println("You can make a move on the subboard: (" + currentSubBoardX + ", " + currentSubBoardY + ")");
            }

            System.out.println("Provide the X of the move you want to play:");
            int x = Integer.parseInt(inputScanner.nextLine());
            System.out.println("Provide the Y of the move you want to play:");
            int y = Integer.parseInt(inputScanner.nextLine());

            attemptMove(x, y, false);
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Display the end screen
        FieldState result = getResult();
        if (result == FieldState.CIRCLE) {
            // Circle won

            System.out.println("Circle player has won.");
        } else if (result == FieldState.CROSS) {
            // Cross won

            System.out.println("Cross player has won.");
        } else {
            // Draw

            System.out.println("It's a draw.");
        }
    }

    void attemptMove(int x, int y, boolean remote) {
        try {
            if ((currentSubBoardX != -1 && x / 3 != currentSubBoardX) || (currentSubBoardY != -1 && y / 3 != currentSubBoardY)) {
                throw new IllegalMoveException();
            }

            move(x, y);

            if (board.getSubBoardState(x % 3,y % 3) == GameState.FINISHED) {
                currentSubBoardX = -1;
                currentSubBoardY = -1;
            } else {
                currentSubBoardX = x % 3;
                currentSubBoardY = y % 3;
            }
        } catch (IllegalMoveException exception) {
            if (!remote) {
                System.out.println("An illegal move has been played. Try once again.");
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board.getFieldState(j / 3, i / 3, j % 3, i % 3));

                if (j % 3 == 2) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i % 3 == 2) {
                for (int j = 0; j < 15; j++) {
                    System.out.print("-");
                }
                System.out.println();
            }
        }
    }

    public void move(int x, int y) {
        if (x > 8 || y > 8 || x < 0 || y < 0) {
            throw new IllegalMoveException();
        }

        if (board.getSubBoardState(x / 3, y / 3) == GameState.FINISHED) {
            throw new IllegalMoveException();
        }

        if (board.getFieldState(x / 3, y / 3, x % 3, y % 3) != FieldState.NEUTRAL) {
            throw new IllegalMoveException();
        }

        board.move(x / 3, y / 3, x % 3, y % 3, whoseTurn);
        if (online && whoseTurn == localPlayer) {
            // We need to send a packet to the server that we made a move
            try {
                bos.write(x);
                bos.write(y);
                bos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        whoseTurn = (whoseTurn == FieldState.CROSS) ? FieldState.CIRCLE : FieldState.CROSS;
    }

    public GameState getState() {
        return board.getState();
    }

    public FieldState getResult() {
        return board.getResult();
    }
}
