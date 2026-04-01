package com.nomolestar.peopleservice.dto;

import com.nomolestar.peopleservice.enums.PersonRole;

public record PersonResponseDTO(

        Integer id,
        Integer caseId,
        String fullName,
        PersonRole role,
        Integer age,
        String description
) {
}
