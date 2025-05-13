package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.repository.PositionRepository;
import org.springframework.stereotype.Service;

/**
 * PositionServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/03/22
 */
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    public PositionServiceImpl(final PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public DataWrapper getAllPositions() {
        return DataWrapper.builder()
                .data(positionRepository.findAll())
                .status(AppConstant.SUCCESS)
                .build();
    }
}
