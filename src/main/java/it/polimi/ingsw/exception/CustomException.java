package it.polimi.ingsw.exception;

import java.rmi.RemoteException;

/**
 * Container class for custom game network exception structures and text management utilities.
 * It provides custom exception subclasses mapping setup failures, alongside static validation methods.
 */
public class CustomException {

    /**
     * Extracts and cleans the inner user-friendly message text from a raw nested RMI {@link RemoteException}.
     * It strips out standard RMI tracking prefixes and returns only the final descriptive message segment.
     *
     * @param e the raw RemoteException caught from the network layer
     * @return a cleaned summary error description string, or "Unknown error" if no message is present
     */
    public static String cleanRemoteException(RemoteException e) {
        String msg = e.getMessage();
        if (msg == null) return "Unknown error";
        if (msg.contains(": ")) {
            String[] parts = msg.split(": ");
            return parts[parts.length - 1];
        }
        return msg;
    }

    /**
     * Root base exception for all custom domain-specific game faults that need
     * to be safely propagated across network channels over an RMI bridge.
     */
    public static class BaseGameException extends RemoteException {
        /**
         * Constructs a new BaseGameException with a specific detail description string.
         *
         * @param msg the structural reason explaining why this exception was raised
         */
        public BaseGameException(String msg) {
            super(msg);
        }
    }

    /**
     * Thrown during registration validation if a client requests a nickname
     * profile record that is already active inside the current server instance session.
     */
    public static class NicknameAlreadyUsedException extends BaseGameException {
        /**
         * Constructs a standard NicknameAlreadyUsedException with a predefined instruction error prompt.
         */
        public NicknameAlreadyUsedException() {
            super("Nickname already used. Chose another nickname.");
        }
    }

    /**
     * Thrown during registration validation if a client requests a token pieces
     * color assignment entry that has already been claimed by another opponent.
     */
    public static class ColorAlreadyUsedException extends BaseGameException {
        /**
         * Constructs a standard ColorAlreadyUsedException with a predefined instruction error prompt.
         */
        public ColorAlreadyUsedException() {
            super("Color already used. Chose another color.");
        }
    }

    /**
     * Thrown when an incoming user attempts to register into an active matchmaking match container
     * that has already reached its planned capacity quota limit bound.
     */
    public static class LobbyFullException extends BaseGameException {
        /**
         * Constructs a standard LobbyFullException with a predefined rejection warning prompt.
         */
        public LobbyFullException() {
            super("Too late, lobby is full!");
        }
    }

    /**
     * Thrown during match configuration procedures if an invalid threshold limit parameter
     * for total players count setup is rejected by the server system core bounds ruleset.
     */
    public static class InvalidTargetPlayersNumberException extends BaseGameException {
        /**
         * Constructs a new InvalidTargetPlayersNumberException stating the allowed capacity constraints range.
         *
         * @param minPlayers the strict lower threshold bound constraint value
         * @param maxPlayers the strict upper threshold bound constraint value
         */
        public InvalidTargetPlayersNumberException(int minPlayers, int maxPlayers) {
            super("Invalid players number, it must be between " + minPlayers + " and " + maxPlayers);
        }
    }

    /**
     * Thrown if secondary joining clients attempt to invoke configuration methods or enter prematurely
     * while the initial lobby creator/host is busy negotiating setup configuration inputs parameters.
     */
    public static class HostStillSettingLobbyException extends BaseGameException {
        /**
         * Constructs a standard HostStillSettingLobbyException with a predefined retry guidance alert prompt.
         */
        public HostStillSettingLobbyException() {
            super("Host is choosing players number, try later.");
        }
    }
}