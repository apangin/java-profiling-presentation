package one.classfile;

class Ref {
    final byte tag;
    final short index;

    Ref(byte tag, short index) {
        this.tag = tag;
        this.index = index;
    }

    @Override
    public String toString() {
        return ConstantPool.TAG_NAME[tag] + " #" + (index & 0xffff);
    }
}
