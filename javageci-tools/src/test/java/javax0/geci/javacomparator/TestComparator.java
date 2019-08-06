package javax0.geci.javacomparator;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestComparator {

    @Test
    @DisplayName("Two empty source code equals")
    void testEmpty(){
        final String s1 = "";
        final String s2 = "";
        Assertions.assertFalse( new Comparator().test(Arrays.asList(s1.split("\n", -1)), Arrays.asList(s2.split("\n",-1))));
    }

    @Test
    @DisplayName("Reformatted code equals")
    void testReformatted(){
        final String s1 = "    void test(){\n" +
                "        final var s1 = \"\";\n" +
                "        final var s2 = \"\";\n" +
                "        Assertions.assertTrue( new Comparator().test(List.of(s1.split(\"\\n\",-1)),List.of(s2.split(\"\\n\",-1))));\n" +
                "    }";
        final String s2 = "    void test(){\n" +
                "                                 final var s1 = \"\";\n" +
                "        final var \n" +
                "                s2 = \"\";\n" +
                "        Assertions.assertTrue( new Comparator().test(List.of(s1.split(\"\\n\",-1)),List.of(s2.split(\"\\n\",-1))));\n" +
                "    }";
        Assertions.assertFalse( new Comparator().test(Arrays.asList(s1.split("\n",-1)), Arrays.asList(s2.split("\n",-1))));
    }


    @Test
    @DisplayName("Code comparision does not care the radix of numbers")
    void testNumberDifference(){
        final String s1 = "    @Test\n" +
                "    void method(){\n" +
                "        var x = 13;\n" +
                "    }";
        final String s2 = "    @Test\n" +
                "    void method(){\n" +
                "        var x = 0xd;\n" +
                "    }";
        Assertions.assertFalse( new Comparator().test(Arrays.asList(s1.split("\n",-1)), Arrays.asList(s2.split("\n",-1))));
    }
}
