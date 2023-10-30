package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.grandats.api.givedeefive.vo.DirectoryPathVo;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonRootName("filesDirectory")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class FilesDirectoryDto {

    private Long id;
    private boolean active;
    private Long filesDirectoryParentId;

    @NotEmpty(message = "{error.NotEmpty}")
    private String name;
    List<DirectoryPathVo> paths = new ArrayList<>();

    // optional
    private List<Long> directoryPathIds = new ArrayList<>();
    private List<String> directoryPathNames = new ArrayList<>();
}
