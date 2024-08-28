package com.example.HowToProj.service;

import com.example.HowToProj.dto.FriendshipDto;
import com.example.HowToProj.dto.UserActivityDto;
import com.example.HowToProj.entity.Friendship;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipManagementService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Transactional
    public void sendFriendRequest(User fromUser, User toUser) {
        // 이미 친구 요청이 존재하는지 확인
        Optional<Friendship> existingRequest = friendshipRepository.findByUserAndFriend(fromUser, toUser);
        if (existingRequest.isPresent() && existingRequest.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            throw new RuntimeException("Friend request already sent.");
        }

        Friendship friendship = Friendship.builder()
                .user(fromUser)
                .friend(toUser)
                .status(Friendship.FriendshipStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        friendshipRepository.save(friendship);
    }

    @Transactional
    public void acceptFriendRequest(User user, User friend) {
        Optional<Friendship> optionalFriendship = friendshipRepository.findByUserAndFriend(friend, user);
        if (optionalFriendship.isPresent()) {
            Friendship friendship = optionalFriendship.get();
            if (friendship.getStatus() == Friendship.FriendshipStatus.PENDING) {
                friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
                friendship.setUpdatedAt(LocalDateTime.now());
                friendshipRepository.save(friendship);
            } else {
                throw new RuntimeException("Friend request is not pending.");
            }
        } else {
            throw new NotFoundMemberException("Friend request not found.");
        }
    }

    @Transactional
    public void rejectFriendRequest(User user, User friend) {
        Optional<Friendship> optionalFriendship = friendshipRepository.findByUserAndFriend(friend, user);
        if (optionalFriendship.isPresent()) {
            Friendship friendship = optionalFriendship.get();
            if (friendship.getStatus() == Friendship.FriendshipStatus.PENDING) {
                friendship.setStatus(Friendship.FriendshipStatus.REJECTED);
                friendship.setUpdatedAt(LocalDateTime.now());
                friendshipRepository.save(friendship);
            } else {
                throw new RuntimeException("Friend request is not pending.");
            }
        } else {
            throw new NotFoundMemberException("Friend request not found.");
        }
    }

    public List<User> getFriends(User user) {
        List<Friendship> friendships = friendshipRepository.findByUserAndStatus(user, Friendship.FriendshipStatus.ACCEPTED);
        return friendships.stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toList());
    }

    public List<User> getFriendRequests(User user) {
        List<Friendship> requests = friendshipRepository.findByFriendAndStatus(user, Friendship.FriendshipStatus.PENDING);
        return requests.stream()
                .map(Friendship::getUser)
                .collect(Collectors.toList());
    }

    // 친구 활동 피드를 제공하는 기능은 친구의 활동을 추적하는 다른 서비스를 필요로 합니다.
    // 예: ActivityService를 이용해 친구들의 활동을 조회
    public List<UserActivityDto> getFriendActivityFeed(User user) {
        // 이 메서드는 친구들의 활동을 조회하고 ActivityDto로 변환하는 로직을 포함해야 합니다.
        // ActivityService를 주입받아야 하고, 해당 서비스를 통해 친구의 활동을 조회합니다.
        // 예:
        // List<Activity> activities = activityService.getRecentActivitiesForUsers(getFriends(user));
        // return activities.stream().map(ActivityDto::fromEntity).collect(Collectors.toList());

        throw new UnsupportedOperationException("Friend activity feed functionality is not yet implemented.");
    }
}
