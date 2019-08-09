package javax0.geci.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax0.geci.api.Logger;
import javax0.geci.api.Source;
import javax0.geci.tools.basepackage.childpackage.ChildClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestGeciReflectionTools {
    @javax0.geci.annotations.Geci("aaa a='b' b='c' c='d' a$='dollared' b3='bthree' _='-'")
    @javax0.geci.annotations.Geci("xxx x='x' y='y' z='z'")
    private static Object something;
    private HashMap<Map<String, Integer>, Object> b;

    private static java.util.Map.Entry<String, Integer>[] m1() {
        return null;
    }

    private static java.util.Map.Entry<? extends String, ? super Integer>[] m2() {
        return null;
    }

    @Test
    void testParameterFetcher() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("something");
        CompoundParams map = GeciReflectionTools.getParameters(f, "aaa");
        assertNotNull(map);
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");
        assertEquals(map.get("a$"), "dollared");
        assertEquals(map.get("b3"), "bthree");
        assertEquals(map.get("_"), "-");

    }

    @Test
    void testTypeGetting() throws NoSuchMethodException, NoSuchFieldException {
        assertEquals(
                "void",
                GeciReflectionTools.typeAsString(this.getClass().getDeclaredMethod("testTypeGetting")));
        assertEquals(
                "java.util.HashMap<java.util.Map<String,Integer>,Object>",
                GeciReflectionTools.typeAsString(this.getClass().getDeclaredField("b")));
    }

    @Test
    void normalizesGenericNames() {
        Assertions.assertAll(
                () -> assertEquals("String", GeciReflectionTools.normalizeTypeName("java.lang.String")),
                () -> assertEquals("java.util.Map", GeciReflectionTools.normalizeTypeName("java.util.Map")),
                () -> assertEquals("java.util.Map<Integer,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.lang.Integer,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set<java.lang.Integer>,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set< java.lang.Integer>,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<com.java.lang.Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set< com. java.lang.Integer>,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<? extends com.java.lang.Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set<? extends    com. java.lang.Integer> , java.lang.String>"))
        );
    }

    @Test
    void normalizeType() {
        Assertions.assertAll(
                () -> assertEquals("java.util.Set<java.util.Map.Entry<K,V>>", GeciReflectionTools.getGenericTypeName(Map.class.getDeclaredMethod("entrySet").getGenericReturnType())),
                () -> assertEquals("java.util.Map.Entry", GeciReflectionTools.getGenericTypeName(java.util.Map.Entry.class)),
                () -> assertEquals("java.util.Map.Entry<String,Integer>[]", GeciReflectionTools.getGenericTypeName(this.getClass().getDeclaredMethod("m1").getGenericReturnType())),
                () -> assertEquals("java.util.Map.Entry<? extends String,? super Integer>[]", GeciReflectionTools.getGenericTypeName(this.getClass().getDeclaredMethod("m2").getGenericReturnType()))
        );
    }


    private static class Z<H> {
        private static class U<T extends String>{}
    }

    @Test
    void getSimpleGenericName() {
        Assertions.assertAll(
                () -> assertEquals("Entry<K,V>", GeciReflectionTools.getSimpleGenericClassName(java.util.Map.Entry.class)),
                () -> assertEquals("Map.Entry<K,V>", GeciReflectionTools.getLocalGenericClassName(java.util.Map.Entry.class)),
                () -> assertEquals("U<T>", GeciReflectionTools.getSimpleGenericClassName(Z.U.class)),
                () -> assertEquals("TestGeciReflectionTools.Z.U<T>", GeciReflectionTools.getLocalGenericClassName(Z.U.class))
        );
    }

    @Test
    void getParametersFromSource() {
        Source testSource = new AbstractTestSource() {
            @Override
            public java.util.Set<String> segmentNames() {
                return null;
            }

            @Override
            public List<String> getLines() {
                return Arrays.asList("    // @Geci(\"aaa a='b' b='c' c='d' a$='dollared' b3='bthree' _='-'\")",
                    "    // @Geci(\"xxx x='x' y='y' z='z'\")",
                    "    private static Object something;",
                    "    private HashMap<Map<String, Integer>, Object> b;");
            }
            @Override
            public Logger getLogger() {
                return null;
            }
        };

        CompoundParams map = GeciAnnotationTools.getParameters(testSource, "aaa", "//", Pattern.compile(".*something;.*"));
        assertNotNull(map);
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");
        assertEquals(map.get("a$"), "dollared");
        assertEquals(map.get("b3"), "bthree");
        assertEquals(map.get("_"), "-");
    }

    @Test
    @DisplayName("Get private method from base class.")
    public void getOwnMethod() {
        Assertions.assertDoesNotThrow(() -> GeciReflectionTools.getMethod(ChildClass.class, "ownMethod"));
    }

    @Test
    @DisplayName("Get inherited method from superclass")
    public void getInheritedMethod() {
        Assertions.assertDoesNotThrow(() -> GeciReflectionTools.getMethod(ChildClass.class, "inheritedMethod"));
    }

    @Test
    @DisplayName("Throw exception if method does not exists.")
    public void throwExceptionForNoMethod() {
        Assertions.assertThrows(NoSuchMethodException.class, () -> GeciReflectionTools.getMethod(ChildClass.class, "noSuchMethod"));
    }

    @Test
    @DisplayName("Throw exception if method exists in superclass but is not inherited.")
    public void throwExceptionForNotInheritedMethod() {
        Assertions.assertThrows(NoSuchMethodException.class, () -> GeciReflectionTools.getMethod(ChildClass.class, "notInheritedMethod"));
    }

    @Test
    @DisplayName("Throw exception if a package-private method is not inherited, even if the base class and the declaring superclass is in the same package")
    public void throwExceptionForMethodIfPackageInheritanceIsBroken() {
        Assertions.assertThrows(NoSuchMethodException.class, () -> GeciReflectionTools.getMethod(ChildClass.class, "packagePrivateMethod"));
    }

    @Test
    @DisplayName("Get method from superclass even if packages are not the same in the inheritance line.")
    public void findInheritedMethodEvenIfPackageInheritanceIsBroken() {
        Assertions.assertDoesNotThrow(() -> GeciReflectionTools.getMethod(ChildClass.class, "inheritedFromGrandparentMethod"));
    }

    @Test
    @DisplayName("Get private field from base class.")
    public void getOwnField() {
        Assertions.assertDoesNotThrow(() -> GeciReflectionTools.getField(ChildClass.class, "ownField"));
    }

    @Test
    @DisplayName("Get inherited field from superclass")
    public void getInheritedField() {
        Assertions.assertDoesNotThrow(() -> GeciReflectionTools.getField(ChildClass.class, "inheritedField"));
    }

    @Test
    @DisplayName("Throw exception if field does not exists.")
    public void throwExceptionForNoField() {
        Assertions.assertThrows(NoSuchFieldException.class, () -> GeciReflectionTools.getField(ChildClass.class, "noSuchField"));
    }

    @Test
    @DisplayName("Throw exception if method exists in superclass but is not inherited.")
    public void throwExceptionForNotInheritedField() {
        Assertions.assertThrows(NoSuchFieldException.class, () -> GeciReflectionTools.getField(ChildClass.class, "notInheritedField"));
    }

    @Test
    @DisplayName("Throw exception if a package-private field is not inherited, even if the base class and the declaring superclass is in the same package")
    public void throwExceptionForFieldIfPackageInheritanceIsBroken() {
        Assertions.assertThrows(NoSuchFieldException.class, () -> GeciReflectionTools.getField(ChildClass.class, "packagePrivateField"));
    }

    @Test
    @DisplayName("Get field from superclass even if packages are not the same in the inheritance line.")
    public void findInheritedFieldEvenIfPackageInheritanceIsBroken() {
        Assertions.assertDoesNotThrow(() -> GeciReflectionTools.getField(ChildClass.class, "inheritedFromGrandparentField"));
    }

    @Test
    @DisplayName("Get the gecis from the standard annotations")
    @javax0.geci.annotations.Geci("barbarumba k1='v1' k2='v2'")
    void getGecisFromAnnotation() throws NoSuchMethodException {
        final String[] gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromAnnotation"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k1='v1' k2='v2'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations")
    @TestGeciReflectionTools.Geci("barbarumba k1='v1' k2='v2'")
    void getGecisFromOwnAnnotation() throws NoSuchMethodException {
        final String[] gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotation"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k1='v1' k2='v2'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter")
    @TestGeciReflectionTools.Geci(value = "barbarumba k2='v2'", k1 = "v1")
    void getGecisFromOwnAnnotationParams() throws NoSuchMethodException {
        final String[] gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationParams"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k2='v2' k1='v1'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter that is not named Geci")
    @TestGeciReflectionTools.MyGeci(value = "barbarumba k2='v2'", k1 = "v1")
    void getGecisFromOwnAnnotationMyNameParams() throws NoSuchMethodException {
        final String[] gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationMyNameParams"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k2='v2' k1='v1'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter that is not named Geci")
    @TestGeciReflectionTools.MyWrongGeci(value = "barbarumba k2='v2'", k1 = "v1")
    void getGecisFromOwnAnnotationMyWrongNameParams() throws NoSuchMethodException {
        final String[] gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationMyWrongNameParams"));
        Assertions.assertEquals(0, gecis.length);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter that is not named Geci")
    @TestGeciReflectionTools.MyGeci(value = "barbarumba k2='v2'", k1 = "v1")
    @TestGeciReflectionTools.MyGeci(k1 = "v1")
    void getGecisFromOwnAnnotationMyNameParamsMultiple() throws NoSuchMethodException {
        final String[] gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationMyNameParamsMultiple"));
        Assertions.assertEquals(2, gecis.length);
        Assertions.assertEquals("barbarumba k2='v2' k1='v1'", gecis[0]);
        Assertions.assertEquals("myGeci k1='v1'", gecis[1]);
    }

    @Test
    @DisplayName("Package name for a non-primitive name is correct from getPackageName().")
    public void correctPackageName() {
        String packageName = GeciReflectionTools.getPackageName(TestGeciReflectionTools.class);
        Assertions.assertEquals("javax0.geci.tools", packageName);
    }

    @Test
    @DisplayName("getPackageName() returns empty string for primitive types.")
    public void primitiveNoPackage() {
        String packageName = GeciReflectionTools.getPackageName(int.class);
        Assertions.assertEquals("", packageName);
    }

    @Test
    @DisplayName("Mask creation is consistent with java.lang.reflect.Modifier.")
    public void consistentMask() {
        int mod = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
        Assertions.assertEquals(mod, GeciReflectionTools.mask("private, static, final", -1));
    }

    @Test
    @DisplayName("Unmasking is consistent with java.lang.reflect.Modifier.")
    public void consistentUnmask() {
        int mod = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
        Assertions.assertArrayEquals(
            new String[]{"final", "private", "static"},
            Arrays.stream(GeciReflectionTools.unmask(mod).split(" ")).sorted().toArray()
        );
    }

    @Test
    @DisplayName("classForName() finds the proper class like Class.forName().")
    public void findsClass() throws ClassNotFoundException {
        final Class<?> bigDecimalClass = Class.forName("java.math.BigDecimal");
        final Class<?> myBigDecimalClass = GeciReflectionTools.classForName("java.math.BigDecimal");
        Assertions.assertEquals(bigDecimalClass, myBigDecimalClass);
    }

    @Test
    @DisplayName("classForName() throws exception for bad input.")
    public void badInput() {
        Assertions.assertThrows(ClassNotFoundException.class, () -> GeciReflectionTools.classForName("java.lang.NoSuchLanguageElement"));
        Assertions.assertThrows(ClassNotFoundException.class, () -> GeciReflectionTools.classForName("bool"));
        Assertions.assertThrows(ClassNotFoundException.class, () -> GeciReflectionTools.classForName("$ßjava.String{}[]"));
    }

    @Test
    @DisplayName("classForName() finds proper class for primitives.")
    public void findsPrimitiveClass() throws ClassNotFoundException {
        Assertions.assertEquals(int.class, GeciReflectionTools.classForName("int"));
    }

    @Test
    @DisplayName("classForName() finds proper class for array types.")
    public void findsArrayClass() throws ClassNotFoundException {
        Assertions.assertEquals(java.lang.String[][].class, GeciReflectionTools.classForName("java.lang.String[][]"));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Geci {
        String value();

        String k1() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Gecis {
        Geci[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(MyGecis.class)
    @Geci("")
    public @interface MyGeci {
        String value() default "";

        String k1() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyWrongGeci {
        String value();

        String k1() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyGecis {
        MyGeci[] value();
    }
}
