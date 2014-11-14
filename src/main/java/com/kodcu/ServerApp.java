package com.kodcu;

import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.IOException;

/**
 * Created by usta on 12.11.2014.
 */
public class ServerApp {

    public static void main(String[] args) throws DeploymentException, IOException {
        Server server = new Server("0.0.0.0", 8080, "/", null, FaktoryelServerSoket.class);
        server.start();

        System.out.println("MasterStarted");

        System.in.read();


    }
}
