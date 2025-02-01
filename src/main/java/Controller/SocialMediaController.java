package Controller;

import static org.mockito.ArgumentMatchers.booleanThat;

import java.util.List;

import org.eclipse.jetty.jndi.java.javaNameParser;
import org.json.JSONArray;
import org.json.JSONObject;

import DAO.MessageDAO;
import DAO.UserDAO;
import Service.UserService;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
import netscape.javascript.JSObject;
import Model.Account;
import Model.Message;
import Service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
    private UserService userService;
    private MessageService messageService;

    public SocialMediaController() {
        this.userService = new UserService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIDHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIDHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIDHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByAccIdHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * 
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     */
    private void registerHandler(Context context) {
        try {
            JSONObject reqBody = new JSONObject(context.body());
            String username = reqBody.optString("username", "").trim();
            String password = reqBody.optString("password", "");

            Account newUser = userService.registerService(username, password);

            if (newUser == null) {
                context.status(400);
            } else {
                JSONObject responseJson = new JSONObject();
                responseJson.put("account_id", newUser.getAccount_id());
                responseJson.put("username", username);
                responseJson.put("password", password);

                context.status(200).json(responseJson.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.status(500);
        }
    }

    private void loginHandler(Context context) {
        try {
            JSONObject reqBody = new JSONObject(context.body());
            String username = reqBody.optString("username", "").trim();
            String password = reqBody.optString("password", "");

            int userID = UserService.loginService(username, password);

            if (userID >= 0) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("account_id", userID);
                responseJson.put("username", username);
                responseJson.put("password", password);

                context.status(200).json(responseJson.toString());
            } else {
                context.status(401);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMessageHandler(Context context) {
        try {
        
            JSONObject requestBody = new JSONObject(context.body());
            String messageText = requestBody.optString("message_text");
            int postedBy = requestBody.optInt("posted_by");

            if (messageText == null || messageText.trim().isEmpty() || messageText.length() > 255 || postedBy <= 0) {
                context.status(400); 
                return;
            }

            long timePostedEpoch = System.currentTimeMillis() / 1000;
            Message message = new Message(postedBy,messageText,timePostedEpoch);

            boolean isCreated = MessageDAO.createMessageDAO(message);

            if (isCreated) {
                context.status(200).json(message);
            } else {
                context.status(400);
            }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void getAllMessagesHandler(Context context) {
        List<Message> messages = MessageService.getAllMessagesService();
        JSONArray responseArray = new JSONArray();

        for (Message message : messages) {
            JSONObject messageJson = new JSONObject();
            messageJson.put("message_id", message.getMessage_id());
            messageJson.put("posted_by", message.getPosted_by());
            messageJson.put("message_text", message.getMessage_text());
            messageJson.put("time_posted_epoch", message.getTime_posted_epoch());

            responseArray.put(messageJson);
        }

        context.status(200).json(responseArray.toString());
    }

    private void getMessageByIDHandler(Context context) {
        String messageIdString = context.pathParam("message_id");
        try {
            int messageId = Integer.parseInt(messageIdString);
            Message message = MessageService.getMessageByIdService(messageId);

            if (message != null) {
                JSONObject messageJson = new JSONObject();
                messageJson.put("message_id", message.getMessage_id());
                messageJson.put("posted_by", message.getPosted_by());
                messageJson.put("message_text", message.getMessage_text());
                messageJson.put("time_posted_epoch", message.getTime_posted_epoch());

                context.status(200).json(messageJson.toString());
            } else {
                context.status(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMessageByIDHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        try {
            Message deletedMessage = MessageService.deleteMessageByIdService(messageId);

            if (deletedMessage != null) {
                context.status(200).json(deletedMessage);
            } else {
                context.status(200).json("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMessageByIDHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        try {
            JSONObject reqBody = new JSONObject(context.body());
            String newMessage = reqBody.optString("message_text", "").trim();

            if (newMessage.isEmpty() || newMessage.length() > 255) {
                context.status(400);
                return;
            }

            Message updatedMessage = MessageService.updateMessageByIDService(messageId, newMessage);

            if (updatedMessage != null) {
                JSONObject messageJson = new JSONObject();
                messageJson.put("message_id", updatedMessage.getMessage_id());
                messageJson.put("posted_by", updatedMessage.getPosted_by());
                messageJson.put("message_text", updatedMessage.getMessage_text());
                messageJson.put("time_posted_epoch", updatedMessage.getTime_posted_epoch());

                context.status(200).json(messageJson.toString());
            } else {
                context.status(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllMessagesByAccIdHandler(Context context) {
        int accId = Integer.parseInt(context.pathParam("account_id"));
        try {
            List<Message> messages = MessageService.getAllMessagesByAccIdService(accId);

            JSONArray resArray = new JSONArray();
            for (Message message : messages) {
                JSONObject messageJson = new JSONObject();
                messageJson.put("message_id", message.getMessage_id());
                messageJson.put("posted_by", message.getPosted_by());
                messageJson.put("message_text", message.getMessage_text());
                messageJson.put("time_posted_epoch", message.getTime_posted_epoch());

                resArray.put(messageJson);
            }

            context.status(200).json(resArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}