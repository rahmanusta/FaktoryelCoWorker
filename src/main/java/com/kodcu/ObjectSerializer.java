package com.kodcu;

import javax.websocket.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usta on 13.11.2014.
 */
public class ObjectSerializer implements Encoder.Binary<Map>, Decoder.Binary<Map> {

    @Override
    public Map decode(ByteBuffer bytes) throws DecodeException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.array());
             ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);) {

            return (Map) inputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap();
    }


    @Override
    public ByteBuffer encode(Map object) throws EncodeException {
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bao);) {
            oos.writeObject(object);
            return ByteBuffer.wrap(bao.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ByteBuffer.wrap(new byte[0]);
    }

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public void destroy() {}

    @Override
    public boolean willDecode(ByteBuffer bytes) {
        return true;
    }
}
