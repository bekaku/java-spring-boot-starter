package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.dto.AppUserDto;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        AppUserDto principal = (AppUserDto) authentication.getPrincipal();
        if (principal != null) {
            return Optional.of(principal.getId());
        }
        return Optional.empty();

//        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal()!=null){
//            AppUserDto principal = (AppUserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            if(principal!=null){
//                return Optional.of(principal.getId());
//            }
//        }
//        return Optional.empty();

    }
}
