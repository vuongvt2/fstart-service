package com.fstart.service.repository;

import com.fstart.service.entity.Position;
import com.fstart.service.entity.User;
import com.fstart.service.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
    boolean existsByUserAndPosition(User user, Position position);

    List<UserPosition> findAllByUser(User user);

    void deleteAllByUser(User user);
}
