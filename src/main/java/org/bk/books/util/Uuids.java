package org.bk.books.util;

import java.util.Objects;
import java.util.UUID;

@FunctionalInterface
public interface Uuids {
    Uuids SYSTEM = new SystemUuids();

    UUID generateUuid();

    static Uuids systemUuid() {
        return SYSTEM;
    }

    static Uuids fixedUuid(UUID uuid) {
        return new FixedUuids(Objects.requireNonNull(uuid, "uuid must not be null"));
    }

    final class SystemUuids implements Uuids {
        @Override
        public UUID generateUuid() {
            return UUID.randomUUID();
        }
    }

    final class FixedUuids implements Uuids {
        private final UUID uuid;
        public FixedUuids(UUID uuid) {
            this.uuid = uuid;
        }
        @Override
        public UUID generateUuid() {
            return uuid;
        }
    }
}
