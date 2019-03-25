import models.Point;
import models.Rectangle;

import java.util.Random;

// Abstract generation strategy
abstract class GenerationStrategy {
    TestData data;
    Random rand = new Random();
    double height;
    double width;

    // TODO provide contract
    abstract Point[] generate();
    // TODO provide contract
    abstract Rectangle[] generateStart();
    // TODO provide contract

}
