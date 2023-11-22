package com.example.showcase.controllers;

import com.example.showcase.domens.Task;
import com.example.showcase.repo.InMemTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//IT-integration test ИНТЕГРАЦИОННОЕ ТЕСТИРОВАНИЕ
//их минус-зависимость от внешних факторов-например: данные хранятся в структуре ArrayList, если мы захотим перенести их в релиационную БД, то тесты придется переписать
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class TasksRestControllerIT {
//Тесты не должны опираться на результаты выполнения друг друга
//Поэтому в нашем мок-репозитории мы удаляем его содержимое перед вызовом тестового метода
//это реализованно в методе с аннотацией @AfterEach-вызывается перед выполнением каждого теста
@AfterEach
void tearDown(){
    this.taskRepository.getTasks().clear();
}

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemTaskRepository taskRepository;

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception{
        //given
        var requestBuilder= get("/api/tasks");
        this.taskRepository.getTasks() //сохраняем, предварительно, в репозиторий 2 задачи
                .addAll(List.of(new Task(UUID.fromString("71117396-8694-11ed-9ef6-77042ee83937"),
                                "First task", false),
                        new Task(UUID.fromString("7172d834-8694-11ed-8669-d7b17d45fba8"),
                                "Second task", true)));

        //when
        //производим обращение к тестируемому ендПоинту
        this.mockMvc.perform(requestBuilder)
        //then
                .andExpectAll(//проверяем что возвращенная строка соответствует ожидаемой
                        status().isOk(),//статус ОК
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                {
                                "id":"71117396-8694-11ed-9ef6-77042ee83937",
                                "details": "First task",
                                "completed":false
                                },
                                {
                                "id":"7172d834-8694-11ed-8669-d7b17d45fba8",
                                "details": "Second task",
                                "completed":true
                                }
                                ]
                                """)
                );

    }


    @Test //тест по проверки удачного создания новой задачи
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder=post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "details":"Third task"
                        }
                        
                        """);
        //when
        this.mockMvc.perform(requestBuilder)//ожидаем получить следующее
                //then
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().exists(HttpHeaders.LOCATION),//проверяем что в созданном объекте есть заголовок о локале
                        content().json("""
                                 {
                        "details":"Third task",
                        "completed":false
                        }
                                """),
                        jsonPath("$.id").exists());//в посл проверке нужно убедиться что идентификатор существует
        assertEquals(1,this.taskRepository.getTasks().size());//после создания эл-задачи мы проверяем ее наличие в репозитории, как в модульных тестах

final var task=this.taskRepository.getTasks().get(0);//получаем задачу из репо и делаем сл над ней проверки
assertNotNull(task.id());//id существует
assertEquals("Third task",task.details());//описание- "Third task"
assertFalse(task.completed());//не завершена
    }



    @Test //тест по проверки срабатывания ошибки при создании новой задачи
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder=post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE,"en") //передаем английскую локаль для получения описания ошибки из properties-файла для английской локали
                .content("""
                        {
                        "details":null
                        }
                        
                        """);
        //when
        this.mockMvc.perform(requestBuilder)//ожидаем получить следующее
                //then
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().doesNotExist(HttpHeaders.LOCATION),//проверяем что в созданном объекте нет заголовка о локале
                        content().json("""
                                 {
                        "errors": ["Task details must be set"]
                        }
                                """,true)//содержимое JSON-а это описание ошибки из нашего файла properties
        );//проверяем что не появилось новой задачи в нашем репо
        assertTrue(this.taskRepository.getTasks().isEmpty());
    }






}
