package com.indrekvarva.merkletree.domain;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a node in the Merkle (binary) tree.
 * The implementation uses memoization and caches the (hash) hash for efficiency as well as references
 * to traverse the container tree in both directions.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class Node {
    private final Hash hash;
    private Node parent; // bidirectional association
    private final Node left;
    private final Node right;

    public boolean isLeaf() {
        return right == null && left == null;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public static Node ofLeafHash(Hash hash) {
        return new Node(hash, null, null, null);
    }

    public static Node ofAggregate(Hash hash, Node left, Node right) {
        Node node = new Node(hash, null, left, right);
        left.setParent(node); // set back ref
        right.setParent(node); // set back ref
        return node;
    }
}
