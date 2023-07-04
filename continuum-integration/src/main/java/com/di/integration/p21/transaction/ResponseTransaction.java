package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ResponseTransaction {

	   @JsonProperty("DataElements")
    private List<ResponseDataElements> dataElements;
}
