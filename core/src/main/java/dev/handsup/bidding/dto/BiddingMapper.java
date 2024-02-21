package dev.handsup.bidding.dto;

import static lombok.AccessLevel.*;

import dev.handsup.bidding.domain.Bidding;
import dev.handsup.bidding.dto.request.RegisterBiddingRequest;
import dev.handsup.bidding.dto.response.RegisterBiddingResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class BiddingMapper {

	public static Bidding toBidding(RegisterBiddingRequest request) {
		return Bidding.of(
			request.biddingPrice(),
			request.auction(),
			request.bidder()
		);
	}
	public static RegisterBiddingResponse toRegisterBiddingResponse(Bidding bidding) {
		return RegisterBiddingResponse.from(bidding.getId());
	}
}
