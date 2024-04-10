package dev.handsup.notification.domain.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;

import dev.handsup.auction.domain.Auction;
import dev.handsup.fixture.AuctionFixture;
import dev.handsup.fixture.UserFixture;
import dev.handsup.notification.domain.NotificationType;
import dev.handsup.notification.repository.FCMTokenRepository;
import dev.handsup.notification.service.FCMService;
import dev.handsup.notification.service.NotificationService;
import dev.handsup.user.domain.User;

@DisplayName("[FCM 알림 서비스 테스트]")
@ExtendWith(MockitoExtension.class)
class FCMServiceTest {

	private final User receiver = UserFixture.user1();
	private final Auction auction = AuctionFixture.auction();
	@Mock
	private FCMTokenRepository fcmTokenRepository;
	@Mock
	private FirebaseMessaging firebaseMessaging;
	@Mock
	private NotificationService notificationService;
	@InjectMocks
	private FCMService fcmService;

	@Test
	@DisplayName("메시지를 성공적으로 보낸다]")
	void sendMessageSuccessTest() throws FirebaseMessagingException {
		// given
		String receiverEmail = receiver.getEmail();
		String fcmToken = "fcmToken123";
		given(fcmTokenRepository.getFcmToken(receiverEmail)).willReturn(fcmToken);

		// when
		fcmService.sendMessage(
			"senderEmail",
			"senderNickname",
			receiverEmail,
			NotificationType.BOOKMARK,
			auction
		);

		// then
		verify(firebaseMessaging, times(1)).send(any());
	}

}
