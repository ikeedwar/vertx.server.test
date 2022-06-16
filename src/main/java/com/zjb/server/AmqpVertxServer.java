package com.zjb.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonQoS;
import io.vertx.proton.ProtonServer;
import io.vertx.proton.ProtonServerOptions;

import static java.lang.System.*;

/**
 * @author zhangjunbo02
 */
public class AmqpVertxServer extends AbstractVerticle {

    public Future<Void> bindSecureServer() {
        final ProtonServerOptions options =
                new ProtonServerOptions()
                        .setHost("localhost")
                        .setPort(5672)
                        .setMaxFrameSize(16384)
                        // set heart beat to half the idle timeout
                        .setHeartbeat(5000);

        final Promise<Void> result = Promise.promise();
        ProtonServer secureServer = createServer(options);
        secureServer.connectHandler(this::onConnectRequest).listen(ar -> {
            if (ar.succeeded()) {
                out.println("secure AMQP server listening success");
                result.complete();
            } else {
                out.println("secure AMQP server listening failed");
                result.fail(ar.cause());
            }
        });
        return result.future();
    }

    private ProtonServer createServer(final ProtonServerOptions options) {
        Vertx vertx = Vertx.vertx();
        return ProtonServer.create(vertx, options);
    }

    /**
     * Handles a remote peer's request to open a connection.
     *
     * @param con The connection to be opened.
     */
    private void onConnectRequest(final ProtonConnection con) {

        con.disconnectHandler(lostConnection -> {
            out.println("lost connection to device disconnectHandler");
        });

        con.closeHandler(remoteClose -> {
            out.println("lost connection to device closeHandler");
        });

        // when a begin frame is received
        con.sessionOpenHandler(session -> {
            out.println("lost connection to device sessionOpenHandler");
            session.open();
        });
        // when the device wants to open a link for
        // uploading messages
        con.receiverOpenHandler(receiver -> {

            receiver.setAutoAccept(true);
            receiver.setQoS(ProtonQoS.AT_MOST_ONCE);
            receiver.handler((delivery, message) -> {
                out.println("sessionOpenHandler delivery" + delivery);
                out.println("sessionOpenHandler message" + message);
            });
            out.println("lost connection to device receiverOpenHandler");
            receiver.open();
        });
        // when the device wants to open a link for
        // receiving commands
        con.senderOpenHandler(sender -> {

            out.println("lost connection to device senderOpenHandler");
            sender.open();
        });
        con.openHandler(remoteOpen -> {
            out.println("lost connection to device openHandler");
            ProtonConnection conn = remoteOpen.result();
            conn.open();
            out.println("lost connection to device openHandler success");
        });
    }

}
