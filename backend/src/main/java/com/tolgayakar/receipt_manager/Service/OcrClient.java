package com.tolgayakar.receipt_manager.Service;

import java.net.http.HttpClient;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.tolgayakar.receipt_manager.Model.DTO.OcrResponse;

@Service
public class OcrClient {
    private final RestClient restClient;

    public OcrClient() {
        // Fast api does not support HTTP/2, so we need to use HTTP/1.1 for the requests.
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.restClient = RestClient.builder()
                .baseUrl("http://ocr:8000")
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public OcrResponse process(MultipartFile file) throws Exception {

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // Creating Multipart/form-data request body.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        return restClient.post() // Create post request
                .uri("/ocr") // request will be sended to http://ocr:8000/ocr
                .body(body) // Set the request body
                .retrieve() // Send request and retrieve the response
                .body(OcrResponse.class); // Map the python result to OcrResponse data
    }
}
