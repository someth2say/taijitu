package org.someth2say.taijitu.equality.wrapper;
/**
 * Abstract wrapper class.
 * Given an object and an equality, a wrapper is an object that translates external equality methods into internal equality methods.
 * That is, given 
 *   wrapper = new Wrapper(obj,equ);
 * The following calls return the same result
 *   equ.areEquals(obj, other) ==  wrapper.equals(other);
 *   equ.hash(obj) == wrapper.hashCode()
 * ...
 * 
 * @param <WRAPPED>
 */
public abstract class Wrapper<WRAPPED> {
    private final WRAPPED wrapped;

    Wrapper(WRAPPED wrapped) {
        this.wrapped = wrapped;
    }

    public WRAPPED getWraped() {
        return wrapped;
    }

    /**
     * Wrapper factory.
     * This is a class utility used to avoid the creation of a new instance of the new wrapper every time the same equality is used.
     * 
     * Replaces (and is semantically equals) 
     *   wx=new *Wrapper<...>(x,equality); wy=new *Wrapper<...>(y,equality); wz=new *Wrapper<...>(z,equality);...
     * by
     *  *Wrapper.Factory<...> factory= new *Wrapper.Factory(equality);
     *   xw=factory.wrap(x); yw=factory.wrap(y); zw=factory.wrap(z);
     * 
     * @param <WRAPPED>
     * 
     * @see Wrappers
     */
    public interface Factory<WRAPPED> {
        Wrapper<WRAPPED> wrap(WRAPPED wrapped);
    }

}
