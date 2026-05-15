package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService us = new JCFUserService();
        ChannelService cs = new JCFChannelService();
        MessageService ms = new JCFMessageService(us, cs);

        // 사용자 입력
        User user1 = us.create("jang" ,"jang@email.com", "1234");
        User user2 = us.create("song" ,"song@email.com", "1234");
        User user3 = us.create("kim" ,"kim@email.com", "1234");
        User user4 = us.create("lee" ,"lee@email.com", "1234");
        User user5 = us.create("moon" ,"moon@email.com", "1234");

        // 사용자 생성
        System.out.println("=== 사용자 생성 정보 ===");
        System.out.println("아이디: " + user1.getId());
        System.out.println("사용자 이름: " + user1.getUsername());
        System.out.println("사용자 이메일: " + user1.getEmail());
        System.out.println("===================================================");

        System.out.println("아이디: " + user2.getId());
        System.out.println("사용자 이름: " + user2.getUsername());
        System.out.println("사용자 이메일: " + user2.getEmail());
        System.out.println("===================================================");

        System.out.println("아이디: " + user3.getId());
        System.out.println("사용자 이름: " + user3.getUsername());
        System.out.println("사용자 이메일: " + user3.getEmail());
        System.out.println("===================================================");

        System.out.println("아이디: " + user4.getId());
        System.out.println("사용자 이름: " + user4.getUsername());
        System.out.println("사용자 이메일: " + user4.getEmail());
        System.out.println("===================================================");

        System.out.println("아이디: " + user5.getId());
        System.out.println("사용자 이름: " + user5.getUsername());
        System.out.println("사용자 이메일: " + user5.getEmail());
        System.out.println("===================================================");
        System.out.println();

        // 사용자 단건 조회
        User readUser = us.read(user2.getId());
        System.out.println("=== 사용자 단건 조회 정보 ===");
        System.out.println("아이디: " + readUser.getId());
        System.out.println("사용자 이름: " + readUser.getUsername());
        System.out.println("사용자 이메일: " + readUser.getEmail());
        System.out.println();

        // 사용자 전체 조회
        List<User> allUsers = us.readAll();
        System.out.println("=== 사용자 전체 조회 정보 ===");
        for(int i = 0; i < allUsers.size(); i++) {
            System.out.println("아이디: " + allUsers.get(i).getId());
            System.out.println("사용자 이름: " + allUsers.get(i).getUsername());
            System.out.println("사용자 이메일: " + allUsers.get(i).getEmail());
            System.out.println("===================================================");
        }
        System.out.println();

        // 사용자 수정
        User updateUser = us.update(user2.getId(), "seol", "seol@email.com", "1234");
        System.out.println("=== 사용자 수정 조회 정보 ===");
        System.out.println("아이디: " + updateUser.getId());
        System.out.println("사용자 이름: " + updateUser.getUsername());
        System.out.println("사용자 이메일: " + updateUser.getEmail());
        System.out.println();

        // 사용자 삭제
        us.delete(user4.getId());
        System.out.println("=== 사용자 삭제 조회 정보 ===");

       try {
           us.read(user4.getId());
           System.out.println("삭제 실패");
       } catch (IllegalArgumentException e) {
           System.out.println("삭제 완료");
       }

        System.out.println();

        // 삭제 후 다시 정보 조회
        System.out.println("=== 삭제 후 사용자 전체 조회 정보 ===");

        List<User> allUsers2 = us.readAll();
        for(int i = 0; i < allUsers2.size(); i++) {
            System.out.println("아이디: " + allUsers2.get(i).getId());
            System.out.println("사용자 이름: " + allUsers2.get(i).getUsername());
            System.out.println("사용자 이메일: " + allUsers2.get(i).getEmail());
            System.out.println("===================================================");
        }

        System.out.println();

        // 채널 입력
        Channel channel1 = cs.create(ChannelType.PUBLIC, "공지사항", "서비스 공지와 업데이트 안내 채널");
        Channel channel2 = cs.create(ChannelType.PUBLIC, "자유게시판", "사용자들이 자유롭게 대화하는 채널");
        Channel channel3 = cs.create(ChannelType.PUBLIC, "질문답변", "궁금한 내용을 질문하고 답변하는 채널");
        Channel channel4 = cs.create(ChannelType.PRIVATE, "운영진회의", "운영진만 접근 가능한 회의 채널");
        Channel channel5 = cs.create(ChannelType.PRIVATE, "프로젝트팀", "프로젝트 팀원 전용 협업 채널");

        //  채널 생성
        System.out.println("=== 채널 생성 정보 ===");
        System.out.println("채널 아이디: " + channel1.getId());
        System.out.println("채널 종류: " + channel1.getType());
        System.out.println("채널 이름: " + channel1.getName());
        System.out.println("채널 뜻: " + channel1.getDescription());
        System.out.println("===================================================");

        System.out.println("채널 아이디: " + channel2.getId());
        System.out.println("채널 종류: " + channel2.getType());
        System.out.println("채널 이름: " + channel2.getName());
        System.out.println("채널 뜻: " + channel2.getDescription());
        System.out.println("===================================================");

        System.out.println("채널 아이디: " + channel3.getId());
        System.out.println("채널 종류: " + channel3.getType());
        System.out.println("채널 이름: " + channel3.getName());
        System.out.println("채널 뜻: " + channel3.getDescription());
        System.out.println("===================================================");

        System.out.println("채널 아이디: " + channel4.getId());
        System.out.println("채널 종류: " + channel4.getType());
        System.out.println("채널 이름: " + channel4.getName());
        System.out.println("채널 뜻: " + channel4.getDescription());
        System.out.println("===================================================");

        System.out.println("채널 아이디: " + channel5.getId());
        System.out.println("채널 종류: " + channel5.getType());
        System.out.println("채널 이름: " + channel5.getName());
        System.out.println("채널 뜻: " + channel5.getDescription());
        System.out.println("===================================================");
        System.out.println();

        // 채널 단건 조회
        Channel readChannel = cs.read(channel2.getId());
        System.out.println("=== 채널 단건 조회 정보 ===");
        System.out.println("채널 아이디: " + readChannel.getId());
        System.out.println("채널 종류: " + readChannel.getType());
        System.out.println("채널 이름: " + readChannel.getName());
        System.out.println("채널 뜻: " + readChannel.getDescription());
        System.out.println();

        // 채널 전체 조회
        List<Channel> allChannels = cs.readAll();
        System.out.println("=== 채널 전체 조회 정보 ===");
        for(int i = 0; i < allChannels.size(); i++) {
            System.out.println("채널 아이디: " + allChannels.get(i).getId());
            System.out.println("채널 종류: " + allChannels.get(i).getType());
            System.out.println("채널 이름: " + allChannels.get(i).getName());
            System.out.println("채널 뜻: " + allChannels.get(i).getDescription());
            System.out.println("===================================================");
        }
        System.out.println();

        // 채널 수정 조회
        Channel updateChannel = cs.update(channel2.getId(), ChannelType.PRIVATE, "비밀게시판", "사용자들이 은밀하게 대화하는 채널");
        System.out.println("=== 채널 수정 조회 정보 ===");
        System.out.println("채널 아이디: " + updateChannel.getId());
        System.out.println("채널 종류: " + updateChannel.getType());
        System.out.println("채널 이름: " + updateChannel.getName());
        System.out.println("채널 뜻: " + updateChannel.getDescription());
        System.out.println();

        // 채널 삭제
        cs.delete(channel3.getId());
        System.out.println("=== 채널 삭제 조회 정보 ===");

        try {
            cs.read(channel3.getId());
            System.out.println("삭제 실패");
        } catch (IllegalArgumentException e) {
            System.out.println("삭제 완료");
        }

        System.out.println();

        // 삭제 후 다시 정보 조회
        System.out.println("=== 삭제 후 채널 전체 조회 정보 ===");

        List<Channel> allChannels2 = cs.readAll();
        for(int i = 0; i < allChannels2.size(); i++) {
            System.out.println("채널 아이디: " + allChannels2.get(i).getId());
            System.out.println("채널 종류: " + allChannels2.get(i).getType());
            System.out.println("채널 이름: " + allChannels2.get(i).getName());
            System.out.println("채널 뜻: " + allChannels2.get(i).getDescription());
            System.out.println("===================================================");
        }

        System.out.println();

        // 메시지 입력
        Message message1 = ms.create("안녕하세요! 처음 가입했습니다.", user1.getId(), channel2.getId());
        Message message2 = ms.create("오늘 업데이트 내용 확인했습니다.", user2.getId(), channel1.getId());
        Message message3 = ms.create("Java 인터페이스 구현이 조금 헷갈립니다.", user3.getId(), channel2.getId());
        Message message4 = ms.create("프로젝트 일정 공유드립니다.", user3.getId(), channel5.getId());
        Message message5 = ms.create("운영진 회의는 몇 시에 시작하나요?", user5.getId(), channel4.getId());

        // 메시지 생성
        System.out.println("=== 메시지 생성 정보 ===");

        System.out.println("메시지 아이디: " + message1.getId());
        System.out.println("작성자: " + us.read(message1.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(message1.getChannelId()).getName());
        System.out.println("내용: " + message1.getContent());
        System.out.println("===================================================");

        System.out.println("메시지 아이디: " + message2.getId());
        System.out.println("작성자: " + us.read(message2.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(message2.getChannelId()).getName());
        System.out.println("내용: " + message2.getContent());
        System.out.println("===================================================");

        System.out.println("메시지 아이디: " + message3.getId());
        System.out.println("작성자: " + us.read(message3.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(message3.getChannelId()).getName());
        System.out.println("내용: " + message3.getContent());
        System.out.println("===================================================");

        System.out.println("메시지 아이디: " + message4.getId());
        System.out.println("작성자: " + us.read(message4.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(message4.getChannelId()).getName());
        System.out.println("내용: " + message4.getContent());
        System.out.println("===================================================");

        System.out.println("메시지 아이디: " + message5.getId());
        System.out.println("작성자: " + us.read(message5.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(message5.getChannelId()).getName());
        System.out.println("내용: " + message5.getContent());
        System.out.println("===================================================");
        System.out.println();

        // 메시지 단건 조회
        Message readMessage = ms.read(message2.getId());

        System.out.println("=== 메시지 단건 조회 정보 ===");
        System.out.println("메시지 아이디: " + readMessage.getId());
        System.out.println("작성자: " + us.read(readMessage.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(readMessage.getChannelId()).getName());
        System.out.println("내용: " + readMessage.getContent());
        System.out.println();

        // 메시지 전체 조회
        List<Message> allMessages = ms.readAll();

        System.out.println("=== 메시지 전체 조회 정보 ===");
        for (int i = 0; i < allMessages.size(); i++) {
            Message message = allMessages.get(i);

            User author = us.read(message.getAuthorId());
            Channel channel = cs.read(message.getChannelId());

            System.out.println("메시지 아이디: " + message.getId());
            System.out.println("작성자: " + author.getUsername());
            System.out.println("채널: " + channel.getName());
            System.out.println("내용: " + message.getContent());
            System.out.println("===================================================");
        }
        System.out.println();

        // 메시지 수정
        Message updateMessage = ms.update(message2.getId(), "오늘 업데이트 내용을 확인하지 못했습니다.");

        System.out.println("=== 메시지 수정 조회 정보 ===");
        System.out.println("메시지 아이디: " + updateMessage.getId());
        System.out.println("작성자: " + us.read(updateMessage.getAuthorId()).getUsername());
        System.out.println("채널: " + cs.read(updateMessage.getChannelId()).getName());
        System.out.println("내용: " + updateMessage.getContent());
        System.out.println();

        // 메시지 삭제
        ms.delete(message4.getId());

        System.out.println("=== 메시지 삭제 조회 정보 ===");

        try {
            ms.read(message4.getId());
            System.out.println("삭제 실패");
        } catch (IllegalArgumentException e) {
            System.out.println("삭제 완료");
        }

        System.out.println();

        // 삭제 후 다시 정보 조회
        System.out.println("=== 삭제 후 메시지 전체 조회 정보 ===");

        List<Message> allMessages2 = ms.readAll();

        for (int i = 0; i < allMessages2.size(); i++) {
            Message message = allMessages2.get(i);

            User author = us.read(message.getAuthorId());
            Channel channel = cs.read(message.getChannelId());

            System.out.println("메시지 아이디: " + message.getId());
            System.out.println("작성자: " + author.getUsername());
            System.out.println("채널: " + channel.getName());
            System.out.println("내용: " + message.getContent());
            System.out.println("===================================================");
        }










    }
}
