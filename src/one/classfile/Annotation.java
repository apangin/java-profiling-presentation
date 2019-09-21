package one.classfile;

public class Annotation {
    public final String name;
    public final String[] elementNames;
    public final Object[] elementValues;

    Annotation(String name, String[] elementNames, Object[] elementValues) {
        this.name = name;
        this.elementNames = elementNames;
        this.elementValues = elementValues;
    }

    @SuppressWarnings("unchecked")
    public <T> T getElement(String name) {
        for (int i = 0; i < elementNames.length; i++) {
            if (elementNames[i].equals(name)) {
                return (T) elementValues[i];
            }
        }
        return null;
    }

    public static Annotation find(Annotation[] annotations, String name) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.name.equals(name)) {
                    return annotation;
                }
            }
        }
        return null;
    }
}
