package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private int clientId;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    Client(Socket socket, int clientId){
        this.socket = socket;
        this.clientId = clientId;
        InitializeReaderAndWriter();
    }

    private boolean InitializeReaderAndWriter() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean CheckConnection() {
        return socket.isConnected();
    }

    public String Read() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean CheckReader() {
        try {
            return bufferedReader.ready();
        } catch (IOException e) {
            return false;
        }
    }

    public void Write(String output) {
        printWriter.println(output);
    }

}
