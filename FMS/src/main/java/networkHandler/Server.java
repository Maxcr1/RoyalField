package main.java.networkHandler;

import main.java.networkHandler.client.Client;
import main.java.networkHandler.client.tablet.FieldTablet;
import main.java.networkHandler.client.tablet.RobotTablet;
import main.java.networkHandler.client.tablet.TabletManager;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class Server extends Thread{

    private ServerSocket server;

    //Constructor that creates the ServerSocket
    Server() throws IOException {
        server = new ServerSocket(90, 1, InetAddress.getLocalHost());
    }

    //Listener function that grabs new clients and hands them information.
    public void run() {

        // running infinite loop for getting
        // client request
        while (System.currentTimeMillis() > 0)
        {
            Socket s = null;

            try
            {
                // socket object to receive incoming client requests
                s = server.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams

                System.out.println("Assigning new thread for this client");

                BufferedReader in;

                try {
                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                    String data;

                    while (true) {
                        //Check if we've got new data from our client.
                        if ((data = in.readLine()) != null) {
                            if(data.equals("JTB")){
                                System.out.println("Recieved Request for new RobotTablet");
                                TabletManager.addRobotTablet(new RobotTablet(s));
                                break;
                            }
                            else if(data.equals("FTB")){
                                TabletManager.addFieldTablet(new FieldTablet(s));
                                break;
                            }
                            System.out.println(data);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            // create a new thread object
                Thread t = new Client(s);

                // Invoking the start() method
                t.start();

            }
            catch (Exception e){
                try {
                    assert s != null;
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

    }

}
