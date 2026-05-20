package it.polimi.ingsw.network.commands;

/**
 * Factory class responsible for dynamically instantiating {@link ClientCommandHandler} implementations.
 * It uses Java Reflection to locate and create command handlers at runtime based on text headers
 * received from the client over the network protocol.
 */
public class ClientCommandFactory {
    /**
     * The base package path where all specific client command handler classes are located.
     */
    private static final String PACKAGE = "it.polimi.ingsw.network.commands.clientHandler.";

    /**
     * Dynamically loads and instantiates the appropriate command handler for a given network header string.
     * For example, a header like "LOGIN" will be mapped to "LoginHandler".
     *
     * @param header the raw command string header received from the network layer
     * @return an instance of the corresponding {@link ClientCommandHandler},
     *         or {@code null} if the class cannot be found or instantiated
     */
    public static ClientCommandHandler getHandler(String header) {
        try {
            String className = convertToClassName(header);
            return (ClientCommandHandler) Class.forName(PACKAGE + className)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts a snake_case protocol header string into a PascalCase class name appended with "Handler".
     * For example, transforms "TILE_SELECT" into "TileSelectHandler".
     *
     * @param header the snake_case header string to convert
     * @return the formatted class name string
     */
    private static String convertToClassName(String header) {
        StringBuilder sb = new StringBuilder();
        for (String part : header.toLowerCase().split("_")) {
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.append("Handler").toString();
    }
}