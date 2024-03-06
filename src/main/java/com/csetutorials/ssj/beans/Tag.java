package com.csetutorials.ssj.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag {

	String shortcode = "";

	String name = "";

	@JsonIgnore
	String url;

}
