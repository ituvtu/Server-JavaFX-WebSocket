package ituvtu.server.controller;

import ituvtu.server.chat.ChatDisplayData;
import ituvtu.server.model.IServer;
import jakarta.xml.bind.JAXBException;
import org.java_websocket.WebSocket;

import java.util.List;

@SuppressWarnings("unused")
public interface IServerController {
    void setServer(IServer server);

    void updateChatList(List<ChatDisplayData> chats);

    void onMessage(WebSocket conn, String input) throws JAXBException;

    void displayLogMessage(String text, String styleClass);

    void clearObservers();
}
