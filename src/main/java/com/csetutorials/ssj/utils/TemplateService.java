package com.csetutorials.ssj.utils;

import com.csetutorials.ssj.beans.SiteConfig;
import com.csetutorials.ssj.contants.DefaultDirs;
import com.csetutorials.ssj.contants.PathService;
import lombok.Setter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Setter
public class TemplateService {

	@Autowired
	PathService pathService;

	public void createEngine(SiteConfig config) throws IOException {
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
		for (File file : FileUtils.getFilesRecursively(pathService.getTempLayoutsDir())) {
			repo.putStringResource(file.getName(), FileUtils.getString(file.getAbsolutePath()));
		}
		repo.putStringResource("sitemap_index.xml", FileUtils.getResourceContent("sitemap_index.xml"));
		repo.putStringResource("page-sitemap.xml", FileUtils.getResourceContent("page-sitemap.xml"));
		config.setVelocityEngine(engine);
	}

	public void addTemplate(SiteConfig config, String templateName, String templateContent) {
		// Initialize my template repository. You can replace the "Hello $w" with your
		// String.
		StringResourceRepository repo = (StringResourceRepository) config.getVelocityEngine()
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource(templateName, templateContent);
	}

	public boolean isTemplateAvailable(SiteConfig config, String templateName) {
		return config.getVelocityEngine().resourceExists(templateName);
	}

	public String formatContent(VelocityEngine engine, VelocityContext context, String templateName) {
		Template template = engine.getTemplate(templateName, "UTF-8");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

	public String parseMarkdown(String content) {
		List<Extension> extensions = Arrays.asList(TablesExtension.create(), HeadingAnchorExtension.create());
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

	private void createLayouts(SiteConfig siteConfig) throws IOException {
		// Extracting themes layouts
		for (File layoutFile : FileUtils
				.getFilesRecursively(siteConfig.getActiveThemeDir() + File.separator + DefaultDirs.layouts)) {
			FileUtils.copyFile(layoutFile, new File(pathService.getTempLayoutsDir() + File.separator + layoutFile.getName()));
		}

		for (File layoutFile : FileUtils.getFilesRecursively(pathService.getRootDir() + File.separator + DefaultDirs.layouts)) {
			FileUtils.copyFile(layoutFile, new File(pathService.getTempLayoutsDir() + File.separator + layoutFile.getName()));
		}

		// Creating actual layouts
		for (File templateFile : FileUtils.getFilesRecursively(pathService.getTempLayoutsDir())) {
			String templateContent = generateTemplate(pathService.getTempLayoutsDir(), templateFile.getName());
			FileUtils.write(pathService.getTempLayoutsDir() + File.separator + templateFile.getName(), templateContent);
		}
	}

	private String generateTemplate(String layoutPath, String templateName) throws IOException {
		layoutPath += File.separator;
		String fileContent = FileUtils.getString(layoutPath + templateName);
		while (true) {

			Map<String, Object> params = StringUtils.getRawParams(fileContent);
			if (params == null || params.isEmpty() || !params.containsKey("layout")) {
				return fileContent;
			}
			templateName = (String) params.get("layout");
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
