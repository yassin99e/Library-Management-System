package ma.ensa.apigw.filters;

import ma.ensa.apigw.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationGlobalFilterTest {

    private JwtUtil jwtUtil;
    private JwtAuthenticationGlobalFilter filter;

    @BeforeEach
    void setUp() {
        jwtUtil = Mockito.mock(JwtUtil.class);
        filter = new JwtAuthenticationGlobalFilter(jwtUtil);
    }

    @Test
    void testFilter_PublicEndpoint_ShouldPassThrough() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login").build());
        Mono<Void> result = filter.filter(exchange, e -> Mono.empty());

        StepVerifier.create(result)
                .verifyComplete(); // Should pass without error
    }

    @Test
    void testFilter_NoAuthHeader_ShouldReturnUnauthorized() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/books").build());
        Mono<Void> result = filter.filter(exchange, e -> Mono.empty());

        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_InvalidToken_ShouldReturnUnauthorized() {
        var request = MockServerHttpRequest.get("/books")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
                .build();
        var exchange = MockServerWebExchange.from(request);

        when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

        StepVerifier.create(filter.filter(exchange, e -> Mono.empty()))
                .expectSubscription()
                .verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }


}
