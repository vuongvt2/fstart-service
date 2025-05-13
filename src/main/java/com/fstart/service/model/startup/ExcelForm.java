package com.fstart.service.model.startup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ExcelForm
 *
 * @author: VuongVT2
 * @since: 2022/04/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelForm {
    private List<StartupExcelForm> startupExcelForms;
    private List<FounderData> founders;
    private List<StartupFounderForm> startupFounderForms;
    private List<StartupFieldForm> startupFieldForms;
}
