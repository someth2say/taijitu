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
import static net.bytebuddy.implementation.MethodDelegation.toField;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class ProxyFactory {

    public static final String DELEGATE_FIELD_NAME = "$delegate";

    public static <T> T proxy(T instance, Equalizer<T> equalizer) {
        return proxyEqualizer(instance, equalizer, (Class<T>) instance.getClass());
    }

    public static <T> T proxyEqualizer(T instance, Equalizer<T> equalizer, Class<? extends T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Equalizable.class);
        EqualizableInterceptor<T, Equalizer<T>> interceptor = new EqualizableInterceptor<>(equalizer);
        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyComparator(T instance, Comparator<T> comparator, Class<? extends T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Comparable.class);
        ComparatorInterceptor<T, Comparator<T>> interceptor = new ComparatorInterceptor<>(comparator);
        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptComparableMethods(clazz, interceptor, builder);
        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyHasher(T instance, Hasher<T> hasher, Class<? extends T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Hashable.class);
        HasherInterceptor<T, Hasher<T>> interceptor = new HasherInterceptor<>(hasher);

        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);

        return getProxyInstance(instance, builder);
    }

    public static <T> T proxyComparatorHasher(T instance, ComparatorHasher<T> comparatorHasher, Class<? extends T> clazz) {
        Builder<? extends T> builder = getProxyClassBuilder(clazz, Comparable.class, Hashable.class);
        ComparatorHasherInterceptor<T, ComparatorHasher<T>> interceptor = new ComparatorHasherInterceptor<>(comparatorHasher);

        builder = interceptEqualizableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);
        builder = interceptHashableMethods(clazz, interceptor, builder);

        return getProxyInstance(instance, builder);
    }

    private static <T> T getProxyInstance(T instance, Builder<? extends T> builder) {
        Class<? extends T> proxyClass = builder.make()
//                .load(ProxyFactory.class.getClassLoader()).getLoaded();
                .load(ProxyFactory.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        try {
            T newInstance = proxyClass.newInstance();
            newInstance.getClass().getField(DELEGATE_FIELD_NAME).set(newInstance, instance);
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            // Something really weird happened...
            return null;
        }
    }

    private static <T> Builder<? extends T> interceptEqualizableMethods(Class<? extends T> clazz, IEqualizableInterceptor<T, ?> interceptor, Builder<? extends T> builder) {
        return builder
                //TODO: evaluate with approach is more performant: defineMethod or just intercept method (despite can't intercept equality methods...)
                .defineMethod("equals", boolean.class, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor))
                .defineMethod("equalsTo", boolean.class, Visibility.PUBLIC).withParameters(clazz).intercept(to(interceptor));
        //.method(named("areEquals")).intercept(to(interceptor))
        //.method(named("equalsTo")).intercept(to(interceptor))
    }

    private static <T> Builder<? extends T> interceptComparableMethods(Class<? extends T> clazz, IComparatorInterceptor<T, ?> interceptor, Builder<? extends T> builder) {
        return builder.defineMethod("compareTo", clazz, Visibility.PUBLIC).withParameters(Object.class).intercept(to(interceptor));
    }

    private static <T> Builder<? extends T> interceptHashableMethods(Class<? extends T> clazz, IHasherInterceptor<T, ?> interceptor, Builder<? extends T> builder) {
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
//        builder = builder.method(any()).intercept(toField(DELEGATE_FIELD_NAME));                            // java.lang.IllegalArgumentException: None of [public abstract java.util.Iterator java.util.stream.BaseStream.iterator(), public abstract java.util.Spliterator java.util.stream.BaseStream.spliterator(), public abstract void java.util.stream.BaseStream.close(), public abstract java.util.stream.BaseStream java.util.stream.BaseStream.parallel(), public abstract boolean java.util.stream.BaseStream.isParallel(), public abstract java.util.stream.BaseStream java.util.stream.BaseStream.onClose(java.lang.Runnable), public abstract java.util.stream.BaseStream java.util.stream.BaseStream.sequential(), public abstract java.util.stream.BaseStream java.util.stream.BaseStream.unordered(), public abstract java.util.stream.Stream java.util.stream.Stream.limit(long), public abstract long java.util.stream.Stream.count(), public abstract java.util.Optional java.util.stream.Stream.min(java.util.Comparator), public abstract java.util.Optional java.util.stream.Stream.max(java.util.Comparator), public abstract java.lang.Object[] java.util.stream.Stream.toArray(java.util.function.IntFunction), public abstract java.lang.Object[] java.util.stream.Stream.toArray(), public abstract java.lang.Object java.util.stream.Stream.collect(java.util.function.Supplier,java.util.function.BiConsumer,java.util.function.BiConsumer), public abstract java.lang.Object java.util.stream.Stream.collect(java.util.stream.Collector), public abstract void java.util.stream.Stream.forEach(java.util.function.Consumer), public abstract java.util.stream.Stream java.util.stream.Stream.skip(long), public abstract java.util.stream.Stream java.util.stream.Stream.peek(java.util.function.Consumer), public abstract java.util.stream.Stream java.util.stream.Stream.filter(java.util.function.Predicate), public abstract java.util.stream.Stream java.util.stream.Stream.map(java.util.function.Function), public abstract java.util.Optional java.util.stream.Stream.reduce(java.util.function.BinaryOperator), public abstract java.lang.Object java.util.stream.Stream.reduce(java.lang.Object,java.util.function.BiFunction,java.util.function.BinaryOperator), public abstract java.lang.Object java.util.stream.Stream.reduce(java.lang.Object,java.util.function.BinaryOperator), public abstract boolean java.util.stream.Stream.allMatch(java.util.function.Predicate), public abstract boolean java.util.stream.Stream.anyMatch(java.util.function.Predicate), public abstract java.util.stream.Stream java.util.stream.Stream.distinct(), public abstract java.util.Optional java.util.stream.Stream.findAny(), public abstract java.util.Optional java.util.stream.Stream.findFirst(), public abstract java.util.stream.Stream java.util.stream.Stream.flatMap(java.util.function.Function), public abstract java.util.stream.DoubleStream java.util.stream.Stream.flatMapToDouble(java.util.function.Function), public abstract java.util.stream.IntStream java.util.stream.Stream.flatMapToInt(java.util.function.Function), public abstract java.util.stream.LongStream java.util.stream.Stream.flatMapToLong(java.util.function.Function), public abstract void java.util.stream.Stream.forEachOrdered(java.util.function.Consumer), public abstract java.util.stream.DoubleStream java.util.stream.Stream.mapToDouble(java.util.function.ToDoubleFunction), public abstract java.util.stream.IntStream java.util.stream.Stream.mapToInt(java.util.function.ToIntFunction), public abstract java.util.stream.LongStream java.util.stream.Stream.mapToLong(java.util.function.ToLongFunction), public abstract boolean java.util.stream.Stream.noneMatch(java.util.function.Predicate), public abstract java.util.stream.Stream java.util.stream.Stream.sorted(), public abstract java.util.stream.Stream java.util.stream.Stream.sorted(java.util.Comparator)]
//                                                                                                              allows for delegation from public java.lang.String java.lang.Object.toString()
        builder = builder.method(not(isDeclaredBy(Object.class)))
                .intercept(
                        MethodDelegation.withEmptyConfiguration()
//                        .withDefaultConfiguration()
//                        .withResolvers(MethodDelegationBinder.AmbiguityResolver.Directional.LEFT)
                        .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                        .withResolvers(CUSTOM_AMBIGUITY_RESOLVER)
//                        .withResolvers(MethodNameEqualityResolver.INSTANCE)
                        .toField(DELEGATE_FIELD_NAME));  // java.lang.IllegalArgumentException: Cannot resolve ambiguous delegation of public abstract void java.util.stream.BaseStream.close() to net.bytebuddy.implementation.bind.MethodDelegationBinder$MethodBinding$Builder$Build@9416032f or net.bytebuddy.implementation.bind.MethodDelegationBinder$MethodBinding$Builder$Build@a6a115df
        // NEED TO ADD AMBIGUITY RESOLVER (i.e. by name)
//      builder = builder.method(isDeclaredBy(clazz)).intercept(toField(DELEGATE_FIELD_NAME));              // java.lang.AbstractMethodError: net.bytebuddy.renamed.java.lang.Object$ByteBuddy$dKrYibhq.spliterator()Ljava/util/Spliterator
//
//      builder = builder.method(isDeclaredBy(clazz).and(not(isDeclaredBy(Object.class)))).intercept(toField(DELEGATE_FIELD_NAME));
        return builder;


    }
}
