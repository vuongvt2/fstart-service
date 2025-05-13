package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.utils.ExcelHelper;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.startup.StartupData;
import com.fstart.service.model.startup.StartupForm;
import com.fstart.service.model.startup.StartupLogoForm;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.StartupService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * StartupController
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@RestController
@RequestMapping(value = "fs/api/v1/startup")
public class StartupController {

    private final AccessTokenService accessTokenService;
    private final StartupService startupService;

    public StartupController(final AccessTokenService accessTokenService, final StartupService startupService) {
        this.accessTokenService = accessTokenService;
        this.startupService = startupService;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper createStartup(@RequestBody @Valid StartupForm startupForm) {
        return startupService.createStartup(startupForm);
    }

    @GetMapping(value = "/public/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper getStartupDetail(@RequestParam(name = "id") long id) {
        return startupService.getStartupDetail(id);
    }

    @GetMapping(value = "/public/all-startups", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<StartupData> filterStartup(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                    @RequestParam(name = "fieldId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fieldId,
                                                    @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                    @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return startupService.filterStartup(search, fieldId, page, size);
    }

    @PostMapping(value = "/import-excel")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                startupService.importExcel(file);
                return "Uploaded the file successfully: " + file.getOriginalFilename();
            } catch (Exception e) {
                return "Could not upload the file: " + file.getOriginalFilename() + "!";
            }
        }
        return "Please upload an excel file!";
    }

    @PostMapping(value = "/update-startup", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper updateStartup(@RequestBody StartupForm startupForm,
                                     HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return startupService.updateStartup(startupForm, role);
    }

    @PostMapping(value = "/update-logo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean updateLogo(StartupLogoForm startupLogoForm,
                              HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return startupService.updateLogo(startupLogoForm, role);
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteStartup(@RequestParam(name = "id") @NotNull Long id,
                                 HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return startupService.deleteStartup(id, role);
    }
}
