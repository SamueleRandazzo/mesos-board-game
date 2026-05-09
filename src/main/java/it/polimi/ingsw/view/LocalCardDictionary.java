package it.polimi.ingsw.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Client-side Dictionary.
 * Converts card IDs received from the server's DTOs into human-readable descriptions
 * by fetching data from local JSON configuration files.
 */
public class LocalCardDictionary {

    private static LocalCardDictionary instance;
    private final Map<String, String> cardDescriptions;

    private LocalCardDictionary() {
        cardDescriptions = new HashMap<>();
        loadDictionary();
    }

    public static LocalCardDictionary getInstance() {
        if (instance == null) {
            instance = new LocalCardDictionary();
        }
        return instance;
    }

    /**
     * Loads the JSON files from the resources folder.
     */
    private void loadDictionary() {
        ObjectMapper mapper = new ObjectMapper();
        // These paths must match the location of your JSON files in the resources folder
        String[] jsonFiles = {
                "/tribe_cards.json",
                "/buildings_era1.json",
                "/buildings_era2.json",
                "/buildings_era3.json",
                "/event_cards.json"
        };

        for (String filePath : jsonFiles) {
            try (InputStream is = getClass().getResourceAsStream(filePath)) {
                if (is != null) {
                    JsonNode root = mapper.readTree(is);
                    for (JsonNode node : root) {
                        if (node.has("id")) {
                            String id = node.get("id").asText();
                            String description = buildDescription(node);
                            cardDescriptions.put(id, description);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Warning: Unable to load card data from " + filePath);
            }
        }
    }

    /**
     * Builds a summary string based on the card's JSON properties.
     * Extracts useful info to print on the CLI for the player.
     */
    private String buildDescription(JsonNode node) {
        String subtype = node.has("subtype") ? node.get("subtype").asText().toUpperCase() : "EVENT";
        StringBuilder desc = new StringBuilder(subtype);

        if (node.has("prestigePoints")) {
            desc.append(" (VP: ").append(node.get("prestigePoints").asText()).append(")");
        }
        if (node.has("foodCost")) {
            desc.append(" [Cost: ").append(node.get("foodCost").asText()).append(" Food]");
        }
        if (node.has("effectType")) {
            desc.append(" [Effect: ").append(node.get("effectType").asText()).append("]");
        }

        return desc.toString();
    }

    /**
     * Returns the formatted card details, or the raw ID if not found in the JSON.
     */
    public String getCardDetails(String cardId) {
        if (cardId == null) return "[Empty]";
        return cardDescriptions.getOrDefault(cardId, "ID: " + cardId);
    }
}