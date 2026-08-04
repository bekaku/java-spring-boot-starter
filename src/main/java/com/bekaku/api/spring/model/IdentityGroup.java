package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.AuditableCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@Table(name = "identity_group",
        indexes = {
                @Index(columnList = "created_user"),
        })
@Entity
public class IdentityGroup extends AuditableCreated<Long> {
}
