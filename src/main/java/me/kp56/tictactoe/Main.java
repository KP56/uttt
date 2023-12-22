package me.kp56.tictactoe;

import me.kp56.tictactoe.game.Game;
import me.kp56.tictactoe.game.boards.fieldstate.FieldState;
import me.kp56.tictactoe.game.network.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static final int PORT = 9623;

    public static void main(String[] args) {
        System.out.println("Choose one of 3 options:");
        System.out.println("1) Two-player local game");
        System.out.println("2) Server setup");
        System.out.println("3) Connect to the server");

        Scanner scanner = new Scanner(System.in);

        int option = Integer.parseInt(scanner.nextLine());

        switch (option) {
            case 1 -> {
                Game game = new Game();
                game.gameLoop();
            }
            case 2 -> {
                System.out.println("Setting up the server...");
                Server server = new Server();
                try {
                    System.out.println("Listening on the port...");
                    server.listen(PORT);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case 3 -> {
                try {
                    System.out.println("Trying to connect to the server...");
                    Game remoteGame = new Game(new Socket(InetAddress.getLocalHost(), PORT));
                    System.out.println("Playing as: " + (remoteGame.localPlayer == FieldState.CROSS ? "Cross" : "Circle"));
                    remoteGame.gameLoop();
                } catch (IOException e) {
                    System.out.println("Could not connect to the server. Make sure it is running locally.");
                }
            }
        }

    }
}