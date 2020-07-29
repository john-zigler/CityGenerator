package main.util;

public class Pair<K, V> {
	private K key;
	private V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	public String toString() {
		return key.toString() + ", " + value.toString();
	}
	public int hashCode() {
		return toString().hashCode();
	}
	public boolean equals(Object object) {
		if (object instanceof Pair) {
			Pair<?, ?> otherPair = (Pair<?, ?>) object;
			return this.key.equals(otherPair.key) && this.value.equals(otherPair.value);
		}
		return false;
	}
}
