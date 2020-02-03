package com.csetutorials.beans;

public class CatTag {

	private String shortcode;

	private String name;

	private String url;

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((shortcode == null) ? 0 : shortcode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CatTag other = (CatTag) obj;
		if (shortcode == null) {
			if (other.shortcode != null)
				return false;
		} else if (!shortcode.equals(other.shortcode))
			return false;
		return true;
	}

}
