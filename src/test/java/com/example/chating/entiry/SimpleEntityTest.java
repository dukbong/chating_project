package com.example.chating.entiry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.chating.entiry.repository.ChatMessageRepository;
import com.example.chating.entiry.repository.ChatRoomRepository;
import com.example.chating.entiry.repository.UserChatRoomRepository;
import com.example.chating.entiry.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    
    @Test
    @Transactional
    @DisplayName("A 사용자와 B 사용자가 C 채팅방에서 대화 후 A 사용자가 채팅방 삭제 시 C 채팅방에는 B 사용자의 대화 내용만 남아있다.")
    void chatDelete() {
    	String roomId = UUID.randomUUID().toString();
    	UserEntity userA = simpleCreateUserEntity("A", "A1!");
    	UserEntity userB = simpleCreateUserEntity("B", "B1!");
    	UserChatRoomEntity userChatRoom1 = new UserChatRoomEntity();
    	UserChatRoomEntity userChatRoom2 = new UserChatRoomEntity();
    	ChatRoomEntity chatRoom1 = simpleCreateChatRoomEntity(roomId, "room1");
    	
    	userRepository.save(userA);
    	userRepository.save(userB);
    	
    	chatRoomRepository.save(chatRoom1);
    	
    	userChatRoomRepository.save(userChatRoom1);
    	userChatRoomRepository.save(userChatRoom2);
    	
    	userA.addUserChatRoom(userChatRoom1);
    	chatRoom1.addUserChatRoomEntity(userChatRoom1);
    	userB.addUserChatRoom(userChatRoom2);
    	chatRoom1.addUserChatRoomEntity(userChatRoom2);
    	
    	ChatMessageEntity chatMessageEntityA1 = ChatMessageEntity.builder()
    			.context("A1")
    			.build();
    	ChatMessageEntity chatMessageEntityA2 = ChatMessageEntity.builder()
    			.context("A2")
    			.build();
    	ChatMessageEntity chatMessageEntityB = ChatMessageEntity.builder()
    			.context("B1")
    			.build();
    	
    	chatMessageRepository.save(chatMessageEntityA1);
    	chatMessageRepository.save(chatMessageEntityB);
    	chatMessageRepository.save(chatMessageEntityA2);
    	
    	userChatRoom1.addChatMessageEntity(chatMessageEntityA1);
    	userChatRoom2.addChatMessageEntity(chatMessageEntityB);
    	userChatRoom1.addChatMessageEntity(chatMessageEntityA2);
    	
    	List<UserChatRoomEntity> findUserChatRoomEntity = userChatRoomRepository.findByChatRoomEntity(chatRoom1);
    	
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
    	
        // 테스트에서는 저장 시간이 같을 수 있기 떄문에 시간 순서로 테스트시 오류가 난다.
        assertThat(result).hasSize(3)
        .containsExactlyInAnyOrder(
                Map.of("username", "A", "message", "A1", "createdAt", chatMessageEntityA1.getCreatedAt().toString()),
                Map.of("username", "B", "message", "B1", "createdAt", chatMessageEntityB.getCreatedAt().toString()),
                Map.of("username", "A", "message", "A2", "createdAt", chatMessageEntityA2.getCreatedAt().toString())
        );
    	
    	userChatRoomRepository.deleteById(userChatRoom1.getId());
    	
    	List<UserChatRoomEntity> AfterDeleteUserChatRoomEntity = userChatRoomRepository.findByChatRoomEntity(chatRoom1);
    	
        List<Map<String, String>> result2 = AfterDeleteUserChatRoomEntity.stream()
                .flatMap(userChatRoom -> userChatRoom.getChatMessageEntities().stream()
                        .map(chatMessage -> Map.of(
                                "username", userChatRoom.getUserEntity().getUsername(),
                                "message", chatMessage.getContext(),
                                "createdAt", chatMessage.getCreatedAt().toString() // createdAt도 추가
                        ))
                )
                .sorted(Comparator.comparing(chatMap -> chatMap.get("createdAt")))
                .toList();
    	
        assertThat(result2).hasSize(1)
        .containsExactly(
            Map.of("username", "B", "message", "B1", "createdAt", chatMessageEntityB.getCreatedAt().toString())
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