package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.logging.AppLogger;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.ExcelHelper;
import com.fstart.service.common.utils.IDGenerator;
import com.fstart.service.component.S3Component;
import com.fstart.service.dao.StartupDAO;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.enumeration.EStartupStatus;
import com.fstart.service.model.common.CommonData;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.startup.*;
import com.fstart.service.repository.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StartupServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Service
public class StartupServiceImpl implements StartupService {

    private final Message message;
    private final S3Component s3Component;

    private final StartupRepository startupRepository;
    private final StartupFieldRepository startupFieldRepository;
    private final StartupFounderRepository startupFounderRepository;
    private final CountryRepository countryRepository;
    private final FounderRepository founderRepository;
    private final FieldRepository fieldRepository;
    private final StartupDAO startupDAO;

    public StartupServiceImpl(final Message message,
                              final S3Component s3Component,
                              final StartupRepository startupRepository,
                              final StartupFieldRepository startupFieldRepository,
                              final StartupFounderRepository startupFounderRepository,
                              final CountryRepository countryRepository,
                              final FounderRepository founderRepository,
                              final FieldRepository fieldRepository,
                              final StartupDAO startupDAO) {
        this.message = message;
        this.s3Component = s3Component;
        this.startupRepository = startupRepository;
        this.startupFieldRepository = startupFieldRepository;
        this.startupFounderRepository = startupFounderRepository;
        this.countryRepository = countryRepository;
        this.founderRepository = founderRepository;
        this.fieldRepository = fieldRepository;
        this.startupDAO = startupDAO;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper createStartup(final StartupForm startupForm) {
        String id = IDGenerator.generateID(startupRepository, 10);
        return null;
    }

    @Override
    public DataWrapper getStartupDetail(final long id) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        StartupData startupData = DataBuilder.to(startup, StartupData.class);
        startupData.setStatus(String.valueOf(startup.getStatus()));

        List<CommonData> fieldDataList = startupFieldRepository.findByStartup(startup)
                .stream()
                .map(field -> DataBuilder.to(field.getField(), CommonData.class))
                .collect(Collectors.toList());
        startupData.setFields(fieldDataList);

        List<FounderData> founderDataList = startupFounderRepository.findByStartup(startup)
                .stream()
                .map(founder -> DataBuilder.to(founder.getFounder(), FounderData.class))
                .collect(Collectors.toList());
        startupData.setFounders(founderDataList);

        Country country = countryRepository.findByStartups(startup);
        CommonData countryData = DataBuilder.to(country, CommonData.class);
        startupData.setCountry(countryData);

        return DataWrapper.builder()
                .data(startupData)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<StartupData> filterStartup(final String search, final String fieldId, final Integer page, final Integer size) {

        List<Field> fields = StringUtils.hasLength(fieldId) ? fieldRepository.findAllById(Arrays.asList(fieldId.split(","))) : null;

        List<Startup> startupList = startupDAO.findBySearchAndField(search, fields, size, (page - 1) * size);
        long totalElements = startupDAO.countBySearchAndField(search, fields, size, (page - 1) * size);

        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        List<StartupData> startupDataList = new ArrayList<>();


        startupList.forEach(startup -> {
            StartupData startupData = DataBuilder.to(startup, StartupData.class);
            startupData.setStatus(String.valueOf(startup.getStatus()));

            List<CommonData> fieldDataList = startupFieldRepository.findByStartup(startup)
                    .stream()
                    .map(field -> DataBuilder.to(field.getField(), CommonData.class))
                    .collect(Collectors.toList());
            startupData.setFields(fieldDataList);

            List<FounderData> founderDataList = startupFounderRepository.findByStartup(startup)
                    .stream()
                    .map(founder -> DataBuilder.to(founder.getFounder(), FounderData.class))
                    .collect(Collectors.toList());
            startupData.setFounders(founderDataList);

            Country country = countryRepository.findByStartups(startup);
            CommonData countryData = DataBuilder.to(country, CommonData.class);
            startupData.setCountry(countryData);
            startupDataList.add(startupData);
        });
        return new PagedResponse<>(startupDataList, page, size, totalElements, totalPages);
    }

    @Override
    public void importExcel(final MultipartFile file) {
        try {
            ExcelForm excelForm = ExcelHelper.excelToStartup(file.getInputStream());
            List<StartupExcelForm> startupExcelForms = excelForm.getStartupExcelForms();
            List<Startup> startups = startupExcelForms.stream()
                    .map(startupExcelForm -> Startup.builder()
                            .id(startupExcelForm.getId())
                            .logo(startupExcelForm.getLogo())
                            .name(startupExcelForm.getName())
                            .shortDescription(startupExcelForm.getShortDescription())
                            .description(startupExcelForm.getDescription())
                            .originalLink(startupExcelForm.getOriginalLink())
                            .founded(startupExcelForm.getFounded())
                            .startupSize(startupExcelForm.getStartupSize())
                            .status(EStartupStatus.valueOf(startupExcelForm.getStatus()))
                            .country(countryRepository.getById(startupExcelForm.getCountryId()))
                            .build())
                    .collect(Collectors.toList());
            startupRepository.saveAll(startups);

            List<FounderData> founderDataList = excelForm.getFounders();
            List<Founder> founders = founderDataList.stream()
                    .map(founderData -> Founder.builder()
                            .id(founderData.getId())
                            .name(founderData.getName())
                            .socialNetwork(founderData.getSocialNetwork())
                            .build())
                    .collect(Collectors.toList());
            founderRepository.saveAll(founders);

            List<StartupFounderForm> startupFounderForms = excelForm.getStartupFounderForms();
            List<StartupFounder> startupFounders = startupFounderForms.stream()
                    .map(startupFounderForm -> StartupFounder.builder()
                            .startup(startups.stream().filter(startup -> startup.getId().equals(startupFounderForm.getStartupId())).findAny().orElse(null))
                            .founder(founders.stream().filter(founder -> founder.getId().equals(startupFounderForm.getFounderId())).findAny().orElse(null))
                            .build())
                    .collect(Collectors.toList());
            startupFounderRepository.saveAll(startupFounders);

            List<StartupFieldForm> startupFieldForms = excelForm.getStartupFieldForms();
            List<StartupField> startupFields = startupFieldForms.stream()
                    .map(startupFieldForm -> StartupField.builder()
                            .startup(startups.stream().filter(startup -> startup.getId().equals(startupFieldForm.getStartupId())).findAny().orElse(null))
                            .field(fieldRepository.getById(startupFieldForm.getFieldId()))
                            .build())
                    .collect(Collectors.toList());
            startupFieldRepository.saveAll(startupFields);

        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper updateStartup(final StartupForm startupForm, final String role) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        Startup startup = startupRepository.findById(startupForm.getId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        startup.setName(startupForm.getName());
        startup.setDescription(startupForm.getDescription());
        startup.setShortDescription(startupForm.getShortDescription());
        startup.setStartupSize(startupForm.getStartupSize());
        startup.setStatus(EStartupStatus.valueOf(startupForm.getStatus()));
        startup.setCountry(countryRepository.getById(startupForm.getCountryId()));
        startup.setFounded(startupForm.getFounded());
        startup.setOriginalLink(startupForm.getOriginalLink());

        startupRepository.saveAndFlush(startup);

        startupFieldRepository.deleteAllByStartup(startup);
        if (!CollectionUtils.isEmpty(startupForm.getStartupFields())) {
            List<StartupField> startupFields = startupForm.getStartupFields().stream()
                    .map(fieldId -> StartupField.builder()
                            .startup(startup)
                            .field(fieldRepository.findById(fieldId)
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .build())
                    .collect(Collectors.toList());
            startupFieldRepository.saveAllAndFlush(startupFields);
        }
        startupFounderRepository.deleteAllByStartup(startup);

        List<FounderData> founderDataList = startupForm.getStartupFounders();
        if (!CollectionUtils.isEmpty(founderDataList)) {
            founderDataList.forEach(founderData -> {
                if (founderData.isDelete()) {
                    Founder founder = founderRepository.findById(founderData.getId())
                            .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
                    founderRepository.delete(founder);
                } else {
                    if (!Objects.nonNull(founderData.getId())) {
                        Long id;
                        do {
                            id = Long.parseLong(RandomStringUtils.randomNumeric(10));
                        } while (founderRepository.findById(id).isPresent());
                        founderData.setId(id);
                    }
                    Founder founder = DataBuilder.to(founderData, Founder.class);
                    founderRepository.saveAndFlush(founder);
                    Founder newData = founderRepository.getById(founder.getId());
                }
            });
        }

        if (!CollectionUtils.isEmpty(startupForm.getStartupFounders())) {
            List<FounderData> startupFounders = startupForm.getStartupFounders();
            List<FounderData> newStartupFounders = new ArrayList<>();
            startupFounders.forEach(founderData -> {
                if (!founderData.isDelete()) {
                    newStartupFounders.add(founderData);
                }
            });
            List<StartupFounder> startupFounderList = newStartupFounders.stream()
                    .map(founderData -> StartupFounder.builder()
                            .startup(startup)
                            .founder(founderRepository.getById(founderData.getId()))
                            .build())
                    .collect(Collectors.toList());

            startupFounderRepository.saveAllAndFlush(startupFounderList);
        }

        return DataWrapper.builder()
                .data(startupForm.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public boolean updateLogo(final StartupLogoForm startupLogoForm, final String role) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        Startup startup = startupRepository.findById(startupLogoForm.getId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        try {
            String fileName = startup.getLogo().substring(startup.getLogo().lastIndexOf("/") + 1);
            s3Component.delete("startups/" + startup.getId() + "/logo", fileName);
            String startupLogo = s3Component.upload("startups/" + startupLogoForm.getId() + "/logo", startupLogoForm.getLogo());
            startup.setLogo(startupLogo);
            startupRepository.saveAndFlush(startup);

        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }

        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean deleteStartup(final Long id, final String role) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        startupFieldRepository.deleteAllByStartup(startup);
        startupFounderRepository.deleteAllByStartup(startup);
        startupRepository.delete(startup);
        return true;
    }
}
