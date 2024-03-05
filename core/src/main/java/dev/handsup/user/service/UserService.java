package dev.handsup.user.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.handsup.auth.domain.EncryptHelper;
import dev.handsup.common.dto.CommonMapper;
import dev.handsup.common.dto.PageResponse;
import dev.handsup.common.exception.CommonErrorCode;
import dev.handsup.common.exception.NotFoundException;
import dev.handsup.common.exception.ValidationException;
import dev.handsup.user.domain.User;
import dev.handsup.user.dto.UserMapper;
import dev.handsup.user.dto.request.JoinUserRequest;
import dev.handsup.user.dto.response.UserReviewLabelResponse;
import dev.handsup.user.exception.UserErrorCode;
import dev.handsup.user.repository.UserRepository;
import dev.handsup.user.repository.UserReviewLabelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final EncryptHelper encryptHelper;
	private final UserReviewLabelRepository userReviewLabelRepository;

	private void validateDuplicateEmail(String email) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new ValidationException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND_BY_ID));
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_BY_EMAIL));
	}

	@Transactional
	public Long join(JoinUserRequest request) {
		validateDuplicateEmail(request.email());
		User user = UserMapper.toUser(request, encryptHelper);
		return userRepository.save(user).getId();
	}

	@Transactional(readOnly = true)
	public PageResponse<UserReviewLabelResponse> getUserReviewLabels(Long userId, Pageable pageable) {
		Slice<UserReviewLabelResponse> userReviewLabelResponsePage = userReviewLabelRepository
			.findByUserIdOrderByIdAsc(userId, pageable)
			.map(UserMapper::toUserReviewLabelResponse);
		return CommonMapper.toPageResponse(userReviewLabelResponsePage);
	}
}
