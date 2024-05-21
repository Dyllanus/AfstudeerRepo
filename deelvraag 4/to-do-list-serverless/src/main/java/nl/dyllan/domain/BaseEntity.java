package nl.dyllan.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RegisterForReflection
public abstract class BaseEntity {

    @Setter
    private UUID id;


    @Setter
    private Long version;


    private Instant created;

    @Setter
    private Instant lastModified;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.created = Instant.now();
        this.lastModified = Instant.now();
        this.version = 1L;
    }

    public BaseEntity (UUID id, Long version, Instant created, Instant lastModified) {
        this.id = id;
        this.version = version;
        this.created = created;
        this.lastModified = lastModified;
    }
}
