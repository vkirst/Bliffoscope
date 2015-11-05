package my.kirst.bliffoscope;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import my.kirst.bliffoscope.Bliffoscope.PointAndProbability;

public class Main {
    public static void main(String[] args) throws IOException {
        Obj field;
        if (args.length > 0) {
            field = Bliffoscope.load(new FileInputStream(args[0]));
        } else {
            field = Bliffoscope.load(ClassLoader.getSystemResourceAsStream("TestData.txt"));
        }

        Obj ship = Bliffoscope.load(ClassLoader.getSystemResourceAsStream("Starship.txt"));
        Obj torpedo = Bliffoscope.load(ClassLoader.getSystemResourceAsStream("SlimeTorpedo.txt"));

        List<String> out = new ArrayList<>(field.lines.size());
        for (BitSet line : field.lines) {
            out.add(Obj.bitSetToString(line, field.dimension.width));
        }

        List<PointAndProbability> shipResult = Bliffoscope.find(field, ship, 0.7d, 3);
        if (!shipResult.isEmpty()) {
            PointAndProbability target = shipResult.get(0);
            System.out.printf("Type: starship, coordinates: column: %d, row: %d, probability: %.2f%n", target.point.x, target.point.y, target.probability);
            placeObj(field, ship, target.point, out);
        }

        List<PointAndProbability> torpedoResult = Bliffoscope.find(field, torpedo, 0.7d, 3);
        if (!torpedoResult.isEmpty()) {
            PointAndProbability target = torpedoResult.get(0);
            System.out.printf("Type: slime torpedo, coordinates: column: %d, row: %d, probability: %.2f%n", target.point.x, target.point.y, target.probability);
            placeObj(field, torpedo, target.point, out);
        }

        System.out.println();
        for (String string : out) {
            System.out.println(string);
        }

    }
    
    
    private static void placeObj(Obj field, Obj obj, Point objXY, List<String> out) {
        for (int y = 0; y < obj.lines.size(); y++) {
            BitSet objLine = obj.lines.get(y);
            BitSet fieldLine = field.lines.get(y + objXY.y);
            StringBuilder newLine = new StringBuilder(out.get(y + objXY.y).substring(0, objXY.x));
            for (int x = 0; x < objLine.length(); x++) {
                newLine.append(combine(objLine.get(x), fieldLine.get(x + objXY.x)));
            }
            newLine.append(out.get(y + objXY.y).substring(objXY.x + obj.dimension.width));
            out.set(y + objXY.y, newLine.toString());
        }
    }

    private static char combine(boolean up, boolean down) {
        if (up) {
            if(down) {
                return '0';
            } else {
                return '.';
            }
        } else {
            if(down) {
                return '+';
            } else {
                return ' ';
            }
        }
    }


}
