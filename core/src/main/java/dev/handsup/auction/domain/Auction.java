package dev.handsup.auction.domain;

import static dev.handsup.auction.domain.auction_field.AuctionStatus.*;
import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;

import dev.handsup.auction.domain.auction_field.AuctionStatus;
import dev.handsup.auction.domain.auction_field.TradeMethod;
import dev.handsup.auction.domain.auction_field.TradingLocation;
import dev.handsup.auction.domain.product.Product;
import dev.handsup.common.entity.TimeBaseEntity;
import dev.handsup.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Auction extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "auction_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "seller_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private User seller;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "buyer_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
	private User buyer;

	@Column(name = "title", nullable = false)
	private String title;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "product_id",
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Product product;

	@Column(name = "init_price", nullable = false)
	private int initPrice;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Embedded
	private TradingLocation tradingLocation;

	@Column(name = "trade_method", nullable = false)
	@Enumerated(STRING)
	private TradeMethod tradeMethod;

	@Column(name = "auction_status", nullable = false)
	@Enumerated(STRING)
	private AuctionStatus status;

	@Column(name = "bidding_count", nullable = false)
	private int biddingCount;

	@Column(name = "bookmark_count", nullable = false)
	private int bookmarkCount;

	@Builder
	public Auction(User seller, String title, Product product, int initPrice, LocalDate endDate,
		TradingLocation tradingLocation, TradeMethod tradeMethod) {
		this.seller = seller;
		this.title = title;
		this.product = product;
		this.initPrice = initPrice;
		this.endDate = endDate;
		this.tradingLocation = tradingLocation;
		this.tradeMethod = tradeMethod;
		biddingCount = 0;
		bookmarkCount = 0;
		status = PROGRESS;
	}
}