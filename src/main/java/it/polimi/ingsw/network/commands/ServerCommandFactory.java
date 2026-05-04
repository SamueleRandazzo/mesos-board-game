package it.polimi.ingsw.network.commands;

public class ServerCommandFactory {
    private static final String PACKAGE = "it.polimi.ingsw.network.commands.serverHandler.";

    public static ServerCommandHandler getHandler(String header) {
        try {
            String className = convertToClassName(header);
            return (ServerCommandHandler) Class.forName(PACKAGE + className)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private static String convertToClassName(String header) {
        StringBuilder sb = new StringBuilder();
        for (String part : header.toLowerCase().split("_")) {
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.append("Handler").toString();
    }
}