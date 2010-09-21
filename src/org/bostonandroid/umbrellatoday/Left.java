package org.bostonandroid.umbrellatoday;
public class Left<T> implements Either<T> {
  private T value;
  Left(T theValue) { this.value = theValue; }

  public Either<T> onSuccess(EitherRunner<T> f) {
    return this;
  }

  public Either<T> onFailure(EitherRunner<T> f) {
    f.run(this.value);
    return this;
  }
}