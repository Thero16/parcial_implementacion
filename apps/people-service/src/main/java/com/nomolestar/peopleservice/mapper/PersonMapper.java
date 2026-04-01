package com.nomolestar.peopleservice.mapper;

import com.nomolestar.peopleservice.dto.PersonCreateDTO;
import com.nomolestar.peopleservice.dto.PersonResponseDTO;
import com.nomolestar.peopleservice.dto.PersonUpdateDTO;
import com.nomolestar.peopleservice.model.PersonEntity;

public class PersonMapper {

    private PersonMapper() {
    }

    public static PersonEntity toEntity(PersonCreateDTO dto) {
        PersonEntity entity = new PersonEntity();

        entity.setCaseId(dto.caseId());
        entity.setFullName(dto.fullName());
        entity.setRole(dto.role());
        entity.setAge(dto.age());
        entity.setDescription(dto.description());

        return entity;
    }

    public static void updateEntity(PersonEntity entity, PersonUpdateDTO dto) {
        entity.setFullName(dto.fullName());
        entity.setRole(dto.role());
        entity.setAge(dto.age());
        entity.setDescription(dto.description());
    }

    public static PersonResponseDTO toResponseDTO(PersonEntity entity) {
        return new PersonResponseDTO(
                entity.getId(),
                entity.getCaseId(),
                entity.getFullName(),
                entity.getRole(),
                entity.getAge(),
                entity.getDescription()
        );
    }
}