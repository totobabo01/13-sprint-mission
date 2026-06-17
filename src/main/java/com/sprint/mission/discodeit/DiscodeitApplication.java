package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 추가한 부분: AuthService Bean 가져오기
		AuthService authService = context.getBean(AuthService.class);

		// 추가한 부분: ReadStatusService Bean 가져오기
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);

		// 추가한 부분: UserStatusService Bean 가져오기
		UserStatusService userStatusService = context.getBean(UserStatusService.class);

		TestData testData = new TestData();

		testUserCrud(userService, testData);

		// 추가한 부분: 로그인 테스트
		testAuthLogin(authService);

		testChannelCrud(channelService, testData);
		testMessageCrud(userService, channelService, messageService, testData);

		// 추가한 부분: 읽음 상태 CRUD 테스트
		testReadStatusCrud(readStatusService, testData);

		// 추가한 부분: 사용자 상태 테스트
		testUserStatusCrud(userStatusService, testData);
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

	// 추가한 부분: ReadStatusRepository Bean 등록
	@Bean
	public ReadStatusRepository readStatusRepository() {
		return new FileReadStatusRepository(Path.of("data", "spring-read-statuses.ser"));
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

	// 추가한 부분: AuthService Bean 등록
	// 같은 Service 계층을 주입하지 않고 Repository를 직접 주입함
	@Bean
	public AuthService authService(
			UserRepository userRepository,
			UserStatusRepository userStatusRepository
	) {
		return new BasicAuthService(
				userRepository,
				userStatusRepository
		);
	}

	@Bean
	public ChannelService channelService(
			ChannelRepository channelRepository,
			UserRepository userRepository,
			MessageRepository messageRepository,
			ReadStatusRepository readStatusRepository
	) {
		return new BasicChannelService(
				channelRepository,
				userRepository,
				messageRepository,
				readStatusRepository
		);
	}

	@Bean
	public MessageService messageService(
			MessageRepository messageRepository,
			UserRepository userRepository,
			ChannelRepository channelRepository,
			BinaryContentRepository binaryContentRepository
	) {
		return new BasicMessageService(
				messageRepository,
				userRepository,
				channelRepository,
				binaryContentRepository
		);
	}

	// 추가한 부분: ReadStatusService Bean 등록
	// 같은 Service 계층을 주입하지 않고 Repository를 직접 주입함
	@Bean
	public ReadStatusService readStatusService(
			ReadStatusRepository readStatusRepository,
			UserRepository userRepository,
			ChannelRepository channelRepository
	) {
		return new BasicReadStatusService(
				readStatusRepository,
				userRepository,
				channelRepository
		);
	}

	// 추가한 부분: UserStatusService Bean 등록
// 같은 Service 계층을 주입하지 않고 Repository를 직접 주입함
	@Bean
	public UserStatusService userStatusService(
			UserStatusRepository userStatusRepository,
			UserRepository userRepository
	) {
		return new BasicUserStatusService(
				userStatusRepository,
				userRepository
		);
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

	// 추가한 부분: AuthService 로그인 테스트
	private static void testAuthLogin(AuthService authService) {
		System.out.println("========== 로그인 테스트 ==========");

		// user2는 testUserCrud에서 song -> seol로 수정됨
		UserResponse loginUser = authService.login(
				new LoginRequest("seol", "1234")
		);

		System.out.println("=== 로그인 성공 정보 ===");
		printUser(loginUser);

		System.out.println("=== 로그인 실패 테스트 ===");
		try {
			authService.login(new LoginRequest("seol", "wrong-password"));
			System.out.println("로그인 실패 검증 실패: 잘못된 비밀번호인데 로그인이 성공했습니다.");
		} catch (IllegalArgumentException e) {
			System.out.println("로그인 실패 검증 성공: 예상 예외가 발생했습니다.");
			System.out.println("예외 메시지: " + e.getMessage());
		}

		System.out.println();
	}

	private static void testChannelCrud(ChannelService cs, TestData testData) {
		System.out.println("========== 채널 CRUD 테스트 ==========");

		testData.channel1 = cs.createPublicChannel(
				new ChannelCreateRequest(
						ChannelType.PUBLIC,
						"공지사항",
						"서비스 공지와 업데이트 안내 채널"
				)
		);

		testData.channel2 = cs.createPublicChannel(
				new ChannelCreateRequest(
						ChannelType.PUBLIC,
						"자유게시판",
						"사용자들이 자유롭게 대화하는 채널"
				)
		);

		testData.channel3 = cs.createPublicChannel(
				new ChannelCreateRequest(
						ChannelType.PUBLIC,
						"질문답변",
						"궁금한 내용을 질문하고 답변하는 채널"
				)
		);

		testData.channel4 = cs.createPrivateChannel(
				new PrivateChannelCreateRequest(
						List.of(
								testData.user1.getId(),
								testData.user2.getId()
						)
				)
		);

		testData.channel5 = cs.createPrivateChannel(
				new PrivateChannelCreateRequest(
						List.of(
								testData.user3.getId(),
								testData.user5.getId()
						)
				)
		);

		System.out.println("=== 채널 생성 정보 ===");
		printChannel(testData.channel1);
		printChannel(testData.channel2);
		printChannel(testData.channel3);
		printChannel(testData.channel4);
		printChannel(testData.channel5);

		ChannelResponse readChannel = cs.find(testData.channel2.getId());
		System.out.println("=== 채널 단건 조회 정보 ===");
		printChannel(readChannel);

		System.out.println("=== 채널 전체 조회 정보 ===");
		printAllChannels(cs.findAllByUserId(testData.user1.getId()));

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
		printAllChannels(cs.findAllByUserId(testData.user1.getId()));
	}

	private static void testMessageCrud(
			UserService us,
			ChannelService cs,
			MessageService ms,
			TestData testData
	) {
		System.out.println("========== 메시지 CRUD 테스트 ==========");

		testData.message1 = ms.create(
				new MessageCreateRequest(
						"안녕하세요! 처음 가입했습니다.",
						testData.user1.getId(),
						testData.channel2.getId(),
						List.of(
								new BinaryContentCreateRequest(
										"hello.txt",
										"text/plain",
										"첨부파일 테스트".getBytes()
								)
						)
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

		MessageResponse readMessage = ms.read(testData.message2.getId());
		System.out.println("=== 메시지 단건 조회 정보 ===");
		printMessage(readMessage, us, cs);

		System.out.println("=== 메시지 전체 조회 정보 ===");
		printAllMessages(ms.findAllByChannelId(testData.channel2.getId()), us, cs);

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
		printAllMessages(ms.findAllByChannelId(testData.channel2.getId()), us, cs);
	}

	// 추가한 부분: ReadStatus CRUD 테스트
	private static void testReadStatusCrud(ReadStatusService rs, TestData testData) {
		System.out.println("========== 읽음 상태 CRUD 테스트 ==========");

		ReadStatusResponse readStatus1 = rs.create(
				new ReadStatusCreateRequest(
						testData.user1.getId(),
						testData.channel1.getId()
				)
		);

		ReadStatusResponse readStatus2 = rs.create(
				new ReadStatusCreateRequest(
						testData.user2.getId(),
						testData.channel2.getId()
				)
		);

		System.out.println("=== 읽음 상태 생성 정보 ===");
		printReadStatus(readStatus1);
		printReadStatus(readStatus2);

		ReadStatusResponse findReadStatus = rs.find(readStatus1.getId());
		System.out.println("=== 읽음 상태 단건 조회 정보 ===");
		printReadStatus(findReadStatus);

		System.out.println("=== 특정 사용자 읽음 상태 전체 조회 정보 ===");
		printAllReadStatuses(rs.findAllByUserId(testData.user1.getId()));

		ReadStatusResponse updateReadStatus = rs.update(
				new ReadStatusUpdateRequest(
						readStatus1.getId(),
						Instant.now()
				)
		);

		System.out.println("=== 읽음 상태 수정 정보 ===");
		printReadStatus(updateReadStatus);

		rs.delete(readStatus2.getId());
		System.out.println("=== 읽음 상태 삭제 조회 정보 ===");
		verifyDeletedReadStatus(rs, readStatus2);

		System.out.println();
	}

	// 추가한 부분: UserStatus 테스트 메서드
	private static void testUserStatusCrud(UserStatusService uss, TestData testData) {
		System.out.println("========== 사용자 상태 테스트 ==========");

		UserStatusResponse onlineStatus = uss.updateOnline(testData.user1.getId());

		System.out.println("=== 사용자 온라인 상태 변경 정보 ===");
		printUserStatus(onlineStatus);

		UserStatusResponse findStatus = uss.findByUserId(testData.user1.getId());

		System.out.println("=== 사용자 상태 단건 조회 정보 ===");
		printUserStatus(findStatus);

		UserStatusResponse offlineStatus = uss.updateOffline(testData.user1.getId());

		System.out.println("=== 사용자 오프라인 상태 변경 정보 ===");
		printUserStatus(offlineStatus);

		uss.deleteByUserId(testData.user1.getId());

		System.out.println("=== 사용자 상태 삭제 조회 정보 ===");
		verifyDeletedUserStatus(uss, testData.user1.getId());

		System.out.println();
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

	private static void printMessage(MessageResponse message, UserService us, ChannelService cs) {
		String authorName = findAuthorName(message, us);
		String channelName = findChannelName(message, cs);

		System.out.println("메시지 아이디: " + message.getId());
		System.out.println("작성자: " + authorName);
		System.out.println("채널: " + channelName);
		System.out.println("내용: " + message.getContent());
		System.out.println("===================================================");
	}

	private static void printAllMessages(List<MessageResponse> messages, UserService us, ChannelService cs) {
		System.out.println("현재 저장된 메시지 수: " + messages.size());

		for (MessageResponse message : messages) {
			printMessage(message, us, cs);
		}

		System.out.println();
	}

	// 추가한 부분: ReadStatus 출력 메서드
	private static void printReadStatus(ReadStatusResponse readStatus) {
		System.out.println("읽음 상태 아이디: " + readStatus.getId());
		System.out.println("사용자 아이디: " + readStatus.getUserId());
		System.out.println("채널 아이디: " + readStatus.getChannelId());
		System.out.println("마지막 읽은 시간: " + readStatus.getLastReadAt());
		System.out.println("===================================================");
	}

	// 추가한 부분: UserStatus 출력 메서드
	private static void printUserStatus(UserStatusResponse userStatus) {
		System.out.println("사용자 상태 아이디: " + userStatus.getId());
		System.out.println("사용자 아이디: " + userStatus.getUserId());
		System.out.println("온라인 여부: " + userStatus.isOnline());
		System.out.println("마지막 활동 시간: " + userStatus.getLastActiveAt());
		System.out.println("===================================================");
	}

	// 추가한 부분: ReadStatus 전체 출력 메서드
	private static void printAllReadStatuses(List<ReadStatusResponse> readStatuses) {
		System.out.println("현재 조회된 읽음 상태 수: " + readStatuses.size());

		for (ReadStatusResponse readStatus : readStatuses) {
			printReadStatus(readStatus);
		}

		System.out.println();
	}

	private static String findAuthorName(MessageResponse message, UserService us) {
		UserResponse author = us.read(message.getAuthorId());
		return author.getUsername();
	}

	private static String findChannelName(MessageResponse message, ChannelService cs) {
		ChannelResponse channel = cs.find(message.getChannelId());
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
			cs.find(deletedChannel.getId());
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

	// 추가한 부분: ReadStatus 삭제 검증 메서드
	private static void verifyDeletedReadStatus(ReadStatusService rs, ReadStatusResponse deletedReadStatus) {
		try {
			rs.find(deletedReadStatus.getId());
			System.out.println("삭제 실패: 삭제 후에도 읽음 상태가 조회됩니다.");
		} catch (IllegalArgumentException e) {
			System.out.println("삭제 완료: 삭제 후 읽음 상태 조회 시 예상 예외가 발생했습니다.");
			System.out.println("예외 메시지: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("삭제 검증 실패: 예상하지 못한 예외가 발생했습니다.");
			System.out.println("예외 종류: " + e.getClass().getSimpleName());
			System.out.println("예외 메시지: " + e.getMessage());
		}

		System.out.println();
	}

	// 추가한 부분: UserStatus 삭제 검증 메서드
	private static void verifyDeletedUserStatus(UserStatusService uss, java.util.UUID userId) {
		try {
			uss.findByUserId(userId);
			System.out.println("삭제 실패: 삭제 후에도 사용자 상태가 조회됩니다.");
		} catch (IllegalArgumentException e) {
			System.out.println("삭제 완료: 삭제 후 사용자 상태 조회 시 예상 예외가 발생했습니다.");
			System.out.println("예외 메시지: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("삭제 검증 실패: 예상하지 못한 예외가 발생했습니다.");
			System.out.println("예외 종류: " + e.getClass().getSimpleName());
			System.out.println("예외 메시지: " + e.getMessage());
		}

		System.out.println();
	}
}