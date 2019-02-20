package Parser;
/*
 * @author = Jeroen Schols
 */

import interfaces.AbstractCollectionInterface;
import interfaces.ParserInterface;
import interfaces.models.LabelInterface;
import javafx.util.Pair;
import models.*;
import Collections.QuadTree;
import Collections.KDTree;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Parser implements ParserInterface {

    private boolean testMode = false;
    private double optHeight;

    @Override
    public DataRecord input(InputStream source, Class<? extends AbstractCollectionInterface> collectionClass) throws NullPointerException, IOException {
        if (source == null) throw new NullPointerException("parser.input: source not found");

        DataRecord rec = new DataRecord();
        Scanner sc = new Scanner(source);

        if (!sc.hasNext()) {
            throw new IllegalArgumentException("Parser.input.pre violated: source length is zero");
        }

        try {
            while (!sc.hasNext("2pos|4pos|1slider")) sc.next();
            rec.placementModel = PlacementModelEnum.fromString(sc.next("2pos|4pos|1slider"));
        } catch (NoSuchElementException e) {
            throw new IOException("parser.input: no placement model found");
        }

        try {
            while (!sc.hasNextFloat()) sc.next();
            rec.aspectRatio = sc.nextFloat();
        } catch (NoSuchElementException e) {
            throw new IOException("parser.input: no aspect ratio found");
        }

        double xMin = 10001;
        double yMin = 10001;
        double xMax = -1;
        double yMax = -1;

        try {
            while (!sc.hasNextInt()) sc.next();
            int n = sc.nextInt();
            rec.labels = new ArrayList<>(n);

            for (int i = 0; i < n; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                if (x < 0 || x > 10000 || y < 0 || y > 10000) {
                    throw new InputMismatchException("parser.input coordinates not in range {0,1,...,10000}");
                }

                xMin = Math.min(xMin, x);
                yMin = Math.min(yMin, y);
                xMax = Math.max(xMax, x);
                yMax = Math.max(yMax, y);

                LabelInterface label = null;
                switch (rec.placementModel) {
                    case TWO_POS:
                    case FOUR_POS:
                        label = new PositionLabel(x, y*rec.aspectRatio, 0, DirectionEnum.NE, i);
                        break;
                    case ONE_SLIDER:
                        label = new SliderLabel(x, y*rec.aspectRatio, 0, 0, i);
                        break;
                }

                rec.labels.add(label);
            }
        } catch (NoSuchElementException e) {
            throw new IOException("parser.input: number of labels does not correspond to found coordinates");
        }

        rec.labels = Collections.unmodifiableList(rec.labels);
        if (collectionClass == QuadTree.class) {
            //TODO: xmin etc can be removed.
            rec.collection = initQuadTree(rec.labels, xMin, xMax, yMin, yMax);
        } else if (collectionClass == KDTree.class) {
            rec.collection = initKDTree();
        } else {
            throw new InputMismatchException("parser.input collection class initializer undefined");
        }

        // when in test-mode, the input file contains a
        if (testMode) {
            while (!sc.hasNextDouble()) sc.next();
            optHeight = sc.nextDouble();
        }

        sc.close();
        return rec;
    }

    private QuadTree initQuadTree(Collection<LabelInterface> points, double xMin, double xMax, double yMin, double yMax) {
        return new QuadTree(new Rectangle(-10000, -10000, 15000, 15000), points);
    }

    private KDTree initKDTree() {
        throw new UnsupportedOperationException("parser.initKDTree not implemented yet");
    }

    /**
     * Parse a test input to program structure retrieving a parsed DataRecord and the optimal height value.
     *
     * @param source {@link Readable}
     * @param collectionClass {@link interfaces.AbstractAlgorithmInterface}
     * @return Pair<DataRecord, Double>
     * @throws NullPointerException if {@code source == null}
     * @throws IOException if read error occurs
     */
    public Pair<DataRecord, Double> inputTestMode(InputStream source, Class<? extends AbstractCollectionInterface> collectionClass) throws IOException {
        testMode = true;
        DataRecord rec = input(source, collectionClass);
        testMode = false;
        return new Pair<DataRecord, Double>(rec, optHeight);
    }

    @Override
    public void output(DataRecord record, OutputStream stream) throws NullPointerException {
        if (record == null) throw new NullPointerException("parser.output: record not found");

        PrintWriter writer = new PrintWriter(stream);

        switch (record.placementModel) {
            case TWO_POS:
                writer.print("placement model: 2pos\n");
                break;
            case FOUR_POS:
                writer.print("placement model: 4pos\n");
                break;
            case ONE_SLIDER:
                writer.print("placement model: 1slider\n");
                break;
            default:
                throw new NoSuchElementException("parser.output placement model unknown");
        }

        DecimalFormat format = new DecimalFormat(".00");

        writer.write(
            "aspect ratio: " + record.aspectRatio + "\n"
            + "number of points: " + record.labels.size() + "\n"
            + "height: " + format.format(record.height / record.aspectRatio) + "\n"
        );

        for (LabelInterface label : record.labels) {
            writer.write( Math.round(label.getPOI().getXMin()) + " " + Math.round(label.getPOI().getYMin() / record.aspectRatio) + " " + label.getPlacement() + "\n");
        }

        writer.flush();
    }
}