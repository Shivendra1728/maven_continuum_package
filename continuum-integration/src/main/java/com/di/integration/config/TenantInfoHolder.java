package com.di.integration.config;
 
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
 
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Component
@Scope(value = "prototype")
public class TenantInfoHolder {
	
    private String domain;
    private String domainUsername;
    private String domainPassword;

}
 