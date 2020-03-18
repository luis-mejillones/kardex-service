package services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import model.events.KardexEvent;
import play.Logger;
import util.Constants;

import javax.inject.Inject;

public class Mediator {

    private final KardexService kardexService;

    @Inject
    public Mediator(final KardexService kardexService) throws Exception {
        this.kardexService = kardexService;
        Logger.info(">>> MediatorMessageReceiver is starting...");
        this.setup();
    }

    private void setup() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.QUEUE_HOST);
        factory.setPort(Constants.QUEUE_PORT);
        factory.setUsername(Constants.QUEUE_USER_NAME);
        factory.setPassword(Constants.QUEUE_PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(Constants.EVENTS_QUEUE, false, false, false, null);
        Logger.info(">>> Event Receiver waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            Logger.info("<<< Received '" + message + "'");
            this.process(message);
        };

        channel.basicConsume(Constants.EVENTS_QUEUE, true, deliverCallback, consumerTag -> { });
    }

    private void process(String msg) {
        KardexEvent event = new KardexEvent();
        try {
            event.fromString(msg);
            switch (event.getEventType()) {
                case KARDEX_UPDATE:
                    this.kardexService.kardexUpdate(event.getContent());
                    break;
                default:
                    Logger.warn(">>> Unknown event type: '" + event.getEventType() + "'");
            }
        } catch (Exception e) {
            Logger.error(">>> Error parsing received event: '" + msg + "' " + e.getMessage());

            return;
        }
    }
}
