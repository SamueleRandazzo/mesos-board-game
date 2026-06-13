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
 * by fetching data from local JSON configuration files. It mimics the physical card layout.
 */
public class LocalCardDictionary {

    private static LocalCardDictionary instance;
    private final Map<String, String> cardDescriptions;
    private final Map<String, String> cardImagePaths;

    /** Template map defining how JSON keys should be formatted into printable strings. */
    private static final Map<String, String> DESCRIPTION_TEMPLATE = new LinkedHashMap<>();
    static {
        // Clean formatting simulating a physical card layout
        DESCRIPTION_TEMPLATE.put("foodCost", " | Cost: %s Food");
        DESCRIPTION_TEMPLATE.put("prestigePoints", " | %s PP");
        DESCRIPTION_TEMPLATE.put("buildingDiscount", " | -%s Build Cost");
        DESCRIPTION_TEMPLATE.put("inventionIcon", " | %s");
        DESCRIPTION_TEMPLATE.put("shamanStars", " | %s Stars");

        // Event specific formatting
        DESCRIPTION_TEMPLATE.put("prestigePerHunter", " | +%s PP per Hunter");
        DESCRIPTION_TEMPLATE.put("prestigeLossPerUnfed", " | -%s PP per Unfed");
        DESCRIPTION_TEMPLATE.put("majorityPrestigeGain", " | Majority: +%s PP");
        DESCRIPTION_TEMPLATE.put("minorityPrestigeLoss", " | Minority: -%s PP");
        DESCRIPTION_TEMPLATE.put("minArtists", " | Req. %s Artists:");
        DESCRIPTION_TEMPLATE.put("prestigeLossIfBelow", " -%s PP if below");
        DESCRIPTION_TEMPLATE.put("prestigePerArtistIfAbove", " +%s PP per Artist");

        // Building specific effects
        DESCRIPTION_TEMPLATE.put("effectType", " | Effect: %s");
    }

    /** Translator map replacing raw internal JSON constants with user-friendly text. */
    private static final Map<String, String> VALUE_TRANSLATOR = new HashMap<>();
    static {
        // Invention Icons
        VALUE_TRANSLATOR.put("BREAD", "Bread");
        VALUE_TRANSLATOR.put("LEATHER", "Leather");
        VALUE_TRANSLATOR.put("POTTERY", "Pottery");

        // Event Names (No "Event" prefix as requested)
        VALUE_TRANSLATOR.put("HUNT", "Hunt");
        VALUE_TRANSLATOR.put("SUSTENANCE", "Sustenance");
        VALUE_TRANSLATOR.put("SHAMANIC_RITUAL", "Shamanic Ritual");
        VALUE_TRANSLATOR.put("CAVE_PAINTINGS", "Cave Paintings");

        // Building Effects
        VALUE_TRANSLATOR.put("COMPLETE_SET_FOOD", "Complete Set Food Reward");
        VALUE_TRANSLATOR.put("DOUBLE_MAJORITY_REWARD", "Double Majority Reward");
        VALUE_TRANSLATOR.put("TURN_ORDER_BONUS", "Turn Order Bonus");
        VALUE_TRANSLATOR.put("IGNORE_MINORITY_PENALTY", "Ignore Minority Penalty");
        VALUE_TRANSLATOR.put("SUSTENANCE_DISCOUNT", "Sustenance Discount");
        VALUE_TRANSLATOR.put("INVENTOR_PAIR_FOOD", "Inventor Pair Food Reward");
        VALUE_TRANSLATOR.put("EXTRA_SHAMAN_ICONS", "Extra Shaman Icons");
    }

    private LocalCardDictionary() {
        cardDescriptions = new HashMap<>();
        cardImagePaths = new HashMap<>();

        loadDictionary();
        loadImagesDictionary();
    }

    /**
     * Retrieves the singleton instance of the dictionary.
     * @return the LocalCardDictionary instance.
     */
    public static LocalCardDictionary getInstance() {
        if (instance == null) {
            instance = new LocalCardDictionary();
        }
        return instance;
    }

    /**
     * Loads card data from the JSON files located in the resources folder.
     */
    private void loadDictionary() {
        ObjectMapper mapper = new ObjectMapper();
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

    /**
     * Translates JSON IDs into PNG file paths for graphical interfaces.
     */
    private void loadImagesDictionary() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("/cards/tribe_cards.json", "/images/Cards/Front/Card ");
        rules.put("/cards/buildings_era1.json", "/images/Houses/Front/House Age 1.");
        rules.put("/cards/buildings_era2.json", "/images/Houses/Front/House Age 2.");
        rules.put("/cards/buildings_era3.json", "/images/Houses/Front/House Age 3.");
        rules.put("/cards/event_cards.json", "/images/Events/Front/Event ");

        for (Map.Entry<String, String> rule : rules.entrySet()) {
            String jsonFilePath = rule.getKey();
            String imagePrefix = rule.getValue();

            try (InputStream is = getClass().getResourceAsStream(jsonFilePath)) {
                if (is != null) {
                    JsonNode root = mapper.readTree(is);
                    int imageIndex = 1;
                    for (JsonNode node : root) {
                        if (node.has("id")) {
                            String cardId = node.get("id").asText();
                            String fullImagePath = imagePrefix + imageIndex + ".png";
                            cardImagePaths.put(cardId, fullImagePath);
                            imageIndex++;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Impossible to translate images from: " + jsonFilePath);
            }
        }
    }

    /**
     * Retrieves the image path for a given card ID.
     * @param cardId the ID of the card.
     * @return the local file path to the card's image.
     */
    public String getImagePath(String cardId) {
        if (cardId == null) return "[Empty]";
        return cardImagePaths.get(cardId);
    }

    /**
     * Builds a comprehensive, formatted string describing a card based on its JSON properties.
     * @param node the JSON node representing the card.
     * @return the formatted description string.
     */
    private String buildDescription(JsonNode node) {
        String subtype = node.has("subtype") ? node.get("subtype").asText().toUpperCase() : "EVENT";
        StringBuilder desc = new StringBuilder();

        // 1. Header Generation
        if (subtype.equals("EVENT")) {
            // Events only show their name (e.g., "Hunt", "Sustenance")
            if (node.has("effectType")) {
                desc.append(VALUE_TRANSLATOR.getOrDefault(node.get("effectType").asText(), node.get("effectType").asText()));
            } else {
                desc.append("Event");
            }
        } else if (subtype.equals("INVENTOR")) {
            // Inventors show type first (e.g., "Leather Inventor")
            String icon = node.has("inventionIcon") ? node.get("inventionIcon").asText() : "";
            desc.append(VALUE_TRANSLATOR.getOrDefault(icon, icon)).append(" Inventor");
        } else {
            // Check if a dedicated custom title field exists in the JSON node
            if (node.has("title")) {
                desc.append(node.get("title").asText());
            } else {
                // Default fallback to raw subtype
                desc.append(subtype);
            }
        }

        // 2. Body Generation (Properties)
        DESCRIPTION_TEMPLATE.forEach((key, format) -> {
            // Prevent duplication since these are already handled in the header
            if (subtype.equals("INVENTOR") && key.equals("inventionIcon")) return;
            if (subtype.equals("EVENT") && key.equals("effectType")) return;

            if (node.has(key)) {
                String rawValue = node.get(key).asText();
                String translatedValue = VALUE_TRANSLATOR.getOrDefault(rawValue, rawValue);
                desc.append(String.format(format, translatedValue));
            }
        });

        // 3. Immediate Food Bonus Check
        if (node.has("immediateFood") && node.get("immediateFood").asInt() == 1) {
            desc.append(" | +1 Food");
        }

        return desc.toString().trim();
    }

    /**
     * Fetches the human-readable details of a card given its ID.
     * @param cardId the internal server ID of the card.
     * @return the formatted details string, or the raw ID if not found.
     */
    public String getCardDetails(String cardId) {
        if (cardId == null) return "[Empty]";
        return cardDescriptions.getOrDefault(cardId, "ID: " + cardId);
    }
}