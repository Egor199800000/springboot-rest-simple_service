package com.example.showcase.controllers;

import com.example.showcase.domens.ErrorsPresentation;
import com.example.showcase.domens.NewTaskPayload;
import com.example.showcase.domens.Task;
import com.example.showcase.repo.TaskRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;



//простой рест контроллер с 3 ендПоинтами:
//получение списка задач; добавление новой задачи; получение задачи по id

@RestController
@RequestMapping("/api/tasks")
public class TasksRestController {

    private final TaskRepository taskRepository;

    //сервис для перевода логов на человеческий язык,
    // а уже на какой язык мы можем использовать локаль(Local) для определения;
    // Используется файл messages.properties где записаны логи и их перевод
    private final MessageSource messageSource;

    public TasksRestController(TaskRepository taskRepository, MessageSource messageSource) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks(){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.taskRepository.findAll());
    }

    @PostMapping //ResponseEntity билдер для
    public ResponseEntity<?> handleCreateNewTask(
            @RequestBody NewTaskPayload payload,
            UriComponentsBuilder uriComponentsBuilder, //для создания ссылки на созданный объект; UriComponentsBuilder в данном случае будет создан относительно того запроса который обрабатывается в данном методе
            Locale locale //применяется для определения локали пользователя
    ){
        if (payload.details()==null||payload.details().isBlank()){
            //для перевода лога исп messageSource
            final var message=this.messageSource
                    .getMessage("tasks.create.details.errors.not_set", //первый аргумент-строка для перевода
                    new Object[0], locale);
return ResponseEntity.badRequest()//билдим ответ
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorsPresentation(List.of(message)));
        }else {
            var task = new Task(payload.details());
            this.taskRepository.save(task);
            return ResponseEntity.created(uriComponentsBuilder
                            .path("/api/tasks/{taskId}") //назначаем путь создаваемого объекта
                            .build(Map.of("taskId", task.id())))//указываем что в переменной пути(URI) явл id
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(task);

        }

    }

//метод для получения задачи по id; например: http://localhost:8080/api/tasks/5184b954-96a5-4a28-8a95-77fd2367ec72
    @GetMapping("{id}") //UUID-стандарт идентификации, используемый в создании программного обеспечения-сам генерирует идентификатор
    public ResponseEntity<Task> handleFindTask(@PathVariable("id") UUID id){
        return ResponseEntity.of(this.taskRepository.findById(id));
    }



}
