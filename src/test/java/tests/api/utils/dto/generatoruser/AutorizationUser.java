package tests.api.utils.dto.generatoruser;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class AutorizationUser {
    private String username;
    private String password;
}
