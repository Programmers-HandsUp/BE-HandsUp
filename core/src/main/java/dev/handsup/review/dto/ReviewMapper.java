package dev.handsup.review.dto;

import static lombok.AccessLevel.*;

import dev.handsup.auction.domain.Auction;
import dev.handsup.review.domain.Review;
import dev.handsup.review.dto.request.RegisterReviewRequest;
import dev.handsup.review.dto.response.ReviewResponse;
import dev.handsup.user.domain.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ReviewMapper {

	public static Review toReview(
		RegisterReviewRequest request,
		Auction auction,
		User writer
	) {
		return Review.of(
			request.evaluationScore(),
			request.content(),
			auction,
			writer
		);
	}

	public static ReviewResponse toReviewResponse(Review review) {
		return ReviewResponse.of(
			review.getEvaluationScore(),
			review.getContent(),
			review.getAuction().getId(),
			review.getWriter().getId()
		);
	}

}