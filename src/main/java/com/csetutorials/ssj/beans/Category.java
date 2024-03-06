package com.csetutorials.ssj.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

	String shortcode = "others";

	String name = "Others";

	@JsonIgnore
	String url;

}
