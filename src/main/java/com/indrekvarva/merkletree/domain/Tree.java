package com.indrekvarva.merkletree.domain;

import lombok.Data;

import java.util.Arrays;

/**
 * Wraps the final tree. Provides entry-point references for traversing the tree
 * bottom-up (for audit trail) or top-down.
 */
@Data
public class Tree {
    private final Node root;
    private final Node[] leaves;

    public Node findLeafForHash(Hash logRecordHash) {
        return Arrays
                .stream(this.getLeaves())
                .filter(leaf -> leaf.getHash().equals(logRecordHash))
                .findFirst()
                .orElse(null);
    }
}
