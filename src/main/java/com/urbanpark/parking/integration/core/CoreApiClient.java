package com.urbanpark.parking.integration.core;

import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.integration.core.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreApiClient {

    private final RestClient restClient;

    public CoreAuthResponse login(String baseUrl, String email, String password) {
        CoreLoginRequest request = new CoreLoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return post(baseUrl, "/api/auth/login", request, CoreAuthResponse.class, null);
    }

    public CoreUsuarioResponse getUsuario(String baseUrl, String token, String userId) {
        return get(baseUrl, "/api/usuarios/" + userId, CoreUsuarioResponse.class, token);
    }

    public CorePaginacionResponse<CoreUsuarioResponse> listUsuarios(String baseUrl, String token, int page, int size) {
        return get(baseUrl, "/api/usuarios?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<>() {}, token);
    }

    public List<CoreVehiculoResponse> listVehiculos(String baseUrl, String token) {
        return get(baseUrl, "/api/vehiculos",
                new ParameterizedTypeReference<List<CoreVehiculoResponse>>() {}, token);
    }

    public CoreApartamentoResponse getApartamento(String baseUrl, String token, Long apartamentoId) {
        return get(baseUrl, "/api/apartamentos/" + apartamentoId, CoreApartamentoResponse.class, token);
    }

    public List<CoreInquilinoResponse> listInquilinos(String baseUrl, String token, Long apartamentoId) {
        return get(baseUrl, "/api/apartamentos/" + apartamentoId + "/inquilinos",
                new ParameterizedTypeReference<List<CoreInquilinoResponse>>() {}, token);
    }

    public CoreCondominioResponse getCondominio(String baseUrl, String token, Long condominioId) {
        return get(baseUrl, "/api/condominios/" + condominioId, CoreCondominioResponse.class, token);
    }

    private <T> T get(String baseUrl, String path, Class<T> type, String token) {
        try {
            var spec = restClient.get().uri(baseUrl + path).accept(MediaType.APPLICATION_JSON);
            if (token != null) {
                spec = spec.header("Authorization", "Bearer " + token);
            }
            return spec.retrieve().body(type);
        } catch (RestClientResponseException ex) {
            throw mapError(ex);
        }
    }

    private <T> T get(String baseUrl, String path, ParameterizedTypeReference<T> type, String token) {
        try {
            var spec = restClient.get().uri(baseUrl + path).accept(MediaType.APPLICATION_JSON);
            if (token != null) {
                spec = spec.header("Authorization", "Bearer " + token);
            }
            return spec.retrieve().body(type);
        } catch (RestClientResponseException ex) {
            throw mapError(ex);
        }
    }

    private <T> T post(String baseUrl, String path, Object body, Class<T> type, String token) {
        try {
            var spec = restClient.post()
                    .uri(baseUrl + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body);
            if (token != null) {
                spec = spec.header("Authorization", "Bearer " + token);
            }
            return spec.retrieve().body(type);
        } catch (RestClientResponseException ex) {
            throw mapError(ex);
        }
    }

    private ApiException mapError(RestClientResponseException ex) {
        if (ex.getStatusCode().value() == 401) {
            return new ApiException("Token del condominio inválido o expirado", HttpStatus.UNAUTHORIZED);
        }
        return new ApiException("Error al comunicarse con API del condominio: " + ex.getStatusText(),
                HttpStatus.BAD_GATEWAY);
    }
}
