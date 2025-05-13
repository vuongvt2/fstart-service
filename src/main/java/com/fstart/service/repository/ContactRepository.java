package com.fstart.service.repository;

import com.fstart.service.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ContactRepository
 *
 * @author: VuongVT2
 * @since: 2022/05/26
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
