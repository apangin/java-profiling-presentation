package one.classfile;

public class Code {
    public final short maxStack;
    public final short maxLocals;
    public final byte[] code;
    public final short[] exceptionTable;
    public final Attributes attributes;

    Code(short maxStack, short maxLocals, byte[] code, short[] exceptionTable, Attributes attributes) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.code = code;
        this.exceptionTable = exceptionTable;
        this.attributes = attributes;
    }
}
