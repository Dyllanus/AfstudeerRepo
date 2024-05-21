package nl.dyllan.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
@RegisterForReflection
public class Tag extends BaseEntity {
    private String title;
    private Color color;

    public Tag(String title, Color color) {
        this.title = title;
        this.color = color;
    }

}
