package dev.bernasss12.bebooks;


import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.Arrays;


public final class RomanNumerals {
    private static final String[] baseNumerals = { "I", "V", "X", "L", "C", "D", "M" };
    
    private static final Long2ReferenceOpenHashMap<String> cache = new Long2ReferenceOpenHashMap<>(new long[]{ 0 },
                                                                                                   new String[]{ "nulla" });
    
    private static final ReferenceArrayList<String> roman = ReferenceArrayList.wrap(baseNumerals);
    
    private static final IntList decimal = IntArrayList.wrap(new int[]{ 1, 5, 10, 50, 100, 500, 1000 });
    static {
        final int baseCount = baseNumerals.length;
        
        for (int level = 0; level < 2; level++) {
            for (int i = 1; i < baseCount; i++) {
                final StringBuilder builder = new StringBuilder();
                
                roman.add(builder.append(baseNumerals[i]).append("§r").toString());
            }
        }
        
        for (int level = 1; level < 3; level++) {
            for (int j = 1; j < baseCount; j++) {
                decimal.add(decimal.getInt(j) * IntMath.pow(1000, level));
            }
        }
        
        System.out.println(Arrays.toString(baseNumerals));
        System.out.println(decimal);
        System.out.println(roman);
    }
    
    public static String fromDecimal(long decimal) {
        final String cachedValue = cache.get(decimal);
        
        if (cachedValue != null) {
            return cachedValue;
        }
        
        final StringBuilder roman = new StringBuilder();
        final int index = RomanNumerals.decimal.size() - 1;
        final int largest = RomanNumerals.decimal.getInt(index);
        long mutableDecimal = decimal;
        
        while (mutableDecimal >= largest) {
            roman.append(RomanNumerals.roman.get(index));
            mutableDecimal -= largest;
        }
        
        int div = 1;
        
        while (mutableDecimal >= div) {
            div *= 10;
        }
        
        div /= 10;
        
        
        while (mutableDecimal > 0) {
            int lastNum = (int) (mutableDecimal / div);
            
            if (lastNum <= 3) {
                for (int i = 0; i < lastNum; i++) {
                    roman.append(getRoman(div));
                }
            } else if (lastNum == 4) {
                roman.append(getRoman(div)).append(getRoman(div * 5));
            } else if (lastNum <= 8) {
                roman.append(getRoman(div * 5));
                
                for (int i = 0, end = lastNum - 5; i < end; i++) {
                    roman.append(getRoman(div));
                }
            } else if (lastNum == 9) {
                roman.append(getRoman(div)).append(getRoman(div * 10));
            }
            
            mutableDecimal %= div;
            div /= 10;
        }
        
        cache.put(decimal, roman.toString());
        
        return roman.toString();
    }
    
    private static String getRoman(int decimal) {
        return roman.get(RomanNumerals.decimal.indexOf(decimal));
    }
}
