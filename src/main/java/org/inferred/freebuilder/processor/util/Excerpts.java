package org.inferred.freebuilder.processor.util;

import static java.util.Arrays.asList;

import com.google.common.collect.ImmutableList;

import org.inferred.freebuilder.processor.util.feature.JavaxPackage;

import java.util.List;

public class Excerpts {

  private static final class AddingExcerpt extends Excerpt {
    private final String fmt;
    private final Object[] args;

    private AddingExcerpt(String fmt, Object[] args) {
      this.args = args;
      this.fmt = fmt;
    }

    @Override
    public void addTo(SourceBuilder source) {
      source.add(fmt, args);
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder()
          .append("Excerpts.add(\"")
          .append(fmt.replaceAll("[\\\"]", "\\\1"))
          .append('"');
      for (Object arg : args) {
        result.append(", ").append(arg);
      }
      result.append(")");
      return result.toString();
    }

    @Override
    protected void addFields(FieldReceiver fields) {
      fields.add("fmt", fmt);
      fields.add("args", asList(args));
    }
  }

  public static Excerpt add(final String fmt, final Object... args) {
    return new AddingExcerpt(fmt, args);
  }

  private static final class EmptyExcerpt extends Excerpt {
    @Override
    public void addTo(SourceBuilder source) {}

    @Override
    protected void addFields(FieldReceiver fields) {}
  }

  private static final Excerpt EMPTY = new EmptyExcerpt();

  public static Excerpt empty() {
    return EMPTY;
  }

  private static final class GeneratedAnnotationExcerpt extends Excerpt {
    private final Class<?> generator;

    GeneratedAnnotationExcerpt(Class<?> generator) {
      this.generator = generator;
    }

    @Override
    public void addTo(SourceBuilder code) {
      code.feature(JavaxPackage.JAVAX).generated().ifPresent(generated -> {
        code.addLine("@%s(\"%s\")", generated, generator.getName());
      });
    }

    @Override
    protected void addFields(FieldReceiver fields) {
      fields.add("generator", generator);
    }
  }

  /**
   * Returns an excerpt of the {@link javax.annotation.Generated} annotation, if available,
   * with value set to the full name of the {@code generator} class as recommended.
   */
  public static Excerpt generated(Class<?> generator) {
    return new GeneratedAnnotationExcerpt(generator);
  }

  private static final class JoiningExcerpt extends Excerpt {
    private final String separator;
    private final List<?> excerpts;

    private JoiningExcerpt(String separator, Iterable<?> excerpts) {
      this.separator = separator;
      this.excerpts = ImmutableList.copyOf(excerpts);
    }

    @Override
    public void addTo(SourceBuilder source) {
      String itemPrefix = "";
      for (Object object : excerpts) {
        source.add("%s%s", itemPrefix, object);
        itemPrefix = separator;
      }
    }

    @Override
    protected void addFields(FieldReceiver fields) {
      fields.add("separator", separator);
      fields.add("excerpts", excerpts);
    }
  }

  public static Object join(final String separator, final Iterable<?> excerpts) {
    return new JoiningExcerpt(separator, excerpts);
  }

  private Excerpts() {}
}
