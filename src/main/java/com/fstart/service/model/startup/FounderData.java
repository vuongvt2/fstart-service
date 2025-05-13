package com.fstart.service.model.startup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FounderForm
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FounderData {
    private Long id;
    private String name;
    private String socialNetwork;
    boolean delete;
}


