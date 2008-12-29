package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * Ref is a monad-like thing that adds state tracking to ObectFactory objects.
 * 
 * @author Todor Boev
 */
public class Refs {
  /**
   * A function to lift an ObjectFactory into the Ref monad.
   */
  public static <A, B> Ref<A, B> ref(ObjectFactory<A, B> fact) {
    return new RefImpl<A, B>(fact);
  }
  
  /**
   * Change the output of a Ref from B to C
   * 
   * @param <A>
   * @param <B>
   * @param <C>
   * @param ref
   * @param fact
   * @return
   */
  public static <A, B, C> Ref<A, C> to(final Ref<A, B> ref, final ObjectFactory<B, C> fact) {
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
   * Change the input of a ref from B to C
   * 
   * @param <A>
   * @param <B>
   * @param <C>
   * @param ref
   * @param fact
   * @return
   */
  public static <A, B, C> Ref<C, A> from(final ObjectFactory<C, B> fact, final Ref<B, A> ref) {
    if (ref == null) {
      throw new NullPointerException();
    }
    
    if (fact == null) {
      throw new NullPointerException();
    }
    
    return new RefImpl<C, A>(new ObjectFactory<C, A>() {
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
//  public static <A, B> ToPipe<A, B> to(ObjectFactory<A, B> fact) {
//    return new ToPipeImpl<A, B>(ref(fact));
//  }
//
//  /**
//   * Start a piping sequence. You might think of this as a variant of the
//   * monadic unit function.
//   * 
//   * @param <A>
//   * @param <B>
//   * @param fact
//   * @return
//   */
//  public static <A, B> FromPipe<B, A> from(ObjectFactory<B, A> fact) {
//    return new FromPipeImpl<B, A>(ref(fact));
//  }
//  
//  /**
//   * Provides infix notation for piping refs
//   */
//  public interface ToPipe<A, B> {
//    <C> ToPipe<A, C> to(ObjectFactory<B, C> fact);
//
//    Ref<A, B> ref();
//  }
//  
//  public interface FromPipe<B, A> {
//    <C> FromPipe<C, A> from(ObjectFactory<C, B> fact);
//    
//    Ref<B, A> ref();
//  }
//  
//
//  private static class ToPipeImpl<A, B> implements ToPipe<A, B> {
//    private final Ref<A, B> ref;
//
//    public ToPipeImpl(Ref<A, B> ref) {
//      this.ref = ref;
//    }
//
//    public <C> ToPipe<A, C> to(ObjectFactory<B, C> fact) {
//      return new ToPipeImpl<A, C>(RefCombinators.to(ref, fact));
//    }
//
//    public Ref<A, B> ref() {
//      return ref;
//    }
//  }
//
//  private static class FromPipeImpl<B, A> implements FromPipe<B, A> {
//    private final Ref<B, A> ref;
//
//    public FromPipeImpl(Ref<B, A> ref) {
//      this.ref = ref;
//    }
//
//    public <C> FromPipe<C, A> from(ObjectFactory<C, B> fact) {
//      return new FromPipeImpl<C, A>(RefCombinators.from(fact, ref));
//    }
//
//    public Ref<B, A> ref() {
//      return ref;
//    }
//  }
//  
//  public static void main(String[] args) {
//    Ref<String, String> right = 
//    to(new ObjectFactory<String, Integer>() {
//      public Integer create(String arg, Map<String, Object> props) {
//        int val = arg.length();
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(Integer val, String arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    })
//    .to(new ObjectFactory<Integer, Integer>() {
//      public Integer create(Integer arg, Map<String, Object> props) {
//        int val = arg * 2;
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(Integer val, Integer arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    })
//    .to(new ObjectFactory<Integer, String>() {
//      public String create(Integer arg, Map<String, Object> props) {
//        String val = "This is the integer " + arg;
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(String val, Integer arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    })
//    .ref();
//
//    System.out.println("--- bind ---");
//    right.bind("This is short string!", null);
//    System.out.println("--- unbind ---");
//    right.unbind();
//    
//    System.out.println("------------------------");
//    
//    Ref<String, String> left = 
//    from(new ObjectFactory<Integer, String>() {
//        public String create(Integer arg, Map<String, Object> props) {
//          String val = "This is the integer " + arg;
//          System.out.println(arg + "->" + val);
//          return val;
//        }
//
//        public void destroy(String val, Integer arg, Map<String, Object> props) {
//          System.out.println(val + "->" + arg);
//        }
//      })
//    .from(new ObjectFactory<Integer, Integer>() {
//      public Integer create(Integer arg, Map<String, Object> props) {
//        int val = arg * 2;
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(Integer val, Integer arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    })
//    .from(new ObjectFactory<String, Integer>() {
//      public Integer create(String arg, Map<String, Object> props) {
//        int val = arg.length();
//        System.out.println(arg + "->" + val);
//        return val;
//      }
//
//      public void destroy(Integer val, String arg, Map<String, Object> props) {
//        System.out.println(val + "->" + arg);
//      }
//    })
//    .ref();
//
//    System.out.println("--- bind ---");
//    left.bind("This is short string!", null);
//    System.out.println("--- unbind ---");
//    left.unbind();
//  }
}
