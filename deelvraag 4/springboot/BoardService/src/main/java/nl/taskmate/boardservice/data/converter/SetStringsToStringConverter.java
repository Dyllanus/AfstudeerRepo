package nl.taskmate.boardservice.data.converter;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SetStringsToStringConverter implements AttributeConverter<Set<String>, String> {
    @Override
    public String convertToDatabaseColumn(Set<String> uuid) {
        return String.join(";", uuid);
    }

    @Override
    public Set<String> convertToEntityAttribute(String s) {
        // this is a workaround for a random empty string which gets in to the set. this empty string only appears in the requests in swagger. Not in the tests.
        var templist = Arrays.stream(s.split(";")).collect(Collectors.toList());
        if (templist.getFirst().isEmpty()) templist.remove(0);
        return new HashSet<>(templist);
    }
}
