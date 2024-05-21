package nl.taskmate.boardservice.presentation.dto;

import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.domain.exceptions.ColorFormatException;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record TagDto(String name, String color) {
    public static TagDto fromTag(Tag tag) {
        return new TagDto(tag.getTitle(), String.format("#%02x%02x%02x", tag.getColor().getRed(), tag.getColor().getGreen(), tag.getColor().getBlue()));
    }

    public static Set<Tag> toTag(Set<TagDto> tags) {
        if (tags == null) return new HashSet<>();
        return tags.stream()
                .map(tagDto -> {
                    if (tagDto.color().startsWith("#") && tagDto.color().length() == 7) {
                        return new Tag(tagDto.name, Color.decode(tagDto.color()));
                    }
                    throw new ColorFormatException("Color string:'" + tagDto.color() + "' not able to decode. Make sure it's a hexadecimal code.");
                })
                .collect(Collectors.toSet());
    }
}
