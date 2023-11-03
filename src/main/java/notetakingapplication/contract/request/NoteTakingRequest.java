package notetakingapplication.contract.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteTakingRequest {
    private String title;
    private String content;
}
