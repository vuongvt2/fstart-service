package com.fstart.service.dao;

import com.fstart.service.entity.*;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.enumeration.EUserStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * UserDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/04/11
 */
@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findByMajorAndPosition(final List<Major> majors, final List<Position> positions, final EUserStatus status, final long limit, final long offset) {
        String hql = "SELECT distinct fu FROM User fu ";
        String hqlCondition = "WHERE " +
                "   fu.status = :status";
        if (!CollectionUtils.isEmpty(positions)) {
            hql += "LEFT JOIN UserPosition fup ON fu.id = fup.position.id ";
            hqlCondition += " AND fup.position IN (:positions)";
        }
        if (!CollectionUtils.isEmpty(majors)) {
            hqlCondition += " AND fu.major IN (:majors)";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY unaccent(lower(concat(fu.lastName,' ', fu.firstName)))");
        Query query = entityManager.createQuery(hqlBuilder.toString(), Project.class)
                .setParameter("status", status);
        if (!CollectionUtils.isEmpty(positions)) {
            query.setParameter("positions", positions);
        }
        if (!CollectionUtils.isEmpty(majors)) {
            query.setParameter("majors", majors);
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countByMajorAndPosition(final List<Major> majors, final List<Position> positions, final EUserStatus status) {
        String hql = "SELECT distinct fu FROM User fu ";
        String hqlCondition = "WHERE " +
                "   fu.status = :status";
        if (!CollectionUtils.isEmpty(positions)) {
            hql += "LEFT JOIN UserPosition fup ON fu.id = fup.position.id ";
            hqlCondition += " AND fup.position IN (:positions)";
        }
        if (!CollectionUtils.isEmpty(majors)) {
            hqlCondition += " AND fu.major IN (:majors)";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY unaccent(lower(concat(fu.lastName,' ', fu.firstName)))");
        Query query = entityManager.createQuery(hqlBuilder.toString(), Project.class)
                .setParameter("status", status);
        if (!CollectionUtils.isEmpty(positions)) {
            query.setParameter("positions", positions);
        }
        if (!CollectionUtils.isEmpty(majors)) {
            query.setParameter("majors", majors);
        }

        return Long.parseLong(query.getSingleResult().toString());
    }

    @Override
    public List<User> findBySearchAndSkillAndFieldAndStatusAndPosition(final String search, final List<Technology> skills, final List<Field> fields, final String status, final ERole roleId, final List<Position> positions, final long limit, final long offset) {
        String hql = "SELECT distinct fu FROM User fu ";
        String hqlCondition = " WHERE fu.role.id = :roleId AND " +
                "unaccent(LOWER(CONCAT(fu.lastName,' ', fu.firstName))) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";
        if (StringUtils.hasLength(status)) {
            hqlCondition += "AND fu.status = :status";
        }

        if (!CollectionUtils.isEmpty(skills)) {
            hql += "LEFT JOIN UserSkill fus ON fu.id = fus.user.id ";
            hqlCondition += " AND fus.technology IN (:skills) ";
        }
        if (!CollectionUtils.isEmpty(fields)) {
            hql += "LEFT JOIN UserField fuf ON fu.id = fuf.user.id ";
            hqlCondition += " AND fuf.field IN (:fields) ";
        }
        if (!CollectionUtils.isEmpty(positions)) {
            hql += "LEFT JOIN UserPosition fup ON fu" +
                    ".id = fup.position.id ";
            hqlCondition += " AND fup.position IN (:positions) ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append(" ORDER BY fu.updatedAt ");
        Query query = entityManager.createQuery(hqlBuilder.toString(), User.class)
                .setParameter("roleId", roleId)
                .setParameter("search", search);
        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EUserStatus.valueOf(status));
        }
        if (!CollectionUtils.isEmpty(skills)) {
            query.setParameter("skills", skills);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            query.setParameter("fields", fields);
        }
        if (!CollectionUtils.isEmpty(positions)) {
            query.setParameter("positions", positions);
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countBySearchAndSkillAndFieldAndStatusAndPosition(final String search, final List<Technology> skills, final List<Field> fields, final String status, final ERole roleId, final List<Position> positions) {
        String hql = "SELECT COUNT(distinct fu.id) FROM User fu  ";
        String hqlCondition = " WHERE fu.role.id =:roleId AND " +
                "unaccent(LOWER(CONCAT(fu.lastName,' ', fu.firstName))) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";

        if (StringUtils.hasLength(status)) {
            hqlCondition += "AND fu.status = :status";
        }
        if (!CollectionUtils.isEmpty(skills)) {
            hql += "LEFT JOIN UserSkill fus ON fu.id = fus.user.id ";
            hqlCondition += " AND fus.technology IN (:skills) ";
        }
        if (!CollectionUtils.isEmpty(fields)) {
            hql += "LEFT JOIN UserField fuf ON fu.id = fuf.user.id ";
            hqlCondition += " AND fuf.field IN (:fields) ";
        }
        if (!CollectionUtils.isEmpty(positions)) {
            hql += "LEFT JOIN UserPosition fup ON fu.id = fup.position.id ";
            hqlCondition += " AND fup.position IN (:positions) ";
        }

        StringBuilder hqlBuilder = new StringBuilder()
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("roleId", roleId)
                .setParameter("search", search);
        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EUserStatus.valueOf(status));
        }

        if (!CollectionUtils.isEmpty(skills)) {
            query.setParameter("skills", skills);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            query.setParameter("fields", fields);
        }
        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EUserStatus.valueOf(status));
        }

        return Long.parseLong(query.getSingleResult().toString());
    }
}
