package com.example.HowToProj.controller;

import com.example.HowToProj.dto.FriendshipDto;
import com.example.HowToProj.dto.UserActivityDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.FriendshipManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/friends")
public class FriendshipManagementController {

    @Autowired
    private FriendshipManagementService friendshipManagementService;

    @PostMapping("/request/{friendId}")
    public void sendFriendRequest(@PathVariable Long userId, @PathVariable Long friendId) {
        User user = new User();
        user.setId(userId);
        User friend = new User();
        friend.setId(friendId);
        friendshipManagementService.sendFriendRequest(user, friend);
    }

    @PostMapping("/accept/{friendId}")
    public void acceptFriendRequest(@PathVariable Long userId, @PathVariable Long friendId) {
        User user = new User();
        user.setId(userId);
        User friend = new User();
        friend.setId(friendId);
        friendshipManagementService.acceptFriendRequest(user, friend);
    }

    @PostMapping("/reject/{friendId}")
    public void rejectFriendRequest(@PathVariable Long userId, @PathVariable Long friendId) {
        User user = new User();
        user.setId(userId);
        User friend = new User();
        friend.setId(friendId);
        friendshipManagementService.rejectFriendRequest(user, friend);
    }

    @GetMapping
    public List<User> getFriends(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return friendshipManagementService.getFriends(user);
    }

    @GetMapping("/feed")
    public List<UserActivityDto> getFriendActivityFeed(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return friendshipManagementService.getFriendActivityFeed(user);
    }
}
