package com.nomolestar.evidenceservice.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path
) {}
