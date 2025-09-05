package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.FilesDirectoryDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.mapper.FilesDirectoryMapper;
import com.bekaku.api.spring.model.AppUser;
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
public class FilesDirectoryServiceImpl extends BaseResponseException implements FilesDirectoryService {
    private final FilesDirectoryRepository filesDirectoryRepository;
    private final FilesDirectoryMapper modelMapper;
    private final FilesDirectoryMybatis filesDirectoryMybatis;
    private final I18n i18n;

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

    private ResponseListDto<FilesDirectoryDto> getListFromResult(Page<FilesDirectory> result) {
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

    @Transactional(readOnly = true)
    @Override
    public Optional<FilesDirectory> findByIdAndOwnerId(Long id, Long appUserId) {
        return filesDirectoryRepository.findByIdAndOwnerId(id, appUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent) {
        return filesDirectoryRepository.findAllByFilesDirectoryParent(filesDirectoryParent);
    }

    @Override
    public Page<FilesDirectory> findAllByFilesDirectoryParent(FilesDirectory filesDirectoryParent, Pageable pageable) {
        return filesDirectoryRepository.findAllByFilesDirectoryParent(filesDirectoryParent, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FilesDirectoryDto> findDirectoryById(Long id) {
        return filesDirectoryMybatis.findById(id).flatMap(this::setDtoBy);
    }

    private Optional<FilesDirectoryDto> setDtoBy(FilesDirectoryDto dto) {
        List<DirectoryPathVo> paths = new ArrayList<>();
        if (dto != null) {
            paths.add(new DirectoryPathVo(null, null, dto.getDirectoryPathIds().isEmpty(), true));
            dto.setPaths(paths);
            if (!dto.getDirectoryPathIds().isEmpty()) {
                for (int i = 0; i < dto.getDirectoryPathIds().size(); i++) {
                    Long pathId = dto.getDirectoryPathIds().get(i);
                    String pathName = dto.getDirectoryPathNames().get(i);
                    paths.add(new DirectoryPathVo(pathId, pathName, i == (dto.getDirectoryPathIds().size() - 1), false));
                }
                dto.getDirectoryPathIds().clear();
                dto.getDirectoryPathNames().clear();
            }
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FilesDirectoryDto> findDtoByIdAndOwnerId(Long id, Long ownerId) {
        return filesDirectoryMybatis.findByIdAndOwnerId(id, ownerId).flatMap(this::setDtoBy);
    }

    @Override
    public void validateFolderOwner(AppUser appUser, FilesDirectory folder) {
        if (!appUser.getId().equals(folder.getOwner().getId())) {
            throw this.responseErrorForbidden(i18n.getMessage("error.folder.forbidden"));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public FilesDirectory validateFolderOwnerAndGetBy(AppUser appUser, Long folderID) {
        Optional<FilesDirectory> directoryExist = filesDirectoryRepository.findByOwnerAndId(appUser, folderID);
        if (directoryExist.isEmpty()) {
            throw this.responseErrorForbidden(i18n.getMessage("error.folder.forbidden"));
        }
        return directoryExist.get();
    }

    @Transactional(readOnly = true)
    @Override
    public void validateDuplicateName(AppUser appUser, String name, FilesDirectory filesDirectoryParent) {
        Optional<FilesDirectory> directoryExist;
        if (filesDirectoryParent != null) {
            validateFolderOwner(appUser, filesDirectoryParent);
            directoryExist = filesDirectoryRepository.findByNameAndFilesDirectoryParent(name, filesDirectoryParent);
        } else {
            directoryExist = filesDirectoryRepository.findByNameAndOwnerAndFilesDirectoryParentIsNull(name, appUser);
        }
        if (directoryExist.isPresent()) {
            throw this.responseErrorDuplicate(i18n.getMessage("model.filesDirectory"));
        }
    }
}
