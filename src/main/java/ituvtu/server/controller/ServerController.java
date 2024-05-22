package ituvtu.server.controller;

import ituvtu.server.chat.ChatDisplayData;
import ituvtu.server.database.DatabaseManager;
import ituvtu.server.util.UIFactory;
import ituvtu.server.xml.auth.*;
import ituvtu.server.xml.chat.ChatRequest;
import ituvtu.server.xml.message.Message;
import ituvtu.server.xml.*;
import ituvtu.server.xml.message.MessagesResponse;
import jakarta.xml.bind.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.java_websocket.WebSocket;
import ituvtu.server.model.*;
import java.io.*;
import java.time.*;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class ServerController implements IServerObserver {
    private static ServerController instance;

    @FXML
    private ListView<ChatDisplayData> chatListView;
    @FXML
    private VBox messagesArea;
    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private VBox logMessagesArea;
    private IServer server;
    private int currentChatId = -1;
    private LocalDate currentDisplayedDate = null;
    private String usernameFirst;
    private String usernameSecond;

    private ServerController(IServer server) {
        this.server = server;
    }

    @FXML
    public void initialize() {
        chatListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChatDisplayData> call(ListView<ChatDisplayData> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatDisplayData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });

        String stylesheet = Objects.requireNonNull(getClass().getResource("/ituvtu/server/server-styles.css")).toExternalForm();
        logScrollPane.getStylesheets().add(stylesheet);
        chatListView.getStylesheets().add(stylesheet);
        messageScrollPane.getStylesheets().add(stylesheet);
        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentChatId = newSelection.chatId();
                usernameFirst = newSelection.usernameFirst();
                usernameSecond = newSelection.usernameSecond();
                loadMessagesForChat(newSelection.chatId());
            } else {
                currentChatId = -1;
                usernameFirst = null;
                usernameSecond = null;
            }
        });

        messageScrollPane.setFitToWidth(true);
        logScrollPane.setFitToWidth(true);
    }

    private void loadMessagesForChat(int chatId) {
        messagesArea.getChildren().clear();
        processMessagesResponse(server.processServerGetMessagesRequest(chatId));
    }

    private void processMessagesResponse(String xmlMessage) {
        try {
            JAXBContext context = JAXBContext.newInstance(MessagesResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlMessage);
            MessagesResponse response = (MessagesResponse) unmarshaller.unmarshal(reader);
            updateMessagesArea(response.getMessages());
        } catch (Exception e) {
            displayLogMessage("Error parsing messages: " + e.getMessage(), "tan");
        }
    }

    private void updateMessagesArea(List<Message> messages) {
        Platform.runLater(() -> {
            messagesArea.getChildren().clear();
            currentDisplayedDate = null;

            if (messages != null) {
                for (Message message : messages) {
                    displayMessage(message);
                }
            }
        });
    }

    public void displayMessage(Message message) {
        Platform.runLater(() -> {
            if (isCurrentChat(message.getChatId())) {
                LocalDateTime timestamp = message.getTimestamp();
                LocalDate messageDate = timestamp.toLocalDate();

                addDateLabelIfNecessary(messageDate);
                addMessageBox(message, timestamp);
            }
        });
    }

    private void addDateLabelIfNecessary(LocalDate messageDate) {
        if (currentDisplayedDate == null || !currentDisplayedDate.equals(messageDate)) {
            currentDisplayedDate = messageDate;
            String formattedDate = UIFactory.formatDate(messageDate);
            Label dateLabel = UIFactory.createDateLabel(formattedDate);
            HBox dateBox = UIFactory.createDateBox(dateLabel);
            messagesArea.getChildren().add(dateBox);
        }
    }

    private void addMessageBox(Message message, LocalDateTime timestamp) {
        VBox messageBox = UIFactory.createMessageBox(message, timestamp, usernameFirst);
        messagesArea.getChildren().add(messageBox);
    }

    public static synchronized ServerController getInstance(IServer server) {
        if (instance == null) {
            instance = new ServerController(server);
        }
        return instance;
    }

    public void addChatToList(ChatDisplayData chatInfo) {
        Platform.runLater(() -> chatListView.getItems().add(chatInfo));
    }

    public void setServer(IServer server) {
        this.server = server;
    }

    @Override
    public void updateChatList(List<ChatDisplayData> chats) {
        Platform.runLater(() -> {
            chatListView.getItems().clear();
            chatListView.getItems().addAll(chats);
        });
    }

    public void setChatList(List<ChatDisplayData> chats) {
        Platform.runLater(() -> {
            chatListView.getItems().clear();
            chatListView.getItems().addAll(chats);
        });
    }

    @Override
    public void onMessage(WebSocket conn, String input) throws JAXBException {
        if (input.contains("<userConnectionInfo>")) {
            handleUserConnectionInfo(conn, input);
        } else if (input.contains("<message>")) {
            handleMessage(conn, input);
        } else if (input.contains("<chatRequest>")) {
            handleChatRequest(conn, input);
        } else if (input.contains("<authRequest>")) {
            handleAuthRequest(conn, input);
        } else {
            conn.send("Unsupported input format");
        }
    }

    void handleAuthRequest(WebSocket conn, String input) {
        try {
            JAXBContext context = JAXBContext.newInstance(AuthRequest.class, AuthResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(input);
            AuthRequest authRequest = (AuthRequest) unmarshaller.unmarshal(reader);
            boolean authenticated = DatabaseManager.checkOrCreateUser(authRequest.getUsername(), authRequest.getPassword());
            System.out.println(authenticated);
            AuthResponse authResponse = new AuthResponse(authenticated, authRequest.getUsername());
            String authResponseXml = XMLUtil.toXML(authResponse);
            System.out.println(authResponseXml);
            conn.send(authResponseXml);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void handleChatRequest(WebSocket conn, String input) throws JAXBException {
        ChatRequest chatRequest = XMLUtil.fromXML(input, ChatRequest.class);
        switch (chatRequest.getAction()) {
            case "createChat":
                processChatCreationRequest(conn, chatRequest);
                break;
            case "updateChat":
                processChatUpdateRequest(conn, chatRequest);
                break;
            case "deleteChat":
                processChatDeletionRequest(conn, chatRequest);
                break;
            case "getChats":
                processGetChatsRequest(conn, chatRequest);
                break;
            case "getMessages":
                processGetMessagesRequest(conn, chatRequest);
                break;
            default:
                conn.send("Unsupported chat request action: " + chatRequest.getAction());
                break;
        }
    }

    private void processGetMessagesRequest(WebSocket conn, ChatRequest cR) {
        server.processGetMessagesRequest(conn, cR);
    }

    private void processGetChatsRequest(WebSocket conn, ChatRequest cR) {
        server.processGetChatsRequest(conn, cR);
    }

    private void processChatDeletionRequest(WebSocket conn, ChatRequest cR) {
        server.processChatDeletionRequest(conn, cR);
    }

    private void processChatUpdateRequest(WebSocket conn, ChatRequest cR) {
        server.processChatUpdateRequest(conn, cR);
    }

    private void processChatCreationRequest(WebSocket conn, ChatRequest cR) {
        server.processChatCreationRequest(conn, cR);
    }

    private void handleMessage(WebSocket conn, String input) {
        try {
            Message msg = XMLUtil.fromXML(input, Message.class);
            String logMessage = "Message from " + msg.getFrom() + " to " + msg.getTo() + ": " + msg.getContent();
            server.notifyObserversWithMessage(msg);
            server.sendDirectMessage(msg);
            server.recordMessageInDatabase(msg);
        } catch (JAXBException e) {
            System.err.println("Error parsing XML: " + e.getMessage());
            conn.send("Error processing your XML input");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            conn.send("An unexpected error occurred");
        }
    }

    public boolean isCurrentChat(int chatId) {
        return chatId == currentChatId;
    }

    private void handleUserConnectionInfo(WebSocket conn, String input) throws JAXBException {
        UserConnectionInfo info = XMLUtil.fromXML(input, UserConnectionInfo.class);
        server.updateDatabase(info.getUsername(), server.getPortConn(conn));
    }

    @Override
    public void displayLogMessage(String text, String styleClass) {
        Platform.runLater(() -> {
            Label logLabel = new Label(text);
            logLabel.getStyleClass().addAll("log-message", styleClass);
            logMessagesArea.getChildren().add(logLabel);
        });
    }
}