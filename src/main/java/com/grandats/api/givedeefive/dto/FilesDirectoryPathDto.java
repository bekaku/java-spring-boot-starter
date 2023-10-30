package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonRootName("filesDirectoryPath")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class FilesDirectoryPathDto{
    private Long id;
    //private integer level;
}
