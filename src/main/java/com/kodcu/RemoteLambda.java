package com.kodcu;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by usta on 14.11.2014.
 */
@FunctionalInterface
public interface RemoteLambda<T> extends Consumer<T>, Serializable {

}
