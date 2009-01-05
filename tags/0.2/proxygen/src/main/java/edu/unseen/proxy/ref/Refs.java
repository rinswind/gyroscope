package edu.unseen.proxy.ref;

import java.util.Map;

/**
 * Ref is a monad-like thing that adds state tracking and result storage to
 * ObectFactory instances.
 * 
 * @author Todor Boev
 */
public class Refs {
  /**
   * A function to lift an ObjectFactory into the Ref monad.
   */
  public static <A, B> Ref<A, B> ref(Transformer<A, B> fact) {
    return new RefImpl<A, B>(fact);
  }

  /**
   * @param <A>
   * @param <B>
   * @param seed
   * @return
   */
  public static <A, B> RefFactoryCombinator<A, B> combinator(Transformer<A, B> seed) {
    return new RefFactoryCombinatorImpl<A, B>(seed);
  }

  /**
   * Change the output of a Ref from B to C. The input remains A.
   * 
   * @param <A>
   * @param <B>
   * @param <C>
   * @param ref
   * @param fact
   * @return
   */
  public static <A, B, C> Ref<A, C> to(final Ref<A, B> ref, final Transformer<B, C> fact) {
    if (ref == null) {
      throw new NullPointerException();
    }

    if (fact == null) {
      throw new NullPointerException();
    }

    return ref(new Transformer<A, C>() {
      public C create(A arg, Map<String, Object> props) {
        ref.bind(arg, props);
        return fact.create(ref.val(), props);
      }

      public void destroy(C val, A arg, Map<String, Object> props) {
        fact.destroy(val, ref.val(), props);
        ref.unbind();
      }
    });
  }

  /**
   * Change the input of a ref from B to C. The output remains A.
   * 
   * @param <A>
   * @param <B>
   * @param <C>
   * @param ref
   * @param fact
   * @return
   */
  public static <A, B, C> Ref<C, A> from(final Transformer<C, B> fact, final Ref<B, A> ref) {
    if (ref == null) {
      throw new NullPointerException();
    }

    if (fact == null) {
      throw new NullPointerException();
    }

    return ref(new Transformer<C, A>() {
      public A create(C arg, Map<String, Object> props) {
        ref.bind(fact.create(arg, props), props);
        return ref.val();
      }

      public void destroy(A val, C arg, Map<String, Object> props) {
        B b = ref.arg();
        ref.unbind();
        fact.destroy(b, arg, props);
      }
    });
  }

  /**
   * @param args
   * @return
   */
  public static Ref<Void, Void> and(Object... args) {
    return new SignalRef(args) {
      @Override
      protected boolean mustBind() {
        for (Ref<?, ?> dep : deps()) {
          if (!isBound(dep)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  /**
   * @param args
   * @return
   */
  public static Ref<Void, Void> or(Object... args) {
    return new SignalRef(args) {
      @Override
      protected boolean mustBind() {
        for (Ref<?, ?> dep : deps()) {
          if (isBound(dep)) {
            return true;
          }
        }
        return false;
      }
    };
  }

  /**
   * @param arg
   * @return
   */
  public static Ref<Void, Void> not(Object arg) {
    return new SignalRef(arg) {
      @Override
      protected boolean mustBind() {
        return !isBound(deps().get(0));
      }
    };
  }

  /**
   * Can be used by the user to raise a signal directly by calling bind/unbind.
   * 
   * @return
   */
  public Ref<Void, Void> signal() {
    return new SignalRef() {
      /*
       * This method is actually never called because this ref depends on
       * nothing.
       */
      @Override
      protected boolean mustBind() {
        return true;
      }
    };
  }
}
