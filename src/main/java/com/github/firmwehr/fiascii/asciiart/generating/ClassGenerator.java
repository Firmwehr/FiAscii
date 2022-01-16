package com.github.firmwehr.fiascii.asciiart.generating;

import static java.util.function.Predicate.not;

import com.github.firmwehr.fiascii.asciiart.elements.AsciiBox;
import com.github.firmwehr.fiascii.asciiart.elements.AsciiMergeNode;
import com.github.firmwehr.fiascii.util.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassGenerator {

	private final Map<AsciiBox, FilterElement> elements;
	private final AsciiBox root;

	public ClassGenerator(AsciiBox root) {
		this.root = root;
		this.elements = new HashMap<>();
	}

	public String generate(String name) {
		String result = """
			package com.github.firmwehr.fiascii.generated;

			import firm.Mode;
			import firm.nodes.*;
			import firm.nodes.*;
			import java.util.*;
			import com.github.firmwehr.fiascii.asciiart.generating.BaseMatch;
			import com.github.firmwehr.fiascii.asciiart.parsing.filter.*;
			      
			public class %s {

			""".formatted(name);

		FilterElement rootConverted = convert(root);

		result += buildMatchClass().indent(2).stripTrailing();
		result += "\n";
		result += buildFilter(rootConverted).indent(2).stripTrailing() + "\n";
		result += "\n";
		result += buildMatchMethod().indent(2).stripTrailing() + "\n";
		result += "\n";
		result += "}";

		return result;
	}

	private String buildFilter(FilterElement rootConverted) {
		return "\nprivate static final NodeFilter filter = %s;".formatted(rootConverted.filter);
	}

	private String buildMatchClass() {
		String fields = elements.values()
			.stream()
			.sorted(Comparator.comparing(FilterElement::name))
			.map(it -> "%s %s".formatted(it.type(), it.name()))
			.collect(Collectors.joining(",\n  "))
			.indent(2)
			.stripTrailing();

		List<String> fieldNames = elements.values()
			.stream()
			.sorted(Comparator.comparing(FilterElement::name))
			.map(FilterElement::name)
			.toList();

		String nodesMethod = """
			@Override
			public Set<Node> matchedNodes() {
			  return Set.of(%s);
			}
			""".formatted(String.join(", ", fieldNames)).indent(2).stripTrailing();

		return "public record Match(\n  %s\n) implements BaseMatch {\n%s\n}".formatted(fields,
			nodesMethod);
	}

	private String buildMatchMethod() {
		String matchConstructorArguments = elements.values()
			.stream()
			.sorted(Comparator.comparing(FilterElement::name))
			.map(it -> "(%s) matches.get(\"%s\")".formatted(it.type(), it.name()))
			.collect(Collectors.joining(",  \n"))
			.indent(4)
			.stripTrailing();

		return """
			public static Optional<Match> match(Node node) {
			  if (filter.doesNotMatch(node)) {
			    return Optional.empty();
			  }
			  Map<String, Node> matches = new HashMap<>();
			  if (!filter.storeMatch(matches, node)) {
			    return Optional.empty();
			  }
			  Match match = new Match(
			%s
			  );

			  return Optional.of(match);
			}
			""".formatted(matchConstructorArguments);
	}

	private FilterElement convert(AsciiBox root) {
		if (elements.containsKey(root)) {
			return elements.get(root);
		}

		StringReader reader = new StringReader(String.join(" ", root.lines()));
		reader.readWhitespace();
		String name = reader.readWhile(c -> c != ':');
		String quotedName = '"' + name + '"';
		reader.readChar();
		reader.readWhitespace();
		String nodeFilter = reader.readWhile(not(Character::isWhitespace)).strip();
		String nodeType = nodeFilter.equals("*") ? "Node" : nodeFilter;

		String filter = "new ClassFilter(%s, %s)".formatted(quotedName, nodeType + ".class");

		String argument = reader.readWhile(c -> c != ';').strip();
		if (!argument.isEmpty()) {
			filter = switch (nodeType) {
				case "Cmp" -> "new CmpFilter(%s, firm.Relation.%s)".formatted(quotedName, argument);
				case "Const" -> "new ConstFilter(%s, %s)".formatted(quotedName, argument);
				case "Phi" -> "new PhiFilter(%s, %s)".formatted(
					quotedName,
					argument.equals("+loop") ? "true" : "false"
				);
				case "Proj" -> "new ProjFilter(%s, %s)".formatted(quotedName, argument);
				default -> throw new IllegalArgumentException(
					"This node does not take an argument: " + nodeType + " arg: " + argument);
			};
		}

		if (!root.ins().isEmpty()) {
			List<FilterElement> inFilters;
			String className;
			if (root.ins().get(0).start() instanceof AsciiMergeNode merge) {
				className = "WithInputsUnorderedFilter";
				inFilters = merge.in().stream().map(it -> convert((AsciiBox) it.start())).toList();
			} else {
				className = "WithInputsOrderedFilter";
				inFilters = root.ins().stream().map(it -> convert((AsciiBox) it.start())).toList();
			}

			String inputFilters = inFilters.stream()
				.map(FilterElement::filter)
				.collect(Collectors.joining(",\n"))
				.indent(4);
			filter = "new %s(\n  %s,\n  %s,\n  List.of(\n%s  )\n)".formatted(
				className, quotedName, filter, inputFilters
			);
		}

		if (reader.peek() == ';') {
			reader.readChar();
			reader.readWhitespace();
			char positivity = reader.readChar();
			String property = reader.readWhile(Character::isLetter);
			if (property.equals("memory")) {
				filter = "new ModeFilter(\n  %s,\n  %s,\n  %s,\n  %s\n)".formatted(
					quotedName, "Mode.getM()", positivity == '-' ? "true" : "false", filter
				);
			}
		}

		FilterElement filterElement = new FilterElement(root, name, filter, nodeType);
		elements.put(root, filterElement);

		return filterElement;
	}

	private record FilterElement(
		AsciiBox element,
		String name,
		String filter,
		String type
	) {

	}
}
