package com.gegebot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleConfiguration {

	@JsonProperty("count")
	private Integer count;

	@JsonProperty("emoji")
	private String emojiUnicode;
}
