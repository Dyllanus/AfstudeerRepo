// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package nl.dyllan.dao;

import nl.dyllan.entity.Product;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
      PK, AttributeValue.builder().s(product.getId()).build(),
      NAME, AttributeValue.builder().s(product.getName()).build(),
      PRICE, AttributeValue.builder().n(product.getPrice().toString()).build()
    );
  }
}
