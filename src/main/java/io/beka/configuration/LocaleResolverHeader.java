package io.beka.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleResolverHeader {
    @Value("${spring.mvc.locale}")
    String defaultLocale;

    List<Locale> LOCALES = Arrays.asList(new Locale("en"),
            new Locale("th"));

//    @Override
//    public Locale resolveLocale(HttpServletRequest request) {
//        if (ObjectUtils.isEmpty(request.getHeader("Accept-Language"))) {
//            return LocaleContextHolder.getLocale();
//        }
//        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader("Accept-Language"));
//        return Locale.lookup(list, LOCALES);
//    }

    @Bean
    public LocaleResolver localeResolver() {

        // Seeion
//        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//        localeResolver.setDefaultLocale(new Locale(defaultLocale));

        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(new Locale(defaultLocale));

        //Cookie
//        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
//        localeResolver.setDefaultLocale(new Locale(defaultLocale));

        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
