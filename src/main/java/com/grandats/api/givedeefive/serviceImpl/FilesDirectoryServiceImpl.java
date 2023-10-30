package com.grandats.api.givedeefive.serviceImpl;

import com.grandats.api.givedeefive.dto.ResponseListDto;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.vo.DirectoryPathVo;
import com.grandats.api.givedeefive.dto.FilesDirectoryDto;
import com.grandats.api.givedeefive.mapper.FilesDirectoryMapper;
import com.grandats.api.givedeefive.model.FilesDirectory;
import com.grandats.api.givedeefive.repository.FilesDirectoryRepository;
import com.grandats.api.givedeefive.service.FilesDirectoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final FilesDirectoryMapper filesDirectoryMapper;


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
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
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
        return modelMapper.map(filesDirectory, FilesDirectoryDto.class);
    }

    @Override
    public FilesDirectory convertDtoToEntity(FilesDirectoryDto filesDirectoryDto) {
        return modelMapper.map(filesDirectoryDto, FilesDirectory.class);
    }

    @Override
    public List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent) {
        return filesDirectoryRepository.findAllByFilesDirectoryParent(filesDirectoryParent);
    }

    @Override
    public Optional<FilesDirectoryDto> findDirectoryById(Long id) {
        Optional<FilesDirectoryDto> dto = filesDirectoryMapper.findById(id);
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
