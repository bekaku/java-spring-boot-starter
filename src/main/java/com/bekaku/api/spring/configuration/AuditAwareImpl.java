package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.dto.UserDto;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal()!=null){
            UserDto principal = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(principal!=null){
                return Optional.of(principal.getId());
            }
        }
        return Optional.empty();

    }
}
