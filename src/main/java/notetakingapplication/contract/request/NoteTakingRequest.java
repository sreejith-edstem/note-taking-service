package notetakingapplication.contract.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import notetakingapplication.constant.Folder;

@Getter
@Setter
public class NoteTakingRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @Enumerated(EnumType.STRING)
    private Folder folder;
}
