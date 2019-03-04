package com.indrekvarva.merkletree;

import com.indrekvarva.merkletree.domain.*;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedList;

public class MerkleTreeApplication {

    private static final String ENCODING = "UTF-8";

    public static void main(String... args) {
        validateArgs(args);
        controller(args[0], args[1], args.length == 3 ? args[2] : null);
    }

    private static void controller(String filePath, String hashAlgorithm, String auditTrailRecord) {
        try {

            Hasher hasher = new Hasher(MessageDigest.getInstance(hashAlgorithm));
            Tree tree = new TreeBuilder(hasher).build(readLog(filePath));
            new SigningService().sign(tree.getRoot().getHash()); // empty method call to showcase GuardTime SDK
            printRootHash(tree.getRoot().getHash().toString());

            if (auditTrailRecord != null) {
                printAuditTrail(new AuditTrailBuilder(hasher, ENCODING).createAuditProof(tree, auditTrailRecord));
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println(String.format("Invalid hash algorithm: %s. Please, provide the name of the algorithm as per Java Cryptography Architecture Standard Algorithm Name Documentation (https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest)", hashAlgorithm));
            System.exit(0);
        } catch (IOException e) {
            System.err.format("Exception when trying to read %s", filePath);
            e.printStackTrace();
            System.exit(0);
        } catch (LogRecordNotFoundException e) {
            System.err.format(e.getMessage());
            System.exit(0);
        }
    }

    private static LinkedList<byte[]> readLog(String filePath) throws IOException {
        return new LogFileReader(ENCODING).read(new File(filePath));
    }

    private static void printRootHash(String hash) {
        System.out.println(String.format("Root hash: %s", hash));
    }

    private static void printAuditTrail(LinkedList<String> auditTrail) {
        Iterator it = auditTrail.descendingIterator();
        System.out.println();
        System.out.println("Audit trail (bottom-up)");
        System.out.println();
        while(it.hasNext()) {
            System.out.println((String) it.next());
            if (it.hasNext()) {
                System.out.println("^");
            }
        }
    }

    private static void validateArgs(String... args) {
        String argsErrorMsg = null;

        if (args.length < 2) {
            argsErrorMsg = "Please provide at minimum two arguments: 1) logfile name; 2) name of the hash algorithm as per Java Cryptography Architecture Standard Algorithm Name Documentation (https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest) 3) content of the log record to provide an audit trail for (optional)";
        }

        if (argsErrorMsg != null && argsErrorMsg.length() > 0) {
            System.out.println(argsErrorMsg);
            System.exit(0);
        }

    }

}
