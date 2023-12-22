package me.kp56.tictactoe.game.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    public void listen(int port) throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {
            System.out.println("FINISHED!");
            System.out.println("Waiting for players to connect...");

            Socket player1 = socket.accept();
            System.out.println("First player connected.");
            Socket player2 = socket.accept();
            System.out.println("Second player connected.");

            BufferedOutputStream bos1 = new BufferedOutputStream(player1.getOutputStream());
            BufferedOutputStream bos2 = new BufferedOutputStream(player2.getOutputStream());
            bos1.write(0);
            bos2.write(1);

            bos1.flush();
            bos2.flush();

            BufferedInputStream bis1 = new BufferedInputStream(player1.getInputStream());
            BufferedInputStream bis2 = new BufferedInputStream(player2.getInputStream());

            System.out.println("Starting the game...");
            try {
                while (player1.isConnected() && player2.isConnected()) {
                    int moveX = bis1.read();
                    int moveY = bis1.read();

                    if (moveX == -1 || moveY == -1) {
                        break;
                    }

                    System.out.println("Move played by X: (" + moveX + ", " + moveY + ")");

                    bos2.write(moveX);
                    bos2.write(moveY);
                    bos2.flush();

                    int moveX2 = bis2.read();
                    int moveY2 = bis2.read();

                    System.out.println("Move played by Y: (" + moveX + ", " + moveY + ")");

                    bos1.write(moveX2);
                    bos1.write(moveY2);
                    bos1.flush();
                }
            } catch (SocketException e) {

            }
            System.out.println("The game has ended.");
        }
    }
}
