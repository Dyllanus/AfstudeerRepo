// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package nl.dyllan.data;

import nl.dyllan.domain.Product;
import nl.dyllan.domain.Products;

import java.util.Optional;

public interface ProductDao {

  Optional<Product> getProduct(String id);

  void putProduct(Product product);

  void deleteProduct(String id);

  Products getAllProduct();
}
