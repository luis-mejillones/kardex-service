package services;

import model.Kardex;
import model.events.KardexEvent;
import model.events.EventType;
import play.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;

public class KardexService {
    private EventSender eventSender;

    @Inject
    public KardexService(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    public void create(Kardex kardex) {
        this.sendEvent(kardex);
    }

    private void sendEvent(Kardex kardex) {
        KardexEvent event = new KardexEvent();
        event.setEventType(EventType.KARDEX_UPDATE);
        event.setContent(kardex.toString());

        this.eventSender.send(event);
    }

    public void kardexUpdate(String event) throws IOException {
        Kardex kardex = new Kardex();
        kardex.fromString(event);
        kardex.date = ZonedDateTime.now();
        kardex.save();
        kardex.updatePrice();
        kardex.update();

        Logger.info("Product entry created with id: " + kardex.id);
    }
}
