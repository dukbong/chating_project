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
@SequenceGenerator(name = "chat-room-entity-generator", sequenceName = "chat-room-seq", allocationSize = 1, initialValue = 50)
public class ChatRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "chat-room-entity-generator")
    private Long id;
    @Column(nullable = false, unique = true)
    private String roomId;
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "chatRoomEntity")
    private List<UserChatRoomEntity> userChatRoomEntities = new ArrayList<>();

    @Builder
    public ChatRoomEntity(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void addUserChatRoomEntity(UserChatRoomEntity userChatRoomEntity) {
        userChatRoomEntities.add(userChatRoomEntity);
        userChatRoomEntity.updateChatRoomEntity(this);
    }

}
