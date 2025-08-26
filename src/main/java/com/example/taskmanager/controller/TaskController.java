package com.example.taskmanager.controller;

import com.example.taskmanager.dto.PageResponse;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
//import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;

//import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "CRUD operation for Tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    /*// 1. Get all tasks
    @GetMapping
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponse> responses = new ArrayList<>();

        for(Task task : tasks) {
            responses.add(mapToResponse(task));
        } 

        return responses;
    }*/

    //1. Paged getAllTasks() 

    @Operation(summary = "Get all tasks", description = "Return paginated list of tasks with optional filtering")
    @GetMapping
    public PageResponse<TaskResponse> getAllTasks (@RequestParam(required = false) Boolean completed,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Task> taskPage  = taskService.getTasks(completed, keyword, pageable);

        return PageResponse.of(taskPage.map(this::mapToResponse));
    }

    @Operation(summary = "Get task by ID", description = "Gets the required task with the help of ID from the pathVariable")
    //2. Get task by id
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(mapToResponse(task));
    }

    @Operation(summary = "Create task", description = "Create the task as per parameters passed in the request body")
    //3. Create task
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Task task = taskService.createTask(taskRequest); //service now accepts DTO
        TaskResponse response = mapToResponse(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Helper method to convert entity to DTO
    private TaskResponse mapToResponse (Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .completed(task.isCompleted())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }

    @Operation(summary = "Update the task with ID", description = "Full update of the required task")
    //4. Update task
    @PutMapping ("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        Task updatedTask = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(mapToResponse(updatedTask));
    }

    @Operation(summary = "Delete Task by ID", description = "Delete the task by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        Map<String,String> response = new HashMap<>();
        response.put("message","The task with id = " + id + " has been deleted.");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Partial Update the task by ID", description = "Partial update of the task as per ID")
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> patchTask (@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        Task updatedTask = taskService.patchTask(id, taskRequest);

        return ResponseEntity.ok(mapToResponse(updatedTask));
    }
}
