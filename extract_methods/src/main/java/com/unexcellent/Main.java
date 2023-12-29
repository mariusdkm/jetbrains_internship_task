package com.unexcellent;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws IOException {
        // Replace this with the path to the IntelliJ Community source code
//        String sourceCodePath = "intellij-community/java";
        String sourceCodePath = "intellij-community/java";

        // Replace this with the path where you want to save the extracted methods in JSON format
        String outputPath = "output.json";

        // Create a FileWriter to write the output to a file
        FileWriter fileWriter = new FileWriter(outputPath);

        // Begin JSON array
        fileWriter.write("[\n");

        // Create a JavaParser with java 17 support
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        JavaParser javaParser = new JavaParser(parserConfiguration);

        AtomicInteger countSuccess = new AtomicInteger();
        AtomicInteger countFail = new AtomicInteger();

        // Visit all Java files in the source code directory
        Files.walk(Paths.get(sourceCodePath))
//                .parallel()
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        // Parse the Java file

                        ParseResult<CompilationUnit> cu = javaParser.parse(path.toFile());

                        // Create a visitor to visit method declarations and extract information
                        VoidVisitor<Void> methodVisitor = new MethodVisitor(fileWriter);
//                        cu.getProblems().forEach(System.out::println);
                        if (cu.isSuccessful() && cu.getResult().isPresent()) {
                            methodVisitor.visit(cu.getResult().orElseThrow(), null);
                            countSuccess.getAndIncrement();
                        } else {
                            System.out.println("Error in " + path);
                            countFail.getAndIncrement();
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to parse " + path);
                    }
                });

        // End JSON array
        fileWriter.write("]");

        // Close the FileWriter
        fileWriter.close();

        System.out.println("Success: " + countSuccess.get());
        System.out.println("Fail: " + countFail.get());
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        private final FileWriter fileWriter;

        public MethodVisitor(FileWriter fileWriter) {
            this.fileWriter = fileWriter;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            try {
                // Escape special characters in the method body
                String escapedMethod = escapeSpecialCharacters(n.asMethodDeclaration().toString());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", n.getNameAsString());
                jsonObject.put("method", escapedMethod);
                jsonObject.put("hasBody", n.getBody().isPresent());
//                jsonObject.put("class", n.findAncestor(Node.TreeTraversal.PARENTS, com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class).get().getNameAsString());

                fileWriter.write(jsonObject + ",\n");

                // Write the method as a JSON object
//                fileWriter.write("{ \"name\": \"" + n.getNameAsString() + "\", \"body\": \"" + escapedBody + "\" },\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            super.visit(n, arg);
        }

        private String escapeSpecialCharacters(String input) {
            return input.replace("\"", "\\\"");
        }
    }
}
