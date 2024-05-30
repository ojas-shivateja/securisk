package com.insure.rfq.configuration;

import com.insure.rfq.utils.ExcelUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RfqConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ExcelUtils excelUtils() {
        return new ExcelUtils();
    }
}
