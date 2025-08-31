package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //For assertEquals(), assertThrows, etc.
import static org.mockito.Mockito.*; //For when(), verify(), etc.

//Unit tests for TaskService using Mockito

public class TaskServiceTest {

    /*
     * @Mock -> creates a mock object of TaskRepository, means real DB is not used
     * here, instead
     * Mockito simulates repository behavior
     */
    @Mock
    private TaskRepository taskRepository;

    /*
     * creates and instance of taskService and injects the above mock taskRepository
     * into it.
     * this way we test the TaskSerice logic (not database)
     */
    @InjectMocks
    private TaskService taskService;

    /*
     * runs before every @Test method, intializes mock objects so that they are
     * ready to use
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
     * test case for getAllTasks(),
     * Mock Repository to return two tasks
     * Verifies taskService returns them correctly
     */
    @Test
    void testGetAllTasks() {
        Task task1 = new Task(1L, "Task 1", "Description 1", false, Instant.now(), Instant.now());
        Task task2 = new Task(2L, "Task 2", "Description 2", true, Instant.now(), Instant.now());

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById_Found() {
        Task task = new Task(1L, "Task 1", "Description 1", false, Instant.now(), Instant.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Task 1", result.getTitle());
        assertEquals("Description 1", result.getDescription());
        assertFalse(result.isCompleted());

        // verify that repository was called once during findById()
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateTask() {
        // Arrange: input request DTO
        TaskRequestDTO request = new TaskRequestDTO("New Task", "New Description", false);

        // The entity that the repository would save
        Task savedTask = new Task(
                1L,
                "New Task",
                "New Description",
                false,
                Instant.now(),
                Instant.now());

        // mock repository save
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act : call service method
        Task result = taskService.createTask(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Task", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertFalse(result.isCompleted());

        // verify repository interaction
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_Found() {
        Long taskId = 1L;
        Task existingTask = new Task(
                taskId,
                "Old Title",
                "Old Desc",
                false,
                Instant.now(),
                Instant.now());

        TaskRequestDTO updateRequest = new TaskRequestDTO("Updated Title", "Updated Desc", false);

        Task updatedTask = new Task(
                taskId,
                "Updated Title",
                "Updated Desc",
                false,
                existingTask.getCreatedAt(),
                Instant.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(taskId, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals(taskId, result.getId());

        // verify interactions
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound() {
        TaskRequestDTO updatedTaskRequest = new TaskRequestDTO(
                "Updated Task", "Updated Desc", true);

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, updatedTaskRequest));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteTask_Found() {
        Task existingTask = new Task(1L, "Title", "Desc", false, Instant.now(), Instant.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).findById(1L);
    }
}
