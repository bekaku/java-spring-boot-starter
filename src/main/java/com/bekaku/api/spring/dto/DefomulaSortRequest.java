package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@JsonRootName("defomulaSortRequest")
@Data
@NoArgsConstructor
public class DefomulaSortRequest {
    private Long[] defomulaIds = new Long[]{};
}
