package task.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.manager.controllers.dto.StatisticDto;
import task.manager.service.StatsService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/tasksCount")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTasksCount() {
        return new ResponseEntity<>(statsService.getNumberOfTasks(), OK);
    }

    @GetMapping("/userLoggedTime")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUsersTimeLoggedStats() {
        return new ResponseEntity<>(statsService.getTimeLoggedByUsers(), OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StatisticDto> getStatistics() {
        return new ResponseEntity<>(statsService.getStatistics(), OK);
    }
}
