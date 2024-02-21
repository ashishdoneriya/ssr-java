package com.csetutorials.ssj.services;

import com.csetutorials.ssj.exceptions.JsonParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

@Service
public class JsonService {

	public <T> T convert(String json, Class<T> clazz) throws JsonParsingException {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			throw new JsonParsingException(e);
		}
	}

	public String pretty(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new JsonParsingException(e);
		}
	}

}
