package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.FilesDirectoryDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.FilesDirectoryMapper;
import com.bekaku.api.spring.model.FilesDirectory;
import com.bekaku.api.spring.mybatis.FilesDirectoryMybatis;
import com.bekaku.api.spring.repository.FilesDirectoryRepository;
import com.bekaku.api.spring.service.FilesDirectoryService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.vo.DirectoryPathVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class FilesDirectoryServiceImpl implements FilesDirectoryService {
    private final FilesDirectoryRepository filesDirectoryRepository;
    private final FilesDirectoryMapper modelMapper;
    private final FilesDirectoryMybatis filesDirectoryMybatis;


    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FilesDirectoryDto> findAllWithPaging(Pageable pageable) {
        Page<FilesDirectory> result = filesDirectoryRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<FilesDirectoryDto> findAllWithSearch(SearchSpecification<FilesDirectory> specification, Pageable pageable) {
        Page<FilesDirectory> result = filesDirectoryRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<FilesDirectoryDto> findAllBy(Specification<FilesDirectory> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FilesDirectory> findAllPageSpecificationBy(Specification<FilesDirectory> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FilesDirectory> findAllPageSearchSpecificationBy(SearchSpecification<FilesDirectory> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<FilesDirectoryDto> getListFromResult(Page<FilesDirectory> result){
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<FilesDirectory> findAll() {
        return filesDirectoryRepository.findAll();
    }


    public FilesDirectory save(FilesDirectory filesDirectory) {
        return filesDirectoryRepository.save(filesDirectory);
    }

    @Override
    public FilesDirectory update(FilesDirectory filesDirectory) {
        return filesDirectoryRepository.save(filesDirectory);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FilesDirectory> findById(Long id) {
        return filesDirectoryRepository.findById(id);
    }

    @Override
    public void delete(FilesDirectory filesDirectory) {
        filesDirectoryRepository.delete(filesDirectory);
    }

    @Override
    public void deleteById(Long id) {
        filesDirectoryRepository.deleteById(id);
    }

    @Override
    public FilesDirectoryDto convertEntityToDto(FilesDirectory filesDirectory) {
        return modelMapper.toDto(filesDirectory);
    }

    @Override
    public FilesDirectory convertDtoToEntity(FilesDirectoryDto filesDirectoryDto) {
        return modelMapper.toEntity(filesDirectoryDto);
    }

    @Override
    public List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent) {
        return filesDirectoryRepository.findAllByFilesDirectoryParent(filesDirectoryParent);
    }

    @Override
    public Optional<FilesDirectoryDto> findDirectoryById(Long id) {
        Optional<FilesDirectoryDto> dto = filesDirectoryMybatis.findById(id);
        List<DirectoryPathVo> paths = new ArrayList<>();
        if (dto.isPresent()) {
            paths.add(new DirectoryPathVo(null, null, dto.get().getDirectoryPathIds().isEmpty(), true));
            dto.get().setPaths(paths);
            if (!dto.get().getDirectoryPathIds().isEmpty()) {
                for (int i = 0; i < dto.get().getDirectoryPathIds().size(); i++) {
                    Long pathId = dto.get().getDirectoryPathIds().get(i);
                    String pathName = dto.get().getDirectoryPathNames().get(i);
                    paths.add(new DirectoryPathVo(pathId, pathName, i == (dto.get().getDirectoryPathIds().size() - 1), false));
                }
                dto.get().getDirectoryPathIds().clear();
                dto.get().getDirectoryPathNames().clear();
            }
        }
        return dto;
    }
}
