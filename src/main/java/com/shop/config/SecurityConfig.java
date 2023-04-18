package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin()
//                .loginPage("/members/login")
//                .defaultSuccessUrl("/")
//                .usernameParameter("userID")
//                .failureUrl("/members/login/error")
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
//                .logoutSuccessUrl("/");
//
//
//        http.authorizeRequests()//인증요청
//                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() //해당경로는 모든 경우
//                .mvcMatchers("/admin/**").hasRole("ADMIN") //admin 경로는 권한이 admin인 경우
//                .anyRequest().authenticated()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
//                .and()
//                .oauth2Login()
//                .defaultSuccessUrl("/")
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService);
//
//        http.exceptionHandling() //예외 핸들링은 만들어놓은 CAEP를 통해 함
//                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("Email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
        ;

        http.authorizeRequests()
                .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**","/notice/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/")
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception{
//
//        web.ignoring().antMatchers("/css/**","/js/**","/img/**"); //이 경로에 있는 것들은 시큐리티에서 제외
//    }


}