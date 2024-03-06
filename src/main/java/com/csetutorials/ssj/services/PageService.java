package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.exceptions.MetaDataException;
import com.csetutorials.ssj.utils.StringUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@CommonsLog
public class PageService {

	@Autowired
	FileService fileService;

	@Autowired
	Configuration configuration;

	public List<Page> readPages() {
		String separator = Pattern.quote(configuration.getWebsite().getPostsMetaDataSeparator());
		Pattern pattern = Pattern.compile(separator + "(.+)" + separator + "(.+)", Pattern.MULTILINE);

		Yaml yaml = new Yaml();
		List<Page> pages = new ArrayList<>();
		SimpleDateFormat parseSdf = new SimpleDateFormat(configuration.getWebsite().getPostsMetaDateFormat());
		SimpleDateFormat formatSdf = new SimpleDateFormat(configuration.getWebsite().getPostsDisplayDateFormat());

		for (File file : fileService.listFiles(configuration.getSsjPaths().getPagesDir())) {
			String fileContent = fileService.getString(file);
			Matcher matcher = pattern.matcher(fileContent);
			if (!matcher.find()) {
				log.warn("Cannot identify content for - " + file.getAbsolutePath());
				continue;
			}
			String metaStr = matcher.group(1);
			PostYmlParams meta = parsePageMetadata(yaml, metaStr);
			if (meta.isDraft()) {
				continue;
			}
			meta.setContent(matcher.group(2));
			Page page = createPage(parseSdf, formatSdf, meta, file);
			if (page != null) {
				pages.add(page);
			}
		}

		sortPages(pages);
		return pages;
	}

	private PostYmlParams parsePageMetadata(Yaml yaml, String metaStr) {
		return yaml.loadAs(metaStr, PostYmlParams.class);
	}

	private Page createPage(SimpleDateFormat parseSdf, SimpleDateFormat formatSdf, PostYmlParams meta, File file) {
		try {
			Page page = new Page();
			setPageBasicInfo(parseSdf, formatSdf, meta, page, file);
			setPageAuthor(meta, page);
			setPageUrls(page, meta);
			setContent(page, meta, file);
			return page;
		} catch (MetaDataException e) {
			log.error("Error creating page for file - " + file.getName() + ", " + e.getMessage());
			return null;
		}
	}



	private void setPageBasicInfo(SimpleDateFormat parseSdf, SimpleDateFormat formatSdf, PostYmlParams meta, Page page, File file) throws MetaDataException {
		page.setTitle(meta.getTitle());
		page.setSeoTitle(StringUtils.isNotBlank(meta.getSeoTitle()) ? meta.getSeoTitle() : meta.getTitle());
		page.setDescription(meta.getDescription());
		page.setSeoDescription(StringUtils.isNotBlank(meta.getSeoDescription()) ? meta.getSeoDescription() : meta.getDescription());
		page.setDraft(meta.isDraft());
		if (StringUtils.isNotBlank(meta.getCreated())) {
			try {
				page.setCreated(parseDate(meta.getCreated(), parseSdf, file.getName()));
			} catch (ParseException e) {
				throw new MetaDataException("Incorrect created date");
			}
			page.setCreatedUIDate(formatSdf.format(page.getCreated()));
			if (StringUtils.isNotBlank(meta.getUpdated())) {
				try {
					page.setUpdated(parseDate(meta.getUpdated(), parseSdf, file.getName()));
				} catch (ParseException e) {
					throw new MetaDataException("Incorrect updated date");
				}
			} else {
				page.setUpdated(page.getCreated());
			}
			page.setUpdatedUIDate(formatSdf.format(page.getUpdated()));
		}

	}

	private Date parseDate(String dateString, SimpleDateFormat sdf, String fileName) throws ParseException {
		if (StringUtils.isNotBlank(dateString)) {
			return sdf.parse(dateString);
		} else {
			log.warn("Created Date not defined in page - " + fileName);
			return new Date();
		}
	}

	private void setPageAuthor(PostYmlParams meta, Page page) {
		String authorName = meta.getAuthor();
		Optional<Author> authorOpt = StringUtils.isNotBlank(authorName) ? configuration.getAuthor(authorName) : configuration.getDefaultAuthor();
		if (authorOpt.isPresent()) {
			page.setAuthor(authorOpt.get());
		}
	}

	private void setPageUrls(Page page, PostYmlParams meta) {
		page.setUrl(StringUtils.removeExtraSlash("/" + configuration.getWebsite().getBaseUrl() + "/" + generatePageUrl(page, meta)));
		page.setAbsoluteUrl(StringUtils.removeExtraSlash(configuration.getWebsite().getUrl() + page.getUrl()));
	}

	private void setContent(Page page, PostYmlParams meta, File file) {
		page.setContent(file.getName().endsWith(".md") ? StringUtils.parseMarkdown(meta.getContent()) : meta.getContent());
	}

	private void sortPages(List<Page> pages) {
		pages.sort((p1, p2) -> p2.getCreated().compareTo(p1.getCreated()));
	}

	private String generatePageUrl(Page page, PostYmlParams meta) {
		String slug = StringUtils.isNotBlank(meta.getSlug()) ? meta.getSlug().replaceAll(" +", "-") : page.getTitle().replaceAll("[^\\sa-zA-Z0-9]", "").replaceAll("\\s+", "-");
		if (StringUtils.isNotBlank(slug)) {
			return configuration.getWebsite().getPagePermalink().replace(":slug", slug.trim());
		}
		String permalink = meta.getPermalink();
		if (StringUtils.isNotBlank(permalink)) {
			return permalink;
		}
		String newSlug = page.getTitle().replaceAll("[^\\sa-zA-Z0-9]", "").replaceAll("\\s+", "-");
		return configuration.getWebsite().getPagePermalink().replace(":slug", newSlug);
	}

}
