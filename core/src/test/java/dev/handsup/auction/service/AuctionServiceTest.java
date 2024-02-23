package dev.handsup.auction.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import dev.handsup.auction.domain.Auction;
import dev.handsup.auction.domain.Bookmark;
import dev.handsup.auction.domain.auction_field.PurchaseTime;
import dev.handsup.auction.domain.auction_field.TradeMethod;
import dev.handsup.auction.domain.product.ProductStatus;
import dev.handsup.auction.domain.product.product_category.ProductCategory;
import dev.handsup.auction.dto.request.AuctionSearchCondition;
import dev.handsup.auction.dto.request.RegisterAuctionRequest;
import dev.handsup.auction.dto.response.AuctionResponse;
import dev.handsup.auction.dto.response.CheckBookmarkStatusResponse;
import dev.handsup.auction.dto.response.EditBookmarkResponse;
import dev.handsup.auction.dto.response.FindUserBookmarkResponse;
import dev.handsup.auction.repository.auction.AuctionQueryRepository;
import dev.handsup.auction.repository.auction.AuctionRepository;
import dev.handsup.auction.repository.auction.BookmarkRepository;
import dev.handsup.auction.repository.product.ProductCategoryRepository;
import dev.handsup.common.dto.PageResponse;
import dev.handsup.fixture.AuctionFixture;
import dev.handsup.fixture.BookmarkFixture;
import dev.handsup.fixture.ProductFixture;
import dev.handsup.fixture.UserFixture;
import dev.handsup.user.domain.User;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

	private final String DIGITAL_DEVICE = "디지털 기기";
	private final int PAGE_NUMBER = 0;
	private final int PAGE_SIZE = 5;
	@Mock
	private AuctionRepository auctionRepository;
	@Mock
	private AuctionQueryRepository auctionQueryRepository;
	@Mock
	private ProductCategoryRepository productCategoryRepository;

	@Mock
	private BookmarkRepository bookmarkRepository;

	@InjectMocks
	private AuctionService auctionService;

	private ProductCategory productCategory;
	private User user;
	private PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

	@BeforeEach
	void setUp() {
		productCategory = ProductFixture.productCategory(DIGITAL_DEVICE);
		user = UserFixture.user();
	}

	@Test
	@DisplayName("[경매를 등록할 수 있다.]")
	void registerAuction() {
		// given
		Auction auction = AuctionFixture.auction(productCategory);
		RegisterAuctionRequest registerAuctionRequest =
			RegisterAuctionRequest.of(
				"거의 새상품 버즈 팔아요",
				DIGITAL_DEVICE,
				10000,
				LocalDate.parse("2022-10-18"),
				ProductStatus.NEW.getLabel(),
				PurchaseTime.UNDER_ONE_MONTH.getLabel(),
				"거의 새상품이에요",
				TradeMethod.DELIVER.getLabel(),
				"서울시",
				"성북구",
				"동선동"
			);

		given(productCategoryRepository.findByCategoryValue(DIGITAL_DEVICE))
			.willReturn(Optional.of(productCategory));
		given(auctionRepository.save(any(Auction.class))).willReturn(auction);

		// when
		AuctionResponse auctionResponse = auctionService.registerAuction(registerAuctionRequest);

		// then
		assertAll(
			() -> assertThat(auctionResponse.title()).isEqualTo(registerAuctionRequest.title()),
			() -> assertThat(auctionResponse.tradeMethod()).isEqualTo(registerAuctionRequest.tradeMethod()),
			() -> assertThat(auctionResponse.endDate()).isEqualTo(registerAuctionRequest.endDate()),
			() -> assertThat(auctionResponse.purchaseTime()).isEqualTo(registerAuctionRequest.purchaseTime()),
			() -> assertThat(auctionResponse.productCategory()).isEqualTo(registerAuctionRequest.productCategory())
		);
	}

	@DisplayName("[경매를 정렬, 필터링하여 검색할 수 있다.]")
	@Test
	void searchAuctions() {
		//given
		Auction auction = AuctionFixture.auction(productCategory);
		AuctionSearchCondition condition = AuctionSearchCondition.builder()
			.keyword("버즈")
			.build();

		given(auctionQueryRepository.searchAuctions(condition, pageRequest))
			.willReturn(new SliceImpl<>(List.of(auction), pageRequest, true));

		//when
		PageResponse<AuctionResponse> response
			= auctionService.searchAuctions(condition, pageRequest);

		//then
		AuctionResponse auctionResponse = response.content().get(0);
		Assertions.assertThat(auctionResponse).isNotNull();
	}

	@DisplayName("[북마크를 추가할 수 있다.]")
	@Test
	void addBookmark() {
	    //given
		Auction auction = AuctionFixture.auction(productCategory);
		ReflectionTestUtils.setField(auction,"id",1L);

		Bookmark bookmark = BookmarkFixture.bookmark(user, auction);

		given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
		given(bookmarkRepository.findByUserAndAuction(user, auction)).willReturn(Optional.empty());
		given(bookmarkRepository.save(any(Bookmark.class))).willReturn(bookmark);
		//when
		EditBookmarkResponse response = auctionService.addBookmark(user, auction.getId());

		//then
		assertThat(response.bookmarkCount()).isEqualTo(1);
	}

	@DisplayName("[북마크를 삭제할 수 있다.]")
	@Test
	void cancelBookmark() {
		//given
		Auction auction = AuctionFixture.auction(productCategory);
		ReflectionTestUtils.setField(auction,"id",1L);
		Bookmark bookmark = BookmarkFixture.bookmark(user, auction);


		given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
		given(bookmarkRepository.findByUserAndAuction(user, auction)).willReturn(Optional.of(bookmark));

		//when
		EditBookmarkResponse response = auctionService.cancelBookmark(user, auction.getId());

		//then
		assertThat(response.bookmarkCount()).isEqualTo(-1);
	}

	@DisplayName("[유저 북마크 여부를 확인할 수 있다.")
	@Test
	void checkBookmarkStatus() {
	    //given
		Auction auction = AuctionFixture.auction(ProductCategory.of(DIGITAL_DEVICE));

		given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
		given(bookmarkRepository.existsByUserAndAuction(user, auction)).willReturn(true);

		//when
		CheckBookmarkStatusResponse response = auctionService.checkBookmarkStatus(user,
			auction.getId());

		//then
		assertThat(response.isBookmarked()).isTrue();
	}

	@DisplayName("[유저 북마크를 모두 조회할 수 있다.]")
	@Test
	void findUserBookmarks() {
	    //given
		Auction auction = AuctionFixture.auction(ProductCategory.of(DIGITAL_DEVICE));
		given(auctionRepository.findBookmarkAuction(user, pageRequest))
			.willReturn(new SliceImpl<>(List.of(auction), pageRequest, false));
	    //when
		PageResponse<FindUserBookmarkResponse> response
			= auctionService.findUserBookmarks(user, pageRequest);

		//then
		assertThat(response.size()).isEqualTo(1);
		assertThat(response.content().get(0).createdAt()).isEqualTo(auction.getCreatedAt());
	}
}
