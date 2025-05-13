package com.fstart.service.dao;

import com.fstart.service.enumeration.EReportStatus;
import com.fstart.service.enumeration.EUserReportType;
import com.fstart.service.model.report.ReportProjectData;
import com.fstart.service.model.report.ReportUserData;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * ReportDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/05/11
 */
@Repository
public class ReportDAOImpl implements ReportDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReportProjectData> getAllProjectReportBy(final String status, final long limit, final long offset, final String search) {
        String hql = "SELECT " +
                "new com.fstart.service.model.report.ReportProjectData" +
                "(fr.id AS reportId, " +
                "fr.reason, " +
                "fr.status, " +
                "fr.violation.name, " +
                "fr.createdAt, " +
                "fr.updatedAt, " +
                "fupr.project.id, " +
                "fupr.project.logo, " +
                "fupr.project.title) " +
                "FROM Report fr " +
                "LEFT JOIN ProjectReport fupr ON fr.id = fupr.report.id " +
                "LEFT JOIN Project fp ON fupr.project.id = fp.id ";
        String hqlCondition = " WHERE fupr.project.id != null " +
                "AND LOWER(fp.title) LIKE LOWER(concat('%', :search, '%')) ";

        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND  fr.status = :status ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append(" ORDER BY fr.createdAt ");

        Query query = entityManager.createQuery(hqlBuilder.toString(), ReportProjectData.class)
                .setParameter("search", search);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EReportStatus.valueOf(status));
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public Long countAllProjectReportBy(final String status, String search) {
        String hql = "SELECT COUNT(fr.id) " +
                "FROM Report fr " +
                "LEFT JOIN ProjectReport fupr ON fr.id = fupr.report.id " + 
                "LEFT JOIN Project fp ON fupr.project.id = fp.id ";
        String hqlCondition = " WHERE fupr.project.id != null " +
                "AND LOWER(fp.title) LIKE LOWER(concat('%', :search, '%')) ";


        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND  fr.status = :status  ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EReportStatus.valueOf(status));
        }

        return Long.parseLong(query.getSingleResult().toString());
    }

    @Override
    public List<ReportUserData> getAllUserReportBy(final String status, final EUserReportType type, final long limit, final long offset, String search) {
        String hql = "SELECT " +
                "new com.fstart.service.model.report.ReportUserData" +
                "(fr.id AS reportId, " +
                "fr.reason," +
                "fr.status, " +
                "fr.violation.name AS violation, " +
                "fr.createdAt, " +
                "fr.updatedAt, " +
                "fupr.user.id AS accusedId, " +
                "fupr.user.avatar AS avatar, " +
                "fupr.user.firstName AS firstName, " +
                "fupr.user.lastName AS lastName) " +
                "FROM Report fr " +
                "LEFT JOIN UserReport fupr ON fr.id = fupr.report.id " +
                "LEFT JOIN User fu ON fupr.user.id = fu.id ";
        String hqlCondition = " WHERE fupr.type = :type " +
                "AND unaccent(LOWER(concat(fu.lastName,' ', fu.firstName))) LIKE unaccent(LOWER(concat('%', :search, '%'))) ";

        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND  fr.status = :status  ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append(" ORDER BY fr.createdAt");

        Query query = entityManager.createQuery(hqlBuilder.toString(), ReportUserData.class)
                .setParameter("type", type)
                .setParameter("search", search);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EReportStatus.valueOf(status));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public Long countAllUserReportBy(final String status, final EUserReportType type, String search) {
        String hql = "SELECT COUNT(fr.id) " +
                "FROM Report fr " +
                "LEFT JOIN UserReport fupr ON fr.id = fupr.report.id " +
                "LEFT JOIN User fu ON fupr.user.id = fu.id ";
        String hqlCondition = " WHERE fupr.type = :type " +
                "AND unaccent(LOWER(concat(fu.lastName,' ', fu.firstName))) LIKE unaccent(LOWER(concat('%', :search, '%'))) ";

        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND  fr.status = :status  ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("type", type)
                .setParameter("search", search);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EReportStatus.valueOf(status));
        }

        return Long.parseLong(query.getSingleResult().toString());
    }
}
