package com.fstart.service.repository;

import com.fstart.service.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NewsRepository
 *
 * @author: VuongVT2
 * @since: 2022/03/03
 */
@Repository
public interface NewsRepository extends JpaRepository<News, String> {
    @Transactional(readOnly = true)
    @Query("SELECT n FROM News n ORDER BY n.updatedAt")
    Page<News> getAllNews(Pageable pageable);

//    @Query(value = "SELECT fn  from fs_news fn where  string_to_array(fn.tags, ',') @> string_to_array(:tag, ',') ", nativeQuery = true)

    @Query("SELECT fn FROM News fn WHERE unaccent(lower(fn.tags)) like unaccent(lower(concat('%', :tag ,'%' ))) ORDER BY fn.updatedAt")
    Page<News> getAllNewsByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT fn FROM News fn WHERE unaccent(lower(fn.title)) like unaccent(lower(concat('%', :title ,'%' ))) ORDER BY fn.updatedAt")
    Page<News> getAllNewsByTitle(@Param("title") String title, Pageable pageable);

}
