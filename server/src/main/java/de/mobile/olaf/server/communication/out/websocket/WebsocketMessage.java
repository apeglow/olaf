package de.mobile.olaf.server.communication.out.websocket;

import com.google.gson.Gson;


public class WebsocketMessage {

    private String type;

    private String header;

    private String body;

    public WebsocketMessage() {}

    public static String marshallToJson(WebsocketMessage message) {
        return new Gson().toJson(message);
    }

    public static WebsocketMessage unmarshallFromJson(String message) {
        return new Gson().fromJson(message, WebsocketMessage.class);
    }

    // ----------------------------

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "WebsocketMessage [type=" + type + ", header=" + header + ", body=" + body + "]";
    }

}
