package task.manager.controllers;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class NotificationsControllerTest {

/*
    @Autowired
    private MockMvc mvc;

    @MockBean
    private NotificationsRepository notificationsRepository;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    public void shouldReturn400WhenUserWithIdNotExists() throws Exception {
        // given
        when(notificationsRepository.existsById(1L))
                .thenReturn(false);

        // when & then
        mvc.perform(get("/api/notifications?userId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldGetListOfNotificationsForSpecificUser() throws Exception {
        // given
        when(usersRepository.existsById(1L)).thenReturn(true);

        when(notificationsRepository.findByUserId(1L))
                .thenReturn(Arrays.asList(
                        new Notification(1L,
                                "User assignment",
                                "Task name 1",
                                new User(1L, "username", "email", "password", "John", "Doe", Collections.emptySet()),
                                LocalDateTime.of(2024, 1, 1, 15, 15),
                                false,
                                null),
                        new Notification(2L,
                                "User assignment",
                                "Task name 2",
                                new User(1L, "username", "email", "password", "John", "Doe", Collections.emptySet()),
                                LocalDateTime.of(2024, 1, 1, 16, 5),
                                true,
                                LocalDateTime.of(2024, 1, 1, 17, 32)))
                );

        // when & then
        mvc.perform(get("/api/notifications?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("User assignment")))
                .andExpect(jsonPath("$[0].taskName", is("Task name 1")))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].user.name", is("John")))
                .andExpect(jsonPath("$[0].user.surname", is("Doe")))
                .andExpect(jsonPath("$[0].createDate", is("2024-01-01T15:15:00")))
                .andExpect(jsonPath("$[0].read", is(false)))
                .andExpect(jsonPath("$[0].readDate").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("User assignment")))
                .andExpect(jsonPath("$[1].taskName", is("Task name 2")))
                .andExpect(jsonPath("$[1].user.id", is(1)))
                .andExpect(jsonPath("$[1].user.name", is("John")))
                .andExpect(jsonPath("$[1].user.surname", is("Doe")))
                .andExpect(jsonPath("$[1].createDate", is("2024-01-01T16:05:00")))
                .andExpect(jsonPath("$[1].read", is(true)))
                .andExpect(jsonPath("$[1].readDate", is("2024-01-01T17:32:00")));
    }

    @Test
    public void shouldReturn200WhenUserWithGivenIdHaveNoNotifications() throws Exception {
        // given
        when(usersRepository.existsById(1L)).thenReturn(true);
        when(notificationsRepository.findByUserId(1L)).thenReturn(emptyList());

        // when & then
        mvc.perform(get("/api/notifications?userId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldReturn404WhenNotificationToSetAsReadNotExist() throws Exception {
        // given
        when(notificationsRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        mvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn400WhenNotificationToSetAsReadIsAlreadyRead() throws Exception {
        // given
        when(notificationsRepository.findById(1L)).thenReturn(Optional.of(new Notification(1L,
                "User assignment",
                "Task name",
                new User(1L, "username", "email", "password", "John", "Doe", Collections.emptySet()),
                LocalDateTime.of(2024, 1, 1, 15, 15),
                true,
                null)));

        // when & then
        mvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturnNumberOfUnreadNotifications() throws Exception {
        // given
        when(usersRepository.existsById(1L))
                .thenReturn(true);
        Notification notification = new Notification();
        notification.setRead(false);
        when(notificationsRepository.findByUserId(1L))
                .thenReturn(Arrays.asList(notification, notification, notification, notification));


        // when & then
        mvc.perform(get("/api/notifications/count?userId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }

    @Test
    public void shouldReturn404WhenUserNotExistWhenGettingNotificationsCount() throws Exception {
        // given
        when(usersRepository.existsById(1L)).thenReturn(false);

        // when & then
        mvc.perform(get("/api/notifications/count?userId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteNotification() throws Exception {
        // given
        Notification notification = new Notification(1L,
                "User assignment",
                "Task name",
                new User(),
                LocalDateTime.of(2024, 1, 1, 15, 15),
                true,
                null);
        when(notificationsRepository.existsById(1L))
                .thenReturn(true);
        when(notificationsRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        // when & then
        mvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400WhenTryingDeleteNotExistingNotification() throws Exception {
        // given
        when(notificationsRepository.existsById(1L))
                .thenReturn(true);
        when(notificationsRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when & then
        mvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenNotificationByIdToDeleteNotExists() throws Exception {
        // given
        when(notificationsRepository.existsById(1L))
                .thenReturn(false);

        // when & then
        mvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNotFound());
    }
*/
}
