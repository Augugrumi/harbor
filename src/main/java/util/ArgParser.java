package util;

import org.apache.commons.cli.*;
import org.slf4j.Logger;

import java.net.MalformedURLException;

public class ArgParser {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(ArgParser.class);

    // Short options
    final private static String API_CONF_PATH_OPTION_SHORT = "f";
    final private static String PORT_OPTION_SHORT = "p";
    final private static String KUBERNETES_URL_SHORT = "k";

    // Long options
    //final private static String API_CONF_PATH_OPTION_LONG = "file";
    //final private static String PORT_OPTION_LONG = "port";

    final private String[] ARGS;
    final private Options ARGS_TO_PARSE;

    public ArgParser(String[] args) {

        this.ARGS = args;

        ARGS_TO_PARSE = new Options();

        ARGS_TO_PARSE.addOption(API_CONF_PATH_OPTION_SHORT, true, "Path to API JSON file");
        //ARGS_TO_PARSE.addOption(API_CONF_PATH_OPTION_LONG, true, "Path to API JSON file");
        ARGS_TO_PARSE.addOption(PORT_OPTION_SHORT, true, "Port where Harbor should run");
        //ARGS_TO_PARSE.addOption(PORT_OPTION_LONG, true, "Port where Harbor should run");
        ARGS_TO_PARSE.addOption(KUBERNETES_URL_SHORT, true, "Set custom kubernetes API URL");
    }

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
        if (cmd.hasOption(KUBERNETES_URL_SHORT) && cmd.getOptionValue(KUBERNETES_URL_SHORT) != null) {
            try {
                ConfigManager.getConfig().setKubernetesAddress(cmd.getOptionValue(KUBERNETES_URL_SHORT));
            } catch (MalformedURLException e) {
                LOG.error(cmd.getOptionValue(KUBERNETES_URL_SHORT) + " is not a valid URL");
                e.printStackTrace();
                System.exit(1);
            }
            LOG.debug(KUBERNETES_URL_SHORT + " passed as argument. Value: " + cmd.getOptionValue(KUBERNETES_URL_SHORT));
        }
    }
}
