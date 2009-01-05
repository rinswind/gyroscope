package com.prosyst.mprm.backend.proxy.ref;

/**
 * Represents a closure that crates a new ref every time it is called. Every
 * implementation of this closure has it's own special way to create a Ref.
 */
public interface RefFactory<A, V> {
  Ref<A, V> ref();
}