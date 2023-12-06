package com.di.commons.helper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P21ProductItemHelper {

    @JsonProperty("odata.metadata")
    private String odataMetadata;

	private List<P21ProductItem> value;

}
