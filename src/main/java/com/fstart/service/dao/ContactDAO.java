package com.fstart.service.dao;

import com.fstart.service.entity.Contact;

import java.util.List;

/**
 * ContactDAO
 *
 * @author: VuongVT2
 * @since: 2022/05/27
 */
public interface ContactDAO {
    List<Contact> getAllContactBy(String fullName, String status, long limit, long offset);

    Long countAllContactBy(String fullName, String status, long limit, long offset);
}
