package Applications;

import Chat.Chat;
import Discovery.RendezvousServer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class ChatGUIController {

    Chat chat;

    public ChatGUIController() {
        chat = new Chat();
        displayJoinServerPrompt();
    }

    @FXML
    private VBox onlineUsers;

    @FXML
    private TextArea chatBox;

    @FXML TabPane connectedPeerTabs;

    private Map<String, ScrollPane> peerChatBox = new HashMap<>();

    @FXML
    protected void handleChatBoxSubmit(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && chatBox.getText().trim().length() > 0) {
            // Send the message to the peer.
            String peerToSendTo = connectedPeerTabs.getSelectionModel().getSelectedItem().getText();
            String message = chatBox.getText();
            chat.sendMessage(peerToSendTo, message);

            // Add the message to our vbox.
            this.addMessageToPeerTab(peerToSendTo, this.chat.getPeerName(), message);

            // Clear the chat box.
            chatBox.setText("");
        }

    }

    @FXML
    protected void handleRefreshButton() {
        try {
            Set<String> onlinePeers = chat.getKnownPeers();
            onlineUsers.getChildren().clear();

            for (String peerName : onlinePeers) {
                Label peerNameLabel = new Label(peerName);
                peerNameLabel.setFont(new Font(18));
                peerNameLabel.setCursor(Cursor.HAND);
                peerNameLabel.setOnMouseClicked(event -> {
                    try {
                        chat.connectToPeer(peerName);

                        // Create the new Tab
                        Tab chatTab = new Tab(peerName);
                        AnchorPane chatTabAnchor = new AnchorPane();
                        ScrollPane chatTabScroll = new ScrollPane();
                        VBox chatTextContainer = new VBox();
                        chatTabScroll.setContent(chatTextContainer);
                        chatTabAnchor.getChildren().add(chatTabScroll);
                        chatTab.setContent(chatTabAnchor);
                        connectedPeerTabs.getTabs().add(chatTab);

                        // Style
                        AnchorPane.setBottomAnchor(chatTabScroll, 0.0);
                        AnchorPane.setLeftAnchor(chatTabScroll, 0.0);
                        AnchorPane.setRightAnchor(chatTabScroll, 0.0);
                        AnchorPane.setTopAnchor(chatTabScroll, 0.0);
                        chatTabScroll.setFitToHeight(true);
                        chatTabScroll.setFitToWidth(true);
                        chatTabScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                        chatTabScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

                        // Automatically scroll when new messages are added.
                        chatTextContainer.heightProperty().addListener(observable -> chatTabScroll.setVvalue(1D));

                        // Save the ScrollPane for later to put new messages in.
                        peerChatBox.put(peerName, chatTabScroll);
                    } catch (Exception e) {
                        alertUserError(e.getMessage());
                    }
                });
                onlineUsers.getChildren().add(peerNameLabel);
            }
        } catch (Exception e) {
            alertUserError(e.getMessage());
        }
    }

    @FXML
    protected void displayJoinServerPrompt() {
        this.displayJoinServerPrompt(null);
    }

    @FXML
    protected void displayJoinServerPrompt(String message) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Server Info");
        dialog.setHeaderText("Enter server info and your desired username to obtain Peer list.");

        if (message != null) {
            // Some other message was provided, display it too.
            dialog.setHeaderText(dialog.getHeaderText() + "\n" + message);
        }

        dialog.setContentText("BAD BOY");
        dialog.setResizable(true);

        Label addressLabel = new Label("Address: ");
        Label portLabel = new Label("Port: ");
        Label peerNameLabel = new Label("Username: ");
        TextField addressText = new TextField();
        TextField portText = new TextField(Integer.toString(RendezvousServer.DEFAULT_PORT));
        TextField peerNameText = new TextField();

        GridPane grid = new GridPane();
        grid.add(addressLabel, 1, 1);
        grid.add(addressText, 2, 1);
        grid.add(portLabel, 1, 2);
        grid.add(portText, 2, 2);
        grid.add(peerNameLabel, 1, 3);
        grid.add(peerNameText, 2, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.setResultConverter(button -> {

            if (button.equals(ButtonType.OK)) {
                List<String> result = new ArrayList<>();
                result.add(addressText.getText());
                result.add(portText.getText());
                result.add(peerNameText.getText());
                return result;
            }

            return null;
        });

        Optional<List<String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                InetAddress rendezvousServerAddress = InetAddress.getByName(result.get().get(0));
                int rendezvousServerPort =  Integer.parseInt(result.get().get(1));
                String peerName = result.get().get(2);
                chat.joinRendezvous(rendezvousServerAddress, rendezvousServerPort, peerName);
            } catch (IOException ex) {
                displayJoinServerPrompt(ex.getMessage());
            }
        }
    }

    protected void addMessageToPeerTab(String peerTabName, String sendingPeer, String message) {
        Label messageLabel = new Label(sendingPeer + ": " + message);
        messageLabel.setWrapText(true);
        messageLabel.setFont(new Font(20));
        ScrollPane chatTabScroll = peerChatBox.get(peerTabName);
        VBox chatTextContainer = (VBox) chatTabScroll.getContent();
        chatTextContainer.getChildren().add(messageLabel);
    }

    protected void alertUserError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
        alert.show();
    }
}
