package one.classfile;

public class Signature {

    public static String of(Class<?> cls) {
        if (cls.isPrimitive()) {
            if (cls == int.class) {
                return "I";
            } else if (cls == long.class) {
                return "J";
            } else if (cls == boolean.class) {
                return "Z";
            } else if (cls == byte.class) {
                return "B";
            } else if (cls == short.class) {
                return "S";
            } else if (cls == char.class) {
                return "C";
            } else if (cls == float.class) {
                return "F";
            } else if (cls == double.class) {
                return "D";
            } else {
                return "V";
            }
        } else if (cls.isArray()) {
            return cls.getName();
        } else {
            return 'L' + cls.getName().replace('.', '/') + ';';
        }
    }
}
