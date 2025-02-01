package Service;

import DAO.MessageDAO;
import DAO.UserDAO;
import Model.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }
    
    /**
     * Creates new message
     */
    public static Message createMessageService(String messageText, int postedBy) {
        if (messageText == null || messageText.trim().isEmpty() || messageText.length() > 255) {
            return null;
        }
        Long timePostedEpoch = System.currentTimeMillis() / 1000;
        Message message = new Message(postedBy, messageText, timePostedEpoch);

        if (MessageDAO.createMessageDAO(message)) {
            return message;
        }
        return null;
    }
    /**
     * Get All Messages from database
     */
    public static List<Message> getAllMessagesService() {
        return MessageDAO.getAllMessagesDAO();
    }

    /**
     * get Message with ID
     */
    public static Message getMessageByIdService(int MessageId) {
        return MessageDAO.getMessagByIdDAO(MessageId);
    }

    /**
     * Delete Message With ID
     */
    public static Message deleteMessageByIdService(int messageId) {
        Message message = MessageDAO.getMessagByIdDAO(messageId);
        if (message != null) {
            MessageDAO.deleteMessageByIdDAO(messageId);
            return message;
        }
        return null;
    }

    /**
     * updateMessage with ID
     */
    public static Message updateMessageByIDService(int messageId, String newMessage) {
        if (messageId <= 0 || newMessage == null || newMessage.trim().isEmpty() || newMessage.length() > 255) {
            return null;
        }
        return MessageDAO.updateMessageDAO(messageId, newMessage);
    }

    /**
     * get all user given account ID
     */
    public static List<Message> getAllMessagesByAccIdService(int accountId) {
        if (accountId <= 0) {
            return Collections.emptyList();
        }
        return MessageDAO.getAllMessagesByAccIdDAO(accountId);
    }
}
