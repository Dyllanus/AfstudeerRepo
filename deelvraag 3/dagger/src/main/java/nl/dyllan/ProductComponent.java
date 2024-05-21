// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package nl.dyllan;

import dagger.Component;
import nl.dyllan.handler.*;

import javax.inject.Singleton;

@Singleton
@Component(modules = ProductModule.class)
public interface ProductComponent {
  void inject(GetAllProductsHandler handler);
  void inject(GetProductByIdHandler handler);
  void inject(CreateProductHandler handler);
  void inject(DeleteProductHandler handler);
}
