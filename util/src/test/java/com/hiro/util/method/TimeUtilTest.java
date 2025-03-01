package com.hiro.util.method;

import com.hiro.util.methods.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class TimeUtilTest {

    static Stream<Arguments> provideDateSamples() {
        return Stream.of(
                Arguments.of("2025-02-27 15:30:00", "yyyy-MM-dd HH:mm:ss"),
                Arguments.of("27/02/2025 15:30", "dd/MM/yyyy HH:mm"),
                Arguments.of("02-27-2025 15:30", "MM-dd-yyyy HH:mm"),
                Arguments.of("2025/01/01", "yyyy/MM/dd")
        );
    }

    @ParameterizedTest
    @MethodSource("provideDateSamples")
    void fromStrWithPattern(String dateStr, String pattern) {
        log.info("Test fromStrWithPattern() with args: {}, {}", dateStr, pattern);
        assertDoesNotThrow(() -> {
            LocalDateTime result = TimeUtil.fromStrWithPattern(dateStr, pattern);
            log.info("Get result: {}", result);
        });
    }

}
