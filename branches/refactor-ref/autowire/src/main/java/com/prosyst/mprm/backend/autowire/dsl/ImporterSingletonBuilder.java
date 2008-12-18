package com.prosyst.mprm.backend.autowire.dsl;

import java.util.Comparator;

public interface ImporterSingletonBuilder extends ImporterValBuilder {
  ImporterSingletonBuilder resolvedBy(Comparator comp);
  
  ImporterSingletonBuilder withHotswap();
}
