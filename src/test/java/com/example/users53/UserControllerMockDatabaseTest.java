package com.example.users53;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest // загружается только часть Spring boot приложения которая отвечает за
// работу с контроллерами
@AutoConfigureMockMvc // для нас создается механизм для посылки данных в контроллер
public class UserControllerMockDatabaseTest {
    @Autowired
    MockMvc mockMvc; // возможность посылать запросы в контроллеры не загружая
    // веб-сервер

    @Autowired
    UserController userController;

    @MockBean // заменяем реализацию репо на заглушку
            // позволяет тестировать методы контроллера которые работают с базой данных
            // не загружая в память репо
    UserRepository repository;
    // если создаем заглушку, можем тестировать логику контроллеров которая базу данных
    // не трогает


    @Test
    public void noNameIsError() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content("{\"email\": \"bob@gmail.com\"}")
                        .contentType("application/json")
        )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.name", Is.is("Name is mandatory"))
                );
    }

    // "Email is mandatory"
    @Test
    public void noEmailIsError() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\"name\": \"max\"}")
                                .contentType("application/json")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.email", Is.is("Email is mandatory"))
                );
    }

    @Test
    public void getUserFromDatabase() throws Exception {
        when(repository.findById(10L)).thenReturn(
                Optional.of(new User(10L, "rob", "rob@gmail.com"))
        );

        // выполните запрос
        // get("/users/10")
        // убедитесь что name и email имеют правильные значения
        mockMvc.perform(
                get("/users/10")
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.name", Is.is("rob"))
                ).andExpect(
                        jsonPath("$.email", Is.is("rob@gmail.com"))
                );
        // вызывался только один раз
        verify(repository, times(1)).findById(10L);
        // больше ничего в тесте из repository не вызывалось
        verifyNoMoreInteractions(repository);

    }

    @Test
    public void testReturnMultipleUsers() throws Exception {
        // настройте мок репозитори
        // repository.findAll() ->
        // User(1L, "Max", "max@gmail.com")
        // User(2L, "Lena", "lena@yahoo.com")
        when(repository.findAll()).thenReturn(
                Arrays.asList(
                        new User(1L, "Max", "max@gmail.com"),
                        new User(2L, "Lena", "lena@yahoo.com")
                )
        );

        mockMvc.perform(
                get("/users")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", anyOf(Is.is("Max"), Is.is("Lena"))))
                .andDo(print());

    }
}
