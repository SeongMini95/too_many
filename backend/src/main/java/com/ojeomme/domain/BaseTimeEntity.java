package com.ojeomme.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    @Column(name = "create_datetime", nullable = false)
    private LocalDateTime createDatetime;

    @LastModifiedDate
    @Column(name = "modify_datetime", nullable = false)
    private LocalDateTime modifyDatetime;

    public void setDateTime(LocalDateTime createDatetime, LocalDateTime modifyDatetime) {
        this.createDatetime = createDatetime;
        this.modifyDatetime = modifyDatetime;
    }
}
