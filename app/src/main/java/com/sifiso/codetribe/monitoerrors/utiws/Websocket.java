package com.sifiso.codetribe.monitoerrors.utiws;

import android.content.Context;
import android.service.textservice.SpellCheckerService;
import android.util.Log;

import com.com.boha.monitor.library.dto.RequestDTO;
import com.com.boha.monitor.library.dto.ResponseDTO;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.TimerUtil;
import com.com.boha.monitor.library.util.ZipUtil;
import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * Created by CodeTribe1 on 2014-10-04.
 */
public class Websocket {
    public interface WebSocketListener {
        public void onMessage(ResponseDTO response);

        public void onClose();

        public void onError(String message);

    }

    static WebSocketClient webSocketClient;
    static Gson gson = new Gson();
    static WebSocketListener webSocketListener;
    static RequestDTO request;
    static Context ctx;
    static long start, end;
    static final String LOG = WebSocket.class.getName();

    public static void sendRequest(Context c, final String suffix, RequestDTO req, WebSocketListener listener) {
        webSocketListener = listener;
        request = req;
        ctx = c;

        try {
            if (webSocketClient == null) {
                TimerUtil.startTimer(new TimerUtil.TimerListener() {
                    @Override
                    public void onSessionDisconnected() {
                        try {
                            Log.i(LOG, "He " + "ws://10.50.75.71:8080/mwp/wscompany");
                            connectHandShake(suffix, request);
                            return;
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                String json = gson.toJson(req);
                webSocketClient.send(json);
                Log.d(LOG, "########### web socket message sent\n" + json);
            }
        } catch (WebsocketNotConnectedException e) {
            try {
                Log.i(LOG, suffix);
                Log.e(LOG, "WebsocketNotConnectedException. Problems with web socket", e);
                connectHandShake(suffix, req);
            } catch (URISyntaxException e1) {
                Log.e(LOG, "Problems with web socket", e1);
                webSocketListener.onError("Problem starting server socket communications\n" + e1.getMessage());
            }
        }
    }

    private static void connectHandShake(String url, final RequestDTO req) throws URISyntaxException {
        URI uri = new URI(Statics.WEBSOCKET_URL + url);
        Log.i(LOG, "#### the uri " + uri.toString());
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d(LOG, "######### Check the hand Shake");
                String json = gson.toJson(req);
                //webSocketClient.send(json);
                Log.d(LOG, "######### Hand Shake done \n" + json);
            }


            @Override
            public void onMessage(String response) {
                try {
                    ResponseDTO r = gson.fromJson(response, ResponseDTO.class);
                    if (r.getStatusCode() == 0) {
                        if (r.getSessionID() != null) {
                            // SharedUtil.setSessionID(ctx, r.getSessionID());
                            String json = gson.toJson(req);
                            webSocketClient.send(json);
                            Log.d(LOG, "########### websocket message sent\n" + json.toString());
                        } else {
                            Log.d(LOG, "########### websocket message sent\n");
                            webSocketListener.onMessage(r);
                        }
                    } else {
                        webSocketListener.onError(r.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(LOG, "Failed to parse response from server", e);
                    webSocketListener.onError("Failed to parse response from server");
                }

            }

            @Override
            public void onMessage(ByteBuffer bb) {
                end = System.currentTimeMillis();
                TimerUtil.killTimer();
                Log.i(LOG, "########## onMessage, ByteBuffer capacity: " + bb.capacity());
                String content = null;
                try {
                    content = ZipUtil.unzipedString(bb);
                    Log.w(LOG, "Buffer capacity After unZipping: "+ ByteBuffer.wrap(content.getBytes()));
                    if (content != null) {
                        ResponseDTO response = gson.fromJson(content, ResponseDTO.class);
                        if (response.getStatusCode() == 0) {
                            webSocketListener.onMessage(response);
                        } else {
                            webSocketListener.onError(response.getMessage());
                        }
                    } else {
                        webSocketListener.onError("Content from server failed. Response is null");
                    }
                } catch (Exception e) {
                    Log.e(LOG, "onMessage Failed", e);
                    webSocketListener.onError("Failed to unpack server response: "+ e.toString());
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e(LOG, "#### Hello " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.e(LOG, "###### What?????", e);
            }
        };
        webSocketClient.connect();
    }

    public static void closeSession() {
        webSocketClient.close();
    }


}
