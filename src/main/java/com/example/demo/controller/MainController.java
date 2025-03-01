package com.example.demo.controller;


import com.example.demo.dto.ResponseGameDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.GameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SuppressWarnings("checkstyle:CommentsIndentation")
@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {


    private final UserRepo userRepo;

    @GetMapping("/game/{id}")
    public ResponseEntity<?> getNameOfGame(@PathVariable int id) {
        if (!GameService.isIdValid(id)) {
            return ResponseEntity.badRequest().body("Invalid game ID");
        }
        try {
            String nameOfGame = GameService.getNameOfGame(id);
            if (nameOfGame == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
            }
            return ResponseEntity.ok(new ResponseGameDto(nameOfGame, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }

    @PostMapping("/user")
    public void addUser(@RequestBody User user) {
        log.info("New Row: {}", userRepo.save(user));
    }

    @SneakyThrows
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/user")
    public User getUser(@RequestParam int id) {
        return userRepo.findById(id).orElse(null);
    }

    @DeleteMapping("/user")
    public void deleteUser(@RequestParam int id) {
        userRepo.deleteById(id);
    }

    @PutMapping("/user")
    public String updateUser(@RequestBody User user) {
        if (!userRepo.existsById(user.getId())) {
            return "No such row";
        }
        return userRepo.save(user).toString();

    }


}
