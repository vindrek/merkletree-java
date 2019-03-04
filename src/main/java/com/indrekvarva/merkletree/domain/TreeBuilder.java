package com.indrekvarva.merkletree.domain;

import lombok.Value;

import java.util.LinkedList;
import java.util.stream.IntStream;

/**
 * This factory class exposes a bottom-up method to build a Merkle binary tree based on a final/fixed set of log records.
 * The time complexity of tree building algorithm implementation is O(n).
 *
 *
 * Balancing:
 *
 * The resulting trees are *not balanced* because it's currently being built as an aggregation product going bottom-up
 * with the last odd node being dragged over to the next recursive aggregation. Max depth of the tree is log2(n)+1.
 *
 * If producing a *perfectly balanced* binary trees would've been a requirement, a dynamic tree implementation
 * which re-builds itself upon every node insertion (e.g. red-black tree) or divide and conquer top-down methodology would
 * be more appropriate (see my elixir and javascript solutions).
 *
 * Alternatively, a *more balanced* tree could be achieved when the last odd node would not be dragged over to the next
 * recursive aggregation, but aggregated with a random node on the same level.
 *
 * On the other hand, if it's sane to provide audit proofs of max length of the number of log records,
 * the aggregator could be simplified and optimized to reduce a root node in a
 * single list iteration to return by implementing a linear hash chain (which is, technically, a binary tree, too).
 */

@Value
public class TreeBuilder {

    private Hasher hasher;

    /**
     * @param logRecords read from the provided log file
     * @return a balanced binary tree with the max depth of log2(n) + 1
     */
    public Tree build(LinkedList<byte[]> logRecords) {
        Node[] leaves = logRecords
                .stream()
                .map(this::buildLeafNode)
                .toArray(Node[]::new);
        Node rootNode = aggregate(leaves);
        return new Tree(rootNode, leaves);
    }

    /**
     * Tail-optimized recursive node aggregator which is thread-safe and utilizes parallelism.
     * @param nodes
     * @return root node
     */
    private Node aggregate(Node[] nodes) {

        // recursion base
        if (nodes.length == 1) {
            return nodes[0];
        }

        boolean even = nodes.length % 2 == 0;
        int nextAggregatesLength = even ? nodes.length / 2 : nodes.length / 2 + 1; // cut the array in half (or +1 when odd nr of elements to drag over the last node)
        Node[] nextLevelAggregates = new Node[nextAggregatesLength];


        IntStream
                .range(0, nextAggregatesLength)
                .parallel() // parallel stream creating controlled side-effects on the next level aggregates array
                .forEach(i -> {
                    if (i == nextAggregatesLength - 1 && !even) {
                        nextLevelAggregates[i] = nodes[i]; // drag over the last node in an odd-length array to the next level
                    } else {
                        nextLevelAggregates[i] = aggregateNodes(nodes[i * 2], nodes[i * 2 + 1]); // iteration in pairs
                    }
                });
        return aggregate(nextLevelAggregates); // recursively aggregate the next level, going bottom-up
    }

    private Node buildLeafNode(byte[] value) {
        //System.out.println("Leaf hash " + Hash.of(this.hasher.digest(value)).toString());
        return Node.ofLeafHash(Hash.of(this.hasher.digest(value)));
    }

    private Node aggregateNodes(Node left, Node right) {
        //System.out.println("Aggregate hash " + Hash.of(this.hasher.calculateAggregateHash(left.getHash(), right.getHash())).toString());
        Hash aggregateHash = Hash.of(this.hasher.calculateAggregateHash(left.getHash(), right.getHash()));
        return Node.ofAggregate(aggregateHash, left, right);
    }

}
