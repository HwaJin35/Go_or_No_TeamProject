package com.goorno.canigo.dto.user;

import lombok.Data;

//@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 포함 애너테이션
@Data	
public class PasswordChangeDTO {
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;

}
