package com.example.chating.entiry.repository;

import com.example.chating.entiry.ChatRoomEntity;
import com.example.chating.entiry.UserChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoomEntity, Long> {

    List<UserChatRoomEntity> findByChatRoomEntity(ChatRoomEntity chatRoomEntity);
}
