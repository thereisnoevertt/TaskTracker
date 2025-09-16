package Node;

public class Node<T> {
    public T data;
    public Node<T> prev;
    public Node<T> next;

    public Node(Node<T> prev, Node<T> next, T data) {
        this.prev = prev;
        this.next = next;
        this.data = data;
    }
}
