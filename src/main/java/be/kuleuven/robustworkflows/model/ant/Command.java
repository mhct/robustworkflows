package be.kuleuven.robustworkflows.model.ant;

public interface Command<T> {

	public boolean apply(T data);
}
