package org.sagebionetworks.aclscript;

import java.io.File;
import java.io.IOException;

import org.sagebionetworks.dashboard.config.Config;
import org.sagebionetworks.dashboard.config.DefaultConfig;
import org.sagebionetworks.dashboard.config.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("aclscriptConfig")
public class AclscriptConfig implements Config {

    public AclscriptConfig() {
        try {
            String userHome = System.getProperty("user.home");
            File configFile = new File(userHome + "/.aclscript/aclscript.config");
            if (!configFile.exists()) {
                logger.warn("Missing config file " + configFile.getPath());
                // This file is needed as the source of properties
                // which should be overwritten by environment variables
                // or command-line arguments
                configFile = new File(getClass().getResource("/aclscript.config").getFile());
            }
            config = new DefaultConfig(configFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        return config.get(key);
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public int getInt(String key) {
        return config.getInt(key);
    }

    public long getLong(String key) {
        return config.getLong(key);
    }

    public Stack getStack() {
        return config.getStack();
    }

    public String getAuthEndpoint(String stack) {
        return config.get(stack + ".auth.endpoint");
    }
    public String getRepoEndpoint(String stack) {
        return config.get(stack + ".repo.endpoint");
    }
    public String getFileEndpoint(String stack) {
        return config.get(stack + ".file.endpoint");
    }

    private final Logger logger = LoggerFactory.getLogger(AclscriptConfig.class);
    private final Config config;
}
