package one.classfile;

public class Member {
    public final short accessFlags;
    public final String name;
    public final String descriptor;
    public final Attributes attributes;

    Member(short accessFlags, String name, String descriptor, Attributes attributes) {
        this.accessFlags = accessFlags;
        this.name = name;
        this.descriptor = descriptor;
        this.attributes = attributes;
    }

    public Code getCode() {
        return attributes.get("Code");
    }

    public Annotation[] getAnnotations() {
        return attributes.get("RuntimeVisibleAnnotations");
    }

    public Annotation getAnnotation(String name) {
        return Annotation.find(getAnnotations(), name);
    }

    @Override
    public String toString() {
        return name + ':' + descriptor + " [0x" + Integer.toHexString(accessFlags) + ']';
    }
}
