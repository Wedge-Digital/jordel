package com.bloodbowlclub.lib.web;

import lombok.ToString;

import java.util.Map;

@ToString
public class ApiResponse<T> {
    private String result; // "success" ou "failure"
    private Object content; // Le contenu est de type T ou une HashMap en cas d'erreur

    // Constructeur pour succès
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResult("success");
        response.setContent(data);
        return response;
    }

    // Constructeur pour erreur
    public static <T> ApiResponse<T> failure(Map<String, String> errorContent) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResult("failure");
        response.setContent(errorContent);
        return response;
    }

    // Getters et setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
