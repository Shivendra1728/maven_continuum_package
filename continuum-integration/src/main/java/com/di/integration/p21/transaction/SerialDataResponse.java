package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SerialDataResponse {

	@JsonProperty("odata.metadata")
    private String odataMetadata;

    @JsonProperty("value")
    private List<SerialData> value;
}