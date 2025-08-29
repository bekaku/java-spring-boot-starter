package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.bekaku.api.spring.vo.DirectoryPathVo;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonRootName("filesDirectory")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class FilesDirectoryDto extends DtoId {

    private boolean active;
    private Long filesDirectoryParentId;

    @NotEmpty(message = "{error.NotEmpty}")
    private String name;
    List<DirectoryPathVo> paths = new ArrayList<>();

    private Long fileSize;

    @JsonIgnore
    private Long ownerId;
    @JsonIgnore
    private Long updatedUserId;

    // optional
    private List<Long> directoryPathIds = new ArrayList<>();
    private List<String> directoryPathNames = new ArrayList<>();
}
