package org.augugrumi.harbor.util;

import org.apache.commons.cli.*;
import org.slf4j.Logger;

import java.net.MalformedURLException;

/**
 * It parses arguments given by the CLI
 */
public class ArgParser {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(ArgParser.class);

    // Short options
    final private static String API_CONF_PATH_OPTION_SHORT = "f";
    final private static String PORT_OPTION_SHORT = "p";
    final private static String KUBERNETES_URL_SHORT = "k";
    final private static String HARBOR_STORAGE_HOME_SHORT = "h";
    final private static String ROULETTE_URL_SHORT = "r";
    final private static String TOPOLOGY_PATH_OPTION_SHORT = "t";

    // Long options
    //final private static String API_CONF_PATH_OPTION_LONG = "file";
    //final private static String PORT_OPTION_LONG = "port";

    final private String[] ARGS;
    final private Options ARGS_TO_PARSE;

    /**
     * Prepare the parser but it doesn't execute it
     *
     * @param args the arguments to parse
     */
    public ArgParser(String[] args) {

        this.ARGS = args;

        ARGS_TO_PARSE = new Options();

        ARGS_TO_PARSE.addOption(API_CONF_PATH_OPTION_SHORT, true, "Path to API JSON file");
        //ARGS_TO_PARSE.addOption(API_CONF_PATH_OPTION_LONG, true, "Path to API JSON file");
        ARGS_TO_PARSE.addOption(PORT_OPTION_SHORT, true, "Port where Harbor should run");
        //ARGS_TO_PARSE.addOption(PORT_OPTION_LONG, true, "Port where Harbor should run");
        ARGS_TO_PARSE.addOption(KUBERNETES_URL_SHORT, true, "Set custom kubernetes API URL");
        //ARGS_TO_PARSE.addOption(HARBOR_YAML_HOME_LONG, true, "Set custom Harbor YAML home");
        ARGS_TO_PARSE.addOption(HARBOR_STORAGE_HOME_SHORT, true, "Set custom Harbor storage home");
        //ARGS_TO_PARSE.addOption(ROULETTE_URL_LONG, true, "Set custom Roulette API URL");
        ARGS_TO_PARSE.addOption(ROULETTE_URL_SHORT, true, "Set custom Roulette API URL");
        //ARGS_TO_PARSE.addOption(TOPOLOGY_PATH_OPTION_LONG, true, "Path to topology directory");
        ARGS_TO_PARSE.addOption(TOPOLOGY_PATH_OPTION_SHORT, true, "Path to topology directory");
    }

    /**
     * Execute the parsing procedure
     * @throws ParseException this exception is thrown if the parsing fails
     */
    public void parse() throws ParseException {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(ARGS_TO_PARSE, ARGS);

        if (cmd.hasOption(API_CONF_PATH_OPTION_SHORT) && cmd.getOptionValue(API_CONF_PATH_OPTION_SHORT) != null) {
            ConfigManager.getConfig().setAPIConfig(cmd.getOptionValue(API_CONF_PATH_OPTION_SHORT));
            LOG.debug(API_CONF_PATH_OPTION_SHORT + " passed as argument. Value: " + cmd.getOptionValue(API_CONF_PATH_OPTION_SHORT));
        }
        if (cmd.hasOption(PORT_OPTION_SHORT) && cmd.getOptionValue(PORT_OPTION_SHORT) != null) {
            ConfigManager.getConfig().setPort(Integer.parseInt(cmd.getOptionValue(PORT_OPTION_SHORT)));
            LOG.debug(PORT_OPTION_SHORT + " passed as argument. Value: " + cmd.getOptionValue(PORT_OPTION_SHORT));
        }
        if (cmd.hasOption(HARBOR_STORAGE_HOME_SHORT) && cmd.getOptionValue(HARBOR_STORAGE_HOME_SHORT) != null) {
            ConfigManager.getConfig().setStorageFolder(cmd.getOptionValue(HARBOR_STORAGE_HOME_SHORT));
            LOG.debug(HARBOR_STORAGE_HOME_SHORT + " passed as argument. Value: " + cmd.getOptionValue(HARBOR_STORAGE_HOME_SHORT));
        }
        try {
            if (cmd.hasOption(KUBERNETES_URL_SHORT) && cmd.getOptionValue(KUBERNETES_URL_SHORT) != null) {
                ConfigManager.getConfig().setKubernetesUrl(cmd.getOptionValue(KUBERNETES_URL_SHORT));
            }
            LOG.debug(KUBERNETES_URL_SHORT + " passed as argument. Value: " + cmd.getOptionValue(KUBERNETES_URL_SHORT));
            if (cmd.hasOption(ROULETTE_URL_SHORT) && cmd.getOptionValue(ROULETTE_URL_SHORT) != null) {
                ConfigManager.getConfig().setRouletteUrl(cmd.getOptionValue(ROULETTE_URL_SHORT));
            }
            LOG.debug(ROULETTE_URL_SHORT + " passed as argument. Value: " + cmd.getOptionValue(ROULETTE_URL_SHORT));
        } catch (MalformedURLException e) {
            LOG.error("Error while parsing one of the two possible URLs");
            e.printStackTrace();
            System.exit(1);
        }
        if (cmd.hasOption(TOPOLOGY_PATH_OPTION_SHORT) && cmd.getOptionValue(TOPOLOGY_PATH_OPTION_SHORT) != null) {
            ConfigManager.getConfig().setTopologyPath(cmd.getOptionValue(TOPOLOGY_PATH_OPTION_SHORT));
            LOG.debug(TOPOLOGY_PATH_OPTION_SHORT + " passed as argument. Value: " + cmd.getOptionValue(TOPOLOGY_PATH_OPTION_SHORT));
        }
    }
}
