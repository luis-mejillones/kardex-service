package services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.events.KardexEvent;
import play.Logger;
import util.Constants;

import static util.Constants.QUEUE_HOST;
import static util.Constants.QUEUE_PASSWORD;
import static util.Constants.QUEUE_PORT;
import static util.Constants.QUEUE_USER_NAME;

public class EventSender {
    private ConnectionFactory factory;

    public EventSender() throws Exception {
        Logger.info(">>> EventSender service is starting...");
        this.setup();
    }

    private void setup() {
        this.factory = new ConnectionFactory();
        factory.setHost(QUEUE_HOST);
        factory.setPort(QUEUE_PORT);
        factory.setUsername(QUEUE_USER_NAME);
        factory.setPassword(QUEUE_PASSWORD);
    }

    public void send(KardexEvent event) {
        String msg = event.toString();
        try {
            try (Connection connection = this.factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(Constants.EVENTS_QUEUE, false, false, false, null);
                channel.basicPublish("", Constants.EVENTS_QUEUE, null, msg.getBytes("UTF-8"));

                Logger.info(">>> Sent '" + msg + "'");
            }
        } catch (Exception e) {
            Logger.error(">>> Error: " + e.getMessage());
        }
    }
}
