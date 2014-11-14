package com.kodcu;

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
        URI uri = new URI("ws://localhost:8080/soket");
        Session client = container.connectToServer(FaktoryelWorkerSoket.class, uri);

        Map map = new HashMap<>();
        map.put("start", null);
        map.put("N", 1_000_000);

        client.getAsyncRemote().sendObject(map);


    }
}
