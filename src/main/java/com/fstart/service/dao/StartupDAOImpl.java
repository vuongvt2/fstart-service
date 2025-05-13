package com.fstart.service.dao;

import com.fstart.service.entity.Field;
import com.fstart.service.entity.Startup;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * StartupDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/05/20
 */
@Repository
public class StartupDAOImpl implements StartupDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Startup> findBySearchAndField(final String search, final List<Field> fields, final long limit, final long offset) {
        String hql = "SELECT fs FROM Startup fs ";
        String hqlCondition = "WHERE " +
                "    LOWER(unaccent(fs.name)) LIKE LOWER(CONCAT('%', unaccent(:search), '%')) ";

        if (!CollectionUtils.isEmpty(fields)) {
            hql += "LEFT JOIN StartupField fsf ON fsf.startup.id = fs.id ";
            hqlCondition += " AND fsf.field IN (:fields) ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY fs.name");
        Query query = entityManager.createQuery(hqlBuilder.toString(), Startup.class)
                .setParameter("search", search);

        if (!CollectionUtils.isEmpty(fields)) {
            query.setParameter("fields", fields);
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public Long countBySearchAndField(String search, List<Field> fields, long page, long size) {
        String hql = "SELECT count(distinct fs.id) FROM Startup fs ";
        String hqlCondition = "WHERE " +
                "    LOWER(unaccent(fs.name)) LIKE LOWER(CONCAT('%', unaccent(:search), '%')) ";

        if (!CollectionUtils.isEmpty(fields)) {
            hql += "LEFT JOIN StartupField fsf ON fsf.startup.id = fs.id ";
            hqlCondition += " AND fsf.field IN (:fields) ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);
        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);

        if (!CollectionUtils.isEmpty(fields)) {
            query.setParameter("fields", fields);
        }
        return Long.parseLong(query.getSingleResult().toString());
    }
}