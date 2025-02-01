package DAO;

import static org.mockito.Mockito.lenient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
    /**
     * insert message
     */
    public static boolean createMessageDAO(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message(message_text, posted_by, time_posted_epoch) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,message.getMessage_text());
            preparedStatement.setInt(2, message.getPosted_by());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            int affected = preparedStatement.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
    }
    /**
     * Get All Messages
     */
    public static List<Message> getAllMessagesDAO() {
        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int messageID = resultSet.getInt("message_id");
                int postedby = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");
                
                Message message = new Message(messageID, postedby, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * get message by ID
     */
    public static Message getMessagByIdDAO(int messageId) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("message_id");
                String messageText = resultSet.getString("message_text");
                int posted_by = resultSet.getInt("posted_by");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");

                return new Message(id,posted_by , messageText, timePostedEpoch);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Delete message using ID
     */
    public static boolean deleteMessageByIdDAO(int messageId) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);
            
            int affected = preparedStatement.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update Message
     */
    public static Message updateMessageDAO(int messageId, String newMessage) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement prepareStatement = connection.prepareStatement(sql);

            prepareStatement.setString(1, newMessage);
            prepareStatement.setInt(2, messageId);

            int updated = prepareStatement.executeUpdate();
            if (updated > 0) {
                return getMessagByIdDAO(messageId);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all messages from user
     */
    public static List<Message> getAllMessagesByAccIdDAO(int accountId) {
        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * from message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("message_id");
                String messageText = resultSet.getString("message_text");
                int posted_by = resultSet.getInt("posted_by");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");

                Message message = new Message(id, posted_by, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
