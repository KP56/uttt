package me.kp56.tictactoe.game.boards.fieldstate;

public enum FieldState {
    CROSS,
    CIRCLE,
    NEUTRAL;


    @Override
    public String toString() {
        if (this == CIRCLE) return "o";
        else if (this == CROSS) return "x";
        else return " ";
    }
}
