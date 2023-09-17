package com.example.users53;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // напишите функцию getAll которая возвращает всех пользователей
    // GET /users
    @GetMapping("/users")
    public Iterable<User> getAll()
    {
        return userRepository.findAll();
    }

    @GetMapping("/users1")
    public ResponseEntity<Iterable<User>> getAllUsers()
    {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Post -> User
    // String addUser(User)
    // проверять пользователя @Valid
    // сохранять в базу данных
    // возвращать "User created"
    @PostMapping("/users")
    public String addUser(
            @Valid // будет проверяться по проверкам из Entity
            @RequestBody // будет передаваться в теле запроса
            User user
    )
    {
        userRepository.save(user);
        return "User created";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(
            MethodArgumentNotValidException ex
    )
    {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(
                error ->
                        errors.put(
                                ((FieldError) error).getField(),
                                error.getDefaultMessage()
                        )
        );
        return errors;
    }

    // GET http://localhost:8080/upper?text=hello
    @GetMapping("/upper")
    public Map<String, String> toUpper(
            @RequestParam String text
    )
    {
        Map<String, String> result = new HashMap<>();
        String toUpper = text == null ? "" : text.toUpperCase();
        result.put("result", toUpper);
        return result;
    }

    @GetMapping("/users/{id}")
    public Optional<User> findUserById(
            @PathVariable Long id
    )
    {
        return userRepository.findById(id);
    }


}
