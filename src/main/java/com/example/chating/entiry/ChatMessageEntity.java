package com.example.chating.entiry;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "chat-generator", sequenceName = "chat-seq", allocationSize = 1, initialValue = 50)
public class ChatMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "chat-generator")
    private Long id;
    @Column(nullable = false)
    private String context;
    private boolean show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_chat_room_id")
    private UserChatRoomEntity userChatRoomEntity;

    @Builder
    public ChatMessageEntity(String context) {
        this.context = context;
    }

    public void updateUserChatRoomEntity(UserChatRoomEntity userChatRoomEntity) {
        this.userChatRoomEntity = userChatRoomEntity;
    }
}
