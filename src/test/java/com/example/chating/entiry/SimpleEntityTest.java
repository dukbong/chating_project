package com.example.chating.entiry;

import com.example.chating.entiry.repository.ChatMessageRepository;
import com.example.chating.entiry.repository.ChatRoomRepository;
import com.example.chating.entiry.repository.UserChatRoomRepository;
import com.example.chating.entiry.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@Transactional
class SimpleEntityTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    UserChatRoomRepository userChatRoomRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("UserEntity 저장 및 조회 테스트")
    void saveAndFindUser() {
        // given
        UserEntity userEntity = simpleCreateUserEntity("test1", "test1!");

        // when
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // then
        Optional<UserEntity> findUserEntity = userRepository.findByUsername("test1");
        assertThat(findUserEntity.isPresent()).isTrue();
        assertThat(findUserEntity.get().getUsername()).isEqualTo("test1");
    }

    @Test
    @DisplayName("UserEntity - UserChatRoomEntity - ChatRoomEntity 관계 테스트")
    void AddChatRoomToUserEntity() {
        // given
        String roomId = UUID.randomUUID().toString();
        UserEntity userEntity1 = simpleCreateUserEntity("test1", "test1!");
        UserEntity userEntity2 = simpleCreateUserEntity("test2", "test2!");
        ChatRoomEntity chatRoomEntity1 = simpleCreateChatRoomEntity(roomId, "room1");

//        UserChatRoomEntity userChatRoom1 = UserChatRoomEntity.builder()
//                .userEntity(userEntity1)
//                .chatRoomEntity(chatRoomEntity1)
//                .build();
//        UserChatRoomEntity userChatRoom2 = UserChatRoomEntity.builder()
//                .userEntity(userEntity2)
//                .chatRoomEntity(chatRoomEntity1)
//                .build();
        UserChatRoomEntity userChatRoom1 = new UserChatRoomEntity();
        UserChatRoomEntity userChatRoom2 = new UserChatRoomEntity();
        // when
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        chatRoomRepository.save(chatRoomEntity1);
        userChatRoomRepository.save(userChatRoom1);
        userChatRoomRepository.save(userChatRoom2);

        userEntity1.addUserChatRoom(userChatRoom1);
        chatRoomEntity1.addUserChatRoomEntity(userChatRoom1);

        userEntity2.addUserChatRoom(userChatRoom2);
        chatRoomEntity1.addUserChatRoomEntity(userChatRoom2);

        // then
        Optional<ChatRoomEntity> result = chatRoomRepository.findByName("room1");
        assertThat(result.isPresent()).isTrue();
        ChatRoomEntity resultChatRoomEntity = result.get();

        List<String> usernames = resultChatRoomEntity.getUserChatRoomEntities().stream()
                .map(userChatRoomEntity -> userChatRoomEntity.getUserEntity().getUsername())
                .toList();

        assertThat(usernames).hasSize(2)
                .containsExactlyInAnyOrder("test1", "test2");

    }

    @Test
    @DisplayName("채팅 메시지 전송 및 조회 테스트")
    void sendChatMessageAndFindMessage() {
        // given
        String roomId = UUID.randomUUID().toString();
        UserEntity userEntity1 = simpleCreateUserEntity("test1", "test1!");
        UserEntity userEntity2 = simpleCreateUserEntity("test2", "test2!");
        ChatRoomEntity chatRoomEntity1 = simpleCreateChatRoomEntity(roomId, "room1");
        UserChatRoomEntity userChatRoom1 = new UserChatRoomEntity();
        UserChatRoomEntity userChatRoom2 = new UserChatRoomEntity();

        ChatMessageEntity chatMessageEntity1 = ChatMessageEntity.builder().context("test1가 전송한 메시지").build();
        ChatMessageEntity chatMessageEntity2 = ChatMessageEntity.builder().context("test2가 전송한 메시지").build();

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        chatRoomRepository.save(chatRoomEntity1);

        userChatRoomRepository.save(userChatRoom1);
        userChatRoomRepository.save(userChatRoom2);

        // 유저 2명이 한방에서 채팅 시작
        userEntity1.addUserChatRoom(userChatRoom1);
        chatRoomEntity1.addUserChatRoomEntity(userChatRoom1);

        userEntity2.addUserChatRoom(userChatRoom2);
        chatRoomEntity1.addUserChatRoomEntity(userChatRoom2);

        // 채팅 내용 생성
        chatMessageRepository.save(chatMessageEntity1);
        chatMessageRepository.save(chatMessageEntity2);

        userChatRoom1.addChatMessageEntity(chatMessageEntity1);
        userChatRoom2.addChatMessageEntity(chatMessageEntity2);

        // when
        Optional<ChatRoomEntity> findChatRoomEntity  = chatRoomRepository.findByName("room1");
        assertThat(findChatRoomEntity.isPresent()).isTrue();
        ChatRoomEntity resultChatRoomEntity = findChatRoomEntity.get();

        List<UserChatRoomEntity> findUserChatRoomEntity = userChatRoomRepository.findByChatRoomEntity(resultChatRoomEntity);
        assertThat(findUserChatRoomEntity).hasSize(2);

        List<Map<String, String>> result = findUserChatRoomEntity.stream()
                .flatMap(userChatRoom -> userChatRoom.getChatMessageEntities().stream()
                        .map(chatMessage -> Map.of(
                                "username", userChatRoom.getUserEntity().getUsername(),
                                "message", chatMessage.getContext(),
                                "createdAt", chatMessage.getCreatedAt().toString() // createdAt도 추가
                        ))
                )
                .sorted(Comparator.comparing(chatMap -> chatMap.get("createdAt")))
                .toList();

        assertThat(result).hasSize(2)
                .containsExactly(
                        Map.of("username", "test1", "message", "test1가 전송한 메시지", "createdAt", chatMessageEntity1.getCreatedAt().toString()),
                        Map.of("username", "test2", "message", "test2가 전송한 메시지", "createdAt", chatMessageEntity2.getCreatedAt().toString())
                );

    }

    private UserEntity simpleCreateUserEntity(String username, String password) {
        return UserEntity.builder()
                .username(username)
                .password(password)
                .build();
    }

    private ChatRoomEntity simpleCreateChatRoomEntity(String roomId, String name) {
        return ChatRoomEntity.builder()
                .roomId(roomId)
                .name(name)
                .build();
    }
}