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
@SequenceGenerator(name = "user-chat-room-seq-generator", sequenceName = "user-chat-room-seq", allocationSize = 1, initialValue = 50)
public class UserChatRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "user-chat-room-seq-generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoomEntity;
    @OneToMany(mappedBy = "userChatRoomEntity", cascade = CascadeType.ALL)
    private List<ChatMessageEntity> chatMessageEntities = new ArrayList<>();

    @Builder
    public UserChatRoomEntity(UserEntity userEntity, ChatRoomEntity chatRoomEntity) {
        this.userEntity = userEntity;
        this.chatRoomEntity = chatRoomEntity;
    }

    public void updateUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void updateChatRoomEntity(ChatRoomEntity chatRoomEntity) {
        this.chatRoomEntity = chatRoomEntity;
    }

    public void addChatMessageEntity(ChatMessageEntity chatMessageEntity) {
        this.chatMessageEntities.add(chatMessageEntity);
        chatMessageEntity.updateUserChatRoomEntity(this);
    }
}
