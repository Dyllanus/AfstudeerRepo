// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package nl.dyllan.dao;



import nl.dyllan.entity.Product;
import nl.dyllan.entity.Products;

import java.util.Optional;

public interface ProductDao {

  Optional<Product> getProduct(String id);

  void putProduct(Product product);

  void deleteProduct(String id);

  Products getAllProduct();
}
