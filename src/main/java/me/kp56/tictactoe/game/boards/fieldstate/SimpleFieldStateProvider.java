package me.kp56.tictactoe.game.boards.fieldstate;

public class SimpleFieldStateProvider implements FieldStateProvider {
    private FieldState fieldState;

    public SimpleFieldStateProvider(FieldState fieldState) {
        this.fieldState = fieldState;
    }

    public void modify(FieldState fieldState) {
        this.fieldState = fieldState;
    }

    @Override
    public FieldState provide() {
        return this.fieldState;
    }
}
