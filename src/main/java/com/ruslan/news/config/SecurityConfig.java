package com.ruslan.news.config;



import com.ruslan.news.service.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //конфигурация Spring Security и авторизация + CSRF token(он тут идет по умолчанию если не отключить явно.
        //при создании view HTML CSRF нужно будет указать при помощи thymeleaf
        http.authorizeRequests()
                .antMatchers("/login", "/error").permitAll()
                .anyRequest().hasAnyRole("USER")
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/hello", true)  //после успешной аутентификации сюда будет редирект вместо хелло надо что-то другое
                .failureUrl("/login?error");
    }

    //настройка аутентификации
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(personDetailsService);
    }
        //шифрование пароля, чтобы его не видеть(хэширование) - пароли нужно шифровать, но это для регистрации,
        //а её по заданию делать не нужно.
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
