package ru.itgroup.intouch.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Notification;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.itgroup.intouch.contracts.service.creators.NotificationCreator;
import ru.itgroup.intouch.dto.NotificationDto;
import ru.itgroup.intouch.dto.request.NotificationRequestDto;
import ru.itgroup.intouch.repository.NotificationRepository;
import ru.itgroup.intouch.repository.jooq.FriendRepository;
import ru.itgroup.intouch.repository.jooq.NotificationJooqRepository;
import ru.itgroup.intouch.repository.jooq.NotificationSettingRepository;
import ru.itgroup.intouch.service.notification.sender.NotificationSender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCreatorService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationJooqRepository notificationJooqRepository;
    private final NotificationCreatorFactory notificationCreatorFactory;
    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;
    private final FriendRepository friendRepository;

    private NotificationCreator notificationCreator;

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
        log.info("Executor service was shutdown");
    }

    public void createNotification(@NotNull NotificationRequestDto notificationRequestDto)
            throws ClassNotFoundException {
        notificationCreator = notificationCreatorFactory
                .getNotificationCreator(notificationRequestDto.getNotificationType());

        notificationCreator.validateData(notificationRequestDto);
        String content = notificationCreator.getContent(notificationRequestDto.getEntityId());
        if (notificationRequestDto.getReceiverId() == null) {
            createMassNotifications(notificationRequestDto, content);
        }

        createSingleNotification(notificationRequestDto, content);
    }

    private void createMassNotifications(@NotNull NotificationRequestDto notificationRequestDto, String content) {
        Set<Long> receiverIdList = new HashSet<>(
                friendRepository
                        .getReceiverIds(notificationRequestDto.getAuthorId(), notificationCreator.getTableField())
        );

        if (receiverIdList.isEmpty()) {
            return;
        }

        List<NotificationDto> notificationDtoList = new ArrayList<>();
        for (Long receiverId : receiverIdList) {
            notificationRequestDto.setReceiverId(receiverId);
            notificationDtoList.add(notificationCreator.create(notificationRequestDto, content));
        }

        List<Long> notificationIdList = notificationJooqRepository.saveNotifications(notificationDtoList);
        List<Notification> notifications = notificationRepository.findAllById(notificationIdList);
        notificationSender.send(notifications);
    }

    private void createSingleNotification(@NotNull NotificationRequestDto notificationRequestDto, String content) {
        boolean isEnableNotification = notificationSettingRepository
                .isEnable(notificationRequestDto.getReceiverId(), notificationCreator.getTableField());
        if (!isEnableNotification) {
            return;
        }

        NotificationDto notificationDto = notificationCreator.create(notificationRequestDto, content);
        long notificationId = notificationJooqRepository.saveNotificationFromDto(notificationDto);

        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null) {
            return;
        }

        notificationSender.send(notification);
    }
}
