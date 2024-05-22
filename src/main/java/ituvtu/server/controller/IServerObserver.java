package ituvtu.server.controller;

import ituvtu.server.xml.message.Message;
import ituvtu.server.chat.*;
import jakarta.xml.bind.JAXBException;
import org.java_websocket.WebSocket;

import java.util.List;

@SuppressWarnings("unused")
public interface IServerObserver {
    void onMessage(WebSocket conn, String input) throws JAXBException;
    void updateChatList(List<ChatDisplayData> chats);
    void displayMessage(Message message);
    void displayLogMessage(String message, String styleClass);
}
