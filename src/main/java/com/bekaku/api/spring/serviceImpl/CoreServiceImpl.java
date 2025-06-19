package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.service.CoreService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
public class CoreServiceImpl<T, DTO> implements CoreService<T, DTO> {

    @Override
    public DTO convertEntityToDto(T t, DTO dto) {
        if (t == null || dto == null) {
            return null;
        }

//        return modelMapper.map(t, (Type) dto.getClass());
        return null;
    }

    @Override
    public T convertDtoToEntity(T t, DTO dto) {
        if (t == null || dto == null) {
            return null;
        }

//        return modelMapper.map(dto, (Type) t.getClass());
        return null;
    }
}
