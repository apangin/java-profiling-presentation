package one.classfile;

public class ConstantPool {
    public static final byte CONSTANT_Class = 7;
    public static final byte CONSTANT_Fieldref = 9;
    public static final byte CONSTANT_Methodref = 10;
    public static final byte CONSTANT_InterfaceMethodref = 11;
    public static final byte CONSTANT_String = 8;
    public static final byte CONSTANT_Integer = 3;
    public static final byte CONSTANT_Float = 4;
    public static final byte CONSTANT_Long = 5;
    public static final byte CONSTANT_Double = 6;
    public static final byte CONSTANT_NameAndType = 12;
    public static final byte CONSTANT_Utf8 = 1;
    public static final byte CONSTANT_MethodHandle = 15;
    public static final byte CONSTANT_MethodType = 16;
    public static final byte CONSTANT_Dynamic = 17;
    public static final byte CONSTANT_InvokeDynamic = 18;
    public static final byte CONSTANT_Module = 19;
    public static final byte CONSTANT_Package = 20;

    public static final String[] TAG_NAME = {
            null,
            "Utf8",
            null,
            "Integer",
            "Float",
            "Long",
            "Double",
            "Class",
            "String",
            "FieldRef",
            "MethodRef",
            "InterfaceMethodref",
            "NameAndType",
            null,
            null,
            "MethodHandle",
            "MethodType",
            "Dynamic",
            "InvokeDynamic",
            "Module",
            "Package"
    };
}
