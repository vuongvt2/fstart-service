package com.fstart.service.service;

import com.fstart.service.entity.Contact;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.contact.ContactForm;

/**
 * ContactService
 *
 * @author: VuongVT2
 * @since: 2022/05/25
 */
public interface ContactService {
    DataWrapper createContact(ContactForm contactForm);

    PagedResponse<Contact> getAllContactBy(String role, String fullName, String status, Integer page, Integer size);

    DataWrapper resolveContact(String role, Long id);
}
