package pl.jakobczyk.gps.tracker.consumer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class JsonMapper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .writer().withDefaultPrettyPrinter();


    public static String mapGpsSignalsToJson(List<GpsSignal> gpsSignals) {
        try {
            return OBJECT_WRITER.writeValueAsString(gpsSignals);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse gpsSignals to JSON", e);
        }

    }

    public static GpsSignal mapToGpsSignalDto(String json) {
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(json);
            return parseGpsSignal(rootNode);
        } catch (JsonProcessingException | IllegalStateException e) {
            throw new IllegalArgumentException("Failed to parse JSON: " + json, e);
        }
    }

    private static GpsSignal parseGpsSignal(JsonNode rootNode) {
            UUID deviceUuid = UUID.fromString(getRequiredField(rootNode, "uuid").asText());
            GpsSignal.DeviceStatus status = GpsSignal.DeviceStatus.valueOf(getRequiredField(rootNode, "status").asText());
            JsonNode coordsNode = getRequiredField(rootNode, "coords");
            double latitude = getRequiredField(coordsNode, "latitude").asDouble();
            double longitude = getRequiredField(coordsNode, "longitude").asDouble();
            LocalDateTime timestamp = parseTimestamp(getRequiredField(rootNode, "timestamp").asText());

            return new GpsSignal(deviceUuid, latitude, longitude, timestamp, status);
    }

    private static JsonNode getRequiredField(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field == null || field.isNull()) {
            throw new IllegalArgumentException("Missing required field: " + fieldName);
        }
        return field;
    }

    private static LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timestamp, e);
        }
    }
}
