package com.example.chating.entiry;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "user-entity-seq-generator", sequenceName = "user-entity-seq", initialValue = 1, allocationSize = 50)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "user-entity-seq-generator")
    private long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "userEntity")
    private List<UserChatRoomEntity> userChatRoomEntities = new ArrayList<>();

    @Builder
    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addUserChatRoom(UserChatRoomEntity userChatRoomEntity) {
        userChatRoomEntities.add(userChatRoomEntity);
        userChatRoomEntity.updateUserEntity(this);
    }

}
