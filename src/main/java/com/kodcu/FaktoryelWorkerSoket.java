package com.kodcu;

import com.sun.deploy.util.SessionState;

import javax.websocket.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by usta on 12.11.2014.
 */
@ClientEndpoint(
        encoders = ObjectSerializer.class,
        decoders = ObjectSerializer.class
)
public class FaktoryelWorkerSoket {

    @OnMessage
    public void onmessage(Map message , Session session) throws Exception {

        // Eğer Lambda geldiyse
        if(message.containsKey("chunk")){
            Consumer<Session> task = (Consumer<Session>) message.get("chunk");
            task.accept(session); // Ara sonuç Master'a iletilir
        }

        // Eğer sonuç geldiyse
        if(message.containsKey("result")){
            System.out.println(message);
        }

    }
}
