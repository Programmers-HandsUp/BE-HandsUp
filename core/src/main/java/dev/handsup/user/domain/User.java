package dev.handsup.user.domain;

import static dev.handsup.common.exception.CommonValidationError.*;
import static dev.handsup.user.exception.UserErrorCode.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

import dev.handsup.common.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class User extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "score", nullable = false)
	private int score = 100;

	@Embedded
	private Address address;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "report_count", nullable = false)
	private int reportCount = 0;

	private void validateUser(
		String email,
		String password,
		String nickname,
		Address address,
		String profileImageUrl
	) {
		Assert.hasText(email, getNotEmptyMessage("User", "email"));
		Assert.hasText(password, getNotEmptyMessage("User", "password"));
		Assert.hasText(nickname, getNotEmptyMessage("User", "nickname"));
		Assert.notNull(address, getNotNullMessage("User", "address"));
		Assert.hasText(profileImageUrl, getNotEmptyMessage("User", "profileImageUrl"));
		// 이메일 패턴 검증
		String emailRegex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,19}"
			+ "@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,19}[.][a-zA-Z]{2,3}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		Assert.isTrue(matcher.matches(), NON_VALIDATED_EMAIL.getMessage());
	}

	private User(
		Long id,
		String email,
		String password,
		String nickname,
		Address address,
		String profileImageUrl
	) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.address = address;
		this.profileImageUrl = profileImageUrl;
	}

	@Builder
	public User(
		String email,
		String password,
		String nickname,
		Address address,
		String profileImageUrl
	) {
		validateUser(email, password, nickname, address, profileImageUrl);
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.address = address;
		this.profileImageUrl = profileImageUrl;
	}

	/**
	 * 테스트 용 생성자
	 */
	public static User getTestUser(
		Long id,
		String email,
		String password,
		String nickname,
		Address address,
		String profileImageUrl
	) {
		return new User(id, email, password, nickname, address, profileImageUrl);
	}
}
