package tr.gov.gib.evdbelge.evdbelgeaktarma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApiSecurityConfig {
    @Value("${api.admin.userName}")
    private String adminUserName;
    @Value("${api.admin.password}")
    private String adminPassword;
    @Value("${api.admin.role}")
    private String adminRole;
    @Value("${api.evdbelge.userName}")
    private String evdbelgeUserName;
    @Value("${api.evdbelge.password}")
    private String evdbelgePassword;
    @Value("${api.evdbelge.role}")
    private String evdbelgeRole;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CustomAuthentiationProvider customAuthProvider) throws Exception {
        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/actuator/**", "/monitorRequest").permitAll()
                .antMatchers("/aaa", "/bbb").hasAnyAuthority(adminRole, evdbelgeRole)
                .antMatchers(
                        "/info"
                        ).hasAuthority(adminRole)
                .anyRequest().authenticated()
                .and().authenticationManager(new ProviderManager(customAuthProvider)).httpBasic();
        return httpSecurity.build();
    }
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User
                .withUsername(adminUserName)
                .password("{noop}" + adminPassword)
                .authorities(adminRole)
                .build();
        UserDetails user2 = User
                .withUsername(evdbelgeUserName)
                .password("{noop}" + evdbelgePassword)
                .authorities(evdbelgeRole)
                .build();
        return new InMemoryUserDetailsManager(user, user2);
    }
}
