package nl.dyllan.controller;

import nl.dyllan.data.DynamoProductDao;
import nl.dyllan.domain.Product;
import nl.dyllan.domain.Products;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.NoSuchElementException;

@RestController
@EnableWebMvc
@RequestMapping("/products")
@Import({DynamoProductDao.class})
public class ProductsController {

  private final DynamoProductDao productDao;

  public ProductsController (DynamoProductDao productDao) {
    this.productDao = productDao;
  }

  @RequestMapping(path = "/{productId}", method = RequestMethod.PUT)
  public ResponseEntity<String> putProduct (@PathVariable String productId, @RequestBody Product product){
    if (!product.id().equals(productId)) {
      return new ResponseEntity<>("Product ID in the body does not match path parameter",
        HttpStatus.BAD_REQUEST);
    }
    productDao.putProduct(product);
    return new ResponseEntity<>("Product = "+ product.name() +" created",
      HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<Products> getAllProducts(){
    return ResponseEntity.ok(productDao.getAllProduct());
  }

  @GetMapping("/{productId}")
  public ResponseEntity<Product> getProductById(@PathVariable String productId){
    try {
      return ResponseEntity.ok(productDao.getProduct(productId).get());
    } catch (NoSuchElementException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found", e);
    }
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<String> deleteProduct(@PathVariable String productId){

    productDao.deleteProduct(productId);
    return new ResponseEntity<>("Product = "+ productId +" deleted",
      HttpStatus.NO_CONTENT);
  }

}
