package com.nomolestar.caseservice.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path
) {}
