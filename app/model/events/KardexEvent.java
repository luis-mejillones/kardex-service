package model.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.io.IOException;

public class KardexEvent {
    private EventType eventType;
    private String content;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonIgnore
    public String toString() {
        JsonNode content =  Json.toJson(this);

        return content.toString();
    }

    @JsonIgnore
    public void fromString(String msg) throws IOException {
        JsonNode json = Json.mapper().readTree(msg);
        KardexEvent kardexEvent = Json.mapper().treeToValue(json, KardexEvent.class);
        this.eventType = kardexEvent.eventType;
        this.content = kardexEvent.content;
    }
}
