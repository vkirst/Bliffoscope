package my.kirst.bliffoscope;

import java.io.*;
import java.util.*;

public class Bliffoscope {

    private final static Comparator<PointAndProbability> PROBABILITY_COMPARATOR = new Comparator<PointAndProbability>() {
        @Override
        public int compare(PointAndProbability o1, PointAndProbability o2) {
            return Double.compare(o2.probability, o1.probability);
        }
    };

    public static List<PointAndProbability> find(Obj field, Obj target, double probabilityThreshold, int topCount) {
        List<PointAndProbability> result = new ArrayList<>();

        final int pixels = target.dimension.width * target.dimension.height;

        for (int y = 0; y < field.dimension.height - target.dimension.height; y++) {
            for (int x = 0; x < field.dimension.width - target.dimension.width; x++) {
                double probability = 1.0d - compare(fragment(field.lines, x, y, target.dimension), target.lines) / (double)pixels;
                if (probability < probabilityThreshold) {
                    continue;
                }
                result.add(new PointAndProbability(new Point(x, y), probability));
            }
        }

        Collections.sort(result, PROBABILITY_COMPARATOR);

        return result.subList(0, Math.min(result.size(), topCount));

    }

    public static Obj load(InputStream is) throws IOException {
        try (LineReader reader = new LineReader(is)) {
            List<String> lines = new ArrayList<>();
            String line = null;
            while ((line = reader.read()) != null) {
                lines.add(line);
            }
            return Obj.build(lines);
        }
    }

    private static List<BitSet> fragment(List<BitSet> obj, int x, int y, Dimension dimension) {
        List<BitSet> window = new ArrayList<>(dimension.height);
        for (int i = 0; i < dimension.height; i++) {
            window.add(obj.get(y + i).get(x, x + dimension.width));
        }
        return window;
    }

    private static int compare(List<BitSet> first, List<BitSet> second) {
        int notMatch = 0;
        for (int i = 0; i < first.size(); i++) {
            BitSet line = first.get(i);
            line.xor(second.get(i));
            notMatch += line.cardinality();
        }
        return notMatch;
    }

    public static class PointAndProbability {
        final Point point;
        final double probability;

        public PointAndProbability(Point point, double score) {
            this.point = point;
            this.probability = score;
        }

    }

    public static class LineReader implements AutoCloseable {

        private final BufferedReader reader;

        public LineReader(InputStream is) throws IOException {
            reader = new BufferedReader(new InputStreamReader(is));
        }

        public String read() throws IOException {
            return reader.readLine();
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }

    }

}
