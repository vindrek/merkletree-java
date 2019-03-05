# merkletree-java

## A simple POC application to build Merkle (binary) trees from log files and generate audit proofs of their records.

The implementation builds the tree using a tail call optimized (sadly, not really optimized in JVM) recursive bottom-up "decrease and conquer" strategy while aggregating nodes in parallel, using Java Streams API.

The time complexity of the algorithm is O(n).  

Why recursion? While using imperative loops would've been the go-to style (and more performant) to use with Java, the author finds that the algorithm of tree building is best explained recursively. Moreover, a top-down "divide and conquer" strategy implemented in some functional programming language could further improve the declarativeness/verbosity and make the code more maintainable (with a trade-off of some effectiveness).  

To showcase root hash signing procedure, the application includes dummy calls to Guardtime's KSI SDK for the root hash signing procedure.  


### Performance

It must be noted that this is still a POC and the code could be further optimized for real-world use. For example, the application loads the whole log file (in UTF-8) in to the memory first and processes it all at once.  

1 run test result for getting a root hash for 587,7 MB file with 2647237 rows:

```
time java -jar merkletree-0.1-SNAPSHOT.jar /Users/Indrek/dev/merkletree/src/main/resources/access.log SHA-256
Root hash: 50c8f3ccfab40ce9d9ca57f9b6d3f5ca06ca32a70624d0b47e391b12c49f92af

real	0m12.683s
user	0m37.009s
sys	0m2.807s
```

Therefore, at it's current state, the project is not optimized and appropriate for large log files. As a possible solution, modifications could be made to process the file in N-row chunks and then merge the corresponding subtrees (possibly also in chunks).  

### Balancing  

The resulting trees are *not balanced*. This is due to the fact that because trees are being built as an aggregation product going bottom-up with the last odd node being dragged over to the next recursive aggregation. Max depth of the tree is log2(n)+1.  

If producing *perfectly balanced* binary trees would be a requirement, a dynamic tree implementation which re-builds itself upon every node insertion (e.g. red-black tree) or divide and conquer top-down methodology would be more appropriate (see my elixir and javascript solutions).  

Alternatively, a *more balanced* tree could be achieved when the last odd node would not be dragged over to the next recursive aggregation, but aggregated with a random node on the same level.  

If balancing and audit proof chain depth werent't a requirement at all then the aggregator could be optimized to output linear hash chains (which are also, technically, binary trees).  

### Running the application

Either clone the repo and build the project using the checked in Gradle wrapper or [download the binary](https://github.com/vindrek/merkletree-java/releases/tag/0.1-SNAPSHOT). The application expects the following command-line arguments:  
1) path to the logfile  
2) name of the hashing algorithm as per [Java Cryptography Architecture Standard Algorithm Name Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest)  
3) logfile record for which an audit trail is requested (optional)  

Example input:
```
java -jar merkletree-0.1-SNAPSHOT.jar /path/to/log.txt SHA-256 audit-proof-for-me-please
```

Example output:
```
Root hash: 37cdcb925de966a3c2464df3b129829bfbf9391b27cfe0554f266f9fed73d601

Audit trail (bottom-up)

9a56c9fd833704abc1b862c7a990bafec41b35afaa536da210b04a16bf04742f
^
9a56c9fd833704abc1b862c7a990bafec41b35afaa536da210b04a16bf04742f
^
e11cd946492dfe746e602beaefb32ec0270a69527a30ca7b227878a7776f924e
^
ca26959ec0aefff80626c8c8684abe481c0d78997e2107624256239465110bc5
^
c5ad7b8eb5fc8f230dfd0b898d532d3932d8f8e56893c0a2176b50691e98e477
^
aae489785ae3d60a6e542d44b772a33e72b1d74bcc209dc8faaa651516ee72c1
^
a28375409b45133b143884e3461e2dcbb5c89a4b99135360ed4a64a546e4226f
^
aa3d3974f44df74046a67bffd29baf62c3132ebf68f2a87e241b09b203838e4f
^
524992e8d7c34acdbb3ca480645c9aaf94f7de100ed9f6c67d8924cda98ce1bf
```

Requirements:  
* Open JDK/JRE version 8  