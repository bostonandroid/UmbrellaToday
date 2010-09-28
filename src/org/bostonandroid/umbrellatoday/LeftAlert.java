package org.bostonandroid.umbrellatoday;

class LeftAlert implements AlertOrError {
  private Alert alert;

  LeftAlert(Alert a) {
    this.alert = a;
  }
  
  public AlertOrError onSuccess(ValueRunner<SavedAlert> f) {
    return this;
  }
 
  public AlertOrError onFailure(ValueRunner<Alert> f) {
    f.run(this.alert);
    return this;
  }
}