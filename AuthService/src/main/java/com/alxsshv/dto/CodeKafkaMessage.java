package com.alxsshv.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class CodeKafkaMessage {

    String email;

    String code;

}
