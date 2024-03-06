package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThemeConfig {

	String postLayout = "post.html";

	String pageLayout = "page.html";

	String categoriesLayout = "category.html";

	String tagsLayout = "tag.html";

	String latestPostsLayout = "latest-posts.html";

	String authorLayout = "author.html";

	String indexLayout = "latest-posts.html";

	String staticContentDir = "static";

}
