package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.entity.Contact;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.contact.ContactForm;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.ContactService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * ContactController
 *
 * @author: VuongVT2
 * @since: 2022/05/25
 */
@RestController
@RequestMapping(value = "fs/api/v1/contact")
public class ContactController {

    private final ContactService contactService;
    private final AccessTokenService accessTokenService;

    public ContactController(final ContactService contactService,
                             final AccessTokenService accessTokenService) {
        this.contactService = contactService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping(value = "/public/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper createContact(@Valid @RequestBody ContactForm contactForm) {
        return contactService.createContact(contactForm);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Contact> getAllContactBy(@RequestParam(name = "fullName", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fullName,
                                                  @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
                                                  @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                  @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                  HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return contactService.getAllContactBy(role, fullName, status, page, size);
    }

    @GetMapping(value = "/resolve", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper resolveContact(@NotNull Long id, HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return contactService.resolveContact(role, id);
    }

}
