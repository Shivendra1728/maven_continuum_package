package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor

public class RootObject {
    
    
    @JsonProperty("Results")
    private Results results;
    
    @JsonProperty("Messages")
    private List<Object> messages;
    
    
    @JsonProperty("Summary")
    private Summary summary;
    
    // Getters and setters
}
