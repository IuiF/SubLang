package dev.iuif.sublang.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the current environment
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the config directory path
     */
    Path getConfigDir();
}
