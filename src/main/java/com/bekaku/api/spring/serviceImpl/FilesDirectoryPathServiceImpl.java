package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.FilesDirectoryPathDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.FilesDirectoryPathMapper;
import com.bekaku.api.spring.model.FilesDirectoryPath;
import com.bekaku.api.spring.repository.FilesDirectoryPathRepository;
import com.bekaku.api.spring.service.FilesDirectoryPathService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class FilesDirectoryPathServiceImpl implements FilesDirectoryPathService {
    private final FilesDirectoryPathRepository filesDirectoryPathRepository;
    private final FilesDirectoryPathMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FilesDirectoryPathDto> findAllWithPaging(Pageable pageable) {
        Page<FilesDirectoryPath> result = filesDirectoryPathRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<FilesDirectoryPathDto> findAllWithSearch(SearchSpecification<FilesDirectoryPath> specification, Pageable pageable) {
        Page<FilesDirectoryPath> result = filesDirectoryPathRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<FilesDirectoryPathDto> findAllBy(Specification<FilesDirectoryPath> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FilesDirectoryPath> findAllPageSpecificationBy(Specification<FilesDirectoryPath> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FilesDirectoryPath> findAllPageSearchSpecificationBy(SearchSpecification<FilesDirectoryPath> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<FilesDirectoryPathDto> getListFromResult(Page<FilesDirectoryPath> result){
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }
    @Transactional(readOnly = true)
    @Override
    public List<FilesDirectoryPath> findAll() {
        return filesDirectoryPathRepository.findAll();
    }


    public FilesDirectoryPath save(FilesDirectoryPath filesDirectoryPath) {
        return filesDirectoryPathRepository.save(filesDirectoryPath);
    }

    @Override
    public FilesDirectoryPath update(FilesDirectoryPath filesDirectoryPath) {
        return filesDirectoryPathRepository.save(filesDirectoryPath);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FilesDirectoryPath> findById(Long id) {
        return filesDirectoryPathRepository.findById(id);
    }

    @Override
    public void delete(FilesDirectoryPath filesDirectoryPath) {
        filesDirectoryPathRepository.delete(filesDirectoryPath);
    }

    @Override
    public void deleteById(Long id) {
        filesDirectoryPathRepository.deleteById(id);
    }

    @Override
    public FilesDirectoryPathDto convertEntityToDto(FilesDirectoryPath filesDirectoryPath) {
        return modelMapper.toDto(filesDirectoryPath);
    }

    @Override
    public FilesDirectoryPath convertDtoToEntity(FilesDirectoryPathDto filesDirectoryPathDto) {
        return modelMapper.toEntity(filesDirectoryPathDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FilesDirectoryPath> findAllByFolderId(Long folderId) {
        return filesDirectoryPathRepository.findAllByFolderId(folderId);
    }

    @Override
    public void deleteByFolderId(Long folderId) {
        filesDirectoryPathRepository.deleteByFolderId(folderId);
    }

    @Override
    public void deleteByParentFolderId(Long parentFolderId) {
        filesDirectoryPathRepository.deleteByParentFolderId(parentFolderId);
    }
}
