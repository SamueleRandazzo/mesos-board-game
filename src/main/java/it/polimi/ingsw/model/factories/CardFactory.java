package it.polimi.ingsw.model.factories;

import it.polimi.ingsw.model.BuildingCards.CardAddedBuilding;
import it.polimi.ingsw.model.BuildingCards.CavePaintingBuilding;
import it.polimi.ingsw.model.BuildingCards.HuntBuilding;
import it.polimi.ingsw.model.BuildingCards.InstantEffectBuilding;
import it.polimi.ingsw.model.BuildingCards.ScoringBuilding;
import it.polimi.ingsw.model.BuildingCards.SustenanceBuilding;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.CharacterCards.Builder;
import it.polimi.ingsw.model.CharacterCards.Gatherer;
import it.polimi.ingsw.model.CharacterCards.Hunter;
import it.polimi.ingsw.model.CharacterCards.Inventor;
import it.polimi.ingsw.model.CharacterCards.Shaman;
import it.polimi.ingsw.model.CharacterTypeCounts.ArtistsCount;
import it.polimi.ingsw.model.CharacterTypeCounts.BuildersCount;
import it.polimi.ingsw.model.CharacterTypeCounts.GatherersCount;
import it.polimi.ingsw.model.CharacterTypeCounts.HuntersCount;
import it.polimi.ingsw.model.CharacterTypeCounts.InventorsCount;
import it.polimi.ingsw.model.CharacterTypeCounts.ShamansCount;
import it.polimi.ingsw.model.Enum.InventionIcon;
import it.polimi.ingsw.model.EventEffects.CavePaintings;
import it.polimi.ingsw.model.EventEffects.Hunt;
import it.polimi.ingsw.model.EventEffects.ShamanicRitual;
import it.polimi.ingsw.model.EventEffects.Sustenance;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class CardFactory {

    public TribeDeck createTribeCard(RawTribeCardData data) {
        if (data == null) {
            throw new IllegalArgumentException("RawTribeCardData cannot be null.");
        }

        if (data.subtype == null) {
            throw new IllegalArgumentException("Card subtype cannot be null.");
        }

        boolean isObtainable = true;

        switch (data.subtype.toLowerCase()) {
            case "hunter":
                boolean hasFoodIcon = data.immediateFood != null && data.immediateFood > 0;
                return new Hunter(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        hasFoodIcon
                );

            case "builder":
                int discount = data.buildingDiscount != null ? data.buildingDiscount : 0;
                int prestigePoints = data.prestigePoints;

                return new Builder(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        discount,
                        prestigePoints
                );

            case "gatherer":
                return new Gatherer(
                        data.era,
                        data.minPlayers,
                        isObtainable
                );

            case "artist":
                return new Artist(
                        data.era,
                        data.minPlayers,
                        isObtainable
                );

            case "inventor":
                InventionIcon icon = null;
                if (data.inventionIcon != null) {
                    icon = InventionIcon.valueOf(data.inventionIcon.toUpperCase());
                }

                return new Inventor(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        icon
                );

            case "shaman":
                int symbols = data.shamanStars != null ? data.shamanStars : 1;

                return new Shaman(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        symbols
                );

            default:
                throw new IllegalArgumentException("Unknown subtype: " + data.subtype);
        }
    }

    public EventCard createEventCard(RawEventCardData data) {
        if (data == null) {
            throw new IllegalArgumentException("RawEventCardData cannot be null.");
        }

        if (data.effectType == null) {
            throw new IllegalArgumentException("Event effectType cannot be null.");
        }

        EventEffect effect;

        switch (data.effectType.toUpperCase()) {
            case "HUNT":
                if (data.prestigePerHunter == null) {
                    throw new IllegalArgumentException("HUNT requires prestigePerHunter.");
                }
                effect = new Hunt(data.prestigePerHunter);
                break;

            case "SUSTENANCE":
                if (data.prestigeLossPerUnfed == null) {
                    throw new IllegalArgumentException("SUSTENANCE requires prestigeLossPerUnfed.");
                }
                effect = new Sustenance(data.prestigeLossPerUnfed);
                break;

            case "SHAMANIC_RITUAL":
                if (data.majorityPrestigeGain == null || data.minorityPrestigeLoss == null) {
                    throw new IllegalArgumentException("SHAMANIC_RITUAL requires majorityPrestigeGain and minorityPrestigeLoss.");
                }
                effect = new ShamanicRitual(
                        data.majorityPrestigeGain,
                        data.minorityPrestigeLoss
                );
                break;

            case "CAVE_PAINTINGS":
                if (data.minArtists == null || data.prestigeLossIfBelow == null || data.prestigePerArtistIfAbove == null) {
                    throw new IllegalArgumentException("CAVE_PAINTINGS requires minArtists, prestigeLossIfBelow and prestigePerArtistIfAbove.");
                }
                effect = new CavePaintings(
                        data.minArtists,
                        data.prestigeLossIfBelow,
                        data.prestigePerArtistIfAbove
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown event type: " + data.effectType);
        }

        return new EventCard(
                data.era,
                data.minPlayers,
                data.isFinal,
                effect
        );
    }

    public BuildingCard createBuildingCard(RawBuildingCardData data) {
        if (data == null) {
            throw new IllegalArgumentException("RawBuildingCardData cannot be null.");
        }

        if (data.subtype == null) {
            throw new IllegalArgumentException("Building subtype cannot be null.");
        }

        boolean isObtainable = true;
        int foodCost = data.foodCost != null ? data.foodCost : 0;
        int prestigePoints = data.prestigePoints != null ? data.prestigePoints : 0;

        switch (data.subtype.toLowerCase()) {
            case "hunt":
                return new HuntBuilding(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        foodCost,
                        prestigePoints,
                        data.extraFood != null ? data.extraFood : 0,
                        data.extraPoints != null ? data.extraPoints : 0,
                        resolveCountType(data.countType)
                );

            case "sustenance":
                return new SustenanceBuilding(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        foodCost,
                        prestigePoints,
                        data.foodBonus != null ? data.foodBonus : 0,
                        resolveCountType(data.countType)
                );

            case "scoring":
                return new ScoringBuilding(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        foodCost,
                        prestigePoints,
                        data.fixedPoints != null ? data.fixedPoints : 0,
                        data.multiplier != null ? data.multiplier : 0,
                        data.pointsPerUnit != null ? data.pointsPerUnit : 0,
                        data.setDim != null ? data.setDim : 0,
                        resolveCountType(data.countType)
                );

            case "cave_painting":
                return new CavePaintingBuilding(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        foodCost,
                        prestigePoints,
                        data.extraFood != null ? data.extraFood : 0,
                        resolveCountType(data.countType)
                );

            case "instant":
                return new InstantEffectBuilding(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        foodCost,
                        prestigePoints,
                        data.extraStars != null ? data.extraStars : 0,
                        data.preventLoss != null && data.preventLoss,
                        data.doubleOnWinning != null && data.doubleOnWinning,
                        data.extraCardFromUpper != null && data.extraCardFromUpper,
                        data.extraFoodFromBonus != null && data.extraFoodFromBonus
                );

            case "card_added":
                return new CardAddedBuilding(
                        data.era,
                        data.minPlayers,
                        isObtainable,
                        foodCost,
                        prestigePoints,
                        data.bonusOnDuplicateInventor != null && data.bonusOnDuplicateInventor,
                        data.bonusOnSetCharacters != null && data.bonusOnSetCharacters,
                        data.foodBonus != null ? data.foodBonus : 0,
                        data.setDim != null ? data.setDim : 0
                );

            default:
                throw new IllegalArgumentException("Unknown building subtype: " + data.subtype);
        }
    }

    private CharacterTypeCount resolveCountType(String countType) {
        if (countType == null) {
            return null;
        }

        switch (countType.toUpperCase()) {
            case "BUILDERS_COUNT":
                return new BuildersCount();

            case "HUNTERS_COUNT":
                return new HuntersCount();

            case "GATHERERS_COUNT":
                return new GatherersCount();

            case "ARTISTS_COUNT":
                return new ArtistsCount();

            case "SHAMANS_COUNT":
                return new ShamansCount();

            case "INVENTORS_COUNT":
                return new InventorsCount();

            default:
                throw new IllegalArgumentException("Unknown countType: " + countType);
        }
    }
}