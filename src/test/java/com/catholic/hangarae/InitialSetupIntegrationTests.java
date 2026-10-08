package com.catholic.hangarae;

import com.catholic.hangarae.global.apiPayLoad.error.CommonErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(InitialSetupIntegrationTests.SetupController.class)
class InitialSetupIntegrationTests {
    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void swaggerIsAccessibleWithoutAuthentication() throws Exception {
        var docs = get("/v3/api-docs");
        assertThat(docs.statusCode()).isEqualTo(200);
        var json = mapper.readTree(docs.body());
        assertThat(json.path("info").path("title").asText()).isEqualTo("Hangarae API");
        assertThat(json.path("components").path("securitySchemes").has("BearerAuth")).isTrue();
        assertThat(get("/swagger-ui/index.html").statusCode()).isEqualTo(200);
    }

    @Test
    void successAndBusinessErrorsUseCommonResponse() throws Exception {
        var success = get("/setup-test/success");
        assertThat(success.statusCode()).isEqualTo(200);
        assertThat(body(success).path("code").asText()).isEqualTo("COMMON_200");
        assertThat(body(success).path("data").asText()).isEqualTo("hangarae");

        var failure = get("/setup-test/business-error");
        assertThat(failure.statusCode()).isEqualTo(404);
        assertThat(body(failure).path("code").asText()).isEqualTo("COMMON404");
    }

    @Test
    void validationErrorsReturnFieldDetails() throws Exception {
        var response = client.send(HttpRequest.newBuilder(uri("/setup-test/validate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"\"}"))
                .build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(body(response).path("code").asText()).isEqualTo("COMMON400");
        assertThat(body(response).path("data").path("name").asText()).isEqualTo("name is required");
    }

    @Test
    void unknownErrorsReturnCommonServerError() throws Exception {
        var response = get("/setup-test/unknown-error");
        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(body(response).path("code").asText()).isEqualTo("COMMON500");
        assertThat(body(response).has("data")).isFalse();
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode body(HttpResponse<String> response) throws Exception {
        return mapper.readTree(response.body());
    }

    @RestController
    static class SetupController {
        @GetMapping("/setup-test/success")
        Response<String> success() {
            return Response.ok("hangarae");
        }

        @GetMapping("/setup-test/business-error")
        Response<Void> businessError() {
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        }

        @PostMapping("/setup-test/validate")
        Response<Void> validate(@Valid @RequestBody Input input) {
            return Response.ok();
        }

        @GetMapping("/setup-test/unknown-error")
        Response<Void> unknownError() {
            throw new IllegalStateException("test failure");
        }
    }

    record Input(@NotBlank(message = "name is required") String name) {
    }
}
