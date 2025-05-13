package com.fstart.service.service;

import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.startup.StartupData;
import com.fstart.service.model.startup.StartupForm;
import com.fstart.service.model.startup.StartupLogoForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * StartupService
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
public interface StartupService {
    DataWrapper createStartup(StartupForm startupForm);

    DataWrapper getStartupDetail(long id);

    PagedResponse<StartupData> filterStartup(String search, String fieldId, Integer page, Integer size);

    void importExcel(MultipartFile file);

    DataWrapper updateStartup(StartupForm startupForm, String role);

    boolean updateLogo(StartupLogoForm startupLogoForm, String role);

    boolean deleteStartup(Long id, String role);
}
