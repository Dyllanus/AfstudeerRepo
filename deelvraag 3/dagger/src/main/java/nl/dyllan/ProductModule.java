package nl.dyllan;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import nl.dyllan.dao.DynamoProductDao;
import nl.dyllan.dao.ProductDao;

import javax.inject.Singleton;

@Module
public class ProductModule {

  @Provides
  @Singleton
  public static ObjectMapper objectMapper() {
      return new ObjectMapper();
  }

  @Provides
  @Singleton
  public static ProductDao orderDao() {
    return new DynamoProductDao();
  }
}
