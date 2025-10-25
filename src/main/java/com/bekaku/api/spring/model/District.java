package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.configuration.AuditListener;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;


//@EntityListeners(AuditListener.class)
//@GenSourceableTable()
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE district SET deleted = true WHERE id = ?")
@Table(name = "district")
@Getter
@Setter
@Entity
public class District extends SoftDeletedAuditable<Long> {

    public void onUpdate(String name, String nameEn) {
        this.name = name;
        this.nameEn = nameEn;
    }

    @Column(nullable = false)
    private String name;

    @Column
    private String nameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province", nullable = false)
    private Province province;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

    @Override
    public String toString() {
        return "Districts{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                '}';
    }
}
