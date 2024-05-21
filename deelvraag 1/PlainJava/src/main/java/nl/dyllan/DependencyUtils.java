package nl.dyllan;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dyllan.dao.DynamoProductDao;
import nl.dyllan.dao.ProductDao;

public class DependencyUtils {
  public static ProductDao productDao = new DynamoProductDao();

  public static ObjectMapper mapper = new ObjectMapper();
}
