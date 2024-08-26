package com.systemwerx.common.web.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${security.jwt.client-id:sxclientid}")
	private String clientId;

	@Value("${security.jwt.client-secret:sxclientsecret}")
	private String clientSecret;

	@Value("${security.jwt.grant-type:password,refresh_token,implicity}")
	private String grantType;

	@Value("${security.jwt.scope-read:read}")
	private String scopeRead;

	@Value("${security.jwt.scope-write:write}")
	private String scopeWrite = "write";

	@Value("${security.jwt.resource-ids:sxresourceid}")
	private String resourceIds;

	@Value("${security.jwt.token-validity:7200}")
	private int tokenValidity;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
		configurer.inMemory().withClient(clientId).secret(passwordEncoder.encode(clientSecret))
				// .authorizedGrantTypes(grantType)
				.authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes(scopeRead, scopeWrite)
				.resourceIds(resourceIds).accessTokenValiditySeconds(tokenValidity);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
		endpoints.tokenStore(tokenStore).accessTokenConverter(accessTokenConverter).tokenEnhancer(enhancerChain)
				.authenticationManager(authenticationManager).userDetailsService(userDetailsService);
	}

}
