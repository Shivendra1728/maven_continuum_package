package com.continuum.multitenant.constant;

/**
 * @author RK
 */
public class JWTConstants {

	public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
	public static final String SIGNING_KEY = "Z29jb250aW51dW0=";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
}
