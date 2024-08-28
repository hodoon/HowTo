package com.example.HowToProj.repository;

import com.example.HowToProj.entity.Friendship;
import com.example.HowToProj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    List<Friendship> findByUserAndStatus(User user, Friendship.FriendshipStatus status);
    List<Friendship> findByFriendAndStatus(User friend, Friendship.FriendshipStatus status);
    List<Friendship> findByUser(User user);
}
