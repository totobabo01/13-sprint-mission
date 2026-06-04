package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.nio.file.Path;
import java.util.List;

public class JavaApplication {

    // 테스트 저장 방식 선택
    // JCF 테스트: StorageType.JCF
    // File 테스트: StorageType.FILE
    private static final StorageType STORAGE_TYPE = StorageType.FILE;

    enum StorageType {
        JCF,
        FILE
    }

    static class ServiceBundle {
        UserService userService;
        ChannelService channelService;
        MessageService messageService;

        ServiceBundle(UserService userService, ChannelService channelService, MessageService messageService) {
            this.userService = userService;
            this.channelService = channelService;
            this.messageService = messageService;
        }
    }

    static class TestData {
        User user1;
        User user2;
        User user3;
        User user4;
        User user5;

        Channel channel1;
        Channel channel2;
        Channel channel3;
        Channel channel4;
        Channel channel5;

        Message message1;
        Message message2;
        Message message3;
        Message message4;
        Message message5;
    }

    public static void main(String[] args) {
        ServiceBundle services = createServices(STORAGE_TYPE);
        TestData testData = new TestData();

        System.out.println("===========================================");
        System.out.println("현재 테스트 저장 방식: " + STORAGE_TYPE);
        System.out.println("===========================================");
        System.out.println();

        testUserCrud(services.userService, testData);
        testChannelCrud(services.channelService, testData);
        testMessageCrud(
                services.userService,
                services.channelService,
                services.messageService,
                testData
        );
    }

    private static ServiceBundle createServices(StorageType storageType) {
        UserRepository userRepository;
        ChannelRepository channelRepository;
        MessageRepository messageRepository;

        if (storageType == StorageType.JCF) {
            userRepository = new JCFUserRepository();
            channelRepository = new JCFChannelRepository();
            messageRepository = new JCFMessageRepository();
        } else {
            userRepository = new FileUserRepository(Path.of("data", "basic-users.ser"));
            channelRepository = new FileChannelRepository(Path.of("data", "basic-channels.ser"));
            messageRepository = new FileMessageRepository(Path.of("data", "basic-messages.ser"));
        }

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(
                messageRepository,
                userRepository,
                channelRepository
        );

        return new ServiceBundle(userService, channelService, messageService);
    }

    private static void testUserCrud(UserService us, TestData testData) {
        System.out.println("========== 사용자 CRUD 테스트 ==========");

        // 사용자 생성
        testData.user1 = us.create("jang", "jang@email.com", "1234");
        testData.user2 = us.create("song", "song@email.com", "1234");
        testData.user3 = us.create("kim", "kim@email.com", "1234");
        testData.user4 = us.create("lee", "lee@email.com", "1234");
        testData.user5 = us.create("moon", "moon@email.com", "1234");

        System.out.println("=== 사용자 생성 정보 ===");
        printUser(testData.user1);
        printUser(testData.user2);
        printUser(testData.user3);
        printUser(testData.user4);
        printUser(testData.user5);

        // 사용자 단건 조회
        User readUser = us.read(testData.user2.getId());
        System.out.println("=== 사용자 단건 조회 정보 ===");
        printUser(readUser);

        // 사용자 전체 조회
        System.out.println("=== 사용자 전체 조회 정보 ===");
        printAllUsers(us.readAll());

        // 사용자 수정
        User updateUser = us.update(testData.user2.getId(), "seol", "seol@email.com", "1234");
        System.out.println("=== 사용자 수정 조회 정보 ===");
        printUser(updateUser);

        // 사용자 삭제
        us.delete(testData.user4.getId());
        System.out.println("=== 사용자 삭제 조회 정보 ===");

        // 피드백 반영: 삭제 검증 try-catch를 별도 메서드로 분리
        // 예외가 발생하면 무조건 삭제 완료라고만 출력하지 않고,
        // 어떤 예외 메시지가 발생했는지 함께 출력해서 테스트 의도를 명확히 함
        verifyDeletedUser(us, testData.user4);

        // 삭제 후 전체 조회
        System.out.println("=== 삭제 후 사용자 전체 조회 정보 ===");
        printAllUsers(us.readAll());
    }

    private static void testChannelCrud(ChannelService cs, TestData testData) {
        System.out.println("========== 채널 CRUD 테스트 ==========");

        // 채널 생성
        testData.channel1 = cs.create(ChannelType.PUBLIC, "공지사항", "서비스 공지와 업데이트 안내 채널");
        testData.channel2 = cs.create(ChannelType.PUBLIC, "자유게시판", "사용자들이 자유롭게 대화하는 채널");
        testData.channel3 = cs.create(ChannelType.PUBLIC, "질문답변", "궁금한 내용을 질문하고 답변하는 채널");
        testData.channel4 = cs.create(ChannelType.PRIVATE, "운영진회의", "운영진만 접근 가능한 회의 채널");
        testData.channel5 = cs.create(ChannelType.PRIVATE, "프로젝트팀", "프로젝트 팀원 전용 협업 채널");

        System.out.println("=== 채널 생성 정보 ===");
        printChannel(testData.channel1);
        printChannel(testData.channel2);
        printChannel(testData.channel3);
        printChannel(testData.channel4);
        printChannel(testData.channel5);

        // 채널 단건 조회
        Channel readChannel = cs.read(testData.channel2.getId());
        System.out.println("=== 채널 단건 조회 정보 ===");
        printChannel(readChannel);

        // 채널 전체 조회
        System.out.println("=== 채널 전체 조회 정보 ===");
        printAllChannels(cs.readAll());

        // 채널 수정
        Channel updateChannel = cs.update(
                testData.channel2.getId(),
                ChannelType.PRIVATE,
                "비밀게시판",
                "사용자들이 은밀하게 대화하는 채널"
        );

        System.out.println("=== 채널 수정 조회 정보 ===");
        printChannel(updateChannel);

        // 채널 삭제
        cs.delete(testData.channel3.getId());
        System.out.println("=== 채널 삭제 조회 정보 ===");

        // 피드백 반영: 삭제 검증 try-catch를 별도 메서드로 분리
        verifyDeletedChannel(cs, testData.channel3);

        // 삭제 후 전체 조회
        System.out.println("=== 삭제 후 채널 전체 조회 정보 ===");
        printAllChannels(cs.readAll());
    }

    private static void testMessageCrud(
            UserService us,
            ChannelService cs,
            MessageService ms,
            TestData testData
    ) {
        System.out.println("========== 메시지 CRUD 테스트 ==========");

        // 메시지 생성
        testData.message1 = ms.create("안녕하세요! 처음 가입했습니다.", testData.user1.getId(), testData.channel2.getId());
        testData.message2 = ms.create("오늘 업데이트 내용 확인했습니다.", testData.user2.getId(), testData.channel1.getId());
        testData.message3 = ms.create("Java 인터페이스 구현이 조금 헷갈립니다.", testData.user3.getId(), testData.channel2.getId());
        testData.message4 = ms.create("프로젝트 일정 공유드립니다.", testData.user3.getId(), testData.channel5.getId());
        testData.message5 = ms.create("운영진 회의는 몇 시에 시작하나요?", testData.user5.getId(), testData.channel4.getId());

        System.out.println("=== 메시지 생성 정보 ===");
        printMessage(testData.message1, us, cs);
        printMessage(testData.message2, us, cs);
        printMessage(testData.message3, us, cs);
        printMessage(testData.message4, us, cs);
        printMessage(testData.message5, us, cs);

        // 메시지 단건 조회
        Message readMessage = ms.read(testData.message2.getId());
        System.out.println("=== 메시지 단건 조회 정보 ===");
        printMessage(readMessage, us, cs);

        // 메시지 전체 조회
        System.out.println("=== 메시지 전체 조회 정보 ===");
        printAllMessages(ms.readAll(), us, cs);

        // 메시지 수정
        Message updateMessage = ms.update(testData.message2.getId(), "오늘 업데이트 내용을 확인하지 못했습니다.");
        System.out.println("=== 메시지 수정 조회 정보 ===");
        printMessage(updateMessage, us, cs);

        // 메시지 삭제
        ms.delete(testData.message4.getId());
        System.out.println("=== 메시지 삭제 조회 정보 ===");

        // 피드백 반영: 삭제 검증 try-catch를 별도 메서드로 분리
        verifyDeletedMessage(ms, testData.message4);

        // 삭제 후 전체 조회
        System.out.println("=== 삭제 후 메시지 전체 조회 정보 ===");
        printAllMessages(ms.readAll(), us, cs);
    }

    private static void printUser(User user) {
        System.out.println("아이디: " + user.getId());
        System.out.println("사용자 이름: " + user.getUsername());
        System.out.println("사용자 이메일: " + user.getEmail());
        System.out.println("===================================================");
    }

    private static void printAllUsers(List<User> users) {
        System.out.println("현재 저장된 사용자 수: " + users.size());

        for (User user : users) {
            printUser(user);
        }

        System.out.println();
    }

    private static void printChannel(Channel channel) {
        System.out.println("채널 아이디: " + channel.getId());
        System.out.println("채널 종류: " + channel.getType());
        System.out.println("채널 이름: " + channel.getName());
        System.out.println("채널 설명: " + channel.getDescription());
        System.out.println("===================================================");
    }

    private static void printAllChannels(List<Channel> channels) {
        System.out.println("현재 저장된 채널 수: " + channels.size());

        for (Channel channel : channels) {
            printChannel(channel);
        }

        System.out.println();
    }

    private static void printMessage(Message message, UserService us, ChannelService cs) {
        // 피드백 반영: 메시지 출력 시 작성자/채널 조회 로직을 보조 메서드로 분리
        // 기존에는 printMessage 안에서 us.read(...), cs.read(...)를 직접 반복 호출했지만,
        // 이제 findAuthorName(), findChannelName()으로 분리해서 출력 코드의 중복과 복잡도를 줄임
        String authorName = findAuthorName(message, us);
        String channelName = findChannelName(message, cs);

        System.out.println("메시지 아이디: " + message.getId());
        System.out.println("작성자: " + authorName);
        System.out.println("채널: " + channelName);
        System.out.println("내용: " + message.getContent());
        System.out.println("===================================================");
    }

    private static void printAllMessages(List<Message> messages, UserService us, ChannelService cs) {
        System.out.println("현재 저장된 메시지 수: " + messages.size());

        for (Message message : messages) {
            printMessage(message, us, cs);
        }

        System.out.println();
    }

    // 피드백 반영: 메시지의 작성자 이름 조회를 별도 메서드로 분리
    private static String findAuthorName(Message message, UserService us) {
        User author = us.read(message.getAuthorId());
        return author.getUsername();
    }

    // 피드백 반영: 메시지의 채널 이름 조회를 별도 메서드로 분리
    private static String findChannelName(Message message, ChannelService cs) {
        Channel channel = cs.read(message.getChannelId());
        return channel.getName();
    }

    // 피드백 반영: 사용자 삭제 검증 로직 분리
    private static void verifyDeletedUser(UserService us, User deletedUser) {
        try {
            us.read(deletedUser.getId());
            System.out.println("삭제 실패: 삭제 후에도 사용자가 조회됩니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("삭제 완료: 삭제 후 사용자 조회 시 예상 예외가 발생했습니다.");
            System.out.println("예외 메시지: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("삭제 검증 실패: 예상하지 못한 예외가 발생했습니다.");
            System.out.println("예외 종류: " + e.getClass().getSimpleName());
            System.out.println("예외 메시지: " + e.getMessage());
        }

        System.out.println();
    }

    // 피드백 반영: 채널 삭제 검증 로직 분리
    private static void verifyDeletedChannel(ChannelService cs, Channel deletedChannel) {
        try {
            cs.read(deletedChannel.getId());
            System.out.println("삭제 실패: 삭제 후에도 채널이 조회됩니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("삭제 완료: 삭제 후 채널 조회 시 예상 예외가 발생했습니다.");
            System.out.println("예외 메시지: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("삭제 검증 실패: 예상하지 못한 예외가 발생했습니다.");
            System.out.println("예외 종류: " + e.getClass().getSimpleName());
            System.out.println("예외 메시지: " + e.getMessage());
        }

        System.out.println();
    }

    // 피드백 반영: 메시지 삭제 검증 로직 분리
    private static void verifyDeletedMessage(MessageService ms, Message deletedMessage) {
        try {
            ms.read(deletedMessage.getId());
            System.out.println("삭제 실패: 삭제 후에도 메시지가 조회됩니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("삭제 완료: 삭제 후 메시지 조회 시 예상 예외가 발생했습니다.");
            System.out.println("예외 메시지: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("삭제 검증 실패: 예상하지 못한 예외가 발생했습니다.");
            System.out.println("예외 종류: " + e.getClass().getSimpleName());
            System.out.println("예외 메시지: " + e.getMessage());
        }

        System.out.println();
    }
}