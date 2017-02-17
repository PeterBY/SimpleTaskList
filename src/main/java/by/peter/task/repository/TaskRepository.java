package by.peter.task.repository;

import by.peter.task.domain.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for Task entity.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByStatusIsFalse(Pageable pageable);

    Page<Task> findAllByStatusIsTrue(Pageable pageable);
}
