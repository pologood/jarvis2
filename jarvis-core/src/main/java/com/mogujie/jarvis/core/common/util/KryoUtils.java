/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年7月13日 下午10:39:57
 */

package com.mogujie.jarvis.core.common.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author wuya
 *
 */
public class KryoUtils {

    public static byte[] writeClassAndObject(Object o) {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, o);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Object readClassAndObject(byte[] bytes) {
        Kryo kryo = new Kryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Object o = kryo.readClassAndObject(input);
        input.close();
        return o;
    }
}
