package com.csetutorials.ssj;

import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.contants.DefaultDirs;
import com.csetutorials.ssj.contants.PathService;
import com.csetutorials.ssj.services.*;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
public class Main {

	@Autowired
	PathService pathService;
	@Autowired
	SiteService siteService;
	@Autowired
	TemplateService templateService;
	@Autowired
	DataService dataService;
	@Autowired
	PageService pageService;
	@Autowired
	SitemapCreator sitemapCreator;
	@Autowired
	FileService fileService;
	@Autowired
	JsonService jsonService;

	public void main(String[] args) throws Exception {

		CommandLine cmd = getCommands(args);

		String root = cmd.getOptionValue("build", new File("").getAbsolutePath());
		pathService.setRootDir(StringUtils.removeExtraSlash(root));
		SiteConfig siteConfig = siteService.getSiteConfig();

		dataService.readData(siteConfig);
		dataService.loadAllAuthors(siteConfig);

		List<Page> posts = pageService.createPostsMetaData(siteConfig);
		List<Page> pages = pageService.createPagesMetaData(siteConfig);

		templateService.createEngine(siteConfig);
		Map<CatTag, List<Page>> tagsPosts = pageService.extractTagsWithRelatedPosts(posts);
		Map<CatTag, List<Page>> catsPosts = pageService.extractCategoriesWithRelatedPosts(posts);
		Map<String, List<Page>> authorsPosts = pageService.extractAuthorWithRelatedPosts(posts);

		siteConfig.getRawConfig().put("tags", pageService.extractTags(posts));
		siteConfig.getRawConfig().put("categories", pageService.extractCategories(posts));
		siteConfig.getRawConfig().put("tagPosts", tagsPosts);
		siteConfig.getRawConfig().put("categoriesPosts", catsPosts);
		siteService.generatePosts(posts, siteConfig, true);
		siteService.generatePosts(pages, siteConfig, false);

		siteService.generateLatestPostsPages(siteConfig);
		siteService.generateCategoriesPages(siteConfig, catsPosts);
		siteService.generateTagsPages(siteConfig, tagsPosts);
		siteService.generateAuthorsPages(siteConfig, authorsPosts);
		sitemapCreator.createSiteMap(siteConfig, posts, pages);
		fileService.copyDirRecursively(siteConfig.getActiveThemeDir() + File.separator + DefaultDirs.staticDir,
				pathService.getGeneratedHtmlDir());
		fileService.copyDirRecursively(pathService.getRootDir() + File.separator + DefaultDirs.staticDir,
				pathService.getGeneratedHtmlDir());
		fileService.deleteDir(new File(pathService.getTempDir()));
	}

	private void generateSampleSite() throws FileNotFoundException {
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
		config.setDefaultAuthor(username);
		kb.close();
		fileService.write(websiteDirPath + File.separator + "ssj.json", jsonService.pretty(config));
	}

	private void createAuthor(String websiteDirPath, String username, Author author)
			throws FileNotFoundException {
		String json = jsonService.pretty(author);
		String path = websiteDirPath + File.separator + "data" + File.separator + "authors" + File.separator + username
				+ ".json";
		fileService.write(path, json);
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

	private String getInput(Scanner kb) {
		String temp = null;
		while (StringUtils.isBlank((temp = kb.nextLine().trim()))) {
		}
		return temp;
	}

	private CommandLine getCommands(String[] args) throws FileNotFoundException {
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

}
