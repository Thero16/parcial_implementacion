package com.nomolestar.peopleservice.repository;

import com.nomolestar.peopleservice.model.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {
    List<PersonEntity> findByCaseId(Integer caseId);

}
