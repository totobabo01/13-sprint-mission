package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		TestData testData = new TestData();

		testUserCrud(userService, testData);
		testChannelCrud(channelService, testData);
		testMessageCrud(userService, channelService, messageService, testData);
	}

	@Bean
	public UserRepository userRepository() {
		return new FileUserRepository(Path.of("data", "spring-users.ser"));
	}

	@Bean
	public ChannelRepository channelRepository() {
		return new FileChannelRepository(Path.of("data", "spring-channels.ser"));
	}

	@Bean
	public MessageRepository messageRepository() {
		return new FileMessageRepository(Path.of("data", "spring-messages.ser"));
	}

	@Bean
	public BinaryContentRepository binaryContentRepository() {
		return new FileBinaryContentRepository(Path.of("data", "spring-binary-contents.ser"));
	}

	@Bean
	public UserStatusRepository userStatusRepository() {
		return new FileUserStatusRepository(Path.of("data", "spring-user-statuses.ser"));
	}

	@Bean
	public UserService userService(
			UserRepository userRepository,
			BinaryContentRepository binaryContentRepository,
			UserStatusRepository userStatusRepository
	) {
		return new BasicUserService(
				userRepository,
				binaryContentRepository,
				userStatusRepository
		);
	}

	@Bean
	public ChannelService channelService(ChannelRepository channelRepository) {
		return new BasicChannelService(channelRepository);
	}

	@Bean
	public MessageService messageService(
			MessageRepository messageRepository,
			UserRepository userRepository,
			ChannelRepository channelRepository
	) {
		return new BasicMessageService(messageRepository, userRepository, channelRepository);
	}

	static class TestData {
		UserResponse user1;
		UserResponse user2;
		UserResponse user3;
		UserResponse user4;
		UserResponse user5;

		ChannelResponse channel1;
		ChannelResponse channel2;
		ChannelResponse channel3;
		ChannelResponse channel4;
		ChannelResponse channel5;

		// 수정한 부분: Message 엔티티가 아니라 MessageResponse 사용
		MessageResponse message1;
		MessageResponse message2;
		MessageResponse message3;
		MessageResponse message4;
		MessageResponse message5;
	}

	private static void testUserCrud(UserService us, TestData testData) {
		System.out.println("========== 사용자 CRUD 테스트 ==========");

		testData.user1 = us.create(new UserCreateRequest("jang", "jang@email.com", "1234", null));
		testData.user2 = us.create(new UserCreateRequest("song", "song@email.com", "1234", null));
		testData.user3 = us.create(new UserCreateRequest("kim", "kim@email.com", "1234", null));
		testData.user4 = us.create(new UserCreateRequest("lee", "lee@email.com", "1234", null));
		testData.user5 = us.create(new UserCreateRequest("moon", "moon@email.com", "1234", null));

		System.out.println("=== 사용자 생성 정보 ===");
		printUser(testData.user1);
		printUser(testData.user2);
		printUser(testData.user3);
		printUser(testData.user4);
		printUser(testData.user5);

		UserResponse readUser = us.read(testData.user2.getId());
		System.out.println("=== 사용자 단건 조회 정보 ===");
		printUser(readUser);

		System.out.println("=== 사용자 전체 조회 정보 ===");
		printAllUsers(us.readAll());

		UserResponse updateUser = us.update(
				new UserUpdateRequest(
						testData.user2.getId(),
						"seol",
						"seol@email.com",
						"1234",
						null
				)
		);

		System.out.println("=== 사용자 수정 조회 정보 ===");
		printUser(updateUser);

		us.delete(testData.user4.getId());
		System.out.println("=== 사용자 삭제 조회 정보 ===");
		verifyDeletedUser(us, testData.user4);

		System.out.println("=== 삭제 후 사용자 전체 조회 정보 ===");
		printAllUsers(us.readAll());
	}

	private static void testChannelCrud(ChannelService cs, TestData testData) {
		System.out.println("========== 채널 CRUD 테스트 ==========");

		testData.channel1 = cs.create(
				new ChannelCreateRequest(
						ChannelType.PUBLIC,
						"공지사항",
						"서비스 공지와 업데이트 안내 채널"
				)
		);

		testData.channel2 = cs.create(
				new ChannelCreateRequest(
						ChannelType.PUBLIC,
						"자유게시판",
						"사용자들이 자유롭게 대화하는 채널"
				)
		);

		testData.channel3 = cs.create(
				new ChannelCreateRequest(
						ChannelType.PUBLIC,
						"질문답변",
						"궁금한 내용을 질문하고 답변하는 채널"
				)
		);

		testData.channel4 = cs.create(
				new ChannelCreateRequest(
						ChannelType.PRIVATE,
						"운영진회의",
						"운영진만 접근 가능한 회의 채널"
				)
		);

		testData.channel5 = cs.create(
				new ChannelCreateRequest(
						ChannelType.PRIVATE,
						"프로젝트팀",
						"프로젝트 팀원 전용 협업 채널"
				)
		);

		System.out.println("=== 채널 생성 정보 ===");
		printChannel(testData.channel1);
		printChannel(testData.channel2);
		printChannel(testData.channel3);
		printChannel(testData.channel4);
		printChannel(testData.channel5);

		ChannelResponse readChannel = cs.read(testData.channel2.getId());
		System.out.println("=== 채널 단건 조회 정보 ===");
		printChannel(readChannel);

		System.out.println("=== 채널 전체 조회 정보 ===");
		printAllChannels(cs.readAll());

		ChannelResponse updateChannel = cs.update(
				new ChannelUpdateRequest(
						testData.channel2.getId(),
						ChannelType.PRIVATE,
						"비밀게시판",
						"사용자들이 은밀하게 대화하는 채널"
				)
		);

		System.out.println("=== 채널 수정 조회 정보 ===");
		printChannel(updateChannel);

		cs.delete(testData.channel3.getId());
		System.out.println("=== 채널 삭제 조회 정보 ===");
		verifyDeletedChannel(cs, testData.channel3);

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

		// 수정한 부분: create는 이제 MessageCreateRequest DTO를 받음
		testData.message1 = ms.create(
				new MessageCreateRequest(
						"안녕하세요! 처음 가입했습니다.",
						testData.user1.getId(),
						testData.channel2.getId()
				)
		);

		testData.message2 = ms.create(
				new MessageCreateRequest(
						"오늘 업데이트 내용 확인했습니다.",
						testData.user2.getId(),
						testData.channel1.getId()
				)
		);

		testData.message3 = ms.create(
				new MessageCreateRequest(
						"Java 인터페이스 구현이 조금 헷갈립니다.",
						testData.user3.getId(),
						testData.channel2.getId()
				)
		);

		testData.message4 = ms.create(
				new MessageCreateRequest(
						"프로젝트 일정 공유드립니다.",
						testData.user3.getId(),
						testData.channel5.getId()
				)
		);

		testData.message5 = ms.create(
				new MessageCreateRequest(
						"운영진 회의는 몇 시에 시작하나요?",
						testData.user5.getId(),
						testData.channel4.getId()
				)
		);

		System.out.println("=== 메시지 생성 정보 ===");
		printMessage(testData.message1, us, cs);
		printMessage(testData.message2, us, cs);
		printMessage(testData.message3, us, cs);
		printMessage(testData.message4, us, cs);
		printMessage(testData.message5, us, cs);

		// 수정한 부분: read는 MessageResponse 반환
		MessageResponse readMessage = ms.read(testData.message2.getId());
		System.out.println("=== 메시지 단건 조회 정보 ===");
		printMessage(readMessage, us, cs);

		System.out.println("=== 메시지 전체 조회 정보 ===");
		printAllMessages(ms.readAll(), us, cs);

		// 수정한 부분: update는 이제 MessageUpdateRequest DTO를 받음
		MessageResponse updateMessage = ms.update(
				new MessageUpdateRequest(
						testData.message2.getId(),
						"오늘 업데이트 내용을 확인하지 못했습니다."
				)
		);

		System.out.println("=== 메시지 수정 조회 정보 ===");
		printMessage(updateMessage, us, cs);

		ms.delete(testData.message4.getId());
		System.out.println("=== 메시지 삭제 조회 정보 ===");
		verifyDeletedMessage(ms, testData.message4);

		System.out.println("=== 삭제 후 메시지 전체 조회 정보 ===");
		printAllMessages(ms.readAll(), us, cs);
	}

	private static void printUser(UserResponse user) {
		System.out.println("아이디: " + user.getId());
		System.out.println("사용자 이름: " + user.getUsername());
		System.out.println("사용자 이메일: " + user.getEmail());
		System.out.println("===================================================");
	}

	private static void printAllUsers(List<UserResponse> users) {
		System.out.println("현재 저장된 사용자 수: " + users.size());

		for (UserResponse user : users) {
			printUser(user);
		}

		System.out.println();
	}

	private static void printChannel(ChannelResponse channel) {
		System.out.println("채널 아이디: " + channel.getId());
		System.out.println("채널 종류: " + channel.getType());
		System.out.println("채널 이름: " + channel.getName());
		System.out.println("채널 설명: " + channel.getDescription());
		System.out.println("===================================================");
	}

	private static void printAllChannels(List<ChannelResponse> channels) {
		System.out.println("현재 저장된 채널 수: " + channels.size());

		for (ChannelResponse channel : channels) {
			printChannel(channel);
		}

		System.out.println();
	}

	// 수정한 부분: Message 엔티티가 아니라 MessageResponse 출력
	private static void printMessage(MessageResponse message, UserService us, ChannelService cs) {
		String authorName = findAuthorName(message, us);
		String channelName = findChannelName(message, cs);

		System.out.println("메시지 아이디: " + message.getId());
		System.out.println("작성자: " + authorName);
		System.out.println("채널: " + channelName);
		System.out.println("내용: " + message.getContent());
		System.out.println("===================================================");
	}

	// 수정한 부분: List<Message>가 아니라 List<MessageResponse>
	private static void printAllMessages(List<MessageResponse> messages, UserService us, ChannelService cs) {
		System.out.println("현재 저장된 메시지 수: " + messages.size());

		for (MessageResponse message : messages) {
			printMessage(message, us, cs);
		}

		System.out.println();
	}

	private static String findAuthorName(MessageResponse message, UserService us) {
		UserResponse author = us.read(message.getAuthorId());
		return author.getUsername();
	}

	private static String findChannelName(MessageResponse message, ChannelService cs) {
		ChannelResponse channel = cs.read(message.getChannelId());
		return channel.getName();
	}

	private static void verifyDeletedUser(UserService us, UserResponse deletedUser) {
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

	private static void verifyDeletedChannel(ChannelService cs, ChannelResponse deletedChannel) {
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

	// 수정한 부분: 삭제 검증 대상도 MessageResponse
	private static void verifyDeletedMessage(MessageService ms, MessageResponse deletedMessage) {
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