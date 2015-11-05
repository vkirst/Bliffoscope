package my.kirst.bliffoscope;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Obj {
    final List<BitSet> lines;
    final Dimension dimension;
    
    protected Obj(List<BitSet> lines, Dimension dimension) {
        this.lines = lines;
        this.dimension = dimension;
    }

    public static String bitSetToString(BitSet bitSet, int width) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <  width; i++) {
            builder.append(bitSet.get(i) ? "+" : " ");
        }
        return builder.toString();
    }
    
    
    public static Obj build(List<String> lines) {
        int width = 0;
        int height = 0;
        List<BitSet> map = new ArrayList<>();
        for (String line : lines) {
            map.add(parse(line));
            width = Math.max(line.length(), width);
            height++;
        }
        return new Obj(map, new Dimension(width, height));
    }
    
    private static BitSet parse(String nextLine) {
        BitSet bitSet = new BitSet();
        for (int i = 0; i <  nextLine.length();i++) {
            switch (nextLine.charAt(i)) {
                case '+':
                    bitSet.set(i);
                    break;
                case ' ':
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected character " + nextLine.charAt(i));
            }
        }
        return bitSet;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (BitSet bitSet : lines) {
            builder.append(bitSetToString(bitSet, dimension.width)).append('\n');
        }
        return builder.toString();
    }
    
}