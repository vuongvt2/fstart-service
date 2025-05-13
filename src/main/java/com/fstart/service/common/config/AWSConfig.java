package com.fstart.service.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fstart.service.common.constant.AWSConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWSConfig
 *
 * @author VuongVT2
 * @since 2021/11/01
 */
@Configuration
public class AWSConfig {

    private final AWSConstant awsConstant;

    public AWSConfig(final AWSConstant awsConstant) {
        this.awsConstant = awsConstant;
    }

    private AWSStaticCredentialsProvider getAwsCredentials() {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials(awsConstant.getAwsAccessKey(), awsConstant.getAwsSecretKey());
        return new AWSStaticCredentialsProvider(credentials);
    }

    @Bean
    public AmazonS3 s3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getAwsCredentials())
                .withRegion(awsConstant.getAwsRegion())
                .build();
    }

}
