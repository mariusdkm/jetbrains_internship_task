# JetBrains internship task `Improving code generation with code analysis and project information`

This project consists of three main parts:
1. *extract_methods* - A small Java program that extracts all the methods from the IntelliJ IDEA Community Edition source code and saves them to a file.  
  Run `extract_methods/src/main/java/com/unexcellent/Main.java` for extracting methods.
2. *pytorch_lightning.ipynb* - A Jupyter Notebook that contains a first approach using PyTorch Lightning to finetune a model to predict the method name from the method body.
3. *huggingface.ipynb* - A Jupyter Notebook that contains a second approach only using standard HuggingFace methods to finetune a model to predict the method name from the method body.
 
There is a `requirements.txt` file that contains all the dependencies needed to run the Jupyter Notebooks.