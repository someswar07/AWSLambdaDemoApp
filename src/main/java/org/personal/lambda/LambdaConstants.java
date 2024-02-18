package org.personal.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.personal.config.LambdaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LambdaConstants {
    private static final String YAML_PATH = "lambda-config.yaml";

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaConstants.class);

    /**
     * This method to be called to get the config from yaml file.
     * @return LambdaConfiguration
     */
    public static LambdaConfiguration getConfigMap() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(new LambdaConstants().getResourceFile(YAML_PATH), LambdaConfiguration.class);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    private File getResourceFile(String filename){
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        return file;
    }

}
