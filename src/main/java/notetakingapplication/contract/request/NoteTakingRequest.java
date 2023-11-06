package notetakingapplication.contract.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteTakingRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
