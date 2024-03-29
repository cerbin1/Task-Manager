package task.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import task.manager.entity.Notification;
import task.manager.entity.repository.NotificationsRepository;
import task.manager.entity.repository.UsersRepository;
import task.manager.security.UserDetailsImpl;
import task.manager.service.NotificationsService;
import task.manager.utils.AuthenticationUtils;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationsRepository notificationsRepository;
    private final NotificationsService notificationsService;
    private final UsersRepository usersRepository;

    @Autowired
    public NotificationsController(NotificationsRepository notificationsRepository, NotificationsService notificationsService, UsersRepository usersRepository) {
        this.notificationsRepository = notificationsRepository;
        this.notificationsService = notificationsService;
        this.usersRepository = usersRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getNotificationsForUserId(@RequestParam Optional<Long> userId) {
        Long userIdFinal = userId
                .orElseGet(AuthenticationUtils::getLoggedUserId);
        // ??? is it ok or userId should be set somewhere in frontend
        if (usersRepository.existsById(userIdFinal)) {
            Iterable<Notification> allNotifications = notificationsRepository.findByUserId(userIdFinal);
            return new ResponseEntity<>(allNotifications, OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        Optional<Notification> maybeNotification = notificationsRepository.findById(notificationId);
        if (maybeNotification.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        Notification notification = maybeNotification.get();
        Boolean alreadyReadNotification = notification.getRead();
        if (alreadyReadNotification) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
        notificationsRepository.markNotificationAsRead(notification);
        return new ResponseEntity<>(OK);
    }

    @PutMapping("/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markAllUserNotificationsAsRead(@RequestParam Long userId) {
        notificationsService.markAllUserNotificationsAsRead(userId);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserNotificationsCount(@RequestParam Optional<Long> userId) {
        Long userIdFinal = userId
                .orElseGet(() -> ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        // ??? is it ok or userId should be set somewhere in frontend
        if (usersRepository.existsById(userIdFinal)) {
            int notificationsCount = notificationsRepository
                    .findByUserId(userIdFinal)
                    .stream()
                    .filter(notification -> !notification.getRead())
                    .toList()
                    .size();
            return new ResponseEntity<>(notificationsCount, OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteBy(@PathVariable Long id) {
        if (notificationsRepository.existsById(id)) {
            Optional<Notification> notificationToDelete = notificationsRepository.findById(id);
            if (notificationToDelete.isPresent()) {
                notificationsRepository.delete(notificationToDelete.get());
                return new ResponseEntity<>(OK);
            }
            return new ResponseEntity<>(BAD_REQUEST);
        }
        return new ResponseEntity<>(NOT_FOUND);
    }
}
