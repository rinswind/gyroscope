package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * Ref is a monad-like thing that adds state tracking to ObectFactory objects.
 * 
 * @author Todor Boev
 */
public class RefCombinators {
  /**
   * A function to lift an ObjectFactory into the Ref monad.
   */
  public static <A, B> Ref<A, B> ref(ObjectFactory<A, B> fact) {
    return new RefImpl<A, B>(fact);
  }
  
  /**
   * A function to sequence a Ref with an ObjectFactory.
   * 
   * @param <A>
   * @param <B>
   * @param <C>
   * @param ref
   * @param fact
   * @return
   */
  public static <A, B, C> Ref<A, C> pipe(final Ref<A, B> ref, final ObjectFactory<B, C> fact) {
    return new RefImpl<A, C>(new ObjectFactory<A, C>() {
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
   * Can be used by the user to raise a signal directly.
   * @return
   */
  public Ref<Void, Void> signal() {
    return new SignalRef() {
      /*
       * This method is actually never called because this ref depends on nothing.
       */
      @Override
      protected boolean mustBind() {
        return true;
      }
    };
  }

//  /**
//   * Start a piping sequence. You might think of this as a variant of the
//   * monadic unit function.
//   * 
//   * @param <A>
//   * @param <B>
//   * @param fact
//   * @return
//   */
//  public static <A, B> RefPipe<A, B> pipe(ObjectFactory<A, B> fact) {
//    return new RefPipeImpl<A, B>(ref(fact));
//  }
//
//  /**
//   * Provides infix notation for piping refs
//   */
//  public interface RefPipe<A, B> {
//    <C> RefPipe<A, C> to(ObjectFactory<B, C> fact);
//
//    Ref<A, B> ref();
//  }
//
//  private static class RefPipeImpl<A, B> implements RefPipe<A, B> {
//    private final Ref<A, B> ref;
//
//    public RefPipeImpl(Ref<A, B> ref) {
//      this.ref = ref;
//    }
//
//    public <C> RefPipe<A, C> to(ObjectFactory<B, C> fact) {
//      return new RefPipeImpl<A, C>(pipe(ref, fact));
//    }
//
//    public Ref<A, B> ref() {
//      return ref;
//    }
//  }
//
//  public static void main(String[] args) {
//    Ref<String, String> pipe = pipe(new ObjectFactory<String, Integer>() {
//      public Integer create(String arg, Map<String, Object> props) {
//        int val = arg.length();
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(Integer val, String arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    }).to(new ObjectFactory<Integer, Integer>() {
//      public Integer create(Integer arg, Map<String, Object> props) {
//        int val = arg * 2;
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(Integer val, Integer arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    }).to(new ObjectFactory<Integer, String>() {
//      public String create(Integer arg, Map<String, Object> props) {
//        String val = "This is the integer " + arg;
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(String val, Integer arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    }).ref();
//
//    pipe.bind("This is short string!", null);
//    pipe.unbind();
//  }
}
