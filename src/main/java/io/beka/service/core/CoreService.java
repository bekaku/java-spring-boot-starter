package io.beka.service.core;

public interface CoreService<T, DTO> {
    public DTO convertEntityToDto(T t, DTO dto);

    public T convertDtoToEntity(T t, DTO dto);
}
