package com.example.myauth.config;

import com.example.myauth.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;

/**
 * @Description TODO
 * @Author Lu Cong
 * @Date 2022/6/21 5:22
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    @Qualifier("redisTokenStore")
//    private TokenStore redisTokenStore;

    @Qualifier("jwtTokenStore")
    private TokenStore jwtTokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                //客户端信息存放在内存
                .inMemory()
                //客户端账号(非用户账号)
                .withClient("clientId")
                //客户端密码（非用户密码）
                .secret(passwordEncoder.encode("123456"))
                //授权模式为授权码模式
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                //接受或者拒绝后重定向地址
                .redirectUris("http://www.baidu.com")
                //表示要求的授权范围
                .scopes("all")
                //设置token令牌过期时间
                .accessTokenValiditySeconds(60)
                //设置刷新token令牌过期时间
                .refreshTokenValiditySeconds(48000)
                .autoApprove(true);

    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userService)
                .tokenStore(jwtTokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(getTokenEnhancer());
    }

    public TokenEnhancerChain getTokenEnhancer(){
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(new ArrayList(){
            {
                add(new JwtTokenEnhancer());
                add(jwtAccessTokenConverter);
            }
        });
        return tokenEnhancerChain;
    }
}
