package by.peter.task;

import by.peter.task.domain.Task;
import by.peter.task.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Bean
    CommandLineRunner init(TaskRepository taskRepository) {
        return (evt) -> {
            for (int i = 1; i < 100; i++)
                taskRepository.save(new Task("Task " + i));
        };
    }
}
