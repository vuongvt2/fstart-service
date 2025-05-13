package com.fstart.service.dao;

import com.fstart.service.entity.Contact;
import com.fstart.service.enumeration.EContactStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * ContactDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/05/27
 */
@Repository
public class ContactDAOImpl implements ContactDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Contact> getAllContactBy(final String fullName, final String status, final long limit, final long offset) {
        String hql = "SELECT fc " +
                "FROM Contact fc ";
        String hqlCondition = " ";

        if (StringUtils.hasLength(fullName) && StringUtils.hasLength(status)) {
            hqlCondition += " WHERE unaccent(lower(fc.fullName)) like unaccent(lower(concat('%', :fullName ,'%' ))) " +
                    "AND fc.status = :status";
        }
        if (StringUtils.hasLength(fullName) && !StringUtils.hasLength(status)) {
            hqlCondition += " WHERE unaccent(lower(fc.fullName)) like unaccent(lower(concat('%', :fullName ,'%' ))) ";
        }
        if (!StringUtils.hasLength(fullName) && StringUtils.hasLength(status)) {
            hqlCondition += " WHERE fc.status = :status ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append(" ORDER BY fc.createdAt ");

        Query query = entityManager.createQuery(hqlBuilder.toString(), Contact.class);
        if (StringUtils.hasLength(fullName)) {
            query.setParameter("fullName", fullName);
        }

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EContactStatus.valueOf(status));
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public Long countAllContactBy(final String fullName, final String status, final long limit, final long offset) {
        String hql = "SELECT COUNT(distinct fc.id) " +
                "FROM Contact fc ";
        String hqlCondition = " ";

        if (StringUtils.hasLength(fullName) && StringUtils.hasLength(status)) {
            hqlCondition += " WHERE unaccent(lower(fc.fullName)) like unaccent(lower(concat('%', :fullName ,'%' ))) " +
                    "AND fc.status = :status ";
        }
        if (StringUtils.hasLength(fullName) && !StringUtils.hasLength(status)) {
            hqlCondition += " WHERE unaccent(lower(fc.fullName)) like unaccent(lower(concat('%', :fullName ,'%' ))) ";
        }
        if (!StringUtils.hasLength(fullName) && StringUtils.hasLength(status)) {
            hqlCondition += " WHERE fc.status = :status ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class);
        if (StringUtils.hasLength(fullName)) {
            query.setParameter("fullName", fullName);
        }

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EContactStatus.valueOf(status));
        }

        return Long.parseLong(query.getSingleResult().toString());
    }
}
