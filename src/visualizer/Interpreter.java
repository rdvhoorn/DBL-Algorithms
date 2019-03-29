package visualizer;

import Collections.QuadTree;
import interfaces.models.GeometryInterface;
import interfaces.models.LabelInterface;
import interfaces.models.PointInterface;
import models.Point;
import models.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class Interpreter {
    public static boolean overlap(List<LabelInterface> geoms) {
        QuadTree tree = new QuadTree(new Rectangle(-10000, -10000, 15000, 15000));

        for (LabelInterface geo : geoms) {
            if (tree.query2D(geo.getRectangle()).size() > 0) {
                return true;
            }

            tree.insert(geo);
        }

        return false;
    }

    public static int overlapCount(List<LabelInterface> geoms) {
        int count = 0;

        QuadTree tree = new QuadTree(new Rectangle(new Point(-10000, -10000), new Point(10000, 10000)));

        for(LabelInterface geom : geoms) {
            count += tree.query2D(geom.getRectangle()).size();
            tree.insert(geom);
        }

        return count;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 0 && new File(args[0]).exists()) {
            FileInputStream is = new FileInputStream(new File(args[0]));
            System.setIn(is);
        }

        Record record = new Record(System.in);

        record.getLabels().forEach((key, value) -> {
            PointInterface point = key.getPOI();
            if (value.size() > 0) {
                System.out.println("(" + point.getX() + "," + point.getY() +") overlaps:");

                value.forEach((v) -> {
                    PointInterface p = v.getPOI();

                    System.out.println("\t(" + p.getX() + "," + p.getY() + ")");
                });
            }
        });
    }
}