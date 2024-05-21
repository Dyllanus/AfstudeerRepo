package nl.dyllan.dao;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import nl.dyllan.entity.Product;
import java.math.BigDecimal;
import java.util.Map;

public class ProductMapper {

  private static final String PK = "PK";
  private static final String NAME = "name";
  private static final String PRICE = "price";

  public static Product productFromDynamoDB(Map<String, AttributeValue> items) {
    return new Product(
      items.get(PK).s(),
      items.get(NAME).s(),
      new BigDecimal(items.get(PRICE).n())
    );
  }

  public static Map<String, AttributeValue> productToDynamoDb(Product product) {
    return Map.of(
      PK, AttributeValue.builder().s(product.id()).build(),
      NAME, AttributeValue.builder().s(product.name()).build(),
      PRICE, AttributeValue.builder().n(product.price().toString()).build()
    );
  }
}
