# Java 8 port specific information

The backport is available [on GitHub](https://github.com/Michael993/javageci/tree/rollback).

The [jamal](https://github.com/verhas/jamal) dependency and module was removed. Later releases may reintroduce them.
Because the main project relies on Java 8+ features, a new class called GeciCompatibilityTools was introduced in javageci-tools/javax0.geci.tools as substitution.
If you encounter any issues with the backport, please send me an e-mail or report it as an issue [on the original project](https://github.com/verhas/javageci/issues).

### Maintainer
Mihály Verhás
E-mail: misi.verhas@gmail.com