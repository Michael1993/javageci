package javax0.geci.tools.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestModifierBuilder {

    @Test
    public void testAll() {
        assertEquals(new ModifiersBuilder(0).toString(), "");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE).toString(), "private ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED).toString(), "protected ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED).toString(), "private protected ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC).toString(), "public ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC).toString(), "private public ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC).toString(), "protected public ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC).toString(), "private protected public ");
        assertEquals(new ModifiersBuilder(Modifier.FINAL).toString(), "final ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.FINAL).toString(), "private final ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.FINAL).toString(), "protected final ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.FINAL).toString(), "private protected final ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.FINAL).toString(), "public final ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.FINAL).toString(), "private public final ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL).toString(), "protected public final ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL).toString(), "private protected public final ");
        assertEquals(new ModifiersBuilder(Modifier.STATIC).toString(), "static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.STATIC).toString(), "private static ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.STATIC).toString(), "protected static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.STATIC).toString(), "private protected static ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.STATIC).toString(), "public static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.STATIC).toString(), "private public static ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.STATIC).toString(), "protected public static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.STATIC).toString(), "private protected public static ");
        assertEquals(new ModifiersBuilder(Modifier.FINAL | Modifier.STATIC).toString(), "final static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.FINAL | Modifier.STATIC).toString(), "private final static ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.FINAL | Modifier.STATIC).toString(), "protected final static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.FINAL | Modifier.STATIC).toString(), "private protected final static ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC).toString(), "public final static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC).toString(), "private public final static ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC).toString(), "protected public final static ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC).toString(), "private protected public final static ");
        assertEquals(new ModifiersBuilder(Modifier.SYNCHRONIZED).toString(), "synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.SYNCHRONIZED).toString(), "private synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.SYNCHRONIZED).toString(), "protected synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.SYNCHRONIZED).toString(), "private protected synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.SYNCHRONIZED).toString(), "public synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.SYNCHRONIZED).toString(), "private public synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.SYNCHRONIZED).toString(), "protected public synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.SYNCHRONIZED).toString(), "private protected public synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "private final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "protected final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "private protected final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "public final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "private public final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "protected public final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL | Modifier.SYNCHRONIZED).toString(), "private protected public final synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "protected static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private protected static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "public static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private public static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "protected public static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private protected public static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "protected final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private protected final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "public final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private public final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "protected public final static synchronized ");
        assertEquals(new ModifiersBuilder(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC | Modifier.SYNCHRONIZED).toString(), "private protected public final static synchronized ");

        String[] a = new String[]{
            "Modifier.PRIVATE",
            "Modifier.PROTECTED",
            "Modifier.PUBLIC",
            "Modifier.FINAL",
            "Modifier.STATIC",
            "Modifier.SYNCHRONIZED"
        };
        String[] b = new String[]{
            "private ",
            "protected ",
            "public ",
            "final ",
            "static ",
            "synchronized "
        };
        for (int i = 0; i < 64; i++) {
            var j = i;
            var k = 0;
            String aa = "0", bb = "";
            while (j > 0) {
                if (j % 2 > 0) {
                    aa += "|" + a[k];
                    bb += b[k];
                }
                j = j / 2;
                k++;
            }
            System.out.println("assertEquals(new ModifiersBuilder(" + aa + ").toString(), \"" + bb + "\");");
        }


    }

}