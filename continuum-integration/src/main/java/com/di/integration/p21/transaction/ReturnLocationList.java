package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnLocationList {
    private String odataMetadata;
    private List<ReturnLocation> value;

    // Constructors, getters, and setters
}
