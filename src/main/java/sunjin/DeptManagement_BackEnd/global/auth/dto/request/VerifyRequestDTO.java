package sunjin.DeptManagement_BackEnd.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyRequestDTO {
    @NotBlank
    private String deptCode;
}
