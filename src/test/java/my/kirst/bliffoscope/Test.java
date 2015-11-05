package my.kirst.bliffoscope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;

import my.kirst.bliffoscope.Bliffoscope;
import my.kirst.bliffoscope.Dimension;
import my.kirst.bliffoscope.Obj;
import my.kirst.bliffoscope.Point;
import my.kirst.bliffoscope.Bliffoscope.PointAndProbability;

public class Test {

    private static Obj testField;
    private static Obj starShip;
    private static Obj slimTorpedo;
    private static Random rnd;

    @BeforeClass
    public static void setUp() throws IOException {
        testField = Bliffoscope.load(ClassLoader.getSystemResourceAsStream("TestData.txt"));
        starShip = Bliffoscope.load(ClassLoader.getSystemResourceAsStream("Starship.txt"));
        slimTorpedo = Bliffoscope.load(ClassLoader.getSystemResourceAsStream("SlimeTorpedo.txt"));

        rnd = new Random(System.currentTimeMillis());
    }

    @org.junit.Test
    public void test() {
        double noise = getNoise(testField);

        Obj field = generateField(testField.dimension.width, testField.dimension.height, noise);

        int sx = rnd.nextInt(field.dimension.width - starShip.dimension.width);
        int sy = rnd.nextInt(field.dimension.height - starShip.dimension.height);
        Point starShipCoordinates = new Point(sx, sy);

        int tx = rnd.nextInt(field.dimension.width - slimTorpedo.dimension.width);
        int ty = rnd.nextInt(field.dimension.height - slimTorpedo.dimension.height);
        while (intersects(sx, sy, starShip.dimension.width, starShip.dimension.height,
                tx, ty, slimTorpedo.dimension.width, slimTorpedo.dimension.height)) {
            tx = rnd.nextInt(field.dimension.width - slimTorpedo.dimension.width);
            ty = rnd.nextInt(field.dimension.height - slimTorpedo.dimension.height);
        }
        Point torpedoCoordinates = new Point(tx, ty);

        placeObj(field, starShip, starShipCoordinates, noise);
        placeObj(field, slimTorpedo, torpedoCoordinates, noise);

        Assert.assertTrue(in(Bliffoscope.find(field, starShip, 0.7d, 3), starShipCoordinates));

        Assert.assertTrue(in(Bliffoscope.find(field, slimTorpedo, 0.7d, 3), torpedoCoordinates));
    }

    
    private static double getNoise(Obj obj) {
        int n = 0;
        for (BitSet line : obj.lines) {
            n += line.cardinality();
        }
        int p = obj.dimension.width * obj.dimension.height;
        return n / (double)p;
    }

    private boolean in(List<PointAndProbability> s, Point coordinats) {
        for (PointAndProbability point : s) {
            if (point.point.equals(coordinats)) {
                return true;
            }
        }
        return false;
    }

    private static boolean intersects(int x1, int y1, int width1, int height1,
            int x2, int y2, int width2, int height2) {
        int tw = width1;
        int th = height1;
        int rw = width2;
        int rh = height2;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = x1;
        int ty = y1;
        int rx = x2;
        int ry = y2;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    private static void placeObj(Obj field, Obj target, Point upperLeft, double noize) {
        for (int y = 0; y < target.dimension.height; y++) {
            BitSet line = field.lines.get(y + upperLeft.y);
            BitSet targetLine = target.lines.get(y);
            for (int x = 0; x < target.dimension.width; x++) {
                boolean bit = targetLine.get(x);
                line.set(x + upperLeft.x, rnd.nextDouble() > noize ? bit : !bit);
            }
        }
    }

    private static Obj generateField(int width, int height, double noize) {
        assert noize <= 1.0d;
        assert noize >= 0.0d;

        List<BitSet> field = new ArrayList<>(height);

        for (int y = 0; y < height; y++) {
            BitSet line = new BitSet();
            field.add(line);
            for (int x = 0; x < width; x++) {
                if (rnd.nextDouble() <= noize) {
                    line.set(x);
                }
            }
        }

        return new Obj(field, new Dimension(width, height));

    }

}
