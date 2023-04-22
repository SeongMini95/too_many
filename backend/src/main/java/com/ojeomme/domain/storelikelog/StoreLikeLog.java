package com.ojeomme.domain.storelikelog;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "store_like_log")
public class StoreLikeLog extends BaseTimeEntity implements Persistable<StoreLikeLogId> {

    @EmbeddedId
    private StoreLikeLogId id;

    @MapsId("storeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public StoreLikeLog(Store store, User user) {
        this.store = store;
        this.user = user;
        this.id = StoreLikeLogId.builder()
                .storeId(store.getId())
                .userId(user.getId())
                .build();
    }

    @Override
    public boolean isNew() {
        return getCreateDatetime() == null;
    }
}
