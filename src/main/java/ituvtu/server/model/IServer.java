// IServer.java
package ituvtu.server.model;

import ituvtu.server.controller.IServerObserver;
import ituvtu.server.xml.chat.ChatRequest;
import ituvtu.server.xml.message.Message;
import org.java_websocket.WebSocket;

@SuppressWarnings("unused")
public interface IServer {
   void addObserver(IServerObserver observer);
   void processGetMessagesRequest(WebSocket conn, ChatRequest chatRequest);
   void processGetChatsRequest(WebSocket conn, ChatRequest chatRequest);
   void processChatDeletionRequest(WebSocket conn, ChatRequest chatRequest);
   void processChatUpdateRequest(WebSocket conn, ChatRequest chatRequest);
   void processChatCreationRequest(WebSocket conn, ChatRequest chatRequest);
   String processServerGetMessagesRequest(int chatId);

   void notifyObserversWithMessage(Message msg);

   void sendDirectMessage(Message msg);

   void recordMessageInDatabase(Message msg);

   int getPortConn(WebSocket conn);

   void updateDatabase(String username, int portConn);
}
