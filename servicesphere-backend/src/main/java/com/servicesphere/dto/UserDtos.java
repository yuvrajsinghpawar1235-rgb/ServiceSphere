package com.servicesphere.dto;

public class UserDtos {
    public record CreateUserRequest(String fullName, String email, String password, Long roleId, Long departmentId) {}
    public record UpdateUserStatusRequest(String status) {}
}