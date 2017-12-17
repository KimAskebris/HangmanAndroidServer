package com.kimaskebris.hangman.server.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Game extends Thread {

    private Socket socket;
    private boolean running;
    private boolean playing;
    private ArrayList<String> messagesToSend;
    private int score = 0;
    private boolean won = false;

    public Game(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try (ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream())) {
            running = true;
            while (running) {
                messagesToSend = new ArrayList<>();
                Word selectedWord = new Word();
                String hangmanWord = selectedWord.getWord().toUpperCase();
                System.out.println(hangmanWord);
                String dashedWord = selectedWord.dash(hangmanWord);
                int numberOfGuesses = hangmanWord.length();
                StringBuilder currentWord = new StringBuilder(dashedWord);
                messagesToSend.add("guesses " + numberOfGuesses);
                messagesToSend.add("word " + dashedWord);
                messagesToSend.add("score " + score);
                toClient.writeObject(messagesToSend);
                toClient.flush();
                messagesToSend.clear();
                playing = true;
                String clientMsg;
                while (playing) {
                    clientMsg = (String) fromClient.readObject();
                    if (clientMsg.equalsIgnoreCase("SFKSMTRELR2423METERT242")) {
                        break;
                    }
                    String guess = clientMsg.toUpperCase();
                    if (guess.length() == 1) {
                        if (hangmanWord.contains(guess)) {                               //Guessed for a letter
                            currentWord = guess(hangmanWord, currentWord, guess);
                        } else {
                            messagesToSend.add("msg Letter " + guess + " isn't in the word");
                            numberOfGuesses--;
                        }
                        if (hangmanWord.equals(currentWord.toString())) {
                            won = true;
                            messagesToSend.add("msg Correct");
                            messagesToSend.add("won true");
                            score++;
                        }
                    } else if (guess.length() > 1) {
                        if (guess(hangmanWord, guess)) {                    //Guessed for a word
                            won = true;
                            messagesToSend.add("msg Correct");
                            messagesToSend.add("won true");
                            score++;
                        } else {
                            messagesToSend.add("msg Wrong answer");
                            numberOfGuesses--;
                        }
                    } else {
                        messagesToSend.add("msg Guess a word or a letter");
                    }
                    if (numberOfGuesses == 0) {
                        messagesToSend.add("msg Lost. Correct: " + hangmanWord);
                        messagesToSend.add("won false");
                        score--;
                    }
                    messagesToSend.add("guesses " + String.valueOf(numberOfGuesses));
                    if (won) {
                        messagesToSend.add("word " + hangmanWord);
                    } else {
                        messagesToSend.add("word " + currentWord.toString());
                    }
                    messagesToSend.add("score " + score);
                    System.out.println(messagesToSend);
                    toClient.reset();
                    toClient.writeObject(messagesToSend);
                    toClient.flush();
                    messagesToSend.clear();
                }
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder guess(String hangmanWord, StringBuilder currentWord, String guess) {

        char[] myCharArray = currentWord.toString().toCharArray();
        char letter = guess.charAt(0);
        for (int i = 0; i < hangmanWord.length(); i++) {
            if (hangmanWord.charAt(i) == letter) {
                myCharArray[i] = letter;
            }
        }
        return new StringBuilder(String.valueOf(myCharArray));
    }

    private boolean guess(String hangmanWord, String guess) {
        return hangmanWord.equals(guess);
    }


}
