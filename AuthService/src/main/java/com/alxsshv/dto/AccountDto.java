package com.alxsshv.dto;


import com.alxsshv.entity.Status;

public record AccountDto(
        String id,
        String email,
        String status) {
}
