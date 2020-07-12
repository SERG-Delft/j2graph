# J2Graph

Given a Java program (source code), J2Graph creates a graph
representation of the source code.

J2Graph's graph has the following nodes:

* **Non-terminal nodes:** All non-terminal nodes that JDT's internal representation
contains.
* **Tokens**: Tokens in the source code. The list of tokens is not complete, as it is
basically reconstructed from the AST. The main tokens appear, yet, it does not contain
parenthesis, colons, etc.
* **Vocabulary**: Words that are extracted from the source code. Words are derived after the 
tokens, e.g., someVariable becomes two words, "some" and "variable".
* **Symbols**: The symbols of the program. Variables and method names are examples.

J2Graph's graph has the following edges, inspired by Miltos et al [1] and Cvitkovic et al [2]:

| Edge             | Explanation                                                                     |
|------------------|---------------------------------------------------------------------------------|
| NEXT_TOKEN       | two consecutive token nodes                                                     |
| CHILD            | non-terminal nodes to their children nodes and tokens                           |
| NEXT_LEXICAL_USE | each token that is bound to a variable to its next lexical use.                 |
| ASSIGNED_FROM    | the right hand side of an assignment expression to its left hand-side.          |
| RETURNS_TO       | all return statements to the method declaration node where control returns.     |
| OCCURRENCE_OF    | all token and syntax nodes that bind to a symbol to the respective symbol node. |
| SUBTOKEN_OF      | each identifier token node to the vocabulary nodes of its subtokens.            |

J2Graph also generates a dot output, so that you can visualize the outcome.

## Example

For the following source code:

```java
class D {

    public void m1() {
        int total = 0;
        for(int i = 0; i < 10; i++) {
            total += i;
        }
        return total;
    }

}
```

J2Graph generates the following graph:

![dotGraph](doc/example.png)

## References

* [1] Allamanis, Miltiadis, Earl T. Barr, Soline Ducousso, and Zheng Gao. "Typilus: neural type hints." arXiv preprint arXiv:2004.10657 (2020).

* [2] Cvitkovic, Milan, Badal Singh, and Animashree Anandkumar. "Open vocabulary learning on source code with a graph-structured cache." In International Conference on Machine Learning, pp. 1475-1485. 2019.
Harvard	

## License

Apache 2.0