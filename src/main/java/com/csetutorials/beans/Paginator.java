package com.csetutorials.beans;

import java.util.List;

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

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getPostsPerPage() {
		return postsPerPage;
	}

	public void setPostsPerPage(Integer postsPerPage) {
		this.postsPerPage = postsPerPage;
	}

	public Integer getTotalPosts() {
		return totalPosts;
	}

	public void setTotalPosts(Integer totalPosts) {
		this.totalPosts = totalPosts;
	}

	public List<Page> getPosts() {
		return posts;
	}

	public void setPosts(List<Page> posts) {
		this.posts = posts;
	}

	public Boolean getHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(Boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public Boolean getHasPreviousPage() {
		return hasPreviousPage;
	}

	public void setHasPreviousPage(Boolean hasPreviousPage) {
		this.hasPreviousPage = hasPreviousPage;
	}

	public String getPreviousPageUrl() {
		return previousPageUrl;
	}

	public void setPreviousPageUrl(String previousPageUrl) {
		this.previousPageUrl = previousPageUrl;
	}

	public String getNextPageUrl() {
		return nextPageUrl;
	}

	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}
}
