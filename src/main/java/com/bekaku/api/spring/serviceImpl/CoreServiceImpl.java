package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.service.CoreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
public class CoreServiceImpl<T, DTO> implements CoreService<T, DTO> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public DTO convertEntityToDto(T t, DTO dto) {
        if (t == null || dto == null) {
            return null;
        }

        return modelMapper.map(t, (Type) dto.getClass());
    }

    @Override
    public T convertDtoToEntity(T t, DTO dto) {
        if (t == null || dto == null) {
            return null;
        }

        return modelMapper.map(dto, (Type) t.getClass());
    }
}
