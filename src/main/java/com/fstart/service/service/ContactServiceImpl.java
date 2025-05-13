package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.dao.ContactDAO;
import com.fstart.service.entity.Contact;
import com.fstart.service.enumeration.EContactStatus;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.contact.ContactForm;
import com.fstart.service.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ContactServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/05/25
 */
@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactDAO contactDAO;

    private final Message message;

    public ContactServiceImpl(final ContactRepository contactRepository,
                              final ContactDAO contactDAO,
                              final Message message) {
        this.contactRepository = contactRepository;
        this.contactDAO = contactDAO;
        this.message = message;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DataWrapper createContact(final ContactForm contactForm) {
        Contact contact = DataBuilder.to(contactForm, Contact.class);
        contact.setStatus(EContactStatus.NEW);
        contact.setCreatedAt(TimeUtils.comNowDatetime());
        contactRepository.saveAndFlush(contact);
        return DataWrapper.builder()
                .data(contact.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<Contact> getAllContactBy(final String role, final String fullName, final String status, final Integer page, final Integer size) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        List<Contact> contacts = contactDAO.getAllContactBy(fullName, status, size, (page - 1) * size);
        long totalElements = contactDAO.countAllContactBy(fullName, status, size, (page - 1) * size);

        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));

        return new PagedResponse<>(contacts, page, size, totalElements, totalPages);

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DataWrapper resolveContact(final String role, final Long id) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        contact.setStatus(EContactStatus.RESOLVED);
        contactRepository.saveAndFlush(contact);

        return DataWrapper.builder()
                .data(contact.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }
}
