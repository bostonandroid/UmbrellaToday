package org.bostonandroid.umbrellatoday;

class Nothing<T> implements Maybe<T> {
  public Maybe<T> perform(EitherRunner<T> f) {
    return this;
  }
}