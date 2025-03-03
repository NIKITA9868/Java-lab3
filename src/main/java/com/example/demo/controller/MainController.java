package com.example.demo.controller;


import com.example.demo.dto.ResponseGameDto;
import com.example.demo.dto.ResponseUserDto;
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
    public ResponseEntity<ResponseGameDto> getNameOfGame(@PathVariable int id) {
        if (GameService.isIdNotValid(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseGameDto("Invalid game ID", id));
        }

        String nameOfGame = GameService.getNameOfGame(id);
        if (nameOfGame == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseGameDto("Game not found", id));
        }

        log.info("Game found: {}", nameOfGame);
        return ResponseEntity.ok(new ResponseGameDto(nameOfGame, id));
    }


    @PostMapping("/user")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userRepo.save(user);
        log.info("New Row: {}", savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @SneakyThrows
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseUserDto> getUser(@RequestParam int id) {
        if (GameService.isIdNotValid(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseUserDto("Invalid game ID", 0, id));
        }
        return userRepo.findById(id).map(user -> ResponseEntity
                .ok(new ResponseUserDto(user.getName(), user.getBalance(), user.getId())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseUserDto("User not found", 0, id)));
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteUser(@RequestParam int id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        userRepo.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/user")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (!userRepo.existsById(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User updatedUser = userRepo.save(user);
        return ResponseEntity.ok(updatedUser);
    }


}
