package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.WebsiteConfig;
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
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Setter
public class TemplateService {

	@Autowired
	PathService pathService;
	@Autowired
	FileService fileService;

	public void createEngine(WebsiteConfig config) {
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
		for (File file : fileService.getFilesRecursively(pathService.getTempLayoutsDir())) {
			repo.putStringResource(file.getName(), fileService.getString(file.getAbsolutePath()));
		}
		repo.putStringResource("sitemap_index.xml", fileService.getResourceContent("sitemap_index.xml"));
		repo.putStringResource("page-sitemap.xml", fileService.getResourceContent("page-sitemap.xml"));
		config.setVelocityEngine(engine);
	}

	public void addTemplate(WebsiteConfig config, String templateName, String templateContent) {
		// Initialize my template repository. You can replace the "Hello $w" with your
		// String.
		StringResourceRepository repo = (StringResourceRepository) config.getVelocityEngine()
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource(templateName, templateContent);
	}

	public boolean isTemplateNotAvailable(WebsiteConfig config, String templateName) {
		return !config.getVelocityEngine().resourceExists(templateName);
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

	private void createLayouts(WebsiteConfig websiteConfig) {
		// Extracting themes layouts
		for (File layoutFile : fileService
				.getFilesRecursively(websiteConfig.getActiveThemeDir() + File.separator + DefaultDirs.layouts)) {
			fileService.copyFile(layoutFile, new File(pathService.getTempLayoutsDir() + File.separator + layoutFile.getName()));
		}

		for (File layoutFile : fileService.getFilesRecursively(pathService.getRootDir() + File.separator + DefaultDirs.layouts)) {
			fileService.copyFile(layoutFile, new File(pathService.getTempLayoutsDir() + File.separator + layoutFile.getName()));
		}

		// Creating actual layouts
		for (File templateFile : fileService.getFilesRecursively(pathService.getTempLayoutsDir())) {
			String templateContent = generateTemplate(pathService.getTempLayoutsDir(), templateFile.getName());
			fileService.write(pathService.getTempLayoutsDir() + File.separator + templateFile.getName(), templateContent);
		}
	}

	private String generateTemplate(String layoutPath, String templateName) {
		layoutPath += File.separator;
		String fileContent = fileService.getString(layoutPath + templateName);
		while (true) {

			Map<String, Object> params = StringUtils.getRawParams(fileContent);
			if (params == null || params.isEmpty() || !params.containsKey("layout")) {
				return fileContent;
			}
			templateName = (String) params.get("layout");
			String parentContent = fileService.getString(layoutPath + templateName);

			parentContent.replaceAll("\\$content", "REPLACE_ME_SSR");
			fileContent = parentContent.replace("$content", StringUtils.getContentBody(fileContent));
			fileContent.replace("REPLACE_ME_SSR", "\\$content");
		}
	}

}
