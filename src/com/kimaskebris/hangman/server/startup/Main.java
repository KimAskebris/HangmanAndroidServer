package com.kimaskebris.hangman.server.startup;

import com.kimaskebris.hangman.server.model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException{
        new Main().startServer();
    }

    private void startServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Running on port: " + PORT );
            while (true) {
                Socket socket = server.accept();
                System.out.println("Connected to " + socket.getInetAddress().getHostName());
                new Game(socket).start();
            }
        } catch (IOException e) {
            System.out.println(" Caught IOException" + e.getMessage());
        }
    }
}
