package com.alxsshv.codedistributionservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class CodeKafkaMessage {

    String email;

    String code;

}
