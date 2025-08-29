package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@GenSourceableTable(createPermission = false)
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "favorite_menu")
public class FavoriteMenu extends Id {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user")
    private AppUser appUser;
    private String url;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "id");
    }
}
