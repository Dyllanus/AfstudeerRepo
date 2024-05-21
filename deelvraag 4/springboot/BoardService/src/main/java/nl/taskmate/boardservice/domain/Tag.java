package nl.taskmate.boardservice.domain;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.taskmate.boardservice.data.converter.ColorToStringConverter;

import java.awt.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Tag extends BaseEntity {
    private String title;
    @Convert(converter = ColorToStringConverter.class)
    private Color color;

    public Tag(String title, Color color) {
        this.title = title;
        this.color = color;
    }
}
