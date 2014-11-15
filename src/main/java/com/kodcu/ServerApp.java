package com.kodcu;

import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by usta on 12.11.2014.
 */
public class ServerApp {

    public static void main(String[] args) throws DeploymentException, IOException {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("org.glassfish.tyrus.incomingBufferSize", 1024*1024*100*999999999);
        Server server = new Server("0.0.0.0", 8080, "/", properties, FaktoryelServerSoket.class);
        server.start();

        System.out.println("MasterStarted");

        System.in.read();


    }
}
