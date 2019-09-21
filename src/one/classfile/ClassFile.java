package one.classfile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import static one.classfile.ConstantPool.*;

public class ClassFile {
    private static final short[] EMPTY_SHORT_ARRAY = {};
    private static final String[] EMPTY_STRING_ARRAY = {};
    private static final Attributes EMPTY_ATTRIBUTES = new Attributes(EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY);

    public final int version;
    public final Object[] pool;
    public final short accessFlags;
    public final String name;
    public final String superName;
    public final String[] interfaces;
    public final Member[] fields;
    public final Member[] methods;
    public final Attributes attributes;

    public static ClassFile parse(String fileName) throws IOException {
        byte[] buf;
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            buf = new byte[(int) raf.length()];
            raf.readFully(buf);
        }
        return new ClassFile(buf);
    }

    public ClassFile(byte[] classData) {
        this(ByteBuffer.wrap(classData));
    }

    public ClassFile(ByteBuffer buf) {
        if (buf.getInt() != 0xcafebabe) {
            throw new IllegalArgumentException("Not a valid class file");
        }

        this.version = buf.getInt();
        this.pool = readConstantPool(buf);
        this.accessFlags = buf.getShort();
        this.name = readClass(buf);
        this.superName = readClass(buf);
        this.interfaces = readInterfaces(buf);
        this.fields = readMembers(buf);
        this.methods = readMembers(buf);
        this.attributes = readAttributes(buf);

        if (buf.hasRemaining()) {
            throw new IllegalArgumentException("Unexpected data at the end of class file");
        }
    }

    public Annotation[] getAnnotations() {
        return attributes.get("RuntimeVisibleAnnotations");
    }

    public Annotation getAnnotation(String name) {
        return Annotation.find(getAnnotations(), name);
    }

    @SuppressWarnings("unchecked")
    public <T> T poolAt(short index) {
        return (T) pool[index & 0xffff];
    }

    private void checkTag(Ref ref, byte tag) {
        if (ref.tag != tag) {
            throw new IllegalArgumentException("Expected " + TAG_NAME[tag]);
        }
    }

    private Object[] readConstantPool(ByteBuffer buf) {
        Object[] pool = new Object[buf.getShort() & 0xffff];
        for (int i = 1; i < pool.length; i++) {
            pool[i] = readCPInfo(buf);
            Class constantType = pool[i].getClass();
            if (constantType == Long.class || constantType == Double.class) {
                i++;
            }
        }
        return pool;
    }

    private Object readCPInfo(ByteBuffer buf) {
        byte tag = buf.get();
        switch (tag) {
            case CONSTANT_Utf8:
                return readUtf8(buf);
            case CONSTANT_Integer:
                return buf.getInt();
            case CONSTANT_Float:
                return buf.getFloat();
            case CONSTANT_Long:
                return buf.getLong();
            case CONSTANT_Double:
                return buf.getDouble();
            case CONSTANT_Class:
            case CONSTANT_String:
            case CONSTANT_MethodType:
            case CONSTANT_Module:
            case CONSTANT_Package:
                return new Ref(tag, buf.getShort());
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
            case CONSTANT_NameAndType:
            case CONSTANT_Dynamic:
            case CONSTANT_InvokeDynamic:
                return new Ref2(tag, buf.getShort(), buf.getShort());
            case CONSTANT_MethodHandle:
                return new Ref2(tag, buf.get(), buf.getShort());
            default:
                throw new IllegalArgumentException("Unrecognized constant pool tag: " + tag);
        }
    }

    private String readUtf8(ByteBuffer buf) {
        int length = buf.getShort() & 0xffff;
        int limit = buf.position() + length;
        char[] result = new char[length];
        int chars = 0;

        while (buf.position() < limit) {
            byte b = buf.get();
            char c;
            if (b >= 0) {
                c = (char) b;
            } else if ((b & 0xe0) == 0xc0) {
                c = (char) ((b & 0x1f) << 6 | (buf.get() & 0x3f));
            } else {
                c = (char) ((b & 0x0f) << 12 | (buf.get() & 0x3f) << 6 | (buf.get() & 0x3f));
            }
            result[chars++] = c;
        }

        return new String(result, 0, chars);
    }

    private String readSymbol(ByteBuffer buf) {
        return (String) pool[buf.getShort() & 0xffff];
    }

    private String readClass(ByteBuffer buf) {
        Ref ref = (Ref) pool[buf.getShort() & 0xffff];
        checkTag(ref, CONSTANT_Class);
        return (String) pool[ref.index & 0xffff];
    }

    private String[] readInterfaces(ByteBuffer buf) {
        String[] interfaces = new String[buf.getShort() & 0xffff];
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = readClass(buf);
        }
        return interfaces;
    }

    private Member[] readMembers(ByteBuffer buf) {
        Member[] members = new Member[buf.getShort() & 0xffff];
        for (int i = 0; i < members.length; i++) {
            members[i] = new Member(buf.getShort(), readSymbol(buf), readSymbol(buf), readAttributes(buf));
        }
        return members;
    }

    private Attributes readAttributes(ByteBuffer buf) {
        int count = buf.getShort() & 0xffff;
        if (count == 0) {
            return EMPTY_ATTRIBUTES;
        }

        String[] names = new String[count];
        Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
            names[i] = readSymbol(buf);
            values[i] = readAttribute(buf, names[i]);
        }
        return new Attributes(names, values);
    }

    private Object readAttribute(ByteBuffer buf, String name) {
        int size = buf.getInt();
        switch (name) {
            case "Code":
                return readCode(buf);
            case "RuntimeVisibleAnnotations":
            case "RuntimeInvisibleAnnotations":
                return readAnnotations(buf);
            default:
                byte[] value = new byte[size];
                buf.get(value);
                return value;
        }
    }

    private Code readCode(ByteBuffer buf) {
        short maxStack = buf.getShort();
        short maxLocals = buf.getShort();

        byte[] code = new byte[buf.getInt()];
        buf.get(code);

        int count = buf.getShort() & 0xffff;
        short[] exceptionTable = count > 0 ? new short[count * 4] : EMPTY_SHORT_ARRAY;
        for (int i = 0; i < exceptionTable.length; i++) {
            exceptionTable[i] = buf.getShort();
        }

        return new Code(maxStack, maxLocals, code, exceptionTable, readAttributes(buf));
    }

    private Annotation[] readAnnotations(ByteBuffer buf) {
        Annotation[] annotations = new Annotation[buf.getShort() & 0xffff];
        for (int i = 0; i < annotations.length; i++) {
            annotations[i] = readAnnotation(buf);
        }
        return annotations;
    }

    private Annotation readAnnotation(ByteBuffer buf) {
        String name = readSymbol(buf);
        int count = buf.getShort() & 0xffff;
        if (count == 0) {
            return new Annotation(name, EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY);
        }

        String[] elementNames = new String[count];
        Object[] elementValues = new Object[count];
        for (int i = 0; i < count; i++) {
            elementNames[i] = readSymbol(buf);
            elementValues[i] = readAnnotationValue(buf);
        }
        return new Annotation(name, elementNames, elementValues);
    }

    private Object readAnnotationValue(ByteBuffer buf) {
        byte tag = buf.get();
        switch (tag) {
            case 'c':
                return new Ref(CONSTANT_Class, buf.getShort());
            case 'e':
                return new Ref2(CONSTANT_Fieldref, buf.getShort(), buf.getShort());
            case '@':
                return readAnnotation(buf);
            case '[': {
                Object[] values = new Object[buf.getShort() & 0xffff];
                for (int i = 0; i < values.length; i++) {
                    values[i] = readAnnotationValue(buf);
                }
                return values;
            }
            default:
                return pool[buf.getShort() & 0xffff];
        }
    }
}
