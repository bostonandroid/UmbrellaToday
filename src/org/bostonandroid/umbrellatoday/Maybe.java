package org.bostonandroid.umbrellatoday;

interface Maybe<T> {
  public Maybe<T> perform(ValueRunner<T> f);
  public Maybe<T> orElse(Runnable f);
}