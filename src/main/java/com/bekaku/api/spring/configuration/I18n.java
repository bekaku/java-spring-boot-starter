package com.bekaku.api.spring.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18n {

    /**
     * Optionally pass to getMessage
     */
    public static final Object[] NO_ARGS = new String[]{};

    @Autowired
    private MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Value("${spring.mvc.locale}")
    String defaultLocale;

    @PostConstruct
    private void init() {
//        accessor = new MessageSourceAccessor(messageSource, LocaleContextHolder.getLocale());
//        accessor = new MessageSourceAccessor(messageSource);
        accessor = new MessageSourceAccessor(messageSource, new Locale(defaultLocale));
    }

//    public String getMessage(String code, Object... args) {
//        try {
//            // LocaleContextHolder.getLocale() pulls the locale identified by
//            // the AcceptHeaderLocaleResolver for the current thread/request
//            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
//        } catch (Exception e) {
//            // Return the code itself so you can see what is missing in your logs
//            return "???" + code + "???";
//        }
//    }
//
//    public String getMessage(String code) {
//        return getMessage(code, (Object[]) null);
//    }
    public String getMessage(String code, Object... args) {
        try {
            return accessor.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
//            return null;
            return "???" + code + "???";
        }
    }

    public String getMessage(String code) {
        return getMessage(code, NO_ARGS);
    }
}
