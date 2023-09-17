package com.example.users53;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// запускать тест на случайном порту
public class IntegrationTest {

    @Value(value = "${local.server.port}")
    private int port;
    // конфигурационная настройка из /resources/application.properties

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;
    // создаваемый на время тестов класс для посылки тестовых rest-запросов
    // в тестируемое приложение

    @Test
    public void createUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"rob\", \"email\": \"rob@email.com\"}",
                headers
        );

        assertEquals(userRepository.count(), 0L);

        String result = restTemplate.postForEntity(
                "http://localhost:"+port+"/users",
                request,
                String.class
        ).getBody();

        assertEquals(userRepository.count(), 1L);


        // проверьте что result это "User created"
        assertEquals(result, "User created");
    }


    @Test
    public void checkEmptyName() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{\"email\": \"rob@email.com\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:"+port+"/users",
                request,
                String.class
        );

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        String result = response.getBody();

        // {"name": "Name is mandatory"}

        JSONObject body = new JSONObject(result);
        assertTrue(body.has("name"));
        // body.get("name")
        // проверьте что значение элемента name "Name is mandatory"
        assertEquals(body.get("name"), "Name is mandatory");
    }


    // напишите тест который проверит что при name=max,email=max
    // возвращается нужная ошибка
    // {"name":"max", "email":"max"} -> {"email":"Email must be valid"}

    @Test
    public void checkInvalidEmail() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"max\", \"email\":\"max\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:"+port+"/users",
                request,
                String.class
        );

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        String result = response.getBody();
        JSONObject body = new JSONObject(result);
        assertTrue(body.has("email"));
        assertEquals(body.get("email"), "Email must be valid");
    }

    @Test
    public void helloTest() throws JSONException {
        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "http://localhost:" + port + "/upper?text=hello",
                        String.class
                );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(response.hasBody());
        JSONObject body = new JSONObject(response.getBody());
        assertTrue(body.has("result"));
        assertEquals(body.get("result"), "HELLO");
    }

}
