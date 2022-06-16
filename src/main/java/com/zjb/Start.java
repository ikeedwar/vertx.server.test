package com.zjb;

import com.zjb.server.AmqpVertxServer;
import io.vertx.core.Future;

/**
 * @author zhangjunbo02
 */
public class Start {

    public static void main(String[] args){
        AmqpVertxServer amqpVertxServer = new AmqpVertxServer();
        Future future = amqpVertxServer.bindSecureServer();
        System.out.println(future.result());
    }

}
