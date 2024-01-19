package com.di.integration.p21.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Summary {
    @JsonProperty("Failed")
    private int failed;
    
    @JsonProperty("Succeeded")
    private int succeeded;
    
    @JsonProperty("Other")
    private int other;
    
    // Getters and setters
}
