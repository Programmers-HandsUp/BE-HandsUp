package dev.handsup.user.dto.request;

import static lombok.AccessLevel.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(access = PRIVATE)
public record JoinUserCredentialsRequest(

	@NotBlank(message = "email 은 필수입니다.")
	@Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,19}"
		+ "@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,19}[.][a-zA-Z]{2,3}$",
		message = "이메일 주소 양식을 확인해주세요.")
	String email,

	@NotBlank(message = "password 는 필수입니다.")
	String password
) {
	public static JoinUserCredentialsRequest of(
		String email,
		String password
	) {
		return JoinUserCredentialsRequest.builder()
			.email(email)
			.password(password)
			.build();
	}
}
