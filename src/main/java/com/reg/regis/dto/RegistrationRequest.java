package com.reg.regis.dto;

import jakarta.validation.constraints.*;

public class RegistrationRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain uppercase, lowercase, number, and special character")
    private String password;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+62[0-9]{9,13}$", message = "Phone must be valid Indonesian number (+62)")
    private String phone;
    
    @NotNull(message = "Age is required")
    @Min(value = 17, message = "Age must be at least 17")
    @Max(value = 120, message = "Age must be less than 120")
    private Integer age;
    
    // Constructors
    public RegistrationRequest() {}
    
    public RegistrationRequest(String name, String email, String password, String phone, Integer age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}

// Response DTOs
class RegistrationResponse {
    private String message;
    private String token;
    private CustomerInfo customer;
    
    public RegistrationResponse(String message, String token, CustomerInfo customer) {
        this.message = message;
        this.token = token;
        this.customer = customer;
    }
    
    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public CustomerInfo getCustomer() { return customer; }
    public void setCustomer(CustomerInfo customer) { this.customer = customer; }
}

class CustomerInfo {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private Boolean emailVerified;
    
    public CustomerInfo(Long id, String name, String email, String phone, Integer age, Boolean emailVerified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.emailVerified = emailVerified;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
}