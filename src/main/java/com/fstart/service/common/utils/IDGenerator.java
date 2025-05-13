package com.fstart.service.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * IDGenerator
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
public class IDGenerator {

    public static String generateID(final JpaRepository jpaRepository, final int length) {
        String id;
        do {
            id = RandomStringUtils.randomAlphanumeric(length).toUpperCase();
        } while (jpaRepository.findById(id).isPresent());

        return id;
    }

}
