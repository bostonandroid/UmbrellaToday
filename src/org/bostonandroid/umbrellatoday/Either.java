package org.bostonandroid.umbrellatoday;
public interface Either<T> {
  public Either<T> onSuccess(EitherRunner<T> f);
  public Either<T> onFailure(EitherRunner<T> f);
}
