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
@SQLDelete(sql = "UPDATE sub_district SET deleted = true WHERE id = ?")
@Table(name = "sub_district")
@Getter
@Setter
@Entity
public class SubDistrict extends SoftDeletedAuditable<Long> {

    @Column(nullable = false)
    private String name;

    @Column
    private String nameEn;

    @Column
    private int zipCode;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district", nullable = false)
    private District district;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

    @Override
    public String toString() {
        return "SubDistrict{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
