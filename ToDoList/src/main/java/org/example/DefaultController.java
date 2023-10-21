package org.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DefaultController {
    private List<Task> taskList = new ArrayList<>();
    private DBConnection dbConnection = new DBConnection();
    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public ResponseEntity add(@RequestParam String title, @RequestParam String description) {
        dbConnection.create(title,description);
        return ResponseEntity.status(201).build();
    }
    @RequestMapping("/")
    public String index(){
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy\sHH:mm:ss");
        String time = "Текущая дата:\s" + formatter.format(currentDate);
        return time;
    }
    @GetMapping(value = "/tasks/ID")
    public ResponseEntity get(@RequestParam(value = "ID", defaultValue = "1") Integer id) {
        Task task = dbConnection.get(id);
        if (task != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(task);
        } else {
            return ResponseEntity.status(404).body("NOT_FOUND");
        }
    }

    @GetMapping(value = "/tasks")
    public ResponseEntity getList() {
        taskList = dbConnection.getListTask();
        if (!taskList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/tasks/ID", method = RequestMethod.PATCH)
    public ResponseEntity path(@RequestParam Integer id, @RequestParam String title,@RequestParam String description, @RequestParam Boolean isDone) {
        Task task = dbConnection.get(id);
        if(task != null){
            dbConnection.update(id,title,description,isDone);
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(404).body("NOT FOUND");
        }
    }
    @RequestMapping(value = "/tasks/ID",method = RequestMethod.DELETE)
    public ResponseEntity delete(@RequestParam Integer id){
        if(dbConnection.get(id) != null) {
            dbConnection.delete(id);
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(404).body("NOT FOUND");
        }
    }
}
