package com.bekaku.api.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration(proxyBeanMethods = false)
public class LocaleResolverHeader {
    @Value("${spring.mvc.locale}")
    String defaultLocale;

    List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("th"));

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
        // Set your supported locales so the resolver knows what it's allowed to pick
        localeResolver.setSupportedLocales(Arrays.asList(new Locale("en"), new Locale("th")));
//        localeResolver.setDefaultLocale(new Locale(defaultLocale));

        // Ensure defaultLocale isn't null/empty from properties
        localeResolver.setDefaultLocale(new Locale(defaultLocale != null ? defaultLocale : "en"));
        //Cookie
//        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
//        localeResolver.setDefaultLocale(new Locale(defaultLocale));

        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
//        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
//        messageSource.setBasenames("i18n/messages");
        messageSource.setBasenames(
                "classpath:/i18n/messages",
                "classpath:/i18n/error/messages",
                "classpath:/i18n/model/messages",
                "classpath:/i18n/permission/messages");
        messageSource.setDefaultEncoding("UTF-8");
//        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
