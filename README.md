# merkletree-java

## A simple POC application to build Merkle (binary) trees from log files and generate audit proofs of their records.

The implementation builds the tree using a tail call optimized (sadly, not really optimized in JVM) recursive bottom-up "decrease and conquer" strategy while aggregating nodes in parallel, using Java Streams API. 

The time complexity of the algorithm is O(n).  

Why recursion? While using imperative loops would've been the go-to style (and more performant) to use with Java, the author finds that the algorithm of tree building is best explained recursively. Moreover, a top-down "divide and conquer" strategy implemented in some functional programming language could further improve the declarativeness/verbosity and make the code more maintainable (with a trade-off of some effectiveness).  

To showcase root hash signing procedure, the application includes dummy calls to GuardTime's KSI SDK for the root hash signing procedure.  

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

For example, 
```
java -jar merkletree-0.1-SNAPSHOT.jar /path/to/log.txt SHA-256 audit-proof-for-me-please
```

Requirements:  
* Open JDK/JRE version 8  