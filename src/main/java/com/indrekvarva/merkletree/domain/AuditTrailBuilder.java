package com.indrekvarva.merkletree.domain;

import lombok.Value;

import java.nio.charset.Charset;
import java.util.LinkedList;

@Value
public class AuditTrailBuilder {

    private final Hasher hasher;
    private final String recordEncoding;

    private enum RecordOriginDirection { LEFT, RIGHT }

    /**
     * Finds the corresponding leaf node for the log record and recursively
     * moves bottom-up to capture the audit trail.
     *
     * @param tree pre-built merkle tree with cached hash labels
     * @param logRecord for which the audit trail is requested
     * @return the hash path from leaf node to the root (excluded)
     * @throws LogRecordNotFoundException
     */
    public LinkedList<String> createAuditProof(Tree tree, String logRecord) throws LogRecordNotFoundException {
        Node recordCorrespondingLeaf =
                tree.findLeafForHash(
                    Hash.of(hasher.digest(logRecord.getBytes(Charset.forName(recordEncoding)))));
        if (recordCorrespondingLeaf == null) {
            throw new LogRecordNotFoundException(String.format(
                    "Couldn't produce audit trail for non-existent record: %s", logRecord));
        }

        return produceTrailToRoot(recordCorrespondingLeaf, new LinkedList<>());
    }

    private LinkedList<String> produceTrailToRoot(
            Node onRecordPathNode, LinkedList<String> runningTrail) {

        // recursion base
        if (onRecordPathNode.isRoot()) {
            return runningTrail;
        }

        RecordOriginDirection recordOriginDirection = findNodeParentRelativePosition(onRecordPathNode);

        runningTrail
                .add(recordOriginDirection
                        .equals(RecordOriginDirection.LEFT)
                        ? onRecordPathNode.getParent().getRight().getHash().toString()
                        : onRecordPathNode.getParent().getLeft().getHash().toString());
        return produceTrailToRoot(onRecordPathNode.getParent(), runningTrail);
    }

    private RecordOriginDirection findNodeParentRelativePosition(Node node) {
        if (node.getParent().getLeft().getHash().equals(node.getHash())) {
            return RecordOriginDirection.LEFT;
        } else {
            return RecordOriginDirection.RIGHT;
        }
    }
}
