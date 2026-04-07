package com.nomolestar.notificationservice.service;

import com.nomolestar.notificationservice.dto.NotificationResponseDTO;
import com.nomolestar.notificationservice.exceptions.ResourceNotFoundException;
import com.nomolestar.notificationservice.mapper.NotificationMapper;
import com.nomolestar.notificationservice.model.NotificationEntity;
import com.nomolestar.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponseDTO> findAll() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> findUnread() {
        return notificationRepository.findByRead(false)
                .stream()
                .map(NotificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public NotificationResponseDTO markAsRead(Integer id) {
        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification with id " + id + " not found"));
        entity.setRead(true);
        NotificationEntity saved = notificationRepository.save(entity);
        return NotificationMapper.toResponseDTO(saved);
    }

    public NotificationEntity save(NotificationEntity entity) {
        return notificationRepository.save(entity);
    }
}
