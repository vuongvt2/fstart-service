package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.repository.TechnologyRepository;
import org.springframework.stereotype.Service;

/**
 * TechnologyServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Service
public class TechnologyServiceImpl implements TechnologyService {

    private final TechnologyRepository technologyRepository;

    public TechnologyServiceImpl(final TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    @Override
    public DataWrapper getAllTechnology() {
        return DataWrapper.builder()
                .data(technologyRepository.findAll())
                .status(AppConstant.SUCCESS)
                .build();
    }
}
