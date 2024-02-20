package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatTag {

	private String shortcode;
	private String name;
	private String url;

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
