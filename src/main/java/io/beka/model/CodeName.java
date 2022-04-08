package io.beka.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
public class CodeName extends BaseId {
}
