package com.sahibinden.codecase.domain;

import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.exception.BadStatusTransition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.sahibinden.codecase.entity.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class StatusMachineTest {
    // ---- canTransition: allowed paths ----
    static Stream<Arguments> allowedTransitions() {
        return Stream.of(
                Arguments.of(PENDING_APPROVAL, ACTIVE),
                org.junit.jupiter.params.provider.Arguments.of(PENDING_APPROVAL, DEACTIVATED),
                org.junit.jupiter.params.provider.Arguments.of(ACTIVE, DEACTIVATED)
        );
    }

    // ---- canTransition: blocked paths ----
    static Stream<org.junit.jupiter.params.provider.Arguments> blockedTransitions() {
        return Stream.of(
                // DUPLICATE is immutable (except idempotent DUPLICATE->DUPLICATE handled above)
                org.junit.jupiter.params.provider.Arguments.of(DUPLICATE, ACTIVE),
                org.junit.jupiter.params.provider.Arguments.of(DUPLICATE, DEACTIVATED),
                org.junit.jupiter.params.provider.Arguments.of(DUPLICATE, PENDING_APPROVAL),

                // DEACTIVATED has no outgoing transitions
                org.junit.jupiter.params.provider.Arguments.of(DEACTIVATED, ACTIVE),
                org.junit.jupiter.params.provider.Arguments.of(DEACTIVATED, PENDING_APPROVAL),
                org.junit.jupiter.params.provider.Arguments.of(DEACTIVATED, DUPLICATE),

                // PENDING_APPROVAL cannot go to DUPLICATE via this API
                org.junit.jupiter.params.provider.Arguments.of(PENDING_APPROVAL, DUPLICATE),

                // ACTIVE cannot go back to ACTIVE->PENDING or ACTIVE->DUPLICATE
                org.junit.jupiter.params.provider.Arguments.of(ACTIVE, PENDING_APPROVAL),
                org.junit.jupiter.params.provider.Arguments.of(ACTIVE, DUPLICATE)
        );
    }

    @ParameterizedTest
    @MethodSource("allowedTransitions")
    void canTransition_allows_defined_paths(Status from, Status to) {
        assertTrue(StatusMachine.canTransition(from, to),
                () -> String.format("Expected %s -> %s to be allowed", from, to));
    }

    @Test
    void canTransition_is_idempotent_for_all_states() {
        for (Status s : Status.values()) {
            assertTrue(StatusMachine.canTransition(s, s), "Idempotent transition must be allowed: " + s);
            assertDoesNotThrow(() -> StatusMachine.assertTransition(s, s));
        }
    }

    @ParameterizedTest
    @MethodSource("blockedTransitions")
    void canTransition_blocks_undefined_paths(Status from, Status to) {
        assertFalse(StatusMachine.canTransition(from, to),
                () -> String.format("Expected %s -> %s to be blocked", from, to));
    }

    @ParameterizedTest
    @MethodSource("blockedTransitions")
    void assertTransition_throws_for_blocked_paths(Status from, Status to) {
        BadStatusTransition ex = assertThrows(BadStatusTransition.class,
                () -> StatusMachine.assertTransition(from, to));
        // Optional: sanity check that message mentions both states
        assertTrue(ex.getMessage().contains(from.name()));
        assertTrue(ex.getMessage().contains(to.name()));
    }

    @ParameterizedTest
    @MethodSource("allowedTransitions")
    void assertTransition_allows_for_allowed_paths(Status from, Status to) {
        assertDoesNotThrow(() -> StatusMachine.assertTransition(from, to));
    }
}
