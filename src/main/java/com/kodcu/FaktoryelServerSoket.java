package com.kodcu;

import com.google.common.collect.Lists;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by usta on 12.11.2014.
 */
@ServerEndpoint(
        value = "/soket",
        encoders = ObjectSerializer.class,
        decoders = ObjectSerializer.class)
public class FaktoryelServerSoket {

    private static Long workStartTimeMillis = null;
    private static List<BigInteger> subFactoriels = Collections.synchronizedList(new ArrayList<>());
    private static int workDoneCount = 0;


    @OnMessage
    public void onmessage(Map message, Session session) throws IOException {

        // if there is a start key, start execution
        if (message.containsKey("start")) {
            dispatchWorks(session, (Integer) message.get("N"));
        }

        // if there is a chunk key, collect chunk results
        if (message.containsKey("chunk")) {
            collectChunks(message, session);
        }

    }

    /**
     * Liste parçalara ayrılır ve tüm istemcilere bir payı gönderilir,
     * Liste parçaları yanında serileştirme yöntemiyle bir Lambda fonksiyonu da iletilir,
     * Bu sayede sunucu tarafında yazılan iş mantığı, client tarafında koşturulmuş olur.
     * @param session
     * @throws IOException
     */
    private void dispatchWorks(Session session,Integer N) throws IOException {

        if (workStartTimeMillis == null) {
            workStartTimeMillis = System.currentTimeMillis();
        }

        // 1,...,250000 arası Liste hazırlanıyor
        List<Integer> numberList = IntStream.rangeClosed(1, N).boxed().collect(Collectors.toList());

        Set<Session> allSessions = session.getOpenSessions();

        // Liste parçalara ayrılıyor
        List<List<Integer>> numberChunkedList = Lists.partition(numberList, (numberList.size() / allSessions.size()));

        // İşlemin bittiğini anlamak için gerekli sonuç sayısı
        workDoneCount = ((numberChunkedList.size() % allSessions.size()) == 0) ? allSessions.size(): allSessions.size() + 1;

        Iterator<Session> allSessionsIterator = allSessions.iterator();

        // Her parçalı sayı listesi bir lambda fonksiyonu içinde kullanıcılara pay ediliyor
        for (List<Integer> numberChunks : numberChunkedList) {

            ArrayList<Integer> chunk = new ArrayList<>(numberChunks);

            // remoteLambda fonksiyonu Worker'da tanımlanır, Node'larda koşturulur
            RemoteLambda<Session> remoteLambda =  (serverSession) -> {

                BigInteger subFactoriel = chunk
                        .parallelStream()
                        .map(BigInteger::valueOf)
                        .reduce(BigInteger.ONE, (first, second) -> first.multiply(second));

                Map data = new HashMap();
                data.put("chunk", subFactoriel);

                serverSession.getAsyncRemote().sendObject(data);
            };


            Map data = new HashMap();
            data.put("chunk", remoteLambda);

            // Kullanıcılara tek tek iletiliyor.
            if (allSessionsIterator.hasNext()) {
                Session next = allSessionsIterator.next();
                next.getAsyncRemote().sendObject(data);
            } else {
                session.getAsyncRemote().sendObject(data);
            }

        }
    }

    /**
     * Her bir alt faktöryel sonucu bu metodda biriktirilir,
     * Tüm alt parçalar hesaplandığında ise,
     * Alt sonuçların da kendi içinde çarpımı sağlanır,
     * Ardından toplam koşma süresi gibi bilgiler tüm istemcilere iletilir.
     * @param message
     * @param session
     */
    private void collectChunks(Map message, Session session) {

        subFactoriels.add((BigInteger) message.get("chunk"));

        if (subFactoriels.size() == workDoneCount) {

            BigInteger factorielResult = subFactoriels.parallelStream()
                    .reduce(BigInteger.ONE, (first, second) -> first.multiply(second));

            long workerEndTimeMillis = System.currentTimeMillis();
            long workerCompleteTime = workerEndTimeMillis - workStartTimeMillis;


            Set<Session> allSessions = session.getOpenSessions();

            Map map = new HashMap();
            map.put("totalWorker", allSessions.size());
            map.put("completeTime", workerCompleteTime);
            map.put("result", factorielResult.toString().substring(0, 10).concat("..."));


            // İşlem tamam, sonuçlar kullanıcılara iletiliyor..
            for (Session e : allSessions) {
                e.getAsyncRemote().sendObject(map);
            }


            workStartTimeMillis = null;
            subFactoriels.clear();


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
