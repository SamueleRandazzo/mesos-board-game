package it.polimi.ingsw.exception;

import java.rmi.RemoteException;

public class CustomException {
    public static String cleanRemoteException(RemoteException e) {
        String msg = e.getMessage();
        if (msg == null) return "Unknown error";
        if (msg.contains(": ")) {
            String[] parts = msg.split(": ");
            return parts[parts.length - 1];
        }
        return msg;
    }

    public static class BaseGameException extends RemoteException {
        public BaseGameException(String msg) {
            super(msg);
        }
    }

    public static class NicknameAlreadyUsedException extends BaseGameException {
        public NicknameAlreadyUsedException() {
            super("Nickname already used. Chose another nickname.");
        }
    }

    public static class ColorAlreadyUsedException extends BaseGameException {
        public ColorAlreadyUsedException() {
            super("Color already used. Chose another color.");
        }
    }

    public static class LobbyFullException extends BaseGameException {
        public LobbyFullException() {
            super("Too late, lobby is full!");
        }
    }

    public static class InvalidTargetPlayersNumberException extends BaseGameException {
        public InvalidTargetPlayersNumberException(int minPlayers, int maxPlayers) {
            super("Invalid players number, it must be between " + minPlayers + " and " + maxPlayers);
        }
    }
}
