package com.csetutorials.ssj;

import com.csetutorials.ssj.services.*;
import org.apache.commons.cli.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class SsjApplication implements CommandLineRunner {

	private static CommandLine commandLine;

	public static void main(String[] args) {
		commandLine = CommandLineService.getCommands(args);
		SpringApplication.run(SsjApplication.class, args);
	}

	@Autowired
	DataLoader dataLoader;
	@Autowired
	HTMLPagesGenerator htmlPagesGenerator;
	@Autowired
	SitemapCreator sitemapCreator;
	@Autowired
	FileService fileService;

	@Override
	public void run(String... args) {
		dataLoader.load(commandLine.getOptionValue("build", new File("").getAbsolutePath()));
		htmlPagesGenerator.generateHtmlPages();
	}
}
