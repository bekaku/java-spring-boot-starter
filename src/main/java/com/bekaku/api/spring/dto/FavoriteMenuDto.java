package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonRootName("favoriteMenu")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class FavoriteMenuDto {
    @NotEmpty(message = "{error.NotEmpty}")
    private String url;
}
