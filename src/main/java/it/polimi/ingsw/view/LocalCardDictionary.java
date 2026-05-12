package it.polimi.ingsw.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client-side Dictionary.
 * Converts card IDs received from the server's DTOs into human-readable descriptions
 * by fetching data from local JSON configuration files.
 */
public class LocalCardDictionary {

    private static LocalCardDictionary instance;
    private final Map<String, String> cardDescriptions;
    private final Map<String, String> cardImagePaths;

    private static final Map<String, String> DESCRIPTION_TEMPLATE = new LinkedHashMap<>();
    static {
        DESCRIPTION_TEMPLATE.put("prestigePoints", " (VP: %s)");
        DESCRIPTION_TEMPLATE.put("foodCost", " [Cost: %s Food]");
        DESCRIPTION_TEMPLATE.put("effectType", " [Effect: %s]");
        DESCRIPTION_TEMPLATE.put("buildingDiscount", " [Building Discount: %s]");
        DESCRIPTION_TEMPLATE.put("inventionIcon", " [Icon: %s]");
        DESCRIPTION_TEMPLATE.put("shamanStars", " [Stars: %s]");
        DESCRIPTION_TEMPLATE.put("prestigePerHunter", " [VP per Hunter: %s]");
        DESCRIPTION_TEMPLATE.put("prestigeLossPerUnfed", " [VP loss per Unfed: %s]");
        DESCRIPTION_TEMPLATE.put("majorityPrestigeGain", " [Majority VP gain: %s]");
        DESCRIPTION_TEMPLATE.put("minorityPrestigeLoss", " [Minority VP gain: %s]");
        DESCRIPTION_TEMPLATE.put("minArtists", " [Min artists for bonus: %s]");
        DESCRIPTION_TEMPLATE.put("prestigeLossIfBelow", " [VP loss if below: %s]");
        DESCRIPTION_TEMPLATE.put("prestigePerArtistIfAbove", " [VP gain per artists: %s]");
    }

    private LocalCardDictionary() {
        cardDescriptions = new HashMap<>();
        cardImagePaths = new HashMap<>();
        
        loadDictionary();
        loadImagesDictionary();
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
                "/cards/tribe_cards.json",
                "/cards/buildings_era1.json",
                "/cards/buildings_era2.json",
                "/cards/buildings_era3.json",
                "/cards/event_cards.json"
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
    
    //Translation from json to PNG in order to upload the images
    private void loadImagesDictionary() {
        
        ObjectMapper mapper = new ObjectMapper();
        
        //Rules to translate from Json to PNG
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("/cards/tribe_cards.json", "/images/Cards/Front/Card ");
        rules.put("/cards/buildings_era1.json", "/images/Houses/Front/House Age 1.");
        rules.put("/cards/buildings_era2.json", "/images/Houses/Front/House Age 2.");
        rules.put("/cards/buildings_era3.json", "/images/Houses/Front/House Age 3.");
        rules.put("/cards/event_cards.json", "/images/Events/Front/Event ");

        for (Map.Entry<String, String> rule : rules.entrySet()) {
            String jsonFilePath = rule.getKey();     // Es. "/cards/buildings_era1.json"
            String imagePrefix = rule.getValue();    // Es. "/images/Houses/Front/House Age 1."

            try (InputStream is = getClass().getResourceAsStream(jsonFilePath)) {
                if (is != null) {
                    JsonNode root = mapper.readTree(is);

                    int imageIndex = 1; // Name of files starts from one for each new JSON

                    for (JsonNode node : root) {
                        if (node.has("id")) {
                            String cardId = node.get("id").asText(); // Es. "building_3"
                            
                            String fullImagePath = imagePrefix + imageIndex + ".png";

                            //Save in the GUI Map
                            cardImagePaths.put(cardId, fullImagePath);

                            imageIndex++; // next card
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Impossible to translate images from: " + jsonFilePath);
            }
        }
        
    }

    public String getImagePath(String cardId) {
        if (cardId == null) return "[Empty]";
        return cardImagePaths.get(cardId);
    }

    /**
     * Builds a summary string based on the card's JSON properties.
     * Extracts useful info to print on the CLI for the player.
     */
    private String buildDescription(JsonNode node) {
        String subtype = node.has("subtype") ? node.get("subtype").asText().toUpperCase() : "EVENT";
        StringBuilder desc = new StringBuilder(subtype);

        DESCRIPTION_TEMPLATE.forEach((key, format) -> {
            if (node.has(key)) {
                desc.append(String.format(format, node.get(key).asText()));
            }
        });

        if (node.has("immediateFood") && node.get("immediateFood").asInt() == 1) {
            desc.append(" [Food Icon]");
        }

        return desc.toString().trim();
    }

    /**
     * Returns the formatted card details, or the raw ID if not found in the JSON.
     */
    public String getCardDetails(String cardId) {
        if (cardId == null) return "[Empty]";
        return cardDescriptions.getOrDefault(cardId, "ID: " + cardId);
    }


}