package org.bostonandroid.umbrellatoday;

interface Maybe<T> {
  public Maybe<T> perform(EitherRunner<T> f);
}