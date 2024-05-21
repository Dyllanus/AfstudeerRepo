package nl.dyllan.data.converter;

import nl.dyllan.domain.Tag;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class TagConverter {

  public static Set<Tag> fromDynamoDbToTags(String tags){
    return Arrays.stream(tags.split("&")).map(tag -> {
      var sTag = tag.split(";");
      return new Tag(sTag[0], Color.decode(sTag[1]));
    }).collect(Collectors.toSet());
  }

  public static String fromTagsToDynamoDb(Set<Tag> tags){
    return tags.stream().map(tag -> tag.getTitle() + ";" + "#"+Integer.toHexString(tag.getColor().getRGB()).substring(2)).collect(Collectors.joining("&"));
  }
}
