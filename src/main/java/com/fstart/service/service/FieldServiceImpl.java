package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.repository.FieldRepository;
import org.springframework.stereotype.Service;

/**
 * FieldServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Service
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;

    public FieldServiceImpl(final FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public DataWrapper getAllField() {
        return DataWrapper.builder()
                .data(fieldRepository.findAll())
                .status(AppConstant.SUCCESS)
                .build();
    }
}
