package com.csetutorials;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.csetutorials.beans.Author;
import com.csetutorials.beans.CatTag;
import com.csetutorials.beans.OrganizationInfo;
import com.csetutorials.beans.Page;
import com.csetutorials.beans.SeoSettings;
import com.csetutorials.beans.SiteConfig;
import com.csetutorials.beans.SocialMediaLinks;
import com.csetutorials.contants.DefaultDirs;
import com.csetutorials.utils.Constants;
import com.csetutorials.utils.DataUtils;
import com.csetutorials.utils.FileUtils;
import com.csetutorials.utils.JsonSchemaUtils;
import com.csetutorials.utils.PageUtils;
import com.csetutorials.utils.SiteUtils;
import com.csetutorials.utils.SitemapCreator;
import com.csetutorials.utils.StringUtils;
import com.csetutorials.utils.TemplateUtils;

public class Main {

	public static void main(String[] args) throws Exception {

		CommandLine cmd = getCommands(args);

		String root = cmd.getOptionValue("build", new File("").getAbsolutePath());

		SiteConfig siteConfig = SiteUtils.getSiteConfig(root);

		DataUtils.readData(siteConfig);
		DataUtils.loadAllAuthors(siteConfig);

		List<Page> posts = PageUtils.createPostsMetaData(siteConfig);
		List<Page> pages = PageUtils.createPagesMetaData(siteConfig);

		TemplateUtils.createEngine(siteConfig);
		Map<CatTag, List<Page>> tagsPosts = PageUtils.extractTagsWithRelatedPosts(posts);
		Map<CatTag, List<Page>> catsPosts = PageUtils.extractCategoriesWithRelatedPosts(posts);
		Map<String, List<Page>> authorsPosts = PageUtils.extractAuthorWithRelatedPosts(posts);

		siteConfig.getRawConfig().put("tags", PageUtils.extractTags(posts, siteConfig));
		siteConfig.getRawConfig().put("categories", PageUtils.extractCategories(posts, siteConfig));
		siteConfig.getRawConfig().put("tagPosts", tagsPosts);
		siteConfig.getRawConfig().put("categoriesPosts", catsPosts);
		JsonSchemaUtils jsonSchemaUtils = new JsonSchemaUtils(siteConfig);
		SiteUtils.generatePosts(posts, siteConfig, true, jsonSchemaUtils);
		SiteUtils.generatePosts(pages, siteConfig, false, jsonSchemaUtils);

		SiteUtils.generateLatestPostsPages(siteConfig);
		SiteUtils.generateCategoriesPages(siteConfig, catsPosts);
		SiteUtils.generateTagsPages(siteConfig, tagsPosts);
		SiteUtils.generateAuthorsPages(siteConfig, authorsPosts);
		SitemapCreator.createSiteMap(siteConfig, posts, pages);
		FileUtils.copyDirRecursively(siteConfig.getActiveThemeDir() + File.separator + DefaultDirs.staticDir,
				siteConfig.getGeneratedHtmlDir());
		FileUtils.copyDirRecursively(siteConfig.getRoot() + File.separator + DefaultDirs.staticDir,
				siteConfig.getGeneratedHtmlDir());
	}

	private static void generateSampleSite() throws FileNotFoundException {
		SiteConfig config = new SiteConfig(true);

		Scanner kb = new Scanner(System.in);
		System.out.print("Website name [My Site] : ");
		String title = kb.nextLine().trim();
		if (title.isEmpty()) {
			title = "My Site";
		}
		config.setTitle(title);

		File file = new File("");
		String dirName = title.replaceAll(" ", "-");
		String websiteDirPath = file.getAbsolutePath() + File.separator + dirName;
		// File websiteDir = new File(websiteDirPath);
		// websiteDir.mkdirs();
		System.out.print("Website url : ");
		String url = null;
		while (true) {
			url = kb.nextLine().trim();
			if (url.isEmpty() || !url.startsWith("http")) {
				System.out.print("Please enter a valid url that starts with 'http://' or 'https://' :");
				continue;
			}
			break;
		}
		config.setUrl(url);
		System.out.print("Website Base [/] : ");
		String baseUrl = kb.nextLine().trim();
		if (!baseUrl.isEmpty()) {
			config.setBaseUrl(baseUrl);
		}
		System.out.print("Tagline : ");
		config.setTagline(kb.nextLine().trim());
		System.out.print("Description of the website [This is my website] : ");
		String description = kb.nextLine().trim();
		if (!description.isEmpty()) {
			config.setDescription(description);
		}
		System.out.print("Website favicon (url link or absolute file path) : ");
		String favicon = kb.nextLine().trim();
		config.setFavicon(favicon);
		System.out.println("Are you a (p) Person or an (o) Organization ");
		char option = 'a';
		while (option != 'p' && option != 'o') {
			option = getInput(kb).toLowerCase().charAt(0);
		}
		SeoSettings seoSettings = new SeoSettings();
		if (option == 'o') {
			seoSettings.setIsPerson(false);
			OrganizationInfo org = new OrganizationInfo();
			System.out.print("Organization Name : ");
			org.setName(getInput(kb));
			System.out.print("Organization Logo (url or absolute file path) : ");
			org.setLogo(kb.nextLine().trim());
			System.out.println("Organization social profiles");
			System.out.println("To let search engines know which social profiles are associated to this site,"
					+ " enter your site social profiles data below. If a Wikipedia page for you or your"
					+ " organization exists, add it too.");
			SocialMediaLinks social = askSocialMediaLinks(kb);
			org.setSocialMediaLinks(social);
			System.out.println("Do you want to add First User (y) Yes (n) No : ");
			char option1 = 'a';
			while (option1 != 'y' && option1 != 'n') {
				option1 = getInput(kb).toLowerCase().charAt(0);
			}
			if (option1 == 'y') {
				System.out.println("Person Name : ");
				String personName = getInput(kb);
				String username = personName.replaceAll("[^a-zA-Z]+", "").toLowerCase();
				System.out.println("Username [" + username + "]");
				String temp = kb.nextLine().trim();
				if (!StringUtils.isBlank(temp)) {
					username = temp;
				}
				System.out.println("Write something about " + personName);
				String personDescription = kb.nextLine().trim();
				System.out.print("Person Image or Logo (url or absolute file path) : ");
				String personImage = kb.nextLine().trim();
				System.out.println("Person social profiles");
				SocialMediaLinks personSocialLinks = askSocialMediaLinks(kb);
				Author author = new Author();
				author.setName(personName);
				author.setUsername(username);
				author.setDescription(personDescription);
				author.setSocialMediaLinks(personSocialLinks);
				author.setImageUrl(personImage);
				createAuthor(websiteDirPath, username, author);
			}
			seoSettings.setOrganizationInfo(org);
		} else {
			seoSettings.setIsPerson(true);
			System.out.println("Your Name : ");
			String personName = getInput(kb);
			String username = personName.replaceAll("[^a-zA-Z]+", "").toLowerCase();
			System.out.println("Username [" + username + "]");
			String temp = kb.nextLine().trim();
			if (!StringUtils.isBlank(temp)) {
				username = temp;
			}
			System.out.println("Write something about " + personName);
			String personDescription = kb.nextLine().trim();
			System.out.print("Your Image or Logo (url or absolute file path) : ");
			String personImage = kb.nextLine().trim();
			System.out.println("Your social profiles");
			SocialMediaLinks social = askSocialMediaLinks(kb);
			Author author = new Author();
			author.setName(personName);
			author.setUsername(username);
			author.setDescription(personDescription);
			author.setSocialMediaLinks(social);
			author.setImageUrl(personImage);
			createAuthor(websiteDirPath, username, author);
			seoSettings.setPersonUsername(username);
		}
		config.setSeoSettings(seoSettings);
		kb.close();
		FileUtils.write(websiteDirPath + File.separator + "ssj.json", Constants.prettyGson.toJson(config));
	}

	private static void createAuthor(String websiteDirPath, String username, Author author)
			throws FileNotFoundException {
		String json = Constants.prettyGson.toJson(author);
		String path = websiteDirPath + File.separator + "data" + File.separator + "authors" + File.separator + username
				+ ".json";
		FileUtils.write(path, json);
	}

	private static SocialMediaLinks askSocialMediaLinks(Scanner kb) {
		SocialMediaLinks social = new SocialMediaLinks();
		System.out.print("Facebook Url : ");
		social.setFacebookUrl(kb.nextLine().trim());
		System.out.print("Twitter Url : ");
		social.setTwitterUrl(kb.nextLine().trim());
		System.out.print("Instagram Url : ");
		social.setInstagram(kb.nextLine().trim());
		System.out.print("LinkedIn Url : ");
		social.setLinkedin(kb.nextLine().trim());
		System.out.print("Pinterest Url : ");
		social.setPinterest(kb.nextLine().trim());
		System.out.print("YouTube Url : ");
		social.setYoutube(kb.nextLine().trim());
		System.out.print("Wikipedia Url : ");
		social.setWikipedia(kb.nextLine().trim());
		System.out.print("MySpace Url : ");
		social.setMyspace(kb.nextLine().trim());
		return social;
	}

	private static String getInput(Scanner kb) {
		String temp = null;
		while (StringUtils.isBlank((temp = kb.nextLine().trim()))) {
		}
		return temp;
	}

	private static CommandLine getCommands(String[] args) throws FileNotFoundException {
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

	public static void generateSampleConfig() {
		System.out.println(Constants.prettyGson.toJson(new SiteConfig()));
		System.exit(0);
	}

	private static Options buildOptions() {
		Options options = new Options();
		options.addOption(Option.builder().longOpt("build").desc("Build website\n Default : Current Directory")
				.argName("build").optionalArg(true).numberOfArgs(1).argName("website base dir path").build());
		options.addOption(
				Option.builder().longOpt("create-site").desc("Create Website Structure").hasArg(false).build());

		options.addOption(Option.builder().longOpt("help").desc("Display this help and exit").argName("help")
				.hasArg(false).build());
		return options;
	}

}
