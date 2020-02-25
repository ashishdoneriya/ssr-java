package com.csetutorials.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.csetutorials.beans.SiteConfig;
import com.csetutorials.contants.DefaultDirs;

public class TemplateUtils {

	public static void createEngine(SiteConfig config) throws IOException {
		// Initialize the engine.
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(Velocity.RESOURCE_LOADER, "string");
		engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		engine.addProperty("string.resource.loader.repository.static", "false");
		// engine.addProperty("string.resource.loader.modificationCheckInterval", "1");
		engine.init();
		StringResourceRepository repo = (StringResourceRepository) engine
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);

		createLayouts(config);
		for (File file : FileUtils.getFilesRecursively(config.getTempLayoutsPath())) {
			repo.putStringResource(file.getName(), FileUtils.getString(file.getAbsolutePath()));
		}
		repo.putStringResource("sitemap_index.xml", FileUtils.getResourceContent("sitemap_index.xml"));
		repo.putStringResource("page-sitemap.xml", FileUtils.getResourceContent("page-sitemap.xml"));
		config.setEngine(engine);
	}

	public static void addTemplate(SiteConfig config, String templateName, String templateContent) {
		// Initialize my template repository. You can replace the "Hello $w" with your
		// String.
		StringResourceRepository repo = (StringResourceRepository) config.getEngine()
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource(templateName, templateContent);
	}

	public static boolean isTemplateAvailable(SiteConfig config, String templateName) {
		return config.getEngine().resourceExists(templateName);
	}

	public static String formatContent(VelocityEngine engine, VelocityContext context, String templateName) {
		Template template = engine.getTemplate(templateName, "UTF-8");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

	public static String parseMarkdown(String content) {
		List<Extension> extensions = Arrays.asList(TablesExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		Node document = parser.parse(content);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
		return renderer.render(document);
	}

	/*
	 * public static VelocityEngine getVelocityEngine(SiteConfig siteConfig) {
	 * VelocityEngine engine = new VelocityEngine();
	 * engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
	 * engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
	 * siteConfig.getTempLayoutsPath()); return engine; }
	 */

	private static void createLayouts(SiteConfig siteConfig) throws IOException {
		// Extracting themes layouts
		for (File layoutFile : FileUtils
				.getFilesRecursively(siteConfig.getActiveThemeDir() + File.separator + DefaultDirs.layouts)) {
			FileUtils.copyFile(layoutFile,
					new File(siteConfig.getTempLayoutsPath() + File.separator + layoutFile.getName()));
		}

		for (File layoutFile : FileUtils
				.getFilesRecursively(siteConfig.getRoot() + File.separator + DefaultDirs.layouts)) {
			FileUtils.copyFile(layoutFile,
					new File(siteConfig.getTempLayoutsPath() + File.separator + layoutFile.getName()));
		}

		// Creating actual layouts
		for (File templateFile : FileUtils.getFilesRecursively(siteConfig.getTempLayoutsPath())) {
			String templateContent = generateTemplate(siteConfig.getTempLayoutsPath(), templateFile.getName());
			FileUtils.write(siteConfig.getTempLayoutsPath() + File.separator + templateFile.getName(), templateContent);
		}
	}

	private static String generateTemplate(String layoutPath, String templateName) throws IOException {
		layoutPath += File.separator;
		String fileContent = FileUtils.getString(layoutPath + templateName);
		while (true) {

			Map<String, String> params = StringUtils.getRawParams(fileContent);
			if (params == null || params.isEmpty() || !params.containsKey("layout")) {
				return fileContent;
			}
			templateName = params.get("layout");
			String parentContent = null;
			try {
				parentContent = FileUtils.getString(layoutPath + templateName);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return StringUtils.getContentBody(fileContent);
			}

			parentContent.replaceAll("\\$content", "REPLACE_ME_SSR");
			fileContent = parentContent.replace("$content", StringUtils.getContentBody(fileContent));
			fileContent.replace("REPLACE_ME_SSR", "\\$content");
		}
	}

}
