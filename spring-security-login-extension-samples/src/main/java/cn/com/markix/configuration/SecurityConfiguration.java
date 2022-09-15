package cn.com.markix.configuration;

import cn.com.markix.dao.UserDao;
import cn.com.markix.security.UserDetailsServiceImpl;
import cn.com.markix.security.email.EmailAuthProcessor;
import cn.com.markix.security.email.EmailCodeService;
import cn.com.markix.security.email.EmailCodeServiceImpl;
import cn.com.markix.security.no.NoAuthProcessor;
import cn.com.markix.security.phone.PhoneAuthProcessor;
import cn.com.markix.security.phone.PhoneCodeService;
import cn.com.markix.security.phone.PhoneCodeServiceImpl;
import cn.com.markix.security.username.NameAuthProcessor;
import cn.com.markix.spring.security.extension.MultipleAuthenticationFilterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author markix
 */
@Configuration
@EnableWebSecurity//(debug = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                //忽略鉴权
                .ignoring()
                .antMatchers("/error")
                // 静态资源 css/js/images
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        ;
    }

    @Autowired
    private UserDao userDao;

    @Bean
    public EmailCodeService emailCodeService() {
        return new EmailCodeServiceImpl();
    }

    @Bean
    public PhoneCodeService phoneCodeService() {
        return new PhoneCodeServiceImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // TIPS 自定义登录认证方式配置
        http.apply(new MultipleAuthenticationFilterConfigurer<>())
//                .loginPage("login.html")
//                .loginProcessingUrl("/multi-login")
//                .defaultSuccessUrl("/")
//                .failureUrl("login.html?error")
                // 扩展 邮箱+验证码登录
                .addAuthProcessor(new EmailAuthProcessor(userDao, emailCodeService()))
                .addPermitUrl("/email/code")
                // 扩展 手机号+验证码登录
                .addAuthProcessor(new PhoneAuthProcessor(userDao, phoneCodeService()))
                .addPermitUrl("/phone/code")
                // 扩展 工号+密码登录
                .addAuthProcessor(new NoAuthProcessor(userDao, passwordEncoder()))
                // 扩展 用户名+密码登录
                .addAuthProcessor(new NameAuthProcessor(userDao, passwordEncoder()))
                // 放行认证相关路径
                .permitAll()
        ;

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // 自定义登录认证已经可以支持用户名+密码登录，不需要内置的登录了
//                .formLogin()
//                .and()
                .rememberMe()
                .and()
                .csrf().disable();
    }

}
