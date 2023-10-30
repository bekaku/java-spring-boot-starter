package com.grandats.api.givedeefive.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CodeNameDto extends DtoId {
    private String code;
    private String name;
}
