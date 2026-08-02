export type StatusNotification = 'OK' | 'ERROR' | 'INIT';

export class State<V, T> {
  value?: V;
  error?: T;
  status?: StatusNotification;

  constructor(status: StatusNotification, value?: V, error?: T) {
    this.value = value;
    this.error = error;
    this.status = status;
  }

  static builder<V, T>() {
    return new StateBuilder<V, T>();
  }
}

class StateBuilder<V, T> {
  private value?: V;
  private error?: T;
  private status: StatusNotification = 'INIT';

  forInit(): StateBuilder<V, T> {
    this.status = 'INIT';
    return this;
  }

  forSuccess(value: V): StateBuilder<V, T> {
    this.value = value;
    this.status = 'OK';
    return this;
  }

  forError(error: T): StateBuilder<V, T> {
    this.error = error;
    this.status = 'ERROR';
    return this;
  }

  build(): State<V, T> {
    return new State<V, T>(this.status, this.value, this.error);
  }
}
