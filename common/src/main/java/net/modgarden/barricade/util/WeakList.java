package net.modgarden.barricade.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class WeakList<T> {
	private final ArrayList<WeakReference<T>> delegate = new ArrayList<>();

	public void add(T element) {
		delegate.add(new WeakReference<>(element));
	}

	public void addWeak(WeakReference<T> element) {
		if (element.get() != null) {
			delegate.add(element);
		}
	}

	public void removeWeak(WeakReference<T> element) {
		delegate.remove(element);
	}

	public void forEach(Consumer<? super T> action) {
		List<Runnable> removeTasks = new ArrayList<>();
		for (WeakReference<T> ref : delegate) {
			T element = ref.get();
			if (element == null) {
				removeTasks.add(() -> delegate.remove(ref));
				continue;
			}

			action.accept(element);
		}

		removeTasks.forEach(Runnable::run);
	}

	public boolean contains(T element) {
		List<Runnable> removeTasks = new ArrayList<>();
		for (WeakReference<T> ref : delegate) {
			if (ref.get() == null) {
				removeTasks.add(() -> delegate.remove(ref));
				continue;
			}

			if (ref.get() == element) {
				return true;
			}
		}

		removeTasks.forEach(Runnable::run);
		return false;
	}
}
