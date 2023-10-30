package com.grandats.api.givedeefive.serviceImpl;

import com.grandats.api.givedeefive.dto.FilesDirectoryPathDto;
import com.grandats.api.givedeefive.dto.ResponseListDto;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.model.FilesDirectoryPath;
import com.grandats.api.givedeefive.repository.FilesDirectoryPathRepository;
import com.grandats.api.givedeefive.service.FilesDirectoryPathService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
    private final ModelMapper modelMapper;

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
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
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
        return modelMapper.map(filesDirectoryPath, FilesDirectoryPathDto.class);
    }

    @Override
    public FilesDirectoryPath convertDtoToEntity(FilesDirectoryPathDto filesDirectoryPathDto) {
        return modelMapper.map(filesDirectoryPathDto, FilesDirectoryPath.class);
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
