package nl.dyllan;

import io.micronaut.crac.OrderedResource;
import jakarta.inject.Singleton;
import nl.dyllan.dao.DynamoProductDao;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PrimingResource implements OrderedResource {
  private static final Logger log = LoggerFactory.getLogger(PrimingResource.class);
  private final DynamoProductDao dynamoProductDao;

  public PrimingResource(DynamoProductDao dynamoProductDao) {
    this.dynamoProductDao = dynamoProductDao;
  }

  @Override
  public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
    log.info("Start Priming DDB Connection: Before Checkpoint");
    try {
      dynamoProductDao.describeTable();
    } catch (Exception e) {
      log.error("Error while priming: ", e);
      throw new RuntimeException(e);
    }
    log.info("Finished Priming DDB Connection: Before Checkpoint");
  }

  public void afterRestore(Context<? extends Resource> context) throws Exception {
    log.info("Priming DDB Connection: After Checkpoint");
  }
}
