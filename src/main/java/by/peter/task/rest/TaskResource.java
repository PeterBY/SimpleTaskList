package by.peter.task.rest;

import by.peter.task.domain.Task;

import by.peter.task.repository.TaskRepository;
import by.peter.task.rest.util.HeaderUtil;
import by.peter.task.rest.util.PaginationUtil;
import by.peter.task.rest.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Task.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    private static final String ENTITY_NAME = "task";

    private final TaskRepository taskRepository;

    public TaskResource(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * POST  /tasks : Create a new task.
     *
     * @param task the task to create
     * @return the ResponseEntity with status 201 (Created) and with body the new task, or with status 400 (Bad Request) if the task has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) throws URISyntaxException {
        log.debug("REST request to save Task : {}", task);
        if (task.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new task cannot already have an ID")).body(null);
        }
        Task result = taskRepository.save(task);
        return ResponseEntity.created(new URI("/api/tasks/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * POST  /tasks:id : Updates an existing task.
     *
     * @param task the task to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated task,
     * or with status 400 (Bad Request) if the task is not valid,
     * or with status 500 (Internal Server Error) if the task couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@RequestBody Task task, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to update Task : {}", task);
        if (task.getId() == null) {
            Task result = taskRepository.save(task);
            return ResponseEntity.created(new URI("/api/tasks/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                    .body(result);
        }
        Task result = taskRepository.save(task);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, task.getId().toString()))
                .body(result);
    }

    /**
     * GET  /tasks : get all tasks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tasks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) String status, Pageable pageable)
            throws URISyntaxException {
        log.debug("REST request to get a page of Tasks");
        Page<Task> page;
        if (status != null && status.equals("active"))
            page = taskRepository.findAllByStatusIsFalse(pageable);
        else if (status != null && status.equals("done"))
            page = taskRepository.findAllByStatusIsTrue(pageable);
        else
            page = taskRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tasks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tasks/:id : get task by "id".
     *
     * @param id the id of the task to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the task, or with status 404 (Not Found)
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        log.debug("REST request to get Task : {}", id);
        Task task = taskRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(task));
    }

    /**
     * DELETE  /tasks/:id : delete task by "id".
     *
     * @param id the id of the task to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.debug("REST request to delete Task : {}", id);
        taskRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
