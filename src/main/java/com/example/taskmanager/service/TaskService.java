package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.exception.TaskNotFoundException;

import java.util.List;
//import java.util.Map;
//import java.util.Optional;

@Service //marks this as the Service bean for Spring to manage
@RequiredArgsConstructor
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    // 1) Create a new task
    public Task createTask(TaskRequest taskRequest) {
        Task task = Task.builder()
            .title(taskRequest.getTitle())
            .description(taskRequest.getDescription())
            .completed(taskRequest.getCompleted() != null ? taskRequest.getCompleted() : false)
            .build();

        return taskRepository.save(task);
    }

    // 2) Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 3) Get Task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    // 4) Update task
    public Task updateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.getCompleted() != null ? taskRequest.getCompleted() : false);

        return taskRepository.save(task);
    }

    // 5) Delete Task
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }

    //6) Patch Update Task
    public Task patchTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        if(taskRequest.getTitle() != null && !taskRequest.getTitle().isBlank()) {
            task.setTitle(taskRequest.getTitle());
        }

        if(taskRequest.getDescription() != null ) {
            task.setDescription(taskRequest.getDescription());
        }

        if(taskRequest.getCompleted() != null) {
            task.setCompleted(taskRequest.getCompleted());
        }

        return taskRepository.save(task);
    }

   //GET with Pagination + Filtering
   public Page<Task> getTasks(Boolean completed, String keyword, Pageable pageable) {
    if(completed!= null) {
        return taskRepository.findByCompleted(completed, pageable);
    } else if (keyword != null &&  !keyword.isBlank()) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    } else {
        return taskRepository.findAll(pageable);
    }
   }
}
