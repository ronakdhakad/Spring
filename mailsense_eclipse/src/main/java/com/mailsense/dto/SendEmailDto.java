package com.mailsense.dto;

import com.mailsense.entity.SentEmail;
import lombok.*;
import javax.validation.constraints.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SendEmailDto {

    @NotBlank(message = "Recipient address is required")
    private String toAddress;

    private String ccAddress;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    private SentEmail.SendType sendType = SentEmail.SendType.NEW;
    private Long   inReplyToEmailId;
    private String inReplyToMessageId;
    private String originalBody;
}
