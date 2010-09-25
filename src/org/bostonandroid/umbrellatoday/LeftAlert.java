package org.bostonandroid.umbrellatoday;

class LeftAlert implements AlertOrError {
  private Alert alert;

  LeftAlert(Alert a) {
    this.alert = a;
  }
  
  public AlertOrError onSuccess(EitherRunner<SavedAlert> f) {
    return this;
  }
 
  public AlertOrError onFailure(EitherRunner<Alert> f) {
    f.run(this.alert);
    return this;
  }
}