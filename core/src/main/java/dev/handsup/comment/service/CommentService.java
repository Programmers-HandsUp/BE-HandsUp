package dev.handsup.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.handsup.auction.domain.Auction;
import dev.handsup.auction.exception.AuctionErrorCode;
import dev.handsup.auction.repository.auction.AuctionRepository;
import dev.handsup.comment.domain.Comment;
import dev.handsup.comment.dto.request.RegisterCommentRequest;
import dev.handsup.comment.dto.response.CommentResponse;
import dev.handsup.comment.mapper.CommentMapper;
import dev.handsup.comment.repository.CommentRepository;
import dev.handsup.common.exception.NotFoundException;
import dev.handsup.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final AuctionRepository auctionRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public CommentResponse registerAuctionComment(Long auctionId, RegisterCommentRequest request, User user) {

		Auction auction = getAuctionById(auctionId);
		auction.validateIfCommentAvailable();
		Comment comment = commentRepository.save(CommentMapper.toComment(request, auction, user));

		return CommentMapper.toCommentResponse(user, comment, auction.isSeller(user));
	}

	public Auction getAuctionById(Long auctionId) {
		return auctionRepository.findById(auctionId)
			.orElseThrow(() -> new NotFoundException(AuctionErrorCode.NOT_FOUND_AUCTION));
	}
}