package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.dto.FilesDirectoryDto;
import com.bekaku.api.spring.model.FilesDirectory;
import com.bekaku.api.spring.model.FilesDirectoryPath;
import com.bekaku.api.spring.model.FilesDirectoryPathId;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.FilesDirectoryPathService;
import com.bekaku.api.spring.service.FilesDirectoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/filesDirectory")
@RestController
@RequiredArgsConstructor
public class FilesDirectoryController extends BaseApiController {

    private final FilesDirectoryService filesDirectoryService;
    private final FilesDirectoryPathService filesDirectoryPathService;
    private final FileManagerService fileManagerService;
    private final I18n i18n;

    @PreAuthorize("isHasPermission('files_directory_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<FilesDirectory> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(filesDirectoryService.findAllWithSearch(specification, getPageable(pageable, FilesDirectory.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('files_directory_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody FilesDirectoryDto dto, @AuthenticationPrincipal UserDto user) {
        FilesDirectory filesDirectory = filesDirectoryService.convertDtoToEntity(dto);
        if (dto.getFilesDirectoryParentId() > 0) {
            Optional<FilesDirectory> directoryParent = filesDirectoryService.findById(dto.getFilesDirectoryParentId());
            directoryParent.ifPresent(filesDirectory::setFilesDirectoryParent);
        }

        filesDirectoryService.save(filesDirectory);
        manageFileDirectoryPath(filesDirectory);
        return this.responseEntity(filesDirectoryService.convertEntityToDto(filesDirectory), HttpStatus.OK);
    }

    private void manageFileDirectoryPath(FilesDirectory filesDirectory) {
        //create to files_directory_path level
        int level = 0;
        FilesDirectoryPathId pathId;
        if (filesDirectory.getFilesDirectoryParent() != null) {
            //create parent folder level path
            List<FilesDirectoryPath> pahtList = filesDirectoryPathService.findAllByFolderId(filesDirectory.getFilesDirectoryParent().getId());
            for (FilesDirectoryPath p : pahtList) {
                pathId = new FilesDirectoryPathId(filesDirectory.getId(), p.getFilesDirectoryPathId().getFilesDirectoryParent());
                filesDirectoryPathService.save(new FilesDirectoryPath(pathId, p.getLevel()));
                level++;
            }
        }
        //create self folder level
        pathId = new FilesDirectoryPathId(filesDirectory.getId(), filesDirectory.getId());
        filesDirectoryPathService.save(new FilesDirectoryPath(pathId, level));
    }

    @PreAuthorize("isHasPermission('files_directory_manage')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody FilesDirectoryDto dto) {
        FilesDirectory filesDirectory = filesDirectoryService.convertDtoToEntity(dto);
        Optional<FilesDirectory> oldData = filesDirectoryService.findById(dto.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        filesDirectoryService.update(filesDirectory);
        return this.responseEntity(filesDirectoryService.convertEntityToDto(filesDirectory), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('files_directory_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<FilesDirectory> filesDirectory = filesDirectoryService.findById(id);
        if (filesDirectory.isEmpty()) {
            throw this.responseErrorNotfound();
        }

        Optional<FilesDirectoryDto> directoryDto = filesDirectoryService.findDirectoryById(id);
        if (directoryDto.isPresent()) {
            return this.responseEntity(directoryDto.get(), HttpStatus.OK);
        }

//        return this.responseEntity(filesDirectoryService.convertEntityToDto(filesDirectory.get()), HttpStatus.OK);
        return this.responseEntity(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isHasPermission('files_directory_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) throws IOException {
        Optional<FilesDirectory> filesDirectory = filesDirectoryService.findById(id);
        if (filesDirectory.isEmpty()) {
            throw this.responseErrorNotfound();
        }

        //find subfolder and files
        List<FilesDirectory> subFolderList = filesDirectoryService.findAllByFilesDirectoryParent(filesDirectory.get());
        for (FilesDirectory f : subFolderList) {
            //delete file manager in this folder
            fileManagerService.deleteAllFileByFilesDirectory(f);

            //delete from folder path
            filesDirectoryPathService.deleteByFolderId(f.getId());
            filesDirectoryPathService.deleteByParentFolderId(f.getId());

            //delete sub folder
            filesDirectoryService.delete(f);
        }

        //delete from folder path
        filesDirectoryPathService.deleteByFolderId(id);
        filesDirectoryPathService.deleteByParentFolderId(id);

        fileManagerService.deleteAllFileByFilesDirectory(filesDirectory.get());
        filesDirectoryService.deleteById(id);

        return this.responseDeleteMessage();
    }
}
