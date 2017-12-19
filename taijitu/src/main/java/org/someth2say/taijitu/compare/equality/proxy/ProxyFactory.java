package org.someth2say.taijitu.compare.equality.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.implementation.MethodDelegation.toField;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyFactory {

    static String DELEGATE_FIELD_NAME = "$delegate";

    public static <T> T proxy(T instance, Equalizer<T> equalizer) {
        return proxyEqualizer(instance, equalizer, (Class<T>) instance.getClass());
    }

    public static <T> T proxyEqualizer(T instance, Equalizer<T> equalizer, Class<T> clazz) {
        Builder<T> builder = getProxyClassBuilder(clazz, Equalizable.class);
        EqualizableInterceptor<T, Equalizer<T>> interceptor = new EqualizableInterceptor<>(equalizer);
        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyComparator(T instance, Comparator<T> comparator, Class<T> clazz) {
        Builder<T> builder = getProxyClassBuilder(clazz, Comparable.class);
        ComparatorInterceptor<T, Comparator<T>> interceptor = new ComparatorInterceptor<>(comparator);
        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptComparableMethods(clazz, interceptor, builder);
        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyHasher(T instance, Hasher<T> hasher, Class<T> clazz) {
        Builder<T> builder = getProxyClassBuilder(clazz, Hashable.class);
        HasherInterceptor<T, Hasher<T>> interceptor = new HasherInterceptor<>(hasher);

        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);

        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyComparatorHasher(T instance, ComparatorHasher<T> comparatorHasher, Class<T> clazz) {
        Builder<T> builder = getProxyClassBuilder(clazz, Comparable.class, Hashable.class);
        ComparatorHasherInterceptor<T, ComparatorHasher<T>> interceptor = new ComparatorHasherInterceptor<>(comparatorHasher);

        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);

        return getProxyInstance(instance, builder);
    }

    private static <T> T getProxyInstance(T instance, Builder<T> builder) {
        Class<? extends T> proxyClass = builder.make().load(ProxyFactory.class.getClassLoader()).getLoaded();

        try {
            T newInstance = proxyClass.newInstance();
            newInstance.getClass().getField(DELEGATE_FIELD_NAME).set(newInstance, instance);
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            // Something really weird happened...
            return null;
        }
    }

    private static <T> ReceiverTypeDefinition<T> interceptEqualizableMethods(Class<T> clazz, IEqualizableInterceptor<T, ?> interceptor, Builder<T> builder) {
        return builder
                //TODO: evaluate with approach is more performant: defineMethod or just intercept method (despite can't intercept equality methods...)
                .defineMethod("equals", boolean.class, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor))
                .defineMethod("equalsTo", boolean.class, Visibility.PUBLIC).withParameters(clazz).intercept(to(interceptor));
        //.method(named("equals")).intercept(to(interceptor))
        //.method(named("equalsTo")).intercept(to(interceptor))
    }

    private static <T> ReceiverTypeDefinition<T> interceptComparableMethods(Class<T> clazz, IComparatorInterceptor<T, ?> interceptor, Builder<T> equalizableIntercepted) {
        return equalizableIntercepted.defineMethod("compareTo", clazz, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor));
    }

    private static <T> ReceiverTypeDefinition<T> interceptHashableMethods(Class<T> clazz, IHasherInterceptor<T, ?> interceptor, Builder<T> builder) {
        return builder.defineMethod("hashCode", clazz, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor));
    }

    private static <T> Builder<T> getProxyClassBuilder(Class<T> clazz, Class<?>... ifaces) {
        ByteBuddy byteBuddy = new ByteBuddy();

        Builder<T> builder;
        if (clazz.isInterface()){
            builder = (Builder<T>) byteBuddy.subclass(Object.class, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR).implement(clazz);
        } else {
            builder = byteBuddy.subclass(clazz, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR);
        }

        for (Class<?> iface : ifaces) {
            builder = builder.implement(iface);
        }
        builder = builder.defineField(DELEGATE_FIELD_NAME, clazz, Visibility.PUBLIC);
        builder = builder.method(any()).intercept(toField(DELEGATE_FIELD_NAME));
//        builder = builder.method(not(isDeclaredBy(Object.class))).intercept(toField(DELEGATE_FIELD_NAME));
//        builder = builder.method(isDeclaredBy(clazz).or(isDeclaredBy(Object.class))).intercept(toField(DELEGATE_FIELD_NAME));
        return builder;
    }

}
