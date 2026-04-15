package com.eventsapp.event.controller;

import com.eventsapp.config.SecurityConfig;
import com.eventsapp.event.dto.CreateEventRequest;
import com.eventsapp.event.dto.EventResponse;
import com.eventsapp.event.model.Event;
import com.eventsapp.event.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @Test
    void listEvents_isPublicAndReturnsEvents() throws Exception {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        when(eventService.listEvents()).thenReturn(List.of(
            new EventResponse(1L, "Concert", now, 100, 42)
        ));

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Concert"))
            .andExpect(jsonPath("$[0].maxParticipants").value(100))
            .andExpect(jsonPath("$[0].registrationsCount").value(42));
    }

    @Test
    void createEvent_withoutAuth_isForbidden() throws Exception {
        var req = new CreateEventRequest("Conference", OffsetDateTime.now(ZoneOffset.UTC), 50);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    void createEvent_asAdmin_returns201() throws Exception {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var event = Event.builder()
            .id(1L)
            .name("Conference")
            .startsAt(now)
            .maxParticipants(50)
            .build();
        when(eventService.createEvent(any())).thenReturn(event);

        var req = new CreateEventRequest("Conference", now, 50);
        mockMvc.perform(post("/api/events")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Conference"))
            .andExpect(jsonPath("$.maxParticipants").value(50))
            .andExpect(jsonPath("$.registrationsCount").value(0));
    }
}
