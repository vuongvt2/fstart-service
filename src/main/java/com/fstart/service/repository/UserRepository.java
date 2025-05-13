package com.fstart.service.repository;

import com.fstart.service.entity.*;
import com.fstart.service.enumeration.EUserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByIdAndStatus(String id, EUserStatus status);

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, EUserStatus status);

    Optional<User> findByEmailAndStatusIn(String email, List<EUserStatus> status);

    List<User> findByRole(Role role);

    List<User> findAllByRoleAndStatusOrderByUpdatedAtDesc(Role role, EUserStatus status, Pageable pageable);

    boolean existsByEmail(String email);

    @Query(value = "SELECT distinct fu" +
            "       FROM User fu " +
            "       LEFT JOIN UserPosition fup " +
            "       ON fu.id = fup.user.id" +
            "       WHERE fup.position" +
            "       IN :positions ")
    List<User> getUsersByPosition(@Param("positions") List<Position> positions);

    @Query(value = "SELECT distinct fu" +
            "       FROM User fu " +
            "       LEFT JOIN UserField fuf " +
            "       ON fu.id = fuf.user.id" +
            "       WHERE fuf.field" +
            "       IN :fields ")
    List<User> getUsersByField(@Param("fields") List<Field> fields);

    @Query(value = "SELECT distinct fu" +
            "       FROM User fu " +
            "       LEFT JOIN UserSkill fus " +
            "       ON fu.id = fus.user.id" +
            "       WHERE fus.technology" +
            "       IN :technologies ")
    List<User> getUsersByTechnology(@Param("technologies") List<Technology> technologies);

    User getByEmail(String email);

    @Query("SELECT count (distinct u.id) FROM User u WHERE u.role.id = 'USER' ")
    Long countAll();

    Long countAllByStatusAndRole(EUserStatus status, Role role);

    Long countAllByStatus(EUserStatus status);

}