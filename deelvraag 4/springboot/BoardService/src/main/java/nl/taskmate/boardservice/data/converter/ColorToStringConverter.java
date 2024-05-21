package nl.taskmate.boardservice.data.converter;

import jakarta.persistence.AttributeConverter;

import java.awt.*;

public class ColorToStringConverter implements AttributeConverter<Color, String> {
    @Override
    public String convertToDatabaseColumn(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public Color convertToEntityAttribute(String s) {
        return Color.decode(s);
    }
}
