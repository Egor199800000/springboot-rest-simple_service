package com.example.showcase;

import com.example.showcase.controllers.TasksRestController;
import com.example.showcase.domens.ErrorsPresentation;
import com.example.showcase.domens.NewTaskPayload;
import com.example.showcase.domens.Task;
import com.example.showcase.repo.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//Покрываем наш контроллер модульными тестами
@ExtendWith(MockitoExtension.class)
public class TasksRestControllerTest {
//любой тест можно разделить на 3 части:
//given-ДАНО;
//when-КОГДА-ВЫЗОВ ТЕСТИРУЕМОГО МЕТОДА И ЗАПИСЬ РЕЗУЛЬТАТА;
//тогда-then --блок ПРОВЕРКИ РЕЗУЛЬТАТА МЕТОДА КОТОРЫЙ ТЕСТИМ

    @Mock //сторонние сервисы подкл как мок-объекты, для имитации их поведения
    TaskRepository taskRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks //создание экземпляра тестируемого класса и внедрил моки в качестве зависимостей
    TasksRestController controller;

     //тестируем метод по получению списка задач
        //имя тест-метода, как правило состоит из 3-х частей:
    // название метода; проверяемое условие; ожидаемое поведение метода
     @Test
    void handleGetAllTasks_ReturnsValidResponseEntity(){
         //given
        var tasks= List.of(new Task(UUID.randomUUID(), "Первая задача", false),
                new Task(UUID.randomUUID(), "Вторая задача", true));
        //моделируем поведение мок-объекта
        doReturn(tasks).when(this.taskRepository).findAll();//список задач должен быть возвращен когда у репозитория вызоввется метод findAll()

        //when
        var responseEntity=this.controller.handleGetAllTasks();

        //then --проверки что:
        assertNotNull(responseEntity);//не нулл
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());//статус ответа OK
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());//заголовок контент-тип это JSON
        assertEquals(tasks,responseEntity.getBody());//в кач ответа приходит список задач
        //assertNotEquals(HttpStatus.OK, responseEntity.getStatusCode()); //в этом случае в тестах будут ошибки
    }


    //в данном случае будет 2 тестовых метода, т.к в методе handleCreateNewTask
    //есть ветвление: метод для проверки ошибки ; метод для правильного выполнения кода
    @Test
    @DisplayName("GET /api/tasks returns an HTTP response with a 200 OK status and a list of tasks")
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity(){
        //given
        var details="Третья задача";

        //when
        //записываем результат работы нашего метода
        var responseEntity=this.controller.
                handleCreateNewTask(new NewTaskPayload(details),
                        UriComponentsBuilder.fromUriString("http://localhost:8080"),
                        Locale.ENGLISH);

        //then
        //проверяем результат-responseEntity нашего метода
        assertNotNull(responseEntity);//не нуловый
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());//статус креатед
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());//заголовок контент-тип это JSON
        if (responseEntity.getBody() instanceof Task task){//тело ответа-результата метода
            //является экземпляром класса Task
            assertNotNull(task.id());//id не равен Null
            assertEquals(details, task.details());//details-переданный в метод соответствует полученному task.details
            assertFalse(task.completed());//полк completed полученной задачи имеет значение false

            //URI-идентификация ресурса по адресу или имени или по обоим этим компонентам
            //проверка: URI в котором ожидаемо лежит нужный ресурс
            //равен URI ресурса, который вернет, проверяемый метод
            assertEquals(URI.create("http://localhost:8080/api/tasks/"+task.id()),
                    responseEntity.getHeaders().getLocation());
            //проверяем что на репозитории был вызван метод save()-для того
            //чтобы убедится что созданный объект был сохранен
            verify(this.taskRepository).save(task);
        }else {//если по какой-то причине тело ответа не явл экз класса Task
            assertInstanceOf(Task.class,responseEntity.getBody());
        }
        //проверяем что больше не было обращений к репозиторию
        verifyNoMoreInteractions(this.taskRepository);

    }

    @Test
    @DisplayName("GET /api/tasks returns an Error")
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity(){
    //given
    var details= "   ";
    var locale=Locale.US;
    var errorMessage="Details is empty";
//моделируем поведение метода getMessage у мок-объекта messageSource
    doReturn(errorMessage).when(this.messageSource) //1-сообщ возвращаемое в контроллере; 2-пустой массив; 3-локаль
            .getMessage("tasks.create.details.errors.not_set",new Object[0],locale);
        //when
        //помещаем результат выполнения метода в переменную
        var responseEntity=this.controller.handleCreateNewTask(
                new NewTaskPayload(details),
                 UriComponentsBuilder.fromUriString("http://localhost:8080"),
                locale
        );

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());//ответ будет BadRequest
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of(errorMessage)),responseEntity.getBody());

//проверка что не было обращения к репозиторию
verifyNoInteractions(this.taskRepository);

    }



}
