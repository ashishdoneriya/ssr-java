package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Paginator {

	private Integer currentPage;
	private Integer totalPages;
	private Integer postsPerPage;
	private Integer totalPosts;
	private List<Page> posts;
	private Boolean hasNextPage;
	private Boolean hasPreviousPage;
	private String previousPageUrl;
	private String nextPageUrl;

}
