package com.nomolestar.evidenceservice.dto;

import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EvidenceUpdateDTO(

        @NotNull(message = "The evidence type is required.")
        EvidenceType evidenceType,

        @NotBlank(message = "The description is required.")
        String description,

        @NotBlank(message = "The location where the evidence was found is required.")
        String locationFound,

        @NotNull(message = "The collection date is required.")
        LocalDateTime dateCollected,

        @NotBlank(message = "The collector name is required.")
        String collectedBy,

        String fileUrl,

        @NotNull(message = "The custody status is required.")
        CustodyStatus custodyStatus,

        @NotBlank(message = "The current custodian is required.")
        String currentCustodian,

        String transferReason
) {
}
