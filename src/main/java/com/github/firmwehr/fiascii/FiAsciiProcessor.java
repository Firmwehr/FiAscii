package com.github.firmwehr.fiascii;

import com.github.firmwehr.fiascii.asciiart.elements.AsciiBox;
import com.github.firmwehr.fiascii.asciiart.generating.ClassGenerator;
import com.github.firmwehr.fiascii.asciiart.parsing.AsciiArtGraphParser;
import com.github.firmwehr.fiascii.asciiart.util.AsciiGrid;
import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.github.firmwehr.fiascii.FiAscii")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class FiAsciiProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(FiAscii.class)) {
			ExecutableElement executable = (ExecutableElement) element;
			DeclaredType returnType = (DeclaredType) executable.getReturnType();
			DeclaredType patternMatchType = (DeclaredType) returnType.getTypeArguments().get(0);
			String patternName = patternMatchType.asElement().getEnclosingElement().getSimpleName()
				.toString();

			String asciiArtString = element.getAnnotation(FiAscii.class).value();
			AsciiBox root = new AsciiArtGraphParser(AsciiGrid.fromString(asciiArtString)).parse();
			String sourceFileString = new ClassGenerator(root).generate(patternName);

			try {
				JavaFileObject sourceFile = processingEnv.getFiler()
					.createSourceFile("com.github.firmwehr.fiascii.generated." + patternName, element);
				try (Writer writer = sourceFile.openWriter()) {
					writer.write(sourceFileString);
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		return false;
	}

}
