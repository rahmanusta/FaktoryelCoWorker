package com.kodcu;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usta on 12.11.2014.
 */
public class ClientApp {

    public static void main(String[] args) throws Exception {

        ClientApp.connect();

        System.in.read();
    }

    public static void connect() throws Exception {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        ClientManager clientManager = ClientManager.createClient();
        clientManager.getProperties().put("org.glassfish.tyrus.incomingBufferSize", 1024*1024*100*999999999);

        URI uri = new URI("ws://localhost:8080/soket");
        Session client = clientManager.connectToServer(FaktoryelWorkerSoket.class, uri);

        Map map = new HashMap<>();
        map.put("start", null);
        map.put("N", 2_000_000);

        client.getAsyncRemote().sendObject(map);


    }
}
