package com.grandats.api.givedeefive.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.enumtype.LoginLogType;
import com.grandats.api.givedeefive.model.superclass.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@GenSourceableTable(createDto = false, createPermission = false)
@Getter
@Setter
@Entity
@JsonRootName("userLoginLogs")
@Table(name = "user_login_logs",
        indexes = {
                @Index(columnList = "login_date"),
                @Index(columnList = "loginFrom"),
        }
)
@NoArgsConstructor
public class UserLoginLogs extends Id {

    public UserLoginLogs(User user, LocalDateTime loginDate, LoginLogType loginFrom) {
        this.user = user;
        this.loginDate = loginDate;
        this.loginFrom = loginFrom;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "login_date", columnDefinition = "datetime", nullable = false)
    private LocalDateTime loginDate;

    @Column(columnDefinition = "tinyint(1) default 1")//1=Web, 2=ios , 3 = andriod
    private LoginLogType loginFrom;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "loginDate");
    }
}
