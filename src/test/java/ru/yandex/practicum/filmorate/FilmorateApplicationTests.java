package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {
    private static final String BASE = "http://localhost:8080";
    private static HttpClient client;

    @BeforeAll
    static void beforeAll() throws IOException {
        SpringApplication.run(FilmorateApplication.class);
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Test
    void contextLoads() {
    }

    // Для Film:
    // Получение всех фильмов
    @DisplayName("FILMS Тест 1 - Получение всех фильмов - возвращает статус 200")
    @Test
    void getAllFilms() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .GET()
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp.statusCode());
    }

    //название не может быть пустым;
    @DisplayName("FILMS Тест 2 - Проверить, что название не может быть пустым")
    @Test
    void createFilmWithEmptyNameFilm() throws Exception {
        String json = "{\n" +
                "  \"name\": \"\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());
    }

    //максимальная длина описания — 200 символов;
    @DisplayName("FILMS Тест 3 - Проверить, что макс длина описания 200")
    @Test
    void createFilmCheckMinMaxDescr() throws Exception {
        // Проверка по границе на 200
        String json = "{\n" +
                "  \"name\": \"Test\",\n" +
                "  \"description\": \"" + "+".repeat(200) + "\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp.statusCode());

        // Проверка на превышение границы 201
        json = "{\n" +
                "  \"name\": \"Test\",\n" +
                "  \"description\": \"" + "+".repeat(201) + "\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());
    }

    //дата релиза — не раньше 28 декабря 1895 года;
    @DisplayName("FILMS Тест 4 - Проверить, что релиз фильма не раньше 28 декабря 1895 года")
    @Test
    void createFilmChecMinMaxDescr() throws Exception {
        // Проверка по границе даты
        String json = "{\n" +
                "  \"name\": \"Test\",\n" +
                "  \"description\": \" Test\",\n" +
                "  \"releaseDate\": \"1895-12-28\",\n" +
                "  \"duration\": 100\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp.statusCode());

        // Проверка на превышение границы релиза фильма
        json = "{\n" +
                "  \"name\": \"Test\",\n" +
                "  \"description\": \"Test\",\n" +
                "  \"releaseDate\": \"1895-12-27\",\n" +
                "  \"duration\": 100\n" +
                "}";
        req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(500, resp.statusCode());
    }

    //продолжительность фильма должна быть положительным числом.
    @DisplayName("FILMS Тест 5 - Проверить, что продолжительность фильма должна быть положительной")
    @Test
    void createFilmCheckDuration() throws Exception {
        // Проверка по границе на 1
        String json = "{\n" +
                "  \"name\": \"Test\",\n" +
                "  \"description\": \"Test\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 1\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp.statusCode());

        // Проверка на превышение границы 201
        json = "{\n" +
                "  \"name\": \"Test\",\n" +
                "  \"description\": \"Test\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": -1 \n" +
                "}";
        req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());
    }

    //Для User:
    //электронная почта не может быть пустой и должна содержать символ @;
    @DisplayName("Users Тест 6 - Проверить, что имейл не может быть пустой и без @")
    @Test
    void createUserCheckEmptyAndHaveDog() throws Exception {
        // Проверка при пустом имейле
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());

        // Проверка при некорректном имейле
        json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());
    }

    //логин не может быть пустым и содержать пробелы;
    @DisplayName("Users Тест 7 - Проверить, что логин не может быть пустым или содержать пробелы")
    @Test
    void createUserCheckEmptyLoginAndHaveSpaceg() throws Exception {
        // Проверка на пустой логин
        String json = "{\n" +
                "  \"login\": \"\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"test@test.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());

        // Проверка на наличие пробелов
        json = "{\n" +
                "  \"login\": \"dolore red\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(500, resp.statusCode());
    }

    //имя для отображения может быть пустым — в таком случае будет использован логин;
    @DisplayName("Users Тест 8 - Проверить, что имя может быть пустым имя")
    @Test
    void createUserCheckEmptyName() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@test.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp.statusCode());
    }

    //дата рождения не может быть в будущем.
    @DisplayName("Users Тест 9 - Проверить, что день рождение не может быть в будущем")
    @Test
    void createUserCheckBirthday() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"test\",\n" +
                "  \"birthday\": \"2946-08-20\"\n" +
                "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(400, resp.statusCode());

    }
}
