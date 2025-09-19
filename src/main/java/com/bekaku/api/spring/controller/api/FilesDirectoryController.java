package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.FilesDirectoryDto;
import com.bekaku.api.spring.model.FilesDirectory;
import com.bekaku.api.spring.model.FilesDirectoryPath;
import com.bekaku.api.spring.model.FilesDirectoryPathId;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.FilesDirectoryPathService;
import com.bekaku.api.spring.service.FilesDirectoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final AppUserService appUserService;
    private final I18n i18n;

    @PreAuthorize("@permissionChecker.hasPermission('files_directory_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<FilesDirectory> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(filesDirectoryService.findAllWithSearch(specification, getPageable(pageable, FilesDirectory.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('files_directory_manage')")
    @PostMapping
    public FileManagerDto create(@Valid @RequestBody FilesDirectoryDto dto, @AuthenticationPrincipal AppUserDto user) {

        AppUser appUser = appUserService.findAndValidateAppUserBy(user);

        FilesDirectory filesDirectory = filesDirectoryService.convertDtoToEntity(dto);
        if (dto.getFilesDirectoryParentId() > 0) {
            Optional<FilesDirectory> directoryParent = filesDirectoryService.findById(dto.getFilesDirectoryParentId());
            directoryParent.ifPresent(filesDirectory::setFilesDirectoryParent);
        }

        //validate duplicate name
        filesDirectoryService.validateDuplicateName(appUser, dto.getName(), filesDirectory.getFilesDirectoryParent());
        filesDirectory.setOwner(appUser);
        filesDirectoryService.save(filesDirectory);
        manageFileDirectoryPath(filesDirectory);
        return fileManagerService.setVoToDto(filesDirectory);
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

    @PreAuthorize("@permissionChecker.hasPermission('files_directory_manage')")
    @PutMapping("/{id}")
    public FileManagerDto update(@PathVariable("id") long id, @AuthenticationPrincipal AppUserDto user, @Valid @RequestBody FilesDirectoryDto dto) {
        Optional<FilesDirectory> oldData = filesDirectoryService.findByIdAndOwnerId(id, user.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound(i18n.getMessage("model.filesDirectory"));
        }
        oldData.get().onUpdate(dto.getName());
        filesDirectoryService.update(oldData.get());
        return fileManagerService.setVoToDto(oldData.get());
    }

    @PreAuthorize("@permissionChecker.hasPermission('files_directory_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id, @AuthenticationPrincipal AppUserDto user) {
        Optional<FilesDirectoryDto> directoryDto = filesDirectoryService.findDtoByIdAndOwnerId(id, user.getId());
        if (directoryDto.isEmpty()) {
            throw this.responseErrorNotfound(i18n.getMessage("model.filesDirectory"));
        }
        return this.responseEntity(directoryDto.get(), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('files_directory_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id, @AuthenticationPrincipal AppUserDto user) throws IOException {
        Optional<FilesDirectory> filesDirectory = filesDirectoryService.findByIdAndOwnerId(id, user.getId());
        if (filesDirectory.isEmpty()) {
            throw this.responseErrorNotfound(i18n.getMessage("model.filesDirectory"));
        }

        //find subfolder and files
        /*
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
         */
        Page<FilesDirectory> page;
        int pageNumber = 0;
        do {
            page = filesDirectoryService.findAllByFilesDirectoryParent(filesDirectory.get(), PageRequest.of(pageNumber++, 20));
            page.forEach(folder -> {
                //delete file manager in this folder
                fileManagerService.deleteAllFileByFilesDirectory(folder);
                //delete from folder path
                filesDirectoryPathService.deleteByFolderId(folder.getId());
                filesDirectoryPathService.deleteByParentFolderId(folder.getId());
                //delete sub folder
                filesDirectoryService.delete(folder);
            });
        } while (!page.isEmpty());

        //delete from folder path
        filesDirectoryPathService.deleteByFolderId(id);
        filesDirectoryPathService.deleteByParentFolderId(id);

        fileManagerService.deleteAllFileByFilesDirectory(filesDirectory.get());
        filesDirectoryService.deleteById(id);

        return this.responseDeleteMessage();
    }
}
