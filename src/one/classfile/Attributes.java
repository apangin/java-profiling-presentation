package one.classfile;

public class Attributes {
    public final String[] names;
    public final Object[] values;

    Attributes(String[] names, Object[] values) {
        this.names = names;
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return (T) values[i];
            }
        }
        return null;
    }

    public <T> T getOrDefault(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }
}
