package Collections;

import distance.EuclideanDistance;
import interfaces.models.GeometryInterface;
import interfaces.models.LabelInterface;
import interfaces.models.PointInterface;
import models.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KDTreeTest extends AbstractCollectionTest {
    List<GeometryInterface> points;

    @Override
    protected void setInstance() {

    }

    @Test
    void createOnePoint() {
        points = new ArrayList<>();
        points.add(new Point(5000, 5000));
        instance = new KDTree(points, 1);
        assertTrue(instance.nodes.contains(points.get(0)));
        assertNull(((KDTree) instance).left);
    }

    @Test
    void insert1() {
        points = new ArrayList<>();
        GeometryInterface p = new Point(5000, 5000);
        instance = new KDTree(points, 1);
        assertEquals(0, instance.size());
        instance.insert(p);
        assertEquals(1, instance.size());
        assertTrue(instance.nodes.contains(p));
        assertNull(((KDTree) instance).left);
        GeometryInterface p1 = new Point(6000, 6000);
        instance.insert(p1);
        assertFalse(instance.nodes.contains(p1));
        // not null children
        assertTrue(((KDTree) instance).left != null || ((KDTree) instance).right != null);
        // only one child has the node
        assertFalse(((KDTree) instance).left.nodes.contains(p1) && ((KDTree) instance).right.nodes.contains(p1));
        assertTrue(((KDTree) instance).left.nodes.contains(p1) || ((KDTree) instance).right.nodes.contains(p1));
        GeometryInterface p2 = new Point(3000, 3000);
        instance.insert(p2);
        assertEquals(3, instance.size());
    }

    @Test
    void insert2() {
        points = new ArrayList<>();
        PointInterface p = new Point(2000, 2000);
        PointInterface p1 = new Point(6000, 6000);
        PointInterface p2 = new Point(3000, 2000);
        points.add(p);
        points.add(p1);
        points.add(p2);
        instance = new KDTree(points, 1);
        assertEquals(3, instance.size());
        assertNotNull(((KDTree) instance).left);
        instance.insert(new Point(8000, 8000));
        assertEquals(4, instance.size());
    }

    @Test
    void squareInsert() {
        points = new ArrayList<>();
        instance = new KDTree(points, 1);
        int count = 0;
        for (int x = 0; x < 100; x += 10) {
            for (int y = 0; y < 500; y += 20) {
                Rectangle square = new Rectangle(x, y, x + 10, y + 10);
                instance.insert(square);
                count++;
            }
        }
        assertEquals(count, instance.size());
    }

    @Test
    void query2D2() {
        PointInterface point1 = new Point(5000, 5000);
        PointInterface point2 = new Point(3000, 3000);
        PointInterface point3 = new Point(9000, 1);
        PointInterface point4 = new Point(7000, 8000);
        points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        instance = new KDTree(points, 1);
        Rectangle queryRange = new Rectangle(2500, 2500, 5500, 5500);
        Collection<GeometryInterface> points = this.instance.query2D(queryRange);
        assertTrue(points.contains(point1));
        assertTrue(points.contains(point2));
    }

    @Test
    void query2D3() {
        PointInterface point1 = new Point(5000, 5000);
        PointInterface point2 = new Point(3000, 3000);
        PointInterface point3 = new Point(9000, 1);
        PointInterface point4 = new Point(7000, 8000);
        points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        instance = new KDTree(points, 1);
        Rectangle queryRange = new Rectangle(2500, 2500, 2501, 2501);
        Collection<GeometryInterface> points = this.instance.query2D(queryRange);
        assertTrue(points.isEmpty());
    }

    @Test
    void query2D4() {
        FourPositionPoint point1 = new FourPositionPoint(new Point(100, 100));
        FourPositionPoint point2 = new FourPositionPoint(new Point(120, 120));
        instance = new KDTree();
        LabelInterface label1a = new FourPositionLabel(30, 1, 0, point1, DirectionEnum.NW);
        LabelInterface label1b = new FourPositionLabel(30, 1, 0, point1, DirectionEnum.SE);
        LabelInterface label1c = new FourPositionLabel(30, 1, 0, point1, DirectionEnum.SW);
        LabelInterface label2a = new FourPositionLabel(30, 1, 0, point2, DirectionEnum.NE);
        LabelInterface label2b = new FourPositionLabel(30, 1, 0, point2, DirectionEnum.NW);
        LabelInterface label2c = new FourPositionLabel(30, 1, 0, point2, DirectionEnum.SE);
        instance.insert(label1a);
        instance.insert(label1b);
        instance.insert(label1c);
        instance.insert(label2a);
        instance.insert(label2b);
        Collection<GeometryInterface> labels = this.instance.query2D(label2c.getRectangle());
        assertTrue(labels.contains(label1b));
    }

    @Test
    void kNN1() {
        points = new ArrayList<>();
        PointInterface p1 = new Point(1500, 6000);
        PointInterface p2 = new Point(5000, 5000);
        PointInterface p3 = new Point(3500, 2500);
        PointInterface p4 = new Point(5000, 1000);
        PointInterface p5 = new Point(7000, 6500);
        PointInterface p6 = new Point(8000, 4500);
        PointInterface p7 = new Point(7000, 2000);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        points.add(p6);
        points.add(p7);
        instance = new KDTree(points, 1);
        assertEquals(points.size(), instance.size());
        Set<GeometryInterface> neighbours = ((KDTree) instance).nearestNeighbours(new EuclideanDistance(), 2, p6);
        assertEquals(2, neighbours.size());
        assertFalse(neighbours.contains(p6));
        assertTrue(neighbours.contains(p5));
        assertTrue(neighbours.contains(p7));
    }

    @Test
    void kNN2() {
        points = new ArrayList<>();
        int neighbourAmount = 1;
        PointInterface p1 = new Point(1500, 6000);
        PointInterface p2 = new Point(5000, 5000);
        PointInterface p3 = new Point(3500, 2500);
        PointInterface p4 = new Point(5000, 1000);
        PointInterface p5 = new Point(7000, 6500);
        PointInterface p6 = new Point(8000, 4500);
        PointInterface p7 = new Point(7000, 2000);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        points.add(p6);
        points.add(p7);
        instance = new KDTree(points, 1);
        assertEquals(points.size(), instance.size());
        Set<GeometryInterface> neighbours = ((KDTree) instance).nearestNeighbours(new EuclideanDistance(), neighbourAmount, p6);
        assertEquals(neighbourAmount, neighbours.size());
        assertFalse(neighbours.contains(p6));
        assertTrue(neighbours.contains(p5));
        assertFalse(neighbours.contains(p7));
    }

    @Test
    void kNN3() {
        points = new ArrayList<>();
        int neighbourAmount = 6;
        PointInterface p1 = new Point(1500, 6000);
        PointInterface p2 = new Point(5000, 5000);
        PointInterface p3 = new Point(3500, 2500);
        PointInterface p4 = new Point(5000, 1000);
        PointInterface p5 = new Point(7000, 6500);
        PointInterface p6 = new Point(8000, 4500);
        PointInterface p7 = new Point(7000, 2000);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        points.add(p6);
        points.add(p7);
        instance = new KDTree(points, 1);
        Set<GeometryInterface> neighbours = ((KDTree) instance).nearestNeighbours(new EuclideanDistance(), neighbourAmount, p2);
        assertEquals(neighbourAmount, neighbours.size());
        assertFalse(neighbours.contains(p2));
        for (GeometryInterface p : points) {
            if (!p.equals(p2)) {
                assertTrue(neighbours.contains(p));
            }
        }

    }
    @Test
    void query2D5() {
        FourPositionPoint point1 = new FourPositionPoint(new Point(100, 100));
        FourPositionPoint point2 = new FourPositionPoint(new Point(120, 120));
        instance = new KDTree();
        LabelInterface label1a = new FourPositionLabel(30, 1, 0, point1, DirectionEnum.NW);
        LabelInterface label1b = new FourPositionLabel(30, 1, 0, point1, DirectionEnum.SE);
        LabelInterface label1c = new FourPositionLabel(30, 1, 0, point1, DirectionEnum.SW);
        LabelInterface label2a = new FourPositionLabel(30, 1, 0, point2, DirectionEnum.NE);
        LabelInterface label2b = new FourPositionLabel(30, 1, 0, point2, DirectionEnum.NW);
        LabelInterface label2c = new FourPositionLabel(30, 1, 0, point2, DirectionEnum.SE);
        instance.insert(label1a);
        instance.insert(label1b);
        instance.insert(label1c);
        instance.insert(label2a);
        instance.insert(label2b);
        Collection<GeometryInterface> labels = this.instance.query2D(label2c.getRectangle());
        assertTrue(labels.contains(label1b));
    }
}
