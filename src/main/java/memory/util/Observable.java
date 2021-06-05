package memory.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Observable<T> {
    private final List<Consumer<T>> observers = new CopyOnWriteArrayList<>();
    private T last;

    public void update(T next) {
        this.observers.forEach((o) -> o.accept(next));
        this.last = next;
    }

    public void observe(Consumer<T> observer) {
        this.observers.add(observer);
        observer.accept(this.last);
    }
}
