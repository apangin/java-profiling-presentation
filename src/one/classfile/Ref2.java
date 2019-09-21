package one.classfile;

class Ref2 {
    final byte tag;
    final short index1;
    final short index2;

    Ref2(byte tag, short index1, short index2) {
        this.tag = tag;
        this.index1 = index1;
        this.index2 = index2;
    }

    @Override
    public String toString() {
        return ConstantPool.TAG_NAME[tag] + " #" + (index1 & 0xffff) + " #" + (index2 & 0xffff);
    }
}
