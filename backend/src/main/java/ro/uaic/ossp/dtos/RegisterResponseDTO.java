package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class RegisterResponseDTO {
    private boolean success;
    private String message;
    private List<String> monitorMessages;

    public RegisterResponseDTO() {}

    public RegisterResponseDTO(boolean success, String message, List<String> monitorMessages) {
        this.success = success;
        this.message = message;
        this.monitorMessages = monitorMessages;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getMonitorMessages() { return monitorMessages; }
    public void setMonitorMessages(List<String> monitorMessages) { this.monitorMessages = monitorMessages; }
}