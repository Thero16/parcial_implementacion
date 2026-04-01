package com.nomolestar.peopleservice.dto;

import com.nomolestar.peopleservice.enums.PersonRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonUpdateDTO(

        @NotBlank(message = "The full name cannot be blank.")
        String fullName,

        @NotNull(message = "The person role cannot be null.")
        PersonRole role,

        Integer age,

        String description
) {
}
