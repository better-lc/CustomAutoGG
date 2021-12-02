package me.onils.customautogg;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String msg, Instrumentation inst){
        String[] split = msg.split(",");
        inst.addTransformer(new Transformer(split[0], split[1]));
    }
}
