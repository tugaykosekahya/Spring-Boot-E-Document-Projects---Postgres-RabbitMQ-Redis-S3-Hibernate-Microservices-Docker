//package tr.gov.gib.evdbelge.evdbelgegoruntuleme.config;
//
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.stereotype.Component;
//import tr.gov.gib.tahsilat.thsexception.custom.GibAuthException;
//import tr.gov.gib.tahsilat.thslogging.GibLogger;
//import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
//
//import java.util.Collection;
//
//@Component
//public class CustomAuthentiationProvider implements AuthenticationProvider {
//    @Autowired
//    InMemoryUserDetailsManager inMemoryUserDetailsManager;
//    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
//    @SneakyThrows
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        if(authentication == null || inMemoryUserDetailsManager == null) {
//            LOGGER.error("authentication inMemoryUserDetailsManager null.!!!");
//            throw new GibAuthException("Authentication başarısız.", "");
//        }
//        String userid = authentication.getName();
//        String password = authentication.getCredentials().toString();
//        if(!inMemoryUserDetailsManager.userExists(userid)) {
//            LOGGER.error("Kullanıcı bulunamadı! KK: " + userid);
//            throw new GibAuthException("", "");
//        }
//        String psw = inMemoryUserDetailsManager.loadUserByUsername(userid).getPassword().split("\\{noop\\}")[1];
//        if(!psw.equals(password)) {
//            LOGGER.error("Şifre yanlış! KK: " + userid + " PSW: " + password);
//            throw new GibAuthException("", "");
//        }
//        Authentication auth = null;
//        Collection<? extends GrantedAuthority> authorityList = inMemoryUserDetailsManager.loadUserByUsername(userid).getAuthorities();
//        try {
//            auth = new UsernamePasswordAuthenticationToken(userid, password, authorityList);
//        } catch (Exception e) {
//            LOGGER.error("Authentication başarısız. KK: " + userid + " PSW: " + password, e);
//            throw new GibAuthException("Authentication başarısız.", "");
//        }
//        return auth;
//
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}
