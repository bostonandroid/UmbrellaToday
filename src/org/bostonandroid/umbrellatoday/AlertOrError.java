package org.bostonandroid.umbrellatoday;

interface AlertOrError {
  public AlertOrError onSuccess(EitherRunner<SavedAlert> f);
  public AlertOrError onFailure(EitherRunner<Alert> f);
}