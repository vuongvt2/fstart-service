package com.fstart.service.model.startup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * StartupForm
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartupLogoForm {

    private Long id;

    private MultipartFile logo;

}
