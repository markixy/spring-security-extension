package cn.com.markix.spring.security.extension;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.*;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义认证的配置器，将 Filter 和 Provider 组装到过滤器链中，使之生效。
 *
 * @author markix
 * @see org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer
 */
@SuppressWarnings("ALL")
public class MultipleAuthenticationFilterConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractHttpConfigurer<MultipleAuthenticationFilterConfigurer<B>, B> {

    // 自定义的认证过滤器
    private MultipleAuthenticationFilter authFilter;
    // 自定义的认证提供者
    private MultipleAuthenticationProvider authProvider;

    // 登录页的路径
    private String loginPage;
    // 登录接口的路径
    private String loginProcessingUrl;
    // 登录失败的跳转路径
    private String failureUrl;
    // 登出后的跳转地址
    private String logoutUrl;

    // 需要放行的路径
    private List<RequestMatcher> permitRequestMatchers = new ArrayList<>();
    private boolean callPermitAll;

    // 默认的认证成功处理器，认证成功后会跳转到上一个访问失败的路径，若没有上一个路径，则默认跳转到'/'
    private SavedRequestAwareAuthenticationSuccessHandler defaultSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    // 认证成功处理器
    private AuthenticationSuccessHandler successHandler = this.defaultSuccessHandler;
    // 认证失败处理器
    private AuthenticationFailureHandler failureHandler;


    public MultipleAuthenticationFilterConfigurer() {
        this.authFilter = new MultipleAuthenticationFilter();
        this.authProvider = new MultipleAuthenticationProvider();
        loginPage("/login.html");
        loginProcessingUrl("/multi-login");
    }


    public MultipleAuthenticationFilterConfigurer<B> loginPage(String loginPage) {
        this.loginPage = loginPage;
        this.logoutUrl = loginPage + "?logout";
        if (failureHandler == null) {
            this.failureUrl = loginPage + "?error";
        }
        return getSelf();
    }

    public MultipleAuthenticationFilterConfigurer<B> loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginProcessingUrl, "POST"));
        return getSelf();
    }

    public final MultipleAuthenticationFilterConfigurer<B> defaultSuccessUrl(String defaultSuccessUrl) {
        return defaultSuccessUrl(defaultSuccessUrl, false);
    }

    public final MultipleAuthenticationFilterConfigurer<B> defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(defaultSuccessUrl);
        handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
        this.defaultSuccessHandler = handler;
        return successHandler(handler);
    }

    public final MultipleAuthenticationFilterConfigurer<B> successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return getSelf();
    }

    public final MultipleAuthenticationFilterConfigurer<B> failureUrl(String authenticationFailureUrl) {
        this.failureUrl = authenticationFailureUrl;
        return failureHandler(new SimpleUrlAuthenticationFailureHandler(authenticationFailureUrl));
    }

    public final MultipleAuthenticationFilterConfigurer<B> failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    public MultipleAuthenticationFilterConfigurer<B> failureForwardUrl(String forwardUrl) {
        failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
        return this;
    }

    public MultipleAuthenticationFilterConfigurer<B> successForwardUrl(String forwardUrl) {
        successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
        return this;
    }


    @Override
    public void init(B http) throws Exception {
        Assert.state(callPermitAll, "没有放行认证相关的接口路径！");
        if (failureHandler == null) {
            failureUrl(this.failureUrl);
        }
        // 登出，重定向到登录页
        LogoutConfigurer<B> logoutConfigurer = getBuilder().getConfigurer(LogoutConfigurer.class);
        if (logoutConfigurer != null) {
            logoutConfigurer.logoutSuccessUrl(logoutUrl);
        }
        // 匿名访问时，重定向到登录页
        ExceptionHandlingConfigurer<B> exceptionHandling = getBuilder().getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(loginPage);
            exceptionHandling.defaultAuthenticationEntryPointFor(postProcess(entryPoint), getAuthenticationEntryPointMatcher(http));
        }
    }

    protected final RequestMatcher getAuthenticationEntryPointMatcher(B http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }

        MediaTypeRequestMatcher mediaMatcher = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
                new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
        mediaMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));

        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));

        return new AndRequestMatcher(Arrays.asList(notXRequestedWith, mediaMatcher));
    }


    @Override
    public void configure(B http) throws Exception {
        RequestCache requestCache = http.getSharedObject(RequestCache.class);
        if (requestCache != null) {
            this.defaultSuccessHandler.setRequestCache(requestCache);
        }

        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        authFilter.setAuthenticationSuccessHandler(successHandler);
        authFilter.setAuthenticationFailureHandler(failureHandler);

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            authFilter.setRememberMeServices(rememberMeServices);
        }
        MultipleAuthenticationFilter filter = postProcess(authFilter);

        // 关键配置，将认证Filter加到过滤器链！
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        // 关键配置，将认证Provider加到认证管理器！
        http.authenticationProvider(authProvider);
    }


    private MultipleAuthenticationFilterConfigurer<B> getSelf() {
        return this;
    }


    /**
     * TIPS 添加认证处理器
     */
    public MultipleAuthenticationFilterConfigurer<B> addAuthProcessor(AuthProcessor processor) {
        this.authFilter.addAuthProcessor(processor);
        return getSelf();
    }

    /**
     * 添加需要放行的路径，比如获取验证码的接口
     */
    public MultipleAuthenticationFilterConfigurer<B> addPermitUrl(String url) {
        this.permitRequestMatchers.add(new AntPathRequestMatcher(url));
        return getSelf();
    }

    public MultipleAuthenticationFilterConfigurer<B> addPermitUrl(RequestMatcher requestMatcher) {
        this.permitRequestMatchers.add(requestMatcher);
        return getSelf();
    }

    /**
     * 获取需要放行的路径
     */
    private List<RequestMatcher> getAllPermitRequestMatchers() {
        return Stream.of(this.permitRequestMatchers, Arrays.asList(
                new AntPathRequestMatcher(loginPage),
                new AntPathRequestMatcher(loginProcessingUrl),
                new AntPathRequestMatcher(failureUrl)
        )).flatMap(Collection::stream).collect(Collectors.toList());
    }


    /**
     * TIPS 放行路径，需要额外调用！
     * <p><b>需要在 http.authorizeRequests().anyRequest() 之前调用！</b></p>
     *
     * <p>放行逻辑没法集成到init()阶段，由于调用init之前外部已经调用 .anyRequest().authenticated()，添加额外的放行路径会报错（框架限制）！！！</p>
     *
     * @see AbstractAuthenticationFilterConfigurer#updateAccessDefaults(org.springframework.security.config.annotation.web.HttpSecurityBuilder)
     */
    public MultipleAuthenticationFilterConfigurer<B> permitAll() throws Exception {
        this.callPermitAll = true;
        ((HttpSecurity) getBuilder()).authorizeRequests().requestMatchers(new OrRequestMatcher(getAllPermitRequestMatchers())).permitAll();
        return getSelf();
    }

}
