package dev.handsup.user.dto;

import static lombok.AccessLevel.*;

import dev.handsup.user.dto.request.JoinUserRequest;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class UserApiMapper {
	public static JoinUserRequest toJoinUserRequest(JoinUserApiRequest request) {
		return JoinUserRequest.of(
			request.email(),
			request.password(),
			request.nickname(),
			request.si(),
			request.gu(),
			request.dong(),
			request.profileImageUrl()
		);
	}
}