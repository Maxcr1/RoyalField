package main.java.networkHandler.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// Client class
public class Client extends Thread {
    private final Socket s;

    // Constructor
    public Client(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {

        try {

            String data;
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            while (s.isConnected()) {
                //Check if we've got new data from our client.
                if ((data = in.readLine()) != null) {
                    useData(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void useData(String s){

    }
}
