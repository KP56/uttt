package me.kp56.tictactoe.game.exceptions;

public class IllegalMoveException extends RuntimeException {
    public IllegalMoveException() {
        super("An illegal move was played");
    }
}
