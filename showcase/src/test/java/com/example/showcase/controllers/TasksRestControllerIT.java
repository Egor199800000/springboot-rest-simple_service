//package com.example.showcase.controllers;
//
//import com.example.showcase.domens.Task;
//import com.example.showcase.repo.InMemTaskRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
////IT-integration test ИНТЕГРАЦИОННОЕ ТЕСТИРОВАНИЕ
////их минус-зависимость от внешних факторов-например: данные хранятся в структуре ArrayList, если мы захотим перенести их в релиационную БД, то тесты придется переписать
//@SpringBootTest
//@AutoConfigureMockMvc(printOnlyOnFailure = false)
//public class TasksRestControllerIT {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    InMemTaskRepository taskRepository;
//
//    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception{
//        //given
//        var requestBuilder= get("/api/tasks");
//        UUID uuid1 = UUID.randomUUID();
//        UUID uuid2 = UUID.randomUUID();
//
//
//        this.taskRepository.getTasks()
//                .addAll(List.of(new Task(UUID.fromString(String.valueOf(uuid1)),
//                                "Первая задача", false),
//                        new Task(UUID.fromString(String.valueOf(uuid2)),
//                                "Вторая задача", true)));
//
//        //when
//        //производим обращение к тестируемому ендПоинту
//        this.mockMvc.perform(requestBuilder)
//        //then
//                .andExpectAll(//делаем проверку на результате метода
//                        status().isOk(),//статус ОК
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        content().json("""
//                                [
//                                {
//                                "id":"",
//                                "details": "Первая задача",
//                                "completed":false
//                                },
//                                {
//                                "id":"",
//                                "details": "Вторая задача",
//                                "completed":true
//                                }
//                                ]
//                                """)
//                );
//
//    }
//
//
//
//
//}
