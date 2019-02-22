package algorithms;

import Collections.AbstractCollection;
import Collections.QuadTree;
import Parser.DataRecord;
import Parser.Parser;
import Parser.Pair;
import interfaces.AbstractAlgorithmInterface;
import interfaces.AbstractCollectionInterface;
import main.Interpreter;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AlgorithmTester {

    private void runTest(DataRecord record, String fileName, double optHeight, AbstractAlgorithmInterface algorithms) {
        algorithms.solve(record);
        assertEquals(optHeight, record.height, "the height found is not correct in file: " + fileName);
        assertTrue(Interpreter.isValid(record), "the solution found is not valid in file: " + fileName);
    }


    private Collection<DynamicTest> readInFiles(String filePath, AbstractAlgorithmInterface algorithm, Class<? extends AbstractCollectionInterface> collection) {
        try {
            File folder = new File(filePath);
            File[] listOfFiles = folder.listFiles();

            Parser parser = new Parser();
            Collection<DynamicTest> tests = new ArrayList<>();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    Pair<DataRecord, Double> input = parser.inputTestMode(new FileInputStream(file), collection);
                    tests.add(dynamicTest("test of " + algorithm.getClass() + " on file: " + file.getName(),
                            () -> runTest(input.getKey(), file.getName(), input.getValue(), algorithm)));
                }
            }
            return tests;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @TestFactory
    public Collection<DynamicTest> TwoPosTest() {
        return readInFiles("tests/algorithms/TestFiles/TwoPosTestFiles", new TwoPositionBinarySearcher(), QuadTree.class);
    }

    @TestFactory
    public Collection<DynamicTest> FourPosTest() {
        return readInFiles("tests/algorithms/TestFiles/FourPosTestFiles", new TwoPositionBinarySearcher(), QuadTree.class);
    }

    @TestFactory
    public Collection<DynamicTest> SliderTest() {
        return readInFiles("tests/algorithms/TestFiles/SliderTestFiles", new GreedySliderAlgorithm(), QuadTree.class);
    }
}
