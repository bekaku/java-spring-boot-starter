package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.configuration.AuditListener;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

//@EntityListeners(AuditListener.class)
//@GenSourceableTable()
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE province SET deleted = true WHERE id = ?")
@Table(name = "province")
@Getter
@Setter
@Entity
public class Province extends SoftDeletedAuditable<Long> {

    public void onUpdate(String name, String nameEn) {
        this.name = name;
        this.nameEn = nameEn;
    }

    @Column
    private String name;

    @Column
    private String nameEn;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

    @Override
    public String toString() {
        return "Province{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
