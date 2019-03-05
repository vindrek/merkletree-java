package com.indrekvarva.merkletree.domain;

import lombok.Value;

import java.security.MessageDigest;

/**
 * Calculates data hashes using the provided MessageDigest instance.
 */
@Value
public class Hasher {
    private MessageDigest digester;

    public byte[] digest(byte[] data) {
        return this.digester.digest(data);
    }

    public byte[] calculateAggregateHash(Hash left, Hash right) {
        try {
            MessageDigest md = (MessageDigest) digester.clone();
            md.update(left.getValue());
            md.update(right.getValue());
            return md.digest();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
        //return this.digester.digest(concatHashByteArrays(left, right));
    }

    /**
     *
     * @param left
     * @param right
     * @return byte array of the concatenated hash values
     *
     * Keeps digester stateless in terms of hashable data.
     * Alternatively, MessageDigest.update() could be used but this would mean that parallel consumption of this service
     * would no longer be thread-safe without cloning the digester for each consumer call.
     */
    private byte[] concatHashByteArrays(Hash left, Hash right) {
        byte[] hashConcatenation = new byte[left.getValue().length + right.getValue().length];
        System.arraycopy(left.getValue(), 0, hashConcatenation, 0, left.getValue().length);
        System.arraycopy(right.getValue(), 0, hashConcatenation, left.getValue().length, right.getValue().length);
        return hashConcatenation;
    }
}
