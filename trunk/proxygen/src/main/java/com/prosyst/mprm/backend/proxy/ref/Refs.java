package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * Monadic DSL for chaining ObjectFactories using Refs.
 * 
 * The side effect is represented by Ref instances that wrap the
 * ObjectFactories. The Refs implement multiple side effects: they store the
 * production of the factories for later use and if the factory was called and
 * it's result is available. In Haskell lingo we can say we lift ObjectFactories
 * into the Ref monad.
 * 
 * @author Todor Boev
 */
public class Refs {
  /**
   * Provides an infix syntax to linking two ObjectFactories in a chain.
   * 
   * @param <A>
   * @param <V>
   */
  public interface RefPipe<A, V> {
    <N> RefPipe<A, N> to(ObjectFactory<V, N> fact);
    
    Ref<A, V> ref();
  }
  
  /**
   * Essentially this is an object that represents the monadic bind operator. It
   * holds the first argument in a field and takes the second as a parameter.
   * 
   * @param <A>
   * @param <V>
   */
  private static class RefPipeImpl<A, V> implements RefPipe<A, V> {
    private final Ref<A, V> ref;
    
    public RefPipeImpl(Ref<A, V> ref) {
      this.ref = ref;
    }
    
    public <N> RefPipe<A, N> to(final ObjectFactory<V, N> fact) {
      return new RefPipeImpl<A, N>(Refs.bind(ref, fact));
    }
    
    public Ref<A, V> ref() {
      return ref;
    }
  }
  
  /**
   * A shorthand that can be used to create a chain of 0 refs.
   * 
   * @param <A>
   * @param <B>
   * @param fact
   * @return
   */
  public static <A, B> RefPipe<A, B> pipe(final ObjectFactory<A, B> fact) {
    return new RefPipeImpl<A, B>(ref(fact));
  }
  
  /**
   * The monadic unit for ObjectFactories.
   * 
   * @param <A>
   * @param <B>
   * @param fact
   * @return
   */
  public static <A, B> Ref<A, B> ref(final ObjectFactory<A, B> fact) {
    return new RefImpl<A, B>(fact);
  }
  
  /**
   * The monadic bind for Refs.
   * 
   * @param <A>
   * @param <B>
   * @param <C>
   * @param fst
   * @param sec
   * @return
   */
  public static <A, B, C> Ref<A, C> bind(final Ref<A, B> fst, final ObjectFactory<B, C> sec) {
    return new RefImpl<A, C>(new ObjectFactory<A, C>() {
      public C create(A arg, Map<String, ?> props) {
        fst.bind(arg, props);
        return sec.create(fst.val(), props);
      }

      public void destroy(C val, A arg, Map<String, ?> props) {
        sec.destroy(val, fst.val(), props);
        fst.unbind();
      }
    });
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    Ref<String, String> pipe = 
     pipe(
      new ObjectFactory<String, Integer>() {
        public Integer create(String arg, Map<String, ?> props) {
          int res = arg.length();
          System.out.println(arg + "->" + res);
          return res;
        }
  
        public void destroy(Integer val, String arg, Map<String, ?> props) {
          System.out.println(val + "->" + arg);
        }
      })
    .to(
      new ObjectFactory<Integer, Integer>() {
        public Integer create(Integer arg, Map<String, ?> props) {
          int res = arg*2;
          System.out.println(arg + "->" + res);
          return res;
        }

        public void destroy(Integer val, Integer arg, Map<String, ?> props) {
          System.out.println(val + "->" + arg);
        }
      })
    .to(
      new ObjectFactory<Integer, String>() {
        public String create(Integer arg, Map<String, ?> props) {
          String res = "This is the integer " + arg;
          System.out.println(arg + "->" + res);
          return res;
        }
        
        public void destroy(String val, Integer arg, Map<String, ?> props) {
          System.out.println(val + "->" + arg);
        }
      })
    .ref();
    
    pipe.bind("This is a long string", null);
    pipe.unbind();
  }
}
