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
public class Results {

	 @JsonProperty("Name")
	    private String name;
	    @JsonProperty("UseCodeValues")
	    private boolean useCodeValues;
	    
	    @JsonProperty("IgnoreDisabled")
	    private boolean ignoreDisabled;
	    
	    @JsonProperty("Transactions")
	    private List<ResponseTransaction> transactions;
}
