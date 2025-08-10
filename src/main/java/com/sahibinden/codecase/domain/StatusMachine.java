package com.sahibinden.codecase.domain;

import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.exception.BadStatusTransition;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class StatusMachine {
    private static final Map<Status, Set<Status>> ALLOWED =
            Map.of(
                    Status.PENDING_APPROVAL, EnumSet.of(Status.ACTIVE, Status.DEACTIVATED),
                    Status.ACTIVE, EnumSet.of(Status.DEACTIVATED),
                    Status.DEACTIVATED, EnumSet.noneOf(Status.class),
                    Status.DUPLICATE, EnumSet.noneOf(Status.class)
            );

    public static boolean canTransition(Status from, Status to) {
        if (from == to) return true;
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public static void assertTransition(Status from, Status to) {
        if (!canTransition(from, to)) {
            throw new BadStatusTransition(String.format("Transition %s -> %s not allowed", from, to)
            );
        }
    }
}
