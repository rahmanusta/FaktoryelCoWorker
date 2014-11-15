package com.kodcu;

import javax.websocket.*;
import java.util.Map;
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

    @OnError
    public void onerror(Throwable throwable){
        throwable.printStackTrace();
    }

    @OnClose
    public void onclose(CloseReason reason){
        System.out.println(reason.getCloseCode());
        System.out.println(reason.getReasonPhrase());
    }

}
