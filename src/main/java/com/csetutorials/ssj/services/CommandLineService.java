package com.csetutorials.ssj.services;

import lombok.experimental.UtilityClass;
import org.apache.commons.cli.*;

@UtilityClass
public class CommandLineService {

	public CommandLine getCommands(String[] args) {
		Options options = buildOptions();

		if (args.length > 0 && args[0].contains("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp("java -jar ssj.jar", options, true);
			System.exit(0);
		}

		// Parsing command line arguments
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Invalid Argument(s)");
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp("java -jar ssj.jar", options, true);
			System.exit(1);
		}

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp("java -jar ssj.jar", options, true);
			System.exit(0);
		}

		if (cmd.hasOption("create-site")) {
			generateSampleSite();
			System.exit(0);
		}
		return cmd;
	}

	private Options buildOptions() {
		Options options = new Options();
		options.addOption(Option.builder().longOpt("build").desc("Build website\n Default : Current Directory")
				.argName("build").optionalArg(true).numberOfArgs(1).argName("website base dir path").build());
		options.addOption(
				Option.builder().longOpt("create-site").desc("Create Website Structure").hasArg(false).build());

		options.addOption(Option.builder().longOpt("help").desc("Display this help and exit").argName("help")
				.hasArg(false).build());
		return options;
	}

	private void generateSampleSite() {

	}


}
