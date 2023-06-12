package ru.itgroup.intouch.config.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "email")
public class EmailContentsConfig {
    private EmailContents passwordRecovery;
    private EmailContents emailChange;
    private EmailContents passwordChange;



}
