package task.manager.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import task.manager.entity.Priority;
import task.manager.entity.Task;
import task.manager.entity.TasksRepository;
import task.manager.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static task.manager.utils.ObjectMapperInstance.getObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TasksControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TasksRepository tasksRepository;

    @Test
    public void shouldGetListOfTasks() throws Exception {
        // given
        when(tasksRepository.findAll())
                .thenReturn(Arrays.asList(
                        new Task(0L, "Mike", LocalDateTime.of(2024, 1, 1, 15, 15), new User(), new Priority()),
                        new Task(1L, "John", LocalDateTime.of(2024, 1, 15, 21, 30), new User(), new Priority()))
                );

        // when & then
        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is("Mike")))
                .andExpect(jsonPath("$[0].deadline", is("2024-01-01T15:15:00")))
                .andExpect(jsonPath("$[0].assignee", Matchers.anEmptyMap()))
                .andExpect(jsonPath("$[0].priority", Matchers.anEmptyMap()))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("John")))
                .andExpect(jsonPath("$[1].deadline", is("2024-01-15T21:30:00")))
                .andExpect(jsonPath("$[1].assignee", Matchers.anEmptyMap()))
                .andExpect(jsonPath("$[1].priority", Matchers.anEmptyMap()));
    }

    @Test
    public void shouldReturn400WhenTaskWithGivenIdNotFound() throws Exception {
        // when & then
        mvc.perform(get("/tasks/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnTaskById() throws Exception {
        // given
        when(tasksRepository.findById(1L))
                .thenReturn(Optional.of(new Task(1L, "Mike", LocalDateTime.of(2024, 1, 1, 15, 15), new User(), new Priority())));

        // when & then
        mvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Mike")))
                .andExpect(jsonPath("$.deadline", is("2024-01-01T15:15:00")))
                .andExpect(jsonPath("$.assignee", Matchers.anEmptyMap()))
                .andExpect(jsonPath("$.priority", Matchers.anEmptyMap()));
    }

    @Test
    public void shouldReturn400WhenTaskToUpdateWithGivenIdNotFound() throws Exception {
        // when & then
        mvc.perform(post("/tasks"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateTask() throws Exception {
        // given
        Task task = new Task(1L, "Mike", LocalDateTime.of(2024, 1, 1, 15, 15), new User(), new Priority());

        // when & then
        mvc.perform(post("/tasks")
                        .content(getObjectMapper().writeValueAsString(task))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturn400WhenThereIsNoTaskObjectInRequestWhenCreatingTask() throws Exception {
        // given
        Task task = new Task(1L, "Mike", LocalDateTime.of(2024, 1, 1, 15, 15), new User(), new Priority());
        when(tasksRepository.existsById(123L))
                .thenReturn(false);

        // when & then
        mvc.perform(put("/tasks/123")
                        .content(getObjectMapper().writeValueAsString(task))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateTask() throws Exception {
        // given
        Task task = new Task(1L, "Mike", LocalDateTime.of(2024, 1, 1, 15, 15), new User(), new Priority());
        Task taskToUpdate = new Task(1L, "John", LocalDateTime.of(2024, 1, 10, 10, 10), new User(), new Priority());
        when(tasksRepository.existsById(1L))
                .thenReturn(true);
        when(tasksRepository.save(ArgumentMatchers.any()))
                .thenReturn(taskToUpdate);

        // when & then
        mvc.perform(put("/tasks/1")
                        .content(getObjectMapper().writeValueAsString(task))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.deadline", is("2024-01-10T10:10:00")))
                .andExpect(jsonPath("$.assignee", Matchers.anEmptyMap()))
                .andExpect(jsonPath("$.priority", Matchers.anEmptyMap()));
    }

    @Test
    public void shouldDeleteTask() throws Exception {
        // given
        Task task = new Task(1L, "Mike", LocalDateTime.of(2024, 1, 1, 15, 15), new User(), new Priority());
        when(tasksRepository.existsById(1L))
                .thenReturn(true);
        when(tasksRepository.findById(1L))
                .thenReturn(Optional.of(task));

        // when & then
        mvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400WhenTryingDeleteNotExistingTask() throws Exception {
        // given
        when(tasksRepository.existsById(1L))
                .thenReturn(true);
        when(tasksRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when & then
        mvc.perform(delete("/tasks/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenTaskByIdToDeleteNotExists() throws Exception {
        // given
        when(tasksRepository.existsById(1L))
                .thenReturn(false);

        // when & then
        mvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound());
    }
}