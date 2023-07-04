package com.di.integration.p21.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ResponseEdit {

	 @JsonProperty("Name")
    private String name;

	 @JsonProperty("Value")
    private String value;
}
