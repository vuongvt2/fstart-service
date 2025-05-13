package com.fstart.service.dao;

import com.fstart.service.entity.Field;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.Technology;
import com.fstart.service.enumeration.EProjectStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * ProjectDAOImpl
 *
 * @author VuongVT2
 * @since 2022/04/11
 */
@Repository
public class ProjectDAOImpl implements ProjectDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Project> findBySearchAndTechnologyAndFieldAndStatus(final String search, final List<Technology> technologies, final List<Field> fields, final String status, final long limit, final long offset) {
        String hql = "SELECT distinct fp FROM Project fp ";
        String hqlCondition = "WHERE " +
                "    unaccent(LOWER(fp.title)) LIKE unaccent(LOWER(CONCAT('%', :search, '%')))";

        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND fp.status = :status ";
        }
        if (!CollectionUtils.isEmpty(technologies)) {
            hql += "LEFT JOIN ProjectTechnology fpt ON fp.id = fpt.project.id ";
            hqlCondition += " AND fpt.technology IN (:technologies)";
        }
        if (!CollectionUtils.isEmpty(fields)) {
            hql += "LEFT JOIN ProjectField fpf ON fp.id = fpf.project.id ";
            hqlCondition += " AND fpf.field IN (:fields)";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY fp.updatedAt");
        Query query = entityManager.createQuery(hqlBuilder.toString(), Project.class)
                .setParameter("search", search);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EProjectStatus.valueOf(status));
        }
        if (!CollectionUtils.isEmpty(technologies)) {
            query.setParameter("technologies", technologies);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            query.setParameter("fields", fields);
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countBySearchAndTechnologyAndField(final String search, final List<Technology> technologies, final List<Field> fields, final String status) {
        String hql = "SELECT COUNT(distinct fp.id) FROM Project fp ";
        String hqlCondition = "WHERE " +
                "   unaccent(LOWER(fp.title)) LIKE unaccent(LOWER(CONCAT('%', :search, '%')))";

        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND fp.status = :status ";
        }
        if (!CollectionUtils.isEmpty(technologies)) {
            hql += "LEFT JOIN ProjectTechnology fpt ON fp.id = fpt.project.id ";
            hqlCondition += " AND fpt.technology IN (:technologies)";
        }
        if (!CollectionUtils.isEmpty(fields)) {
            hql += "LEFT JOIN ProjectField fpf ON fp.id = fpf.project.id ";
            hqlCondition += " AND fpf.field IN (:fields)";
        }

        StringBuilder hqlBuilder = new StringBuilder()
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EProjectStatus.valueOf(status));
        }

        if (!CollectionUtils.isEmpty(technologies)) {
            query.setParameter("technologies", technologies);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            query.setParameter("fields", fields);
        }

        return Long.parseLong(query.getSingleResult().toString());
    }

}
