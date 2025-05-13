package com.fstart.service.repository;

import com.fstart.service.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PositionRepository
 *
 * @author: VuongVT2
 * @since: 2022/03/21
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, String> {
    List<Position> findByIdIn(List<String> id);

    List<Position> findByIdNotIn(List<String> id);

}
