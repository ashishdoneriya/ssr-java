package com.csetutorials.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.csetutorials.beans.SiteConfig;

public class TemplateUtils {

	public static void setEngine(SiteConfig config) throws IOException {
		// Initialize the engine.
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(Velocity.RESOURCE_LOADER, "string");
		engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		engine.addProperty("string.resource.loader.repository.static", "false");
		// engine.addProperty("string.resource.loader.modificationCheckInterval", "1");
		engine.init();
		StringResourceRepository repo = (StringResourceRepository) engine
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		for (File file : FileUtils.getFilesRecursively(config.getTempLayoutsPath())) {
			repo.putStringResource(file.getName(), FileUtils.getString(file.getAbsolutePath()));
		}
		config.setEngine(engine);
	}

	public static void addTemplate(SiteConfig config, String templateName, String templateContent) {
		// Initialize my template repository. You can replace the "Hello $w" with your
		// String.
		StringResourceRepository repo = (StringResourceRepository) config.getEngine()
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource(templateName, templateContent);
	}

	public static String formatContent(VelocityEngine engine, VelocityContext context, String templateName) {
		Template template = engine.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	public static String parseMarkdown(String content) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(content);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}

}
