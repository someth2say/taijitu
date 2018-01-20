package org.someth2say.taijitu.compare.equality.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.*;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import org.someth2say.taijitu.compare.equality.aspects.external.Comparator;
import org.someth2say.taijitu.compare.equality.aspects.external.ComparatorHasher;
import org.someth2say.taijitu.compare.equality.aspects.external.Equalizer;
import org.someth2say.taijitu.compare.equality.aspects.external.Hasher;
import org.someth2say.taijitu.compare.equality.aspects.internal.Comparable;
import org.someth2say.taijitu.compare.equality.aspects.internal.Equalizable;
import org.someth2say.taijitu.compare.equality.aspects.internal.Hashable;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class Proxies {

    private static final String DELEGATE_FIELD_NAME = "$delegate";

    public static <T> T proxy(T instance, Equalizer<? extends T> equalizer, Class<T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Equalizable.class);
        EqualizableInterceptor<? extends T, ? extends Equalizer<? extends T>> interceptor = new EqualizableInterceptor<>(equalizer);
        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyComparator(T instance, Comparator<? extends T> comparator, Class<T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Comparable.class);
        ComparatorInterceptor<? extends T, ? extends Comparator<? extends T>> interceptor = new ComparatorInterceptor<>(comparator);
        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptComparableMethods(clazz, interceptor, builder);
        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyHasher(T instance, Hasher<? extends T> hasher, Class<T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Hashable.class);
        HasherInterceptor<? extends T, ? extends Hasher<? extends T>> interceptor = new HasherInterceptor<>(hasher);

        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);

        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyComparatorHasher(T instance, ComparatorHasher<T> comparatorHasher, Class<T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Comparable.class, Hashable.class);
        ComparatorHasherInterceptor<T, ComparatorHasher<T>> interceptor = new ComparatorHasherInterceptor<>(comparatorHasher);

        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);

        return getProxyInstance(instance, builder);
    }

    private static <T> T getProxyInstance(T instance, Builder<? extends T> builder) {
        Class<? extends T> proxyClass = builder.make()
                .load(Proxies.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        try {
            T newInstance = proxyClass.newInstance();
            newInstance.getClass().getField(DELEGATE_FIELD_NAME).set(newInstance, instance);
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            // Something really weird happened...
            return null;
        }
    }

    private static <T> Builder<? extends T> interceptEqualizableMethods(Class<? extends T> clazz, EqualizableInterceptor<? extends T, ? extends Equalizer<? extends T>> interceptor, Builder<? extends T> builder) {
        return builder
                .defineMethod("equals", boolean.class, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor))
                .defineMethod("equalsTo", boolean.class, Visibility.PUBLIC).withParameters(clazz).intercept(to(interceptor));
    }

    private static <T> Builder<? extends T> interceptComparableMethods(Class<? extends T> clazz, IComparatorInterceptor<? extends T, ?> interceptor, Builder<? extends T> builder) {
        return builder.defineMethod("compareTo", clazz, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor));
    }

    private static <T> Builder<? extends T> interceptHashableMethods(Class<? extends T> clazz, IHasherInterceptor<? extends T, ?> interceptor, Builder<? extends T> builder) {
        return builder.defineMethod("hash", clazz, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor));
    }


    private static MethodDelegationBinder.AmbiguityResolver CUSTOM_AMBIGUITY_RESOLVER = new MethodDelegationBinder.AmbiguityResolver.Compound(
            BindingPriority.Resolver.INSTANCE,
            MethodNameEqualityResolver.INSTANCE,
            ArgumentTypeResolver.INSTANCE,
            ParameterLengthResolver.INSTANCE,
            DeclaringTypeResolver.INSTANCE);

    private static <T> Builder<? extends T> getProxyClassBuilder(Class<? extends T> clazz, Class<?>... ifaces) {
        ByteBuddy byteBuddy = new ByteBuddy();

        Builder<? extends T> builder;
        if (!clazz.isInterface()) {
            builder = byteBuddy.subclass(clazz, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR);

        } else {
            // Interface...
            Builder<Object> subclass = byteBuddy.subclass(Object.class, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR);
            builder = (Builder<? extends T>) subclass.implement(clazz);

        }
        for (Class<?> iface : ifaces) {
            builder = builder.implement(iface);
        }
        builder = builder.defineField(DELEGATE_FIELD_NAME, clazz, Visibility.PUBLIC); //Define delegate field
        builder = builder.method(not(isDeclaredBy(Object.class)))
                .intercept(
                        MethodDelegation.withEmptyConfiguration()
                                .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                                .withResolvers(CUSTOM_AMBIGUITY_RESOLVER)
                                .toField(DELEGATE_FIELD_NAME));  // java.lang.IllegalArgumentException: Cannot resolve ambiguous delegation of public abstract void java.util.stream.BaseStream.close() to net.bytebuddy.implementation.bind.MethodDelegationBinder$MethodBinding$Builder$Build@9416032f or net.bytebuddy.implementation.bind.MethodDelegationBinder$MethodBinding$Builder$Build@a6a115df
        return builder;


    }
}
