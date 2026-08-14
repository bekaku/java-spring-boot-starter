package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class FilesDirectoryPathDto extends DtoId {
    //private integer level;
}
