package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Paginator {

	Integer currentPage;

	Integer totalPages;

	Integer postsPerPage;

	Integer totalPosts;

	List<Page> posts;

	Boolean hasNextPage;

	Boolean hasPreviousPage;

	String previousPageUrl;

	String nextPageUrl;

}
