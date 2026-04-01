package com.nomolestar.peopleservice.dto;

import com.nomolestar.peopleservice.enums.PersonRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonCreateDTO(

        @NotNull(message = "The case ID is required.")
        Integer caseId,

        @NotBlank(message = "The full name is required.")
        String fullName,

        @NotNull(message = "The person role is required.")
        PersonRole role,

        Integer age,

        String description
) {
}